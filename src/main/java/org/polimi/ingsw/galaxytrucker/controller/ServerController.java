package org.polimi.ingsw.galaxytrucker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import org.polimi.ingsw.galaxytrucker.network.Heartbeat;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class ServerController extends UnicastRemoteObject implements ServerControllerHandles {

    private final ArrayList<LobbyManager> lobbyManagers;
    private final MessageManager messageManager;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final HashMap<UUID, String> clientNicknameMap = new HashMap<>();
    //private final ArrayList<String> usedNicknames = new ArrayList<>();
    private final ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();
    private final ArrayList<Heartbeat> heartbeats = new ArrayList<>();

    private ArrayList<Tile> gameTiles;

    private static final NetworkMessageNameVisitor networkMessageNameVisitor = new NetworkMessageNameVisitor();

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public ServerController() throws RemoteException {
        super();

        this.lobbyManagers = new ArrayList<>();
        messageManager = new MessageManager(this);
        initActionsAllowed();
//        model.setRealGame(new Game(4, false));
        generateGameTiles();
    }


    public ArrayList<Tile> getGameTiles() {
        return gameTiles;
    }

    public void generateGameTiles() {
        File file = new File("src/main/resources/tiledata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.gameTiles = (ArrayList<Tile>) mapper.readValue(file, new TypeReference<List<Tile>>() {
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public ArrayList<ClientHandler> getClients() {
        synchronized (clients) {
            return clients;
        }
    }

    public void addLobbyManager(LobbyManager game) {
        lobbyManagers.add(game);
    }

    /**
     * Safely removes a client, freeing its nickname if it exists, and kicking it from an eventual game it's in.
     *
     * @param client The client's {@link ClientHandler}
     * @author Alessandro Giuseppe Gioia
     */
    public void removeClient(ClientHandler client) {
        String nickname = getNicknameFromClientHandler(client);
        LobbyManager game = getLobbyFromHandler(client);

        if (game != null) {
            game.getGameController().kickPlayerFromGame(nickname);

            synchronized (lobbyInfos) {
                lobbyInfos.get(lobbyManagers.indexOf(game)).removeConnectedPlayer();
            }

            if (game.getPlayerHandlers().isEmpty()) {
                synchronized (lobbyInfos) {
                    lobbyInfos.removeIf(info -> info.getLobbyID() == lobbyManagers.indexOf(game));
                }
                //System.out.println("A game was empty. Cleared from the list of games.");
                synchronized (lobbyManagers) {
                    lobbyManagers.remove(game);
                }
            }
        }

        if (nickname != null && !nickname.isBlank()) {
            synchronized (clientNicknameMap) {
                clientNicknameMap.remove(client.getClientID());
            }
        }

        synchronized (clients) {
            clients.remove(client);
        }
    }

//    public ArrayList<String> getClientHandlerToNicknameMap() {
//        synchronized (usedNicknames) {
//            return usedNicknames;
//        }
//    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    @NeedsToBeChecked("Un po' confusionaria la query, magari si può fare una Map<ClientHandler, LobbyManager> o Map<UUID, LobbyManager>")
    public LobbyManager getLobbyFromHandler(ClientHandler clientHandler) {

        LobbyManager lobbyManager;
        synchronized (lobbyManagers) {
            lobbyManager = lobbyManagers.stream().filter(gameModel ->
                    gameModel.getPlayerHandlers().values().stream().anyMatch(h -> h.getClientID().equals(clientHandler.getClientID()))).findFirst().orElse(null);
        }

        return lobbyManager;
    }

    public ArrayList<LobbyInfo> getLobbyInfos() {
        synchronized (lobbyInfos) {
            return lobbyInfos;
        }
    }


    /*
     *
     * HANDLES
     *
     */

    public void handleNicknameRequest(NicknameRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
                    Boolean result = false;
                    boolean flag = false;

                    //get nickname & check
                    String nickname = message.getNickname();
                    NicknameResponse nicknameResponse = new NicknameResponse(null, message.getID());


                    //System.out.println("REQ");
                    synchronized (clientNicknameMap) {

                        if (!clientNicknameMap.containsValue(nickname)) {
                            clientNicknameMap.put(clientHandler.getClientID(), nickname);
                            nicknameResponse.setResponse("VALID");
                        } else {
                            System.out.println("[+] NOT ADDED " + message.getNickname());
                            nicknameResponse.setResponse("INVALID");
                        }

                    }

                    clientHandler.sendMessage(nicknameResponse);
        });
        //System.out.println("SENDING RESPONSE\n");
    }

    public void handleCreateRoomRequest(CreateRoomRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            //get nickname & check
            String tempNick = message.getNickName();

            LobbyManager newGame = new LobbyManager();
            Player myPlayer = new Player(message.getNickName(), 0, 0, message.getIsLearningMatch());

            Color myColor = newGame.useNextAvailableColor();
            myPlayer.setColor(myColor);

            newGame.getPlayerColors().putIfAbsent(message.getNickName(), myColor);
            newGame.getRealGame().setLearningMatch(message.getIsLearningMatch());
            newGame.getRealGame().setnMaxPlayer(message.getMaxPlayers());
            try {
                newGame.getRealGame().addPlayer(myPlayer);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                System.err.println(e.getMessage());
                return;
            }
            newGame.addPlayerHandler(clientHandler, myPlayer.getNickName());
            newGame.getRealGame().initFlightBoard();

            Tile centralTile = null;

            for (Tile tile : gameTiles) {
                if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")) {
                    //System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                    CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                    if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)) {
                        centralTile = tile;
//                    gameTiles.remove(tile);
                    }

                }
            }

            newGame.getRealGame().getTileBunch().getTiles().remove(centralTile);


            myPlayer.getShip().putTile(centralTile, new Position(3, 2));


            synchronized (lobbyManagers) {
                lobbyManagers.add(newGame);
            }

            int index = lobbyManagers.indexOf(newGame);
            synchronized (lobbyInfos) {
                lobbyInfos.add(new LobbyInfo(message.getNickName(), message.getMaxPlayers(), 1, index, message.getIsLearningMatch()));

            }

            JoinRoomResponse joinRoomResponse = new JoinRoomResponse(message.getID());
            joinRoomResponse.setOperationSuccess(true);
            joinRoomResponse.setColor(myColor);
            joinRoomResponse.setMyShip(myPlayer.getShip());

            PlayerInfo playerInfo1 = new PlayerInfo();
            playerInfo1.setShip(myPlayer.getShip());
            playerInfo1.setNickName(myPlayer.getNickName());
            playerInfo1.setColor(myColor);
            newGame.addPlayerInfo(playerInfo1);


            clientHandler.sendMessage(joinRoomResponse);
            clientHandler.sendMessage(new FlightBoardUpdate(newGame.getRealGame().getFlightBoard()));
        });
    }

    public void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            JoinRoomOptionsResponse joinRoomOptionsResponse = new JoinRoomOptionsResponse(null, message.getID());
            synchronized (lobbyInfos) {
                joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos, message.getID());
            }
            clientHandler.sendMessage(joinRoomOptionsResponse);
        });
    }

    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            String mess = "";
            LobbyInfo myLobbyInfo;

            JoinRoomResponse joinRoomResponse = new JoinRoomResponse(message.getID());
            ArrayList<ClientHandler> playerHandlers;
            boolean result = false;
            PlayerJoinedUpdate playerJoinedUpdate;
            LobbyManager myGame = null;


            if (message.getRoomId() < lobbyManagers.size()) myGame = lobbyManagers.get(message.getRoomId());

            if (myGame == null) {
                mess = "Lobby number " + message.getRoomId() + " doesn't exist. Try again.";
                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(false);
                joinRoomResponse.setColor(null);
                clientHandler.sendMessage(joinRoomResponse);
                return;
            }

            synchronized (myGame) {

                //System.out.println("SIZE: " + myGame.getPlayerColors().size());

                if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers() || myGame.getGameController().getGameState() != GameState.LOBBY) {
                    //System.out.println("2");


                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY" + message.getRoomId() + "DOESN'T MUNGI YOU");
                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(false);
                    joinRoomResponse.setColor(null);
                    clientHandler.sendMessage(joinRoomResponse);
                    return;

                } else {

                    //System.out.println("3");

                    Player myPlayer = new Player(message.getNickName(), 0, 0, myGame.getRealGame().getIsLearningMatch());

                    //trovo la cabina con il colore

                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());


//                    lobbyInfos.get(0).addConnectedPlayer();


                    Color myColor = myGame.useNextAvailableColor();
                    myGame.getPlayerColors().putIfAbsent(message.getNickName(), myColor);
                    myPlayer.setColor(myColor);

                    //System.out.println("4");

                    //trovo la cabina centrale del colore dell'utente
                    Tile centralTile = null;

                    for (Tile tile : gameTiles) {
                        if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")) {
                            //System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                            CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                            if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)) {
                                centralTile = tile;
                                break;
                            }

                        }
                    }

                    myGame.getRealGame().getTileBunch().getTiles().remove(centralTile);


                    myPlayer.getShip().putTile(centralTile, new Position(3, 2));


                    try {
                        myGame.getRealGame().addPlayer(myPlayer);
                    } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }

                    myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());

                    playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());


                    PlayerInfo playerInfo1 = new PlayerInfo();
                    playerInfo1.setShip(myPlayer.getShip());
                    playerInfo1.setNickName(myPlayer.getNickName());
                    playerInfo1.setColor(myColor);
                    //qua
//                ArrayList<ClientHandler> PlayerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());

                    playerJoinedUpdate = new PlayerJoinedUpdate(playerInfo1);


                    synchronized (lobbyInfos) {
                        myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
                    }


                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(true);
                    joinRoomResponse.setColor(myColor);
                    joinRoomResponse.setMyShip(myPlayer.getShip());
//                joinRoomResponse.setIsLearningMatch(myGame.getRealGame().getIsLearningMatch());
                    myGame.addPlayerInfo(playerInfo1);

                    if (myLobbyInfo != null) {

                        myLobbyInfo.addConnectedPlayer();
                        result = true;
                    } else {

                        mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NOT FOUND :) " + message.getRoomId());
                        joinRoomResponse.setOperationSuccess(false);
                        joinRoomResponse.setColor(null);
                        joinRoomResponse.setMyShip(null);


                    }
                }

                //fine synchronized e

                PlayerInfo hostPlayerInfo = new PlayerInfo();
                hostPlayerInfo.setNickName(myLobbyInfo.getHost());
                hostPlayerInfo.setShip(myGame.getRealGame().getPlayer(myLobbyInfo.getHost()).getShip());
                hostPlayerInfo.setColor(myGame.getPlayerColors().get(myLobbyInfo.getHost()));

                joinRoomResponse.setIsLearningMatch(myLobbyInfo.isLearningMatch());
                if (result) joinRoomResponse.setPlayerInfos(myGame.getPlayerInfos());

//            clientHandler.sendMessage(new PlayerJoinedUpdate(hostPlayerInfo));
                clientHandler.sendMessage(new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));
                clientHandler.sendMessage(joinRoomResponse);
                //System.out.println("ID RESP: " + joinRoomResponse.getID());

                //se tutto è andato bene
                if (result) {

                    ArrayList<ClientHandler> original = new ArrayList<>(playerHandlers);
                    playerHandlers.remove(clientHandler);

                    playerJoinedUpdate.setPlayersJoinedBefore(myGame.getPlayerInfos());
                    broadCast(playerHandlers, playerJoinedUpdate);

                    //dopo aver mandato la notifica di connessione vedo se ho raggiunto il numero massimo di player per la lobby
                    //e starto il gioco automaticamente lato server

                    if (myGame.getRealGame().getMaxPlayers() == myGame.getRealGame().getNumPlayers()) {

                        //creo i deck

                        //creo flightDeck
                        myGame.getRealGame().createDecks();
                        ArrayList<CardDeck> decks = myGame.getRealGame().getDecks();


//                    CardDeck flightDeck = myGame.getRealGame().createFlightDeck(decks);


                        DecksUpdate decksUpdate = new DecksUpdate();
                        decksUpdate.setDecks(decks);
                        decksUpdate.setFlightDeck(null);


                        //broadcst tutte le player info

                        myGame.getGameController().nextState();
                        broadCast(original, decksUpdate);
                        broadCast(original, new PhaseUpdate(GameState.BUILDING_START));
                        //MANDARE I DECK -> -> ->

                        //dopo aver notificato tutti starto il gioco

                    }
                }

            }
        });

    }

    public void handleDrawTileRequest(DrawTileRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            //il client mi chiede una Tile, e devo restituirla
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Tile myTile = null;
            DrawTileResponse drawTileResponse;
            Boolean flag = false;


            if (!isActionAllowed(myGame, GameAction.DRAW_TILE)) {

                drawTileResponse = new DrawTileResponse(null, message.getID());
                drawTileResponse.setErrorMessage("INVALID_STATE");
                clientHandler.sendMessage(drawTileResponse);

                return;
            }

            synchronized (myGame.getTileBunch()) {

                //null significa cge si pesca dal mazzo, invece se è presente un valore valido di Tile si prende da quelle face-up
                if (message.getTile() != null) {

                    myTile = myGame.getTileBunch().drawFaceUpTile(message.getTile().getId());
                    if (myTile == null) {
                        drawTileResponse = new DrawTileResponse(null, message.getID());
                        drawTileResponse.setErrorMessage("TAKEN");
                    } else {
                        drawTileResponse = new DrawTileResponse(myTile, message.getID());
                        drawTileResponse.setErrorMessage("VALID");
                    }

                    FaceUpTileUpdate faceUpTileUpdate = new FaceUpTileUpdate();
                    faceUpTileUpdate.setFaceUpTiles(myGame.getTileBunch().getFaceUpTiles());
                    ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
                    broadCast(playerHandlers, faceUpTileUpdate);

                } else {

                    myTile = myGame.getTileBunch().drawTile();
                    if (myTile == null) {
                        drawTileResponse = new DrawTileResponse(null, message.getID());
                        drawTileResponse.setErrorMessage("EMPTY");
                    } else {
                        drawTileResponse = new DrawTileResponse(myTile, message.getID());
                        drawTileResponse.setErrorMessage("VALID");
                    }

                }

            }
            clientHandler.sendMessage(drawTileResponse);
        });
    }

    @NeedsToBeCompleted
    //Se player inserisce un Nickname non esiste? cosa ricevo
    public void handleFetchShipRequest(FetchShipRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);

            Player targetPlayer = myGame.getRealGame().getPlayer(message.getTargetNickname());
            Ship targetShip;
            ShipUpdate shipViewUpdate;

            targetShip = targetPlayer.getShip();
            shipViewUpdate = new ShipUpdate(targetShip, targetPlayer.getNickName());
            shipViewUpdate.setShouldDisplay(true);

            clientHandler.sendMessage(shipViewUpdate);
        });
    }

    public void handleCheckShipStatusRequest(CheckShipStatusRequest message, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            //devo controllare se la nave è corretta

            //prima di tutto la salvo come nuova nave

            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Player player = getPlayerFromClientHandler(clientHandler);
            Ship ship = player.getShip();

            //trovo tutte le Tiles da rimuovere
            List<Slot> Slots = Arrays.stream(ship.getShipBoard())
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toList();

            for (Slot slot : Slots) {
                if (slot.getTile() != null && message.getRemovedTilesId().contains(slot.getTile().getId())) {
                    ship.removeTile(slot.getPosition(), true);
                }
            }

            Boolean result = ship.checkShip();
            ShipUpdate shipUpdate = new ShipUpdate(ship, player.getNickName());
            CheckShipStatusResponse response = new CheckShipStatusResponse(ship, result, message.getID());
            clientHandler.sendMessage(shipUpdate);
            clientHandler.sendMessage(response);


            if (result) {
                synchronized (myGame.checkShipLock) {
                    myGame.getGameController().addCompletedShip();
                    if (myGame.getRealGame().getNumPlayers() == myGame.getGameController().getnCompletedShips() && !myGame.getGameController().getGameState().equals(GameState.CREW_INIT)) {
                        myGame.getGameController().nextState();

                        ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());

                        PhaseUpdate phaseUpdate = new PhaseUpdate(GameState.CREW_INIT);
                        broadCast(playerHandlers, phaseUpdate);
                    }
                }
            }

        });
    }

    public void handleAskPositionResponse(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            try {
                handleFinishBuildingRequest2(askPositionResponse, clientHandler);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleSelectPlanetResponse(SelectPlanetResponse selectPlanetResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(selectPlanetResponse);
            tryExecutePhaseAfterMessage(game, NetworkMessageType.SelectPlanetResponse);
        });
    }

    public void handleFinishBuildingRequest(FinishBuildingRequest finishBuildingRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            AskPositionUpdate askPositionUpdate;
            ArrayList<Integer> validPos = new ArrayList<>();

            // [1] fisso l'ultima tile di Player

            //devo chiedere in che posizione vuole essere
            myGame.addPlayerShipFinished(nickname);


            synchronized (myGame.positionLock) {
                int maxPos = myGame.getRealGame().getNumPlayers();
                int minPos = 1;
                ArrayList<Integer> takenPos = myGame.getRealGame().getFlightBoard().getOccupiedPositions();


                for (int i = 1; i <= maxPos; i++) {

                    int realPos = 0;

                    switch (i) {
                        case 1 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFirstPos();
                        case 2 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getSecondPos();
                        case 3 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getThirdPos();
                        case 4 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFourthPos();
                    }

                    if (!takenPos.contains(realPos)) {
                        validPos.add(i);
                    }
                }

                //in occupied positions ho i valori interi che vanno tradotti nelle posizioni
//            for (int pos: takenPos){
//
//            }

                askPositionUpdate = new AskPositionUpdate(validPos);
                askPositionUpdate.nickname = nickname;
                CompletableFuture<NetworkMessage> future = new CompletableFuture<>();

                //aggiungo alle pending responses

                myGame.addPendingResponse(future, askPositionUpdate.getID());
                //System.out.println(askPositionUpdate.getID());

                clientHandler.sendMessage(askPositionUpdate);
                //ho la mia posizione scelta, e lo posiziono
            }
        });
    }

    public void handleFinishBuildingRequest2(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            Boolean flag = false;

            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);

            Color playerColor = myGame.getPlayerColors().get(nickname);

            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());

            int realPos = 0;
            synchronized (myGame.positionLock) {
                switch (askPositionResponse.getPosition()) {
                    case 1 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFirstPos();
                    case 2 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getSecondPos();
                    case 3 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getThirdPos();
                    case 4 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFourthPos();
                }
                myGame.getRealGame().getFlightBoard().positionPlayer(playerColor, realPos);
                myGame.getRealGame().getPlayer(nickname).setPlacement(askPositionResponse.getPosition());
            }


            broadCast(playerHandlers, new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));

//            myGame.getRealGame().getFlightBoard()


            synchronized (myGame.getRealGame().getPlayer(nickname).getShip()) {

                Ship myShip = myGame.getRealGame().getPlayer(nickname).getShip();


                //se quando arriva building request il client e' aggiornato
                Tile lastTile = myGame.getRealGame().getPlayer(nickname).getShip().getLastTile();

                if (!(lastTile == null)) {
                    int lastTileId = lastTile.getId();
//                Ship myShip = myGame.getRealGame().getPlayer(nickname).getShip();

                    //prendo tutte le tile
                    List<Slot> slots = Arrays.stream(myShip.getShipBoard())
                            .flatMap(Arrays::stream)
                            .filter(Objects::nonNull)
                            .toList();

                    //trovo la tile e la fisso
                    for (Slot slot : slots) {
                        if (slot.getTile() != null && slot.getTile().getId() == lastTileId) {
                            myShip.getShipBoard()[slot.getPosition().getX()][slot.getPosition().getY()].getTile().setFixed(true);
                            break;
                        }
                    }

                }

                broadCast(playerHandlers, new ShipUpdate(myShip, nickname));
            }

            //controllo se tutti hanno finito
            if (myGame.getPlayerShipFinishedSize() == myGame.getRealGame().getNumPlayers()) {

                myGame.getGameController().nextState();
                if (myGame.getGameController().getGameState().equals(GameState.BUILDING_END))
                //se hanno finito tutti allora si passa alla fase di check_ship
                {
                    myGame.getGameController().nextState();
                    //System.out.println("STATE: " + myGame.getGameController());
                    broadCast(playerHandlers, new PhaseUpdate(GameState.SHIP_CHECK));
                }

            }
        });
    }

    public void handlePlaceTileRequest(PlaceTileRequest placeTileRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            PlaceTileResponse placeTileResponse = new PlaceTileResponse(null, placeTileRequest.getID());
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            Player myPlayer = myGame.getRealGame().getPlayer(nickname);
            Ship myShip = myPlayer.getShip();


            //controllo se posso eseguirla

            if (!isActionAllowed(myGame, GameAction.PLACE_TILE)) {
                placeTileResponse.setMessage("INVALID_STATE");
                clientHandler.sendMessage(placeTileResponse);
                return;
            }

            //dopo che ho tutto devo semplicemente inserire la Tile

            synchronized (myShip) {

                Position myPos = placeTileRequest.getPos();
                Tile myTile = placeTileRequest.getTile();
                List<Slot> Slots = Arrays.stream(myShip.getShipBoard())
                        .flatMap(Arrays::stream)
                        .filter(Objects::nonNull)
                        .toList();

                //controllo la posizione


                //se e' valida
                if (!myShip.getInvalidPositions().contains(myPos)) {

                    //controllo se la posizione non è occupata
                    for (Slot slot : Slots) {
                        Tile tempTile = slot.getTile();
                        if (tempTile != null && tempTile.getId() == myTile.getId()) {

                            //esiste gia la tile
                            placeTileResponse.setMessage("INVALID_POS");
                            clientHandler.sendMessage(placeTileResponse);
                            return;
                        }
                    }

                    //non esiste, allora la inserisco

//            myTile.setFixed(true);
                    myShip.putTile(myTile, myPos);
                    //resetto lastTile
                    myShip.setLastTile(myTile);
//            //la ship e' aggiornata
//            myShip.setSynch(true);
                    //setto il messaggio
                    placeTileResponse.setMessage("VALID");

                    //da capire se ha senso creare una PlaceTileResponse


                    //broadCasto la nuova nave a tutti
                    ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
                    ShipUpdate shipUpdate = new ShipUpdate(myShip, myPlayer.getNickName());
                    broadCast(playerHandlers, shipUpdate);
                    clientHandler.sendMessage(placeTileResponse);


                } else {
                    PlaceTileResponse resp = new PlaceTileResponse(null, placeTileRequest.getID());
                    resp.setMessage("INVALID_POS");
                    clientHandler.sendMessage(resp);
                }


            }

        });
    }

    @NeedsToBeCompleted
    public void handleDiscardTileRequest(DiscardTileRequest discardTileRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            //myGame.getTileBunch().getFaceUpTiles();
            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
            myGame.getTileBunch().returnTile(discardTileRequest.getTile());


            broadCast(playerHandlers, new TileDiscardedUpdate(discardTileRequest.getTile()));

//
            FaceUpTileUpdate faceUpTileUpdate = new FaceUpTileUpdate();
            faceUpTileUpdate.setFaceUpTiles(myGame.getTileBunch().getFaceUpTiles());
            broadCast(playerHandlers, faceUpTileUpdate);
        });
    }

    @NeedsToBeCompleted
    public void handleViewAdventureDecksRequest(ViewAdventureDecksRequest viewAdventureDecksRequest, ClientHandler clientHandler) throws RemoteException {

    }

    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Player myPlayer = getPlayerFromClientHandler(clientHandler, myGame);
            Ship myShip = myPlayer.getShip();

            List<Position> positions = new ArrayList<>(crewInitUpdate.getCrewPos().stream().map(Pair::getKey).toList());

            List<Slot> Slots = Arrays.stream(myShip.getShipBoard())
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toList();

            for (Slot s : Slots) {

                Tile tempTile = s.getTile();
                Position tempPos = s.getPosition();

                if (positions.contains(tempPos) && s.getTile() != null) {

                    AlienColor color = crewInitUpdate.getCrewPos().stream().filter(pair -> pair.getKey().equals(tempPos)).map(Pair::getValue).findFirst().orElse(null);

                    if (color == null) return;

                    if (color.equals(AlienColor.PURPLE)) {
                        ModularHousingUnit purpleHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        purpleHousing.addPurpleAlien();
                    }

                    if (color.equals(AlienColor.BROWN)) {
                        ModularHousingUnit brownHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        brownHousing.addBrownAlien();
                    } else {
                        ModularHousingUnit humanHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        humanHousing.addHumanCrew();
                    }
                }
            }

            //dopo aver implementato la lista di quelli che hanno finito e si broadcasta la fase nuova


            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
            ShipUpdate shipUpdate = new ShipUpdate(myShip, myPlayer.getNickName());
            broadCast(playerHandlers, shipUpdate);


            myGame.addPlayerCrewFinished(myPlayer.getNickName());
            if (myGame.getPlayerCrewSize() == myGame.getRealGame().getNumPlayers()) {

                myGame.getGameController().nextState();
                PhaseUpdate phaseUpdate = new PhaseUpdate(GameState.FLIGHT);
                broadCast(playerHandlers, phaseUpdate);

                //new Thread(() -> {
                try {
                    myGame.getGameController().startFlight();
                } catch (ExecutionException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
                //}).start();
            }
        });
    }

    public void handleActivateAdventureCardResponse(ActivateAdventureCardResponse activateAdventureCardResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            synchronized (game.getGameController()) {
                game.getGameController().notify();
            }
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(activateAdventureCardResponse);
            tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateAdventureCardResponse);
        });
    }

    public void handleActivateComponentResponse(ActivateComponentResponse activateComponentResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            Player player = getPlayerFromClientHandler(clientHandler);
            Ship ship = player.getShip();
            ActivatableComponent activatableComponent = activateComponentResponse.getActivatableComponentType();

            ArrayList<Position> activatedComponentPositions = activateComponentResponse.getActivatedComponentPositions();
            ArrayList<Position> batteriesPositions = activateComponentResponse.getBatteriesPositions();


            if (activatedComponentPositions == null || batteriesPositions == null ||
                    activatedComponentPositions.isEmpty() || batteriesPositions.isEmpty()) {
                tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateComponentResponse);
                return;
            }

            for (int i = 0; i < activateComponentResponse.getActivatedComponentPositions().size(); i++) {
                //TODO FIX
                Position componentPos = activatedComponentPositions.get(i);
                Position batteryPos = batteriesPositions.get(i);

                switch (activatableComponent) {
                    case DoubleEngine -> ship.activateDoubleEngine(componentPos, batteryPos);
                    case DoubleCannon -> ship.activateDoubleCannon(componentPos, batteryPos);
                    case Shield -> ship.activateShield(componentPos, batteryPos);
                }
            }

            //Mando la shipUpdate
            ShipUpdate shipUpdate = new ShipUpdate(ship, player.getNickName());
            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(game.getPlayerHandlers().values());

            broadCast(playerHandlers, shipUpdate);

            tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateComponentResponse);
        });
    }

    public void handleHeartbeatRequest(HeartbeatRequest ignoredHeartbeatRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            //System.out.println(PrinterUtils.getTextWithLabel(PrinterLabels.Heartbeat, TuiColor.BRIGHT_RED, "Received heartbeat from " + clientHandler.toString() + "."));
            heartbeats.stream().filter(h -> h.getClientHandler().getClientID().equals(clientHandler.getClientID())).findFirst().ifPresent(h -> {
                heartbeats.remove(h);
                h.regenerate();
            });
        });
    }

    public void handleShipUpdate(ShipUpdate shipUpdate, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            if (shipUpdate.getOnlyFix()) {
                String nickname = getNicknameFromClientHandler(clientHandler);
                Player myPlayer = game.getRealGame().getPlayer(nickname);
                Ship myShip = myPlayer.getShip();

                synchronized (myShip) {
                    List<Slot> Slots = Arrays.stream(shipUpdate.getShipView().getShipBoard())
                            .flatMap(Arrays::stream)
                            .filter(Objects::nonNull)
                            .toList();

                    //trovo la tile non fissata

                    for (Slot slot : Slots) {

                        Tile tempTile = slot.getTile();

                        if (tempTile != null) {

                            if (!tempTile.getFixed()) {
                                tempTile.setFixed(true);
                                break;
                            }
                        }
                    }


                }

                ShipUpdate update = new ShipUpdate(myShip, myPlayer.getNickName());
                ArrayList<ClientHandler> playerHandlers = new ArrayList<>(game.getPlayerHandlers().values());

                broadCast(playerHandlers, update);
            }

            if (game.getGameController().getGameState() == GameState.FLIGHT) {
                tryExecutePhaseAfterMessage(game, shipUpdate.accept(networkMessageNameVisitor));
            }
        });
    }

    public void handleDiscardCrewMembersResponse(DiscardCrewMembersResponse discardCrewMembersResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(discardCrewMembersResponse);
            tryExecutePhaseAfterMessage(game, discardCrewMembersResponse.accept(networkMessageNameVisitor));
        });
    }

    public void handleCollectRewardsResponse(CollectRewardsResponse collectRewardsResponse, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(collectRewardsResponse);
            tryExecutePhaseAfterMessage(game, collectRewardsResponse.accept(networkMessageNameVisitor));
        });
    }

    public void handleDrawAdventureCardRequest(DrawAdventureCardRequest drawAdventureCardRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            GameController gameController = myGame.getGameController();

            if (gameController.getCurrentCardContext() != null) {
                clientHandler.sendMessage(new GameMessage("È già in corso una carta avventura."));
                return;
            }

            gameController.handleTurn();
        });
    }

    public void handleReadyTurnRequest(ReadyTurnRequest readyTurnRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            GameController gameController = myGame.getGameController();
            try {

                new Thread(() -> {
                    myGame.addReadyPlayer(nickname);
                    if (myGame.allActivePlayerReady()) {
                        gameController.sendMatchInfoUpdate();
                    }
                }).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleEarlyLandingRequest(EarlyLandingRequest earlyLandingRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            GameController gameController = myGame.getGameController();

            myGame.getGameController().removePlayerFromGame(nickname, true);
            new Thread(() -> {
//                myGame.getGameController().handleTurnBeforeDrawnCard();
                myGame.addEarlyLandingPlayer(nickname);
                if (myGame.allActivePlayerReady()) {
                    gameController.sendMatchInfoUpdate();
                }
            }).start();
        });
    }

    public void handleAskTimerInfoRequest(AskTimerInfoRequest askTimerInfoRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
//        myGame.completePendingResponse(askTimerInfoRequest.getID());
            TimerInfoResponse timerInfoResponse = new TimerInfoResponse(askTimerInfoRequest.getID());
            //predo i timerInfo

            ArrayList<TimerInfo> timerInfos = myGame.getRealGame().getTimerInfos();

            timerInfoResponse.setTimerInfoList(timerInfos);
            clientHandler.sendMessage(timerInfoResponse);
        });
    }

    public void handleFlipTimerRequest(FlipTimerRequest flipTimerRequest, ClientHandler clientHandler) throws RemoteException {
        executor.submit(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);

            ArrayList<TimerInfo> timerInfos = myGame.getRealGame().getTimerInfos();
            TimerInfo timer = timerInfos.get(flipTimerRequest.getTimerIndex());

//        timer.setTimerStatus(TimerStatus.STARTED);
            timer.setFlipped(true);

            boolean lastTimer = (flipTimerRequest.getTimerIndex() == myGame.getRealGame().getTimerInfos().size() - 1);

            startTimer(10, myGame.getGameController(), new ArrayList<>(myGame.getPlayerHandlers().values()), lastTimer, flipTimerRequest.getTimerIndex());
        });
    }

    /*
     *
     * UTILS
     * */

    public void startTimer(int seconds, GameController gameController, ArrayList<ClientHandler> clients, boolean last, int index) {
        //mando a tutti la notifica di end_timer\

        TimerInfo timer = new TimerInfo(index,0,true);
        GameMessage gameMessage = new GameMessage("Timer n. "+ index + " started");
        broadCast(clients, gameMessage);

        new Thread(()->{
            LobbyManager game = getLobbyFromHandler(clients.getFirst());
            TimerInfo timerinfo = game.getRealGame().getTimerInfos().stream().filter(t -> t.getIndex() == index).findFirst().orElse(null);
            if(timerinfo == null)
            {
                System.err.println("TimerInfo null");
                return;
            }

            timerinfo.setTimerStatus(TimerStatus.STARTED);

            int secondsIn = 0;

            while (secondsIn < seconds){
                try {
                    Thread.sleep(1000);
                    secondsIn += 1;
                    timerinfo.setValue(secondsIn);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            timerinfo.setTimerStatus(TimerStatus.ENDED);
            if (last){

                if (!(gameController.getGameState() == GameState.BUILDING_END)) {

                    gameController.nextState();
                    PhaseUpdate update = new PhaseUpdate(GameState.BUILDING_END);
                    broadCast(clients, update);
                }

            }




        }).start();




        // Timer scaduto → cambio stato
//        scheduler.schedule(() -> {
//            gameController.nextState();
//            PhaseUpdate update = new PhaseUpdate(GameState.BUILDING_END);
//            broadCast(clients, update);
//
//        }, seconds, TimeUnit.SECONDS);
    }

    private void tryExecutePhaseAfterMessage(LobbyManager game, NetworkMessageType type) {
        CardContext cardContext =game.getGameController().getCurrentCardContext();
        cardContext.decrementExpectedNumberOfNetworkMessages(type);
        int expectedNetworkMessages = cardContext.getExpectedNumberOfNetworkMessagesPerType().get(type);
        if (expectedNetworkMessages == 0) {
            game.getGameController().getCurrentCardContext().executePhase();
        } else if (expectedNetworkMessages == -1) {
            game.getGameController().getCurrentCardContext().incrementExpectedNumberOfNetworkMessages(type);
        }
    }

    public void broadCast(ArrayList<ClientHandler> clients, NetworkMessage message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    private static final Map<GameState, Set<GameAction>> allowedActionsPerState = new EnumMap<>(GameState.class);

    private void initActionsAllowed() {
        allowedActionsPerState.put(GameState.BUILDING_START, EnumSet.of(GameAction.DRAW_TILE, GameAction.PLACE_TILE, GameAction.DISCARD_TILE));
        allowedActionsPerState.put(GameState.BUILDING_TIMER, EnumSet.of(GameAction.DRAW_TILE, GameAction.PLACE_TILE, GameAction.DISCARD_TILE, GameAction.FINISH_BUILDING));
//        allowedActionsPerState.put(GameState.SHIP_CHECK, EnumSet.of(GameAction.FETCH_SHIP));
        // altri stati se necessario
    }

    private boolean isActionAllowed(LobbyManager myGame, GameAction action) {
        GameState currentState = myGame.getGameController().getGameState();
        Set<GameAction> allowedActions = allowedActionsPerState.getOrDefault(currentState, Collections.emptySet());
        return allowedActions.contains(action);
    }

    public String getNicknameFromClientHandler(ClientHandler clientHandler) {
        return clientNicknameMap.get(clientHandler.getClientID());
    }

    private Player getPlayerFromClientHandler(ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }

    private Player getPlayerFromClientHandler(ClientHandler clientHandler, LobbyManager myGame) {
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }

    public void startNewHeartbeat(ClientHandler clientHandler) {
        Heartbeat heartbeat = new Heartbeat(this, clientHandler);
        heartbeats.add(heartbeat);
        heartbeat.start();
    }
}



