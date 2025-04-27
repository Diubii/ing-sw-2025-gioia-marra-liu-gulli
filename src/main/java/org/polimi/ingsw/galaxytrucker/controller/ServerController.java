package org.polimi.ingsw.galaxytrucker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
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
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;

import java.io.File;
import java.io.IOException;
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
    private ArrayList<Tile> gameTiles;



    public ServerController(ArrayList<LobbyManager> model) throws IOException {
        this.LobbyManagers = model;
        messageManager = new MessageManager(this);
        initActionsAllowed();
//        model.setRealGame(new Game(4, false));
        generateGameTiles();
    }


    public ArrayList<Tile> getGameTiles(){
        return gameTiles;
    }

    public void generateGameTiles() throws IOException {
        File file = new File("src/main/resources/tiledata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        this.gameTiles = (ArrayList<Tile>) mapper.readValue(file, new TypeReference<List<Tile>>() {});
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
        NicknameResponse nicknameResponse = new NicknameResponse(null, message.getID());


        System.out.println("REQ");
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

    public void handleCreateRoomRequest(CreateRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition {

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
        newGame.getRealGame().initFlightBoard();

        Tile centralTile = null;

        for (Tile tile: gameTiles){
            if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")){
                System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)){
                    centralTile = tile;
                }

            }
        }


        myPlayer.getShip().putTile(centralTile, new Position(2,3));


        synchronized (LobbyManagers) {
            LobbyManagers.add(newGame);

        }
        int index = LobbyManagers.indexOf(newGame);
        synchronized (lobbyInfos) {
            lobbyInfos.add(new LobbyInfo(message.getNickName(), message.getMaxPlayers(), 1, index));

        }

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null,true, message.getID());
        joinRoomResponse.setColor(myColor);
        joinRoomResponse.setMyShip(myPlayer.getShip());


        clientHandler.sendMessage(joinRoomResponse);
        clientHandler.sendMessage(new FlightBoardUpdate(newGame.getRealGame().getFlightBoard()));

    }

    public void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest message, ClientHandler clientHandler) {

        JoinRoomOptionsResponse joinRoomOptionsResponse = new JoinRoomOptionsResponse(null, message.getID());
        synchronized (lobbyInfos) {
            joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos, message.getID());
        }
        clientHandler.sendMessage(joinRoomOptionsResponse);
    }

    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException, IOException, InvalidTilePosition {


        String mess = "";
        LobbyInfo myLobbyInfo;

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null, null, message.getID());
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

                System.out.println("SIZE: " + myGame.getPlayerColors().size());

            if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers() || myGame.getGameController().getGameState() != GameState.LOBBY) {
                System.out.println("2");


                mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY" + message.getRoomId() + "DOESN'T MUNGI YOU");
                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(false);
                joinRoomResponse.setColor(null);
                clientHandler.sendMessage(joinRoomResponse);
                return;

            } else {

                System.out.println("3");

                Player myPlayer = new Player(message.getNickName(), 0, 0, myGame.getRealGame().getIsLearningMatch());

                //trovo la cabina con il colore

                mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());


//                    lobbyInfos.get(0).addConnectedPlayer();


                Color myColor =  myGame.useNextAvailableColor();
                myGame.getPlayerColors().putIfAbsent(message.getNickName(),myColor);

                System.out.println("4");

                //trovo la cabina centrale del colore dell'utente
                Tile centralTile = null;

                for (Tile tile: gameTiles){
                    if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")){
                        System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                        CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                        if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)){
                                centralTile = tile;
                        }

                    }
                }

                System.out.println("5");


                myPlayer.getShip().putTile(centralTile, new Position(2,3));
                myGame.getRealGame().addPlayer(myPlayer);

                myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());

                playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());



//                    ArrayList<Player> players = (ArrayList<Player>) myGame.getRealGame().getPlayers();
                HashMap<String, Color> playerInfo = myGame.getPlayerColors();

                PlayerInfo playerInfo1 = new PlayerInfo();
                playerInfo1.setShip(myPlayer.getShip());
                playerInfo1.setNickName(myPlayer.getNickName());
                playerInfo1.setColor(myColor);

                playerJoinedUpdate = new PlayerJoinedUpdate(playerInfo1);


                synchronized (lobbyInfos) {
                    myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
                }


                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(true);
                joinRoomResponse.setColor(myColor);
                joinRoomResponse.setMyShip(myPlayer.getShip());

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


            clientHandler.sendMessage(new PlayerJoinedUpdate(hostPlayerInfo));
            clientHandler.sendMessage(new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));
            clientHandler.sendMessage(joinRoomResponse);
            System.out.println("ID RESP: " + joinRoomResponse.getID());

            //se tutto è andato bene
            if (result) {

                ArrayList<ClientHandler> original = new ArrayList<>(playerHandlers);
                playerHandlers.remove(clientHandler);

                broadCast(playerHandlers, playerJoinedUpdate);

                //dopo aver mandato la notifica di connessione vedo se ho raggiunto il numero massimo di player per la lobby
                //e starto il gioco automaticamente lato server

                if (myGame.getRealGame().getMaxPlayers() == myGame.getRealGame().getNumPlayers()) {

                    //creo i deck

                    myGame.getRealGame().setFlightDeck();

                    CardDeck flightDeck = myGame.getRealGame().getFlightDeck();

                    ArrayList<CardDeck> decks = myGame.getRealGame().getDecks();

                    DecksUpdate decksUpdate = new DecksUpdate();
                    decksUpdate.setDecks(decks);
                    decksUpdate.setFlightDeck(flightDeck);


                    myGame.getGameController().nextState();
                    broadCast(original, decksUpdate);
                    broadCast(original, new PhaseUpdate(GameState.BUILDING_START));
                    //MANDARE I DECK -> -> ->

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

            drawTileResponse = new DrawTileResponse(null,message.getID());
            drawTileResponse.setErrorMessage("INVALID_STATE");
            clientHandler.sendMessage(drawTileResponse);

            return;
        }

        synchronized (myGame.getTileBunch()) {

            //null significa cge si pesca dal mazzo, invece se è presente un valore valido di Tile si prende da quelle face-up
            if (message.getTile() != null) {

                myTile = myGame.getTileBunch().drawFaceUpTile(message.getTile().getId());
                if (myTile == null) {
                    drawTileResponse = new DrawTileResponse(null,message.getID());
                    drawTileResponse.setErrorMessage("TAKEN");
                } else {
                    drawTileResponse = new DrawTileResponse(myTile,message.getID());
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


    }

    @NeedsToBeCompleted
    //Se player inserisce un Nickname non esiste? cosa ricevo
    public void handleFetchShipRequest(FetchShipRequest message, ClientHandler clientHandler) {

        LobbyManager myGame = getLobbyFromHandler(clientHandler);

        Player targetPlayer = myGame.getRealGame().getPlayer(message.getTargetNickname());
        Ship targetShip;
        ShipUpdate shipViewUpdate;

        synchronized (targetPlayer.getShip()) {
            targetShip = targetPlayer.getShip();
            shipViewUpdate = new ShipUpdate(targetShip, targetPlayer.getNickName());
            shipViewUpdate.setShouldDisplay(true);
        }
        clientHandler.sendMessage(shipViewUpdate);

    }

    public void handleCheckShipStatusRequest(CheckShipStatusRequest message, ClientHandler clientHandler) {

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
        myGame.completePendingResponse(askPositionResponse.getID(), askPositionResponse);
    }

    public void handleFinishBuildingRequest(FinishBuildingRequest finishBuildingRequest, ClientHandler clientHandler) throws ExecutionException, InterruptedException {

        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
        AskPositionUpdate askPositionUpdate;
        ArrayList<Integer> validPos = new ArrayList<>();

        // [1] fisso l'ultima tile di Player

        //devo chiedere in che posizione vuole essere

        if (myGame.getPlayerShipFinishedSize() == 0) {
            myGame.addPlayerShipFinished(nickname);

            //allroa e' il primo e fa partire il timer, e lo aggiungo
            startTimer(20, myGame.getGameController(), new ArrayList<>(myGame.getPlayerHandlers().values()));
        } else {
            //mandare un messaggio "ATTENDENDO ... SCELTA PRECEDENTI"
            myGame.addPlayerShipFinished(nickname);

        }


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

            myGame.addPendingResponse(future, askPositionUpdate.getID());

            clientHandler.sendMessage(askPositionUpdate);

            AskPositionResponse response = (AskPositionResponse) future.get();

            //ho la mia posizione scelta, e lo posiziono

            Color playerColor = myGame.getPlayerColors().get(nickname);
            myGame.getRealGame().getFlightBoard().positionPlayer(playerColor, response.getPosition());

            ArrayList<ClientHandler> playerHandlers = new ArrayList<ClientHandler>(myGame.getPlayerHandlers().values());
            broadCast(playerHandlers, new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));

//            myGame.getRealGame().getFlightBoard()

        }


        synchronized ( myGame.getRealGame().getPlayer(nickname).getShip()) {

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
                        if (slot.getTile().getId() == lastTileId) {
                            myShip.getShipBoard()[slot.getPosition().getY()][slot.getPosition().getX()].getTile().setFixed(true);
                            break;
                        }
                    }

                }

            ArrayList<ClientHandler> playerHandlers = new ArrayList<ClientHandler>(myGame.getPlayerHandlers().values());

            broadCast(playerHandlers, new ShipUpdate(myShip,nickname));
        }

        //controllo se tutti hanno finito
        if (myGame.getPlayerShipFinishedSize() == myGame.getRealGame().getNumPlayers()) {
            //se hanno finito tutti allora si passa alla fase di check_ship
            myGame.getGameController().nextState();
            System.out.println("STATE: " + myGame.getGameController());
            ArrayList<ClientHandler> playerHandlers =  new ArrayList<>(myGame.getPlayerHandlers().values()) ;
            broadCast(playerHandlers, new PhaseUpdate(GameState.SHIP_CHECK));

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

//            myTile.setFixed(true);
            myShip.putTile(myTile, myPos);
            //resetto lastTile
            myShip.setLastTile(myTile);
//            //la ship e' aggiornata
//            myShip.setSynch(true);
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

        ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
        broadCast(playerHandlers, new TileDiscardedUpdate(discardTileRequest.getTile()));

        FaceUpTileUpdate faceUpTileUpdate = new FaceUpTileUpdate();
        faceUpTileUpdate.setFaceUpTiles(myGame.getTileBunch().getFaceUpTiles());
        broadCast(playerHandlers, faceUpTileUpdate);
    }

    @NeedsToBeCompleted
    public void handleViewAdventureDecksRequest(ViewAdventureDecksRequest viewAdventureDecksRequest, ClientHandler clientHandler) {

    }

    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate, ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        Player myPlayer = getPlayerFromClientHandler(clientHandler, myGame);
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

        //dopo aver implementato la lista di quelli che hanno finito e si broadcasta la fase nuova
        new Thread(() -> {
            try {
                myGame.getGameController().startFlight();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleActivateAdventureCardResponse(ActivateAdventureCardResponse activateAdventureCardResponse, ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        myGame.getGameController().notify();
    }

    public void handleActivateDoubleEnginesResponse(ActivateDoubleEnginesResponse activateDoubleEnginesResponse, ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        Player player = getPlayerFromClientHandler(clientHandler);
        Ship ship = player.getShip();

        ArrayList<Position> doubleEnginesPositions = activateDoubleEnginesResponse.getActivatedDoubleEnginesPositions();
        ArrayList<Position> batteriesPositions = activateDoubleEnginesResponse.getBatteriesPositions();

        for(int i=0; i<activateDoubleEnginesResponse.getActivatedDoubleEnginesPositions().size(); i++){
            ship.activateDoubleEngine(doubleEnginesPositions.get(i), batteriesPositions.get(i)); //Usare il bool ritornato? //Assumo che ci siano posizioni duplicate nella lista di quelle delle batterie
        }

        //Mando la shipUpdate
        ShipUpdate shipUpdate = new ShipUpdate(ship, player.getNickName());
        ArrayList<ClientHandler> playerHandlers =  new ArrayList<>(myGame.getPlayerHandlers().values()) ;

        broadCast(playerHandlers, shipUpdate);

        //Sveglio visitOpenSpace
        myGame.completePendingResponse(activateDoubleEnginesResponse.getID(), activateDoubleEnginesResponse);
    }


    /*
    *
    * UTILS
    * */

    public void startTimer(int seconds, GameController gameController, ArrayList<ClientHandler> clients) {
        //mando a tutti la notifica di end_timer\
        gameController.nextState();

        PhaseUpdate timer = new PhaseUpdate(GameState.BUILDING_TIMER);
        broadCast(clients, timer);

        // Timer scaduto → cambio stato
        scheduler.schedule(() -> {
            gameController.nextState();
            PhaseUpdate update = new PhaseUpdate(GameState.BUILDING_END);
            broadCast(clients, update);

        }, seconds, TimeUnit.SECONDS);
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

    private String getNicknameFromClientHandler(ClientHandler clientHandler){
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        return myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
    }

    private String getNicknameFromClientHandler(ClientHandler clientHandler, LobbyManager myGame){
        return myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
    }
    public Void handleShipUpdate(ShipUpdate shipUpdate, ClientHandler clientHandler) {
        if (shipUpdate.getOnlyFix()) {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = myGame.getPlayerHandlers().entrySet().stream().filter(entry -> entry.getValue().equals(clientHandler)).findFirst().get().getKey();
            Player myPlayer = myGame.getRealGame().getPlayer(nickname);
            Ship myShip = myPlayer.getShip();

            synchronized (myShip) {
                List<Slot> Slots = Arrays.stream(shipUpdate.getShipView().getShipBoard())
                        .flatMap(Arrays::stream)
                        .filter(Objects::nonNull)
                        .toList();

                //trovo la tile non fissata

                for (Slot slot: Slots){

                    Tile tempTile = slot.getTile();

                    if (tempTile != null){

                        if (!tempTile.getFixed()){
                            tempTile.setFixed(true);
                            break;
                        }
                    }
                }


            }

            ShipUpdate update = new ShipUpdate(myShip, myPlayer.getNickName());
            ArrayList<ClientHandler> playerHandlers =  new ArrayList<>(myGame.getPlayerHandlers().values()) ;

            broadCast(playerHandlers , shipUpdate);

        }

        return null;
    }


    private Player getPlayerFromClientHandler(ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }

    private Player getPlayerFromClientHandler(ClientHandler clientHandler, LobbyManager myGame){
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }
}



