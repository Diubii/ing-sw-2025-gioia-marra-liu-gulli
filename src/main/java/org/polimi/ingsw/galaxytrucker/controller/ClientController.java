package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.enums.PLAYER_PHASE;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.CardPrintUtils;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Observer {


    private Client client;

    private final ExecutorService taskQueue;
    private View view;
    ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    private final CompletableFuture<Void> nicknameAsked = new CompletableFuture<>();
    private final ExecutorService viewExecutor = Executors.newSingleThreadExecutor();

    private CompletableFuture<NetworkMessage> completableFuture;
    private Pair<Integer, CompletableFuture<NetworkMessage>> pair;

    private final ClientModel myModel;
    private final NetworkMessageVisitorsInterface<Void> messageVisitor = new ClientNetworkMessageVisitor(this);
    private Tile currentTileInHand = null;

    private Position currentPosition;
    private ClientPhaseController clientPhaseController = new ClientPhaseController(this);

    private GameState phase = GameState.LOBBY;

    public ClientModel getMyModel() {
        return myModel;
    }

    public CompletableFuture<NetworkMessage> getCompletableFuture(){
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<NetworkMessage> completableFuture, int id){
        this.completableFuture = completableFuture;
        this.pair = new Pair<>(id, completableFuture);
    }



    private Boolean isHost = false;
    private Boolean isSocket = false;

    public ClientController(View view, Boolean flag) {

        this.myModel = new ClientModel();
        this.isSocket = flag;
        this.view = view;

//        clientPhaseController.nextPhase();
        taskQueue = Executors.newSingleThreadExecutor();
        InputStream in = System.in;

        //per vedere se si chiude per sbaglio System.in nel programma
        System.setIn(new FilterInputStream(in) {
            @Override
            public void close() throws IOException {
                System.out.println("⚠️ QUALCOSA STA CHIUDENDO System.in!");
                super.close();
            }
        });
    }



    @Override
    public void update(NetworkMessage message) throws IOException, ExecutionException, TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, InterruptedException {

        try {
            message.accept(messageVisitor);
        } catch (Exception e) {
            System.err.println("[Error] The message was not processed correctly: " + e.getMessage());
        }
    }


    public void connectToServer(String address, int port) throws IOException {
        if (isSocket) {
            client = new ClientSocket(address, port);
            ((ClientSocket) client).create(address, port);
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket)client).receiveMessage();
        } else {
            try {
                client = new ClientRMI(port, this); // già fa addObserver
            } catch (RemoteException e) {
                throw new IOException(e);
            }
        }
    }

    public void handleServerInfo(SERVER_INFO info)  {
        try {
            connectToServer(info.getAddress(), info.getPort());
        } catch (IOException e) {

                view.showGenericMessage(" Failed to connect to server: " + e.getMessage());

            return;
        }
        new Thread(() -> {
            try {
                view.askNickname();
            } catch (Exception e) {
                System.err.println("Errore nell'invio del nickname: " + e.getMessage());
            }
        }).start();
    }

    public void handleNicknameInput(String nickname){
        NicknameRequest request = new NicknameRequest(nickname);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Error sending nickname request: " + e.getMessage());
            return;
        }

        new Thread(() -> {
            try {
                NicknameResponse response = (NicknameResponse) future.get();
                if ("VALID".equals(response.getResponse())) {
                    setNickname(nickname);
                    view.showGenericMessage("Nickname accepted.");
                    view.askJoinOrCreateRoom();
                } else {
                    view.showGenericMessage("Nickname rejected. Try again.");
                    view.askNickname();
                }
            } catch (Exception e) {
                view.showGenericMessage("Failed to receive nickname response: " + e.getMessage());
            }
        }).start();

    }

    public void handleCreateOrJoinChoice(String choice) throws ExecutionException {
        if ("a".equals(choice)) {
            view.askCreateRoom();
        } else if ("b".equals(choice)) {
            handleJoinRoomOptionsChoice();
        } else {
            view.showGenericMessage("Invalid choice.");
            view.askJoinOrCreateRoom();
        }
    }

    public void handleCreateChoice(int maxPlayers,boolean isLearningMatch) {
        CreateRoomRequest request = new CreateRoomRequest(maxPlayers, isLearningMatch, getNickname());
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        try {
            client.sendMessage(request);
            view.showGenericMessage("Room creation request sent.");
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send create room request: " + e.getMessage());
        }

        new Thread(() -> {
            try {
                JoinRoomResponse response = (JoinRoomResponse) future.get();
                if (response.getOperationSuccess()) {
                    clientPhaseController.setPhase(PLAYER_PHASE.BUILDING);
                    myModel.getMyInfo().setColor(response.getColor());
                    myModel.getMyInfo().setShip(response.getMyShip());
                    myModel.getMyInfo().setNickName(getNickname());

                    view.showGenericMessage("Room created and joined successfully! Waiting for other players...");
                } else {
                    view.showGenericMessage("Room creation failed: " + response.getErrMess());
                    view.askJoinOrCreateRoom();
                }
            } catch (Exception e) {
                view.showGenericMessage("Error while waiting for room creation response: " + e.getMessage());
            }
        }).start();
    }

    public void handleJoinRoomOptionsChoice(){
        JoiniRoomOptionsRequest request = new JoiniRoomOptionsRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send room options request: " + e.getMessage());
            return;
        }

      new Thread(() -> {
            try {
                JoinRoomOptionsResponse response = (JoinRoomOptionsResponse) future.get();
                List<LobbyInfo> lobbies = response.getLobbyInfos();

                if (lobbies.isEmpty()) {
                    view.showGenericMessage("No lobbies found. Try creating a new one.");
                    view.askJoinOrCreateRoom();
                } else {
                    view.showLobbies(lobbies);
                    view.askRoomCode();
                }
            } catch (Exception e) {
                view.showGenericMessage("Error receiving room options: " + e.getMessage());
            }
        }).start();
    }


    public void handleJoinChoice(int roomId) {
        JoinRoomRequest request = new JoinRoomRequest(roomId, getNickname());
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            new Thread(()-> {
                view.showGenericMessage("Failed to send join room request.");
            });
            return;
        }


        try {

            JoinRoomResponse response = (JoinRoomResponse) future.get();
            if (response.getOperationSuccess()) {
                clientPhaseController.setPhase(PLAYER_PHASE.LOBBY);

                view.showGenericMessage("Successfully joined the lobby! Waiting for other players...");

                myModel.getMyInfo().setColor(response.getColor());
                myModel.getMyInfo().setShip(response.getMyShip());
                myModel.getMyInfo().setNickName(getNickname());

            } else {
                view.showGenericMessage("Failed to join the lobby: " + response.getErrMess());
                view.askRoomCode();
            }
        } catch (Exception e) {
            view.showGenericMessage("Error waiting for join room response: " + e.getStackTrace());
        }

    }






    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate){
                PlayerInfo playerInfo = playerJoinedUpdate.getPlayerInfo();

                synchronized (myModel.getPlayerInfos()){
                    myModel.getPlayerInfos().add(playerJoinedUpdate.getPlayerInfo());
                }

//                view.showPlayerJoined(playerInfo);

    }

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate){
        phase = phaseUpdate.getState();


//        if (phaseUpdate.getState().equals(GameState.BUILDING_END)){
//            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING) || clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING_TIMER)){
//                //allora  devo gestirla
//                view.forceReset();
//                clientPhaseController.handlePhaseUpdate(phaseUpdate);
//
//                try {
//                    clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);
//                    client.sendMessage(new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile()));
//
//                } catch (IOException | ExecutionException | InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
////                clientPhaseController.handlePhaseUpdate(phaseUpdate);
//                return;
//            }
//        }

        if (phaseUpdate.getState().equals(GameState.BUILDING_END)){
            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING) || clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING_TIMER)){
                view.forceReset();
                clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);
                try {
                    client.sendMessage(new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile()));

                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return;
            }


            view.handlePhaseUpdate(phaseUpdate);
            return;

        }



//        clientPhaseController.handlePhaseUpdate(phaseUpdate);
        view.handlePhaseUpdate(phaseUpdate);

    }

    public void handleBuildingMenuChoice(String input) {

            switch (input) {
                case "menu", "?", "m" -> view.handleChoiceForPhase(phase);
                case "a" -> view.askFetchShip();
                case "b" -> {
                    if(myModel.getCardDecks().size() != 1) {

                        try {
                            sendShipUpdate();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        view.askViewAdventureDecks();
                    }
                    else{
                        view.showGenericMessage("You are not allowed to spy on the learningMatch!");
                        view.showBuildingMenu();
                    }
                }
                case "c" -> {

                        sendGetFaceUpTilesRequest();
                }
                case "d" -> {
                    if (currentTileInHand != null) {
                    view.showGenericMessage("You already have a tile in hand! Place it or discard it before drawing a new one.");
                    view.showBuildingMenu();

                } else {
                    view.askDrawTile();
                        try {
                            sendShipUpdate();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                }


                }
                case "e" -> showTileInHand();
                case "f" -> view.askRotation();
                case "g" -> {
                    try {
                        view.askPosition();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "h" -> view.askTilePlacement();
                case "i" -> sendDiscardRequest();
                case "j" -> {
                  try {
                      clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);
                    client.sendMessage(new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile()));
                  } catch (IOException | ExecutionException | InterruptedException e) {
                      throw new RuntimeException(e);
                  }
            }
                case "k" -> view.askPickReservedTile(false);

                case "reset" -> {
                  break;
                }
                default -> {
                    new Thread(() -> {
                    view.showGenericMessage("Invalid option." + input+ " Please try again.");
                    view.showBuildingMenu();}).start();
                }
            }

    }


    //case(a)
    public void sendShipUpdate() throws IOException, ExecutionException, InterruptedException {
                    ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
                    currentPosition = null;
                    currentTileInHand = null;
                    update.setOnlyFix(true);
                    client.sendMessage(update);
    }



    public void handleFetchShip(String targetNickname){

        FetchShipRequest request = new FetchShipRequest(targetNickname);

        try {
            client.sendMessage(request);
             } catch (Exception e) {
            view.showGenericMessage("Failed to send fetch ship request: " + e.getMessage());
                return;
        }



    }


    @NeedsToBeCompleted
    // metodo nel view o altri class per stampare ship
    public void handleShipUpdate(ShipUpdate update)  {
        String owner = update.getNickName();
        Ship ship = update.getShipView();
//        view.showGenericMessage("In Update: " + owner);
        if(owner != null ) {
//            view.showGenericMessage("Ship belongs to: " + owner);
            if (getNickname().equals(owner)) {
                synchronized (myModel.getMyInfo()) {
                    myModel.getMyInfo().setShip(ship);  // per avere la versione l'utima del mio ship dal lato server
                }
            }else {
                synchronized (myModel.getPlayerInfos()){
                    myModel.getPlayerInfos().stream()
                            .filter(info -> info.getNickName().equals(owner))
                            .findFirst()
                            .ifPresentOrElse(
                                    info -> info.setShip(ship),
                                    () -> view.showGenericMessage("Player with nickname " + owner + " not found.")
                            );
                }
            }
            if(update.getShouldDisplay()){
                view.showShip(ship);
                view.handleChoiceForPhase(phase);

            }
            else{
                return;
            }

        }
        else{
            view.showGenericMessage("No ship belongs to this player.");
            view.handleChoiceForPhase(phase);
        }

    }

//case (b)
    public void viewAdventureCardDeck(int DeckID){
        ArrayList<CardDeck> cardDecks = myModel.getCardDecks();

        CardDeck deck = cardDecks.get(DeckID);
        boolean spyable = deck.isSpyable();
        if(!cardDecks.isEmpty()){

                if (spyable) {
                    int colums = 4;
                    CardPrintUtils.printDeck(deck, colums);
                    DeckID++;
                    view.showGenericMessage("Deck  " + DeckID + " received successfully. ");
                } else {
                    view.showGenericMessage("You are not allowed to spy on this deck!");
                    view.showBuildingMenu();
                }


//
        }else{
            view.showGenericMessage("No card decks found.");
        }
        view.showBuildingMenu();
    }

//case (c)

    private void sendGetFaceUpTilesRequest() {
    }

    public void getFaceUpTilesRequest() throws IOException, ExecutionException, InterruptedException {

        view.showFaceUpTiles();
        view.showGenericMessage("Fetching face-up tiles...");
        view.showBuildingMenu();

    }
    public void handleFaceUpTileUpdate(FaceUpTileUpdate update, boolean shouldShow){
//        List<Tile> faceUpTiles = update.getFaceUpTiles();
//        synchronized (myModel) {
//            myModel.setFaceUpTiles(faceUpTiles);
//        }
//        if(shouldShow){
//            showFaceUpTiles(faceUpTiles);
//        }
        view.showBuildingMenu();
    }

    private void showFaceUpTiles(List<Tile> tiles) {
        if (tiles == null || tiles.isEmpty()) {
            view.showGenericMessage("No face-up tiles available.");
            view.showBuildingMenu();
        } else {
            view.showFaceUpTiles();
            view.showBuildingMenu();
            return;
        }
    }


    //case(d) draw tile
    public void handleDrawFaceDownTile(){
        DrawTileRequest request = new DrawTileRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send draw tile request: " + e.getMessage());
        }
        new Thread(()->{
            try{
                DrawTileResponse response = (DrawTileResponse) future.get();
                String error = response.getErrorMessage();

                if ("VALID".equals(error)) {
                    view.showTile(response.getTile());
                    ComponentNameVisitor visitor = new ComponentNameVisitor();
                    Component c = response.getTile().getMyComponent();
                    String name = c.accept(visitor);
                    int id = response.getTile().getId();
                    view.showGenericMessage("You drew a " + name + " tile."+ "ID: " + id);
                    currentTileInHand = response.getTile();

                } else if ("EMPTY".equals(error)) {
                    view.showGenericMessage("The tile bunch is empty.");

                } else if("INVALID_STATE".equals(error)){
                    view.showGenericMessage("You cannot draw a tile right now.");
                }
                    else {
                    view.showGenericMessage("Unexpected response while drawing tile: " + error);
                }
                    view.showBuildingMenu();

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    public void startChooseTile(){
        List<Tile> tiles = myModel.getFaceUpTiles();
        if(tiles == null || tiles.isEmpty()){
            view.showGenericMessage("No face-up tiles available.");
            return;
        }
        view.showGenericMessage("--Current face-up tiles--");
        view.showFaceUpTiles();

        view.askChooseTile();
    }

    public void handleChooseFaceUpTile(Tile tile) {

                DrawTileRequest request = new DrawTileRequest(tile);
                CompletableFuture<NetworkMessage>future = new CompletableFuture<>();
                setCompletableFuture(future,request.getID());
                try{
                    client.sendMessage(request);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    view.showGenericMessage("Failed to send draw tile request(Face-Up): " + e.getMessage());
                }

                new Thread(()-> {

                    try {
                        DrawTileResponse response = (DrawTileResponse) future.get();
                        String error = response.getErrorMessage();

                        switch (error) {
                            case "VALID":
                                Tile drawnTile = response.getTile();
                                if (drawnTile != null) {
                                    view.showTile(drawnTile);
                                    currentTileInHand = drawnTile;
                                }
                                break;
                            case "TAKEN":
                                view.showGenericMessage("The tile is chosen by another player.");
                                break;
                            case "INVALID_STATE":
                                view.showGenericMessage("You cannot draw a tile right now.");
                                break;
                            default:
                                view.showGenericMessage("Unexpected response while drawing tile(Face-Up): " + error);
                                break;
                        }
                    } catch (Exception e) {
                        view.showGenericMessage("An error occurred while processing the response: " + e.getMessage());

                    }

                }).start();

    }


@NeedsToBeCompleted
//cambiare metodi di stampa
    public void showTileInHand() {

        view.showTile(currentTileInHand);
        view.showGenericMessage("Tile in hand successfully.");
        view.showBuildingMenu();
    }

    // f
    public void rotateCurrentTile(int rotation) {
        currentTileInHand.rotate(rotation);
        view.showTile(currentTileInHand);
        view.showGenericMessage("Tile rotated successfully.");
        view.showBuildingMenu();

    }

// g
public void moveCurrentTile(int x, int y) throws ExecutionException {
    Position pos = new Position(x, y);
    Ship ship = myModel.getMyInfo().getShip();

    if (pos.getX() < 0 || pos.getY() < 0 ||
            pos.getX() >= ship.getShipBoard()[0].length ||
            pos.getY() >= ship.getShipBoard().length) {
        throw new IllegalArgumentException("Position out of bounds.");
    }

    if (ship.getInvalidPositions().contains(pos) || ship.getShipBoard()[pos.getY()][pos.getX()].getTile() != null) {
        throw new IllegalArgumentException("Invalid position: cannot place tile here.");
    }

    currentPosition = pos;
    view.showGenericMessage("Tile moved successfully.");
    view.showBuildingMenu();
}

    // h
    public void handleTilePlacement(Boolean confirm) throws InvalidTilePosition {
        if (currentPosition == null || currentTileInHand == null) {
            view.showGenericMessage("No tile or position selected.");
            view.showBuildingMenu();
            return;
        }
        myModel.getMyInfo().getShip().putTile(currentTileInHand,currentPosition);   //ship.putTile(tileInHand)
        myModel.getMyInfo().getShip().setLastTile(currentTileInHand);               //ship.setLastTile

        PlaceTileRequest request = new PlaceTileRequest(currentTileInHand, currentPosition);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send tile placement: " + e.getMessage());
            view.showBuildingMenu();
            return;
        }


        new Thread(() -> {
            try {
                PlaceTileResponse response = (PlaceTileResponse) future.get();
                view.showGenericMessage(response.getMessage());

            } catch (Exception e) {
                view.showGenericMessage("Error during tile placement: " + e.getMessage());
            }finally {
                view.showBuildingMenu();
            }

        }).start();

    }


//i
    public void sendDiscardRequest() {
        if (currentTileInHand == null) {
            view.showGenericMessage("No tile in hand to discard.");
            return;
        }

        DiscardTileRequest request = new DiscardTileRequest(currentTileInHand);

        try {
            client.sendMessage(request);
            currentTileInHand = null;
            view.showGenericMessage("Tile discarded successfully.");
            view.showBuildingMenu();
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send discard request: " + e.getMessage());
        }
    }

    public void handleTileDiscardUpdate(TileDiscardedUpdate update){
        new Thread(() -> {
            Tile discardedTile = update.getTile();
            synchronized (myModel) {
                myModel.getFaceUpTiles().add(discardedTile);
            }
            view.showGenericMessage("A tile has been discarded and added back to face-up tiles.");
            view.showBuildingMenu();
        }).start();
    }
    public void handlePickReservedTile(int slotIndex, boolean isPicking) {

        Tile[] reservedTiles = myModel.getReservedTiles();
        Tile tile = reservedTiles[slotIndex];
        if(isPicking) {
            if (tile == null) {
                view.showGenericMessage("No reserved tile at slot " + slotIndex + ".");
                view.showBuildingMenu();


            } else {
                currentTileInHand = tile;
                myModel.getReservedTiles()[slotIndex] = null;
                view.showGenericMessage("Tile picked successfully.");
                view.showBuildingMenu();
            }
        }
        //placing
        else{
            if (tile == null) {
                myModel.getReservedTiles()[slotIndex] = currentTileInHand;
                currentTileInHand = null;
                view.showGenericMessage("Tile reserved successfully.");
                view.showBuildingMenu();
            }
            else{
                view.showGenericMessage("A tile is already reserved at slot " + slotIndex + ".");
                view.showBuildingMenu();
            }
        }


    }

    //update per generic message
    public void completeFuture(NetworkMessage message) {
        int responseId = message.getID();
        if (completableFuture == null) {
            view.showGenericMessage("⚠️ CompletableFuture is null when receiving response with ID: " + responseId);
            return;
        }

        if (!pair.getKey().equals(responseId)) {
            view.showGenericMessage("⚠️ ID mismatch! Expected: " + pair.getKey() + ", but got: " + responseId);
            return;
        }
        if (completableFuture != null && pair.getKey().equals(message.getID())) {
            completableFuture.complete(message);
            completableFuture = null;
        } else {
            System.err.println("⚠️ Cannot complete future: ID mismatch or future was null");

        }
    }

    public Tile[] getReservedTiles() {
        return myModel.getReservedTiles();
    }
    @Override
    public void update(String message) {

        System.out.println("[+]" + message);
    }

    public Client getClient() {
        return client;
    }

    public void setView(View v) {
        this.view = v;
    }

    public View getView(){
        return view;
    }


    public void setNickname(String nickname) {

        this.myModel.getMyInfo().setNickName(nickname);
    }

    public String getNickname() {
        return myModel.getMyInfo().getNickName();
    }
    public Tile getCurrentTileInHand(){
        return this.currentTileInHand;
    }
    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentTileInHand(Tile currentTileInHand) {
        this.currentTileInHand = currentTileInHand;
    }

    public boolean hasTileInHand() {
        return this.getCurrentTileInHand() != null;
    }

    public void handleDecksUpdate(DecksUpdate decksUpdate) {

        synchronized (myModel.getCardDecks()){
            myModel.setCardDecks(decksUpdate.getDecks());
        }
    }

    public void handleFlightBoardUpdate(FlightBoardUpdate flightBoardUpdate) {

        if (myModel.getFlightBoard() != null) {
            synchronized (myModel.getFlightBoard()){
                myModel.setFlightBoard(flightBoardUpdate.getFlightBoard());
            }
        } else {
            myModel.setFlightBoard(flightBoardUpdate.getFlightBoard());
        }

    }

    public void handleAskPositionUpdate(AskPositionUpdate askPositionUpdate) {
        new Thread(()->{

            try {
                view.askFlightBoardPosition(askPositionUpdate.getValidPositions(), askPositionUpdate.getID());
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }


    public boolean getIsSocket() {
        return isSocket;
    }



    public void handleCheckShipChoice(String input) {
        new Thread(() -> {

            switch (input) {
                case "a" -> view.askFetchShip();
//            case "b" -> viewAdventureDecks();
//            case "c" -> client.sendMessage(new ShowFaceUpTilesRequest());

                default -> {
                    new Thread(() -> {
                        view.showGenericMessage("Invalid option. Please try again.");
                        view.showcheckShipMenu();}).start();
                }
            }



        }).start();

    }


}
