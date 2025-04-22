package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameAction;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
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

import java.util.*;
import java.util.concurrent.*;

public class ServerController {

    final ArrayList<LobbyManager> LobbyManagers;
    private final MessageManager messageManager;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> usedNicknames = new ArrayList<>();
    private final ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Object positionLock = new Object();

    public ServerController(ArrayList<LobbyManager> model) {
        this.LobbyManagers = model;
        messageManager = new MessageManager(this);
        initActionsAllowed();
//        model.setRealGame(new Game(4, false));
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

    public ArrayList<String> getUsedNicknames() {
        synchronized (usedNicknames) {
            return usedNicknames;
        }
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
    public LobbyManager getLobbyFromHandler(ClientHandler clientHandler) {

        LobbyManager lobbyManager;
        synchronized (LobbyManagers) {
            lobbyManager = LobbyManagers.stream().filter(gameModel ->
                    gameModel.getPlayerHandlers().containsValue(clientHandler)).findFirst().orElse(null);
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
    * HANDLERS
    *
    * */

    public void handleNicknameRequest(NicknameRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        Boolean result = false;
        boolean flag = false;

        //get nickname & check
        String tempNick = message.getNickname();
        NicknameResponse nicknameResponse = new NicknameResponse(null, message.getId());


        synchronized (usedNicknames) {

            if (!usedNicknames.contains(tempNick)) {
                usedNicknames.add(tempNick);
                nicknameResponse.setResponse("VALID");
            } else {
                System.out.println("[+] NOT ADDED " + message.getNickname());
                nicknameResponse.setResponse("INVALID");
            }

        }

        clientHandler.sendMessage(nicknameResponse);
        System.out.println("SENDING RESPONSE\n");

    }

    public void handleCreateRoomRequest(CreateRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {

        //get nickname & check
        String tempNick = message.getNickName();

        LobbyManager newGame = new LobbyManager();
        Player myPlayer = new Player(message.getNickName(), 0, 0, message.getIsLearningMatch());
        Color myColor = newGame.useNextAvailableColor();


        newGame.getPlayerColors().putIfAbsent(message.getNickName(),myColor );
        newGame.getRealGame().setLearningMatch(message.getIsLearningMatch());
        newGame.getRealGame().setnMaxPlayer(message.getMaxPlayers());
        newGame.getRealGame().addPlayer(myPlayer);
        newGame.addPlayerHandler(clientHandler, myPlayer.getNickName());


        synchronized (LobbyManagers) {
            LobbyManagers.add(newGame);

        }
        int index = LobbyManagers.indexOf(newGame);
        synchronized (lobbyInfos) {
            lobbyInfos.add(new LobbyInfo(message.getNickName(), message.getMaxPlayers(), 1, index));

        }

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null,true, message.getId());
        joinRoomResponse.setColor(myColor);

        clientHandler.sendMessage(joinRoomResponse);

    }

    public void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest message, ClientHandler clientHandler) {

        JoinRoomOptionsResponse joinRoomOptionsResponse = new JoinRoomOptionsResponse(null, message.getId());
        synchronized (lobbyInfos) {
            joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos, message.getId());
        }
        clientHandler.sendMessage(joinRoomOptionsResponse);
    }

    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {


        String mess = "";
        LobbyInfo myLobbyInfo;

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null, null, message.getId());
        ArrayList<ClientHandler> playerHandlers;
        boolean result = false;
        PlayerJoinedUpdate playerJoinedUpdate = null;


        LobbyManager myGame = LobbyManagers.get(message.getRoomId());


        if (myGame == null) {
            mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NUMBER " + message.getRoomId() + "NOT EXISTS");
            joinRoomResponse.setErrMess(mess);
            joinRoomResponse.setOperationSuccess(false);
            joinRoomResponse.setColor(null);
            clientHandler.sendMessage(joinRoomResponse);
            return;
        }

        synchronized (myGame) {
            System.out.println("SI");

            if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers() || myGame.getGameController().getGameState() != GameState.LOBBY) {
                mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY" + message.getRoomId() + "DOESN'T MUNGI YOU");
                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(false);
                joinRoomResponse.setColor(null);
                clientHandler.sendMessage(joinRoomResponse);
                return;

            } else {


                Player myPlayer = new Player(message.getNickName(), 0, 0, myGame.getRealGame().getIsLearningMatch());
                mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());


//                    lobbyInfos.get(0).addConnectedPlayer();


                Color myColor =  myGame.useNextAvailableColor();
                myGame.getPlayerColors().putIfAbsent(message.getNickName(),myColor);


                myGame.getRealGame().addPlayer(myPlayer);

                myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());
                playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());


//                    ArrayList<Player> players = (ArrayList<Player>) myGame.getRealGame().getPlayers();
                HashMap<String, Color> playerInfo = myGame.getPlayerColors();
                playerJoinedUpdate = new PlayerJoinedUpdate(playerInfo);


                synchronized (lobbyInfos) {
                    myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
                }


                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(true);
                joinRoomResponse.setColor(myColor);

                if (myLobbyInfo != null) {

                    myLobbyInfo.addConnectedPlayer();
                    result = true;
                } else {

                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NOT FOUND :) " + message.getRoomId());
                    joinRoomResponse.setOperationSuccess(false);
                    joinRoomResponse.setColor(null);

                }
            }

            //fine synchronized e

            clientHandler.sendMessage(joinRoomResponse);
            System.out.println("ID RESP: " + joinRoomResponse.getId());

            //se tutto è andato bene
            if (result) {
                broadCast(playerHandlers, playerJoinedUpdate);

                //dopo aver mandato la notifica di connessione vedo se ho raggiunto il numero massimo di player per la lobby
                //e starto il gioco automaticamente lato server

                if (myGame.getRealGame().getMaxPlayers() == myGame.getRealGame().getNumPlayers()) {
                    myGame.getGameController().nextState();
                    System.out.println("PLAYER: HANDL : " + playerHandlers.size());
                    broadCast(playerHandlers, new PhaseUpdate(myGame.getGameController().getGameState()));

                    //dopo aver notificato tutti starto il gioco

                }
            }

        }

    }

    public void handleDrawTileRequest(DrawTileRequest message, ClientHandler clientHandler) {
        //il client mi chiede una Tile, e devo restituirla
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        Tile myTile = null;
        DrawTileResponse drawTileResponse;
        Boolean flag = false;


        if (!isActionAllowed(myGame, GameAction.DRAW_TILE)) {

            drawTileResponse = new DrawTileResponse(null);
            drawTileResponse.setErrorMessage("INVALID_STATE");
            clientHandler.sendMessage(drawTileResponse);

            return;
        }

        synchronized (myGame.getTileBunch()) {

            //null significa cge si pesca dal mazzo, invece se è presente un valore valido di Tile si prende da quelle face-up
            if (message.getTile() != null) {

                myTile = myGame.getTileBunch().drawFaceUpTile(message.getTile().getId());
                if (myTile == null) {
                    drawTileResponse = new DrawTileResponse(null);
                    drawTileResponse.setErrorMessage("TAKEN");
                } else {
                    drawTileResponse = new DrawTileResponse(myTile);
                    drawTileResponse.setErrorMessage("VALID");
                }

            } else {

                myTile = myGame.getTileBunch().drawTile();
                if (myTile == null) {
                    drawTileResponse = new DrawTileResponse(null);
                    drawTileResponse.setErrorMessage("EMPTY");
                } else {
                    drawTileResponse = new DrawTileResponse(myTile);
                    drawTileResponse.setErrorMessage("VALID");
                }

            }

        }
        clientHandler.sendMessage(drawTileResponse);


    }

    public void handleFetchShipRequest(FetchShipRequest message, ClientHandler clientHandler) {

        LobbyManager myGame = getLobbyFromHandler(clientHandler);

        Player targetPlayer = myGame.getRealGame().getPlayer(message.getTargetNickname());
        Ship targetShip;
        ShipUpdate shipViewUpdate;

        synchronized (targetPlayer.getShip()) {
            targetShip = targetPlayer.getShip();
            shipViewUpdate = new ShipUpdate(targetShip, targetPlayer.getNickName());
        }
        clientHandler.sendMessage(shipViewUpdate);

    }

    public void handleCheckShipStatusRequest(CheckShipStatusRequest message, ClientHandler clientHandler) {

        //devo controllare se la nave è corretta

        //prima di tutto la salvo come nuova nave

        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
        Player player = myGame.getRealGame().getPlayer(nickname);
        Ship ship = player.getShip();

        //trovo tutte le Tiles da rimuovere
        List<Slot> Slots = Arrays.stream(ship.getShipBoard())
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .toList();

        for (Slot slot : Slots) {
            if (message.getRemovedTilesId().contains(slot.getTile().getId())) {
                ship.removeTile(slot.getTile(), slot.getPosition(), true);
            }
        }

        Boolean result = ship.checkShip();
        CheckShipStatusResponse response = new CheckShipStatusResponse(ship, result);
        clientHandler.sendMessage(response);



        if (result) {
            myGame.getGameController().addCompletedShip();
            if (myGame.getRealGame().getNumPlayers() == myGame.getGameController().getnCompletedShips()){
                myGame.getGameController().nextState();
            }
        }


    }

    public void handleAskPositionResponse(AskPositionResponse askPositionResponse, ClientHandler clientHandler) {

        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        myGame.completePendingResponse(askPositionResponse.getId(), askPositionResponse);
    }

    public void handleFinishBuildingRequest(FinishBuildingRequest finishBuildingRequest, ClientHandler clientHandler) throws ExecutionException, InterruptedException {

        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
        AskPositionUpdate askPositionUpdate;
        ArrayList<Integer> validPos = new ArrayList<>();

        // [1] fisso l'ultima tile di Player

        //devo chiedere in che posizione vuole essere

        synchronized (positionLock) {
            int maxPos = myGame.getRealGame().getNumPlayers();
            int minPos = 1;
            ArrayList<Integer> takenPos = myGame.getRealGame().getFlightBoard().getOccupiedPositions();

            for (int i = 1; i <= maxPos; i++) {

                if (!takenPos.contains(i)) {
                    validPos.add(i);
                }
            }

            askPositionUpdate = new AskPositionUpdate(validPos);
            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();

            //aggiungo alle pending responses

            myGame.addPendingResponse(future, askPositionUpdate.getId());

            clientHandler.sendMessage(askPositionUpdate);

            AskPositionResponse response = (AskPositionResponse) future.get();

            //ho la mia posizione scelta, e lo posiziono

            Color playerColor = myGame.getPlayerColors().get(nickname);
            myGame.getRealGame().getFlightBoard().positionPlayer(playerColor, response.getPosition());
//            myGame.getRealGame().getFlightBoard()

        }








        synchronized ( myGame.getRealGame().getPlayer(nickname).getShip()) {

            Ship myShip = myGame.getRealGame().getPlayer(nickname).getShip();

            if (myGame.getPlayerShipFinishedSize() == 0) {
                //allroa e' il primo e fa partire il timer, e lo aggiungo
                myGame.addPlayerShipFinished(nickname);
                startTimer(60, myGame.getGameController(), (ArrayList<ClientHandler>) myGame.getPlayerHandlers().values());
            }

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
                        if (slot.getTile().getId() == lastTileId) {
                            myShip.getShipBoard()[slot.getPosition().getY()][slot.getPosition().getX()].getTile().setFixed(true);
                            break;
                        }
                    }

                }

                clientHandler.sendMessage(new FetchShipResponse(nickname,myShip));
        }

        //controllo se tutti hanno finito
        if (myGame.getPlayerShipFinishedSize() == myGame.getRealGame().getNumPlayers()) {
            //se hanno finito tutti allora si passa alla fase di check_ship
            myGame.getGameController().nextState();
            ArrayList<ClientHandler> playerHandlers = (ArrayList<ClientHandler>) myGame.getPlayerHandlers().values();
            broadCast(playerHandlers, new PhaseUpdate(myGame.getGameController().getGameState()));

        }


    }

    public void handlePlaceTileRequest(PlaceTileRequest placeTileRequest, ClientHandler clientHandler) throws InvalidTilePosition {

        PlaceTileResponse placeTileResponse = new PlaceTileResponse(null);
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
        Player myPlayer = myGame.getRealGame().getPlayer(nickname);
        Ship myShip = myPlayer.getShip();


        //controllo se posso eseguirla

        if (!isActionAllowed(myGame, GameAction.PLACE_TILE)){
            placeTileResponse.setMessage("INVALID_STATE");
            clientHandler.sendMessage(placeTileResponse);
            return;
        }

        //dopo che ho tutto devo semplicemente inserire la Tile

        synchronized (myShip){

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
                if (tempTile.getId() == myTile.getId()) {

                    //esiste gia la tile
                    placeTileResponse.setMessage("INVALID_POS");
                    clientHandler.sendMessage(placeTileResponse);
                    return;
                }
            }

            //non esiste, allora la inserisco

            myTile.setFixed(true);
            myShip.putTile(myTile, myPos);
            //resetto lastTile
            myShip.setLastTile(null);
            //la ship e' aggiornata
            myShip.setSynch(true);
            //setto il messaggio
            placeTileResponse.setMessage("SUCCESS");

            //da capire se ha senso creare una PlaceTileResponse
//            clientHandler.sendMessage(placeTileResponse);



            //broadCasto la nuova nave a tutti
            ShipUpdate shipUpdate = new ShipUpdate(myShip, myPlayer.getNickName());
            broadCast((ArrayList<ClientHandler>) myGame.getPlayerHandlers().values(), shipUpdate);

        }




        }


    }

    @NeedsToBeCompleted
    public void handleDiscardTileRequest(DiscardTileRequest discardTileRequest, ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        myGame.getTileBunch().returnTile(discardTileRequest.getTile());
        broadCast((ArrayList<ClientHandler>) myGame.getPlayerHandlers().values(), new TileDiscardedUpdate(discardTileRequest.getTile()));
    }

    @NeedsToBeCompleted
    public void handleViewAdventureDecksRequest(ViewAdventureDecksRequest viewAdventureDecksRequest, ClientHandler clientHandler) {

    }

    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate, ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
        Player myPlayer = myGame.getRealGame().getPlayer(nickname);
        Ship myShip = myPlayer.getShip();

        ArrayList<Position> positions = (ArrayList<Position>) crewInitUpdate.getCrewPos().stream().map(Pair::getKey).toList();

        List<Slot> Slots = Arrays.stream(myShip.getShipBoard())
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .toList();

        for (Slot s: Slots){

            Tile tempTile = s.getTile();
            Position tempPos = s.getPosition();

            if (positions.contains(tempPos)) {

                AlienColor color = crewInitUpdate.getCrewPos().stream().filter(pair -> pair.getKey().equals(tempPos)).map(Pair::getValue).findFirst().get();

                if (color.equals(AlienColor.PURPLE)){
                    ModularHousingUnit purpleHousing = (ModularHousingUnit) tempTile.getMyComponent();
                    purpleHousing.addPurpleAlien();
                }


                if (color.equals(AlienColor.BROWN)){
                    ModularHousingUnit brownHousing = (ModularHousingUnit) tempTile.getMyComponent();
                    brownHousing.addBrownAlien();
                }  else {
                    ModularHousingUnit humanHousing = (ModularHousingUnit) tempTile.getMyComponent();
                    humanHousing.addHumanCrew();
                }

            }
        }





    }
    
    /*
    *
    * UTILS
    * */

    public void startTimer(int seconds, GameController gameController, ArrayList<ClientHandler> clients) {
        //mando a tutti la notifica di end_timer\
        EndTimerUpdate endTimerUpdate = new EndTimerUpdate(seconds);
        broadCast(clients, endTimerUpdate);

        // Timer scaduto → cambio stato
        scheduler.schedule(gameController::nextState, seconds, TimeUnit.SECONDS);
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



}



