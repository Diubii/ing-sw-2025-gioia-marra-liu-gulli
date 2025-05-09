package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
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
import org.polimi.ingsw.galaxytrucker.visitors.Network.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

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

    private AdventureCard currentAdventureCard;

    public ClientModel getMyModel() {
        return myModel;
    }

    public CompletableFuture<NetworkMessage> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<NetworkMessage> completableFuture, int id) {
        this.completableFuture = completableFuture;
        this.pair = new Pair<>(id, completableFuture);
    }


    private Boolean isHost = false;
    private Boolean isSocket = false;

    public ClientController(View view, Boolean flag) {

        this.myModel = new ClientModel();
        this.isSocket = flag;
        this.view = view;

//        clientPhaseController.next();
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
            ((ClientSocket) client).receiveMessage();
        } else {
            try {
                client = new ClientRMI(port, this); // già fa addObserver
            } catch (RemoteException e) {
                throw new IOException(e);
            }
        }
    }

    public void handleServerInfo(SERVER_INFO info) {
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

    /**
     * Handles the user input for a nickname.
     * <p>
     * Validates the nickname format locally before sending a {@link NicknameRequest}
     * to the server.
     *
     * @param nickname the nickname input provided by the user
     */

    private boolean isNicknameLegal(String nickname) {
        //no accetta nickname == null o la string vuota
        if (nickname == null || nickname.trim().isEmpty()) return false;
        //nickname deve  corrispondere a una stringa che contiene solo lettere (a-z o A-Z), numeri (0-9) o il carattere di sottolineatura (_) e che ha almeno un carattere.
        return nickname.matches("^[a-zA-Z0-9_]+$");
    }


    /**
     * Handles the nickname input provided by the user.
     * <p>
     * This method first validates the nickname legally.
     * If the nickname is invalid, an error message is displayed
     * and the user is prompted to enter it again.
     * <p>
     * If the nickname is valid, a {@link NicknameRequest} is sent to the server.
     * The method then waits for a {@link NicknameResponse}.
     * If the server confirms the nickname is valid,
     * it is set as the current nickname and the user proceeds to the next step.
     * Otherwise, an error is shown and the user is prompted to try a different nickname.
     *
     * @param nickname the nickname input provided by the user
     */

    public void handleNicknameInput(String nickname) throws IOException, ExecutionException, InterruptedException {
        if (!isNicknameLegal(nickname)) {
            view.showGenericMessage("Invalid nickname. It must  contain only letters, numbers, or underscores.");
            view.askNickname();
            return;
        }
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

    public void handleCreateChoice(int maxPlayers, boolean isLearningMatch) {
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
                    clientPhaseController.setPhase(PLAYER_PHASE.LOBBY);
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

    public void handleJoinRoomOptionsChoice() {
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
            new Thread(() -> {
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

                view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());

            } else {
                view.showGenericMessage("Failed to join the lobby: " + response.getErrMess());
                view.askRoomCode();
            }
        } catch (Exception e) {
            view.showGenericMessage("Error waiting for join room response: " + e.getStackTrace());
        }

    }


    //Todo: Mattia mettere lista player mandata da server
    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate) {

        synchronized (myModel.getPlayerInfos()) {
            myModel.setPlayerInfos(playerJoinedUpdate.getPlayersJoinedBefore());
        }

        view.showPlayerJoined(playerJoinedUpdate.getPlayerInfo());
        view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());


    }

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {
        phase = phaseUpdate.getState();

        if (phase.equals(GameState.FLIGHT)) myModel.setPlayerState(PlayerState.Playing);
        if (phase.equals(GameState.BUILDING_END)) {
            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.FINISH_BUILDING)) {
                return;
            }
            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING_TIMER) || clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING)) {

                view.forceReset();

                clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);

                try {
                    FinishBuildingRequest finishBuildingRequest = new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile());
                    finishBuildingRequest.name = getNickname();

                    client.sendMessage(finishBuildingRequest);

                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return;
            }


            view.handlePhaseUpdate(phaseUpdate);
            return;

        }


        clientPhaseController.handlePhaseUpdate(phaseUpdate);
        view.handlePhaseUpdate(phaseUpdate);

    }

    public void handleBuildingMenuChoice(String input) {

        switch (input) {
            case "menu", "?", "m" -> {
                view.handleChoiceForPhase(phase);
                break;
            }
            case "a" -> {
                view.askFetchShip();
                break;
            }
            case "b" -> {
                if (myModel.getCardDecks().size() != 1) {

                    try {
                        sendShipUpdate();
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    view.askViewAdventureDecks();
                } else {
                    view.showGenericMessage("You are not allowed to spy on the learningMatch!");
                    view.showBuildingMenu();
                }

                break;
            }
            case "c" -> {

                view.askShowFaceUpTiles();
                view.showBuildingMenu();
                break;
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

                break;

            }
            case "e" -> {
                showTileInHand();
                break;
            }
            case "f" -> {
                view.askRotation();
                break;
            }

            case "g" -> {
                view.askTilePlacement();
                break;
            }
            case "h" -> {
                sendDiscardRequest();
                break;
            }
            case "i" -> {
                view.askPickOrPlaceReservedTile(false);
                break;
            }

            case "j" -> {
                try {
                    clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);

                    FinishBuildingRequest finishBuildingRequest = new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile());
                    finishBuildingRequest.name = getNickname();

                    client.sendMessage(finishBuildingRequest);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                break;
            }

            case "reset" -> {
                break;
            }
            default -> {
                new Thread(() -> {
                    view.showGenericMessage("Invalid option." + input + " Please try again.");
                    view.showBuildingMenu();
                }).start();
                break;
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


    public void handleFetchShip(String targetNickname) {

        boolean exists;
        exists = myModel.hasPlayerWithNickname(targetNickname);
        if (exists) {
            FetchShipRequest request = new FetchShipRequest(targetNickname);

            try {
                client.sendMessage(request);
            } catch (Exception e) {
                view.showGenericMessage("Failed to send fetch ship request: " + e.getMessage());
                return;
            }

        } else {
            view.showGenericMessage("No player with nickname " + targetNickname + " found. Please try again.");
            view.askFetchShip();
        }

    }


    public void handleShipUpdate(ShipUpdate update) {
        String owner = update.getNickName();
        Ship ship = update.getShipView();

        if (owner != null) {

            if (getNickname().equals(owner)) {
                synchronized (myModel.getMyInfo()) {
                    myModel.getMyInfo().setShip(ship);
                }
            } else {
                synchronized (myModel.getPlayerInfos()) {
                    PlayerInfo playerInfo = myModel.getPlayerInfoByNickname(owner);
                    if (playerInfo != null) {
                        playerInfo.setShip(ship);
                    } else {
                        view.showGenericMessage("Player with nickname " + owner + " not found.");
                    }
                }
            }
            if (update.getShouldDisplay()) {
                view.showShip(ship);
                view.handleChoiceForPhase(phase);

            }

        } else {
            view.showGenericMessage("No ship belongs to this player.");
            view.handleChoiceForPhase(phase);
        }

    }

    //case (b)
    public void viewAdventureCardDeck(int DeckID) {
        ArrayList<CardDeck> cardDecks = myModel.getCardDecks();

        CardDeck deck = cardDecks.get(DeckID);
        boolean spyable = deck.isSpyable();
        if (!cardDecks.isEmpty()) {

            if (spyable) {
                int colums = 3;
                CardPrintUtils.printDeck(deck, colums);
                DeckID++;
                view.showGenericMessage("Deck  " + DeckID + " received successfully. ");
            } else {
                view.showGenericMessage("You are not allowed to spy on this deck!");
            }


//
        } else {
            view.showGenericMessage("No card decks found.");
        }
        view.showBuildingMenu();
    }

//case (c)


    public void handleFaceUpTileUpdate(FaceUpTileUpdate update) {

        ArrayList<Tile> faceUpTiles = update.getFaceUpTiles();
        synchronized (myModel.getFaceUpTiles()) {
            myModel.setFaceUpTiles(faceUpTiles);
        }

    }


    //case(d) draw tile
    public void handleDrawFaceDownTile() {
        DrawTileRequest request = new DrawTileRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send draw tile request: " + e.getMessage());
        }
        new Thread(() -> {
            try {
                DrawTileResponse response = (DrawTileResponse) future.get();
                String error = response.getErrorMessage();

                if ("VALID".equals(error)) {
                    view.showTile(response.getTile());
                    ComponentNameVisitor visitor = new ComponentNameVisitor();
                    Component c = response.getTile().getMyComponent();
                    String name = c.accept(visitor);
                    int id = response.getTile().getId();
                    view.showGenericMessage("You drew a " + name + " tile." + "ID: " + id);
                    currentTileInHand = response.getTile();

                } else if ("EMPTY".equals(error)) {
                    view.showGenericMessage("The tile bunch is empty.");

                } else if ("INVALID_STATE".equals(error)) {
                    view.showGenericMessage("You cannot draw a tile right now.");
                } else {
                    view.showGenericMessage("Unexpected response while drawing tile: " + error);
                }
                view.showBuildingMenu();

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    public void startChooseTile() {
        List<Tile> tiles = myModel.getFaceUpTiles();
        if (tiles == null || tiles.isEmpty()) {
            view.showGenericMessage("No face-up tiles available.");
            view.showBuildingMenu();
            return;
        }
        view.showGenericMessage("--Current face-up tiles--");

        view.showFaceUpTiles();
        view.askChooseTile();
    }

    public void handleChooseFaceUpTile(Tile tile) {

        DrawTileRequest request = new DrawTileRequest(tile);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send draw tile request(Face-Up): " + e.getMessage());
        }

        new Thread(() -> {

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
            view.showBuildingMenu();
        }).start();


    }


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

//

    public void resetCurrentPos() {
        currentPosition = null;
    }

    public void setCurrentPos(int x, int y) throws ExecutionException {
        Position pos = new Position(y, x);
        Ship ship = myModel.getMyInfo().getShip();

        if (!Util.inBoundaries(pos.getY(), pos.getX()) || ship.getInvalidPositions().contains(pos)) {
            throw new IllegalArgumentException("Invalid Position" + pos.getY() + pos.getX());
        }

//    if (ship.getInvalidPositions().contains(pos) || ship.getShipBoard()[pos.getY()][pos.getX()].getTile() != null) {
//        throw new IllegalArgumentException("Invalid position: cannot place tile here.");
//    }

        currentPosition = pos;
//    view.showGenericMessage("Tile moved successfully.");
//    view.showBuildingMenu();
    }

    // h
    public void handleTilePlacement(Boolean confirm) throws InvalidTilePosition {
        if (currentPosition == null || currentTileInHand == null) {
            view.showGenericMessage("No tile or position selected.");
            view.showBuildingMenu();
            return;
        }
        myModel.getMyInfo().getShip().putTile(currentTileInHand, currentPosition);   //ship.putTile(tileInHand)
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
                if (response.getMessage().equals("VALID")) {
                    resetCurrentPos();
                    currentTileInHand = null;
                }

            } catch (Exception e) {
                view.showGenericMessage("Error during tile placement: " + e.getMessage());
            } finally {

                view.showBuildingMenu();
            }

        }).start();

    }


    //i
    public void sendDiscardRequest() {
        if (currentTileInHand == null) {
            view.showGenericMessage("No tile in hand to discard.");
            view.showBuildingMenu();

            return;
        }

        DiscardTileRequest request = new DiscardTileRequest(currentTileInHand);

        try {
            client.sendMessage(request);
            currentTileInHand = null;
            currentPosition = null;
            view.showGenericMessage("Tile discarded successfully.");
            view.showBuildingMenu();
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send discard request: " + e.getMessage());
        }
    }

    public void handleTileDiscardUpdate(TileDiscardedUpdate update) {
//        new Thread(() -> {
//            Tile discardedTile = update.getTile();
//            synchronized (myModel.getFaceUpTiles()) {
//                myModel.getFaceUpTiles().add(discardedTile);
//            }
//
//        }).start();
    }

    public void handlePickReservedTile(int slotIndex, boolean isPicking) {

        Tile[] reservedTiles = myModel.getReservedTiles();
        Tile tile = reservedTiles[slotIndex];
        if (isPicking) {
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
        else {
            if (tile == null) {
                myModel.getReservedTiles()[slotIndex] = currentTileInHand;
                currentTileInHand = null;
                view.showGenericMessage("Tile reserved successfully.");
                view.showBuildingMenu();
            } else {
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

    public View getView() {
        return view;
    }


    public void setNickname(String nickname) {

        this.myModel.getMyInfo().setNickName(nickname);
    }

    public String getNickname() {
        return myModel.getMyInfo().getNickName();
    }

    public Tile getCurrentTileInHand() {
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

        myModel.setCardDecks(decksUpdate.getDecks());

    }

    public void handleFlightBoardUpdate(FlightBoardUpdate flightBoardUpdate) {


        myModel.setFlightBoard(flightBoardUpdate.getFlightBoard());

//        if(phase == GameState.FLIGHT){
//            view.showFlightBoard(myModel.getFlightBoard());
//        }

    }


    public void handleAskPositionUpdate(AskPositionUpdate askPositionUpdate) {
        new Thread(() -> {

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
                case "a" -> {
                    view.showShip(myModel.getMyInfo().getShip());
                    view.showcheckShipMenu();
                }
                case "b" -> {
                    if (myModel.getMyInfo().getShip().remainingTiles() > 0) {
                        view.askRemoveTile(myModel.getMyInfo().getShip());
                    } else {
                        System.out.println("OPTION DISABLED< YOU HAVE NO TILE");
                        view.showcheckShipMenu();
                    }

                }
                case "c" -> handleCheckShipRequest();
                case "menu", "m", "?" -> {
                    view.handleChoiceForPhase(phase);

                }


                default -> {
                    view.showGenericMessage("Invalid option. Please try again.");
                    view.showcheckShipMenu();
                }

            }


        }).start();

    }


    public void handleCheckShipRequest() {

        CheckShipStatusRequest checkShipStatusRequest = new CheckShipStatusRequest();

        checkShipStatusRequest.setRemovedTilesId(myModel.getTilesToRemove());

        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, checkShipStatusRequest.getID());

        try {
            client.sendMessage(checkShipStatusRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try {
                CheckShipStatusResponse response = (CheckShipStatusResponse) future.get();
                boolean isValid = response.getIsValid();
                if (isValid) {
                    view.showGenericMessage("Your ship is immaculate!");
                    view.showGenericMessage("Wait to start choosing your crew...");
                    return;

                } else {
                    view.showGenericMessage("Ship need to be re-check");
                    myModel.getMyInfo().setShip(response.getShip());

                }
                view.showcheckShipMenu();

            } catch (ExecutionException | InterruptedException e) {
                view.showGenericMessage("Check Ship err");
                throw new RuntimeException(e);
            }


        }).start();

    }

    public void handleEmbarkCrewMenu(String string) {
        new Thread(() -> {

            switch (string) {
                case "a" -> {
                    view.showShip(myModel.getMyInfo().getShip());
                    view.showembarkCrewMenu();
                    break;
                }

                case "b" -> {
                    try {
                        view.chooseCrew(myModel.getMyInfo().getShip());

                    } catch (ExecutionException | InterruptedException | IOException | InvalidTilePosition |
                             TooManyPlayersException | PlayerAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

                case "menu", "m", "?" -> {
                    view.handleChoiceForPhase(phase);

                }


                default -> {
                    view.showGenericMessage("Invalid option. Please try again.");
                    view.showembarkCrewMenu();
                }

            }

        }).start();
    }


    public void handleEndTurnUpdate(EndTurnUpdate update) {

        view.showGenericMessage("turn ended");

    }

    public void handleAskEndTurnMenuChoice(String input) throws RuntimeException {
        new Thread(() -> {
            if (!myModel.isLeader()) {
                switch (input) {
                    case "RESET" -> {
                        return;


                    }
                    case "a" -> {
                        view.showShip(myModel.getMyInfo().getShip());
                        view.askEndTurnMenuChoice(myModel.isLeader());
                        break;
                    }

                    case "b" -> {
                        view.showFlightBoard(myModel.getFlightBoard(), myModel.getPlayerInfos(), myModel.getMyInfo());
                        view.askEndTurnMenuChoice(myModel.isLeader());
                    }

                    case "c" -> {
                        try {
                            handleEarlyLandingRequest();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }


                    case "menu", "m", "?" -> {
                        view.showEndTurnMenu(myModel.isLeader());
                        view.askEndTurnMenuChoice(myModel.isLeader());

                    }


                    default -> {
                        view.showGenericMessage("Invalid option. Please try again.");
                        view.askEndTurnMenuChoice(myModel.isLeader());
                    }
                }

            } else {
                switch (input) {
                    case "RESET" -> {
                        return;


                    }
                    case "a" -> {
                        view.showShip(myModel.getMyInfo().getShip());
                        view.askEndTurnMenuChoice(myModel.isLeader());
                        break;
                    }


                    case "b" -> {
                        view.showFlightBoard(myModel.getFlightBoard(), myModel.getPlayerInfos(), myModel.getMyInfo());
                        view.askEndTurnMenuChoice(myModel.isLeader());
                    }

                    case "c" -> {
                        try {
                            handleEarlyLandingRequest();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    case "d" -> {
                        view.askDrawCard();

                    }


                    case "menu", "m", "?" -> {
                        view.showEndTurnMenu(myModel.isLeader());
                        view.askEndTurnMenuChoice(myModel.isLeader());

                    }


                    default -> {
                        view.showGenericMessage("Invalid option. Please try again.");
                        view.askEndTurnMenuChoice(myModel.isLeader());
                    }
                }

            }


        }).start();
    }

    private void handleEarlyLandingRequest() throws IOException, ExecutionException, InterruptedException {
        EarlyLandingRequest request = new EarlyLandingRequest();
        client.sendMessage(request);


    }


    public void handleGameMessage(GameMessage gameMessage) {

        view.showGenericMessage(gameMessage.getMessage());
    }

    public void handleMatchInfoUpdate(MatchInfoUpdate matchInfoUpdate) {

        int remainCards = matchInfoUpdate.getRemainingCards();
        String leaderNickname = matchInfoUpdate.getLeaderNickname();


        boolean amLeader = leaderNickname.equals(getNickname());
        myModel.setLeader(amLeader);
        if (amLeader) {

            view.showEndTurnMenu(true);
            view.askEndTurnMenuChoice(true);
        } else {
            view.showGenericMessage("No sei il leader del questo turno. Dovresti aspettare il leader pesca la carta.");
            view.showEndTurnMenu(false);
            view.askEndTurnMenuChoice(false);
        }

        view.showGenericMessage("Il giocatore: " + leaderNickname + " è il leader, rimangono: " + remainCards + "  carte.");
    }

    public void sendDrawAdventureCardRequest() {
        DrawAdventureCardRequest request = new DrawAdventureCardRequest();
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDrawnAdventureCardUpdate(DrawnAdventureCardUpdate drawnAdventureCardUpdate) {
        view.forceReset();
        currentAdventureCard = drawnAdventureCardUpdate.getCard();
        String nameCard = currentAdventureCard.getName();


        view.showCurrentAdventureCard();
    }

    public void handleActivateAdventureCardRequest(ActivateAdventureCardRequest activateAdventureCardRequest) {
        view.askActivateAdventureCard();
    }

    public void sendActivateAdventureCardResponse(boolean confirm) {
        ActivateAdventureCardResponse response = new ActivateAdventureCardResponse(confirm);
        try {
            client.sendMessage(response);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    //planet
    //Select Planet
    public void handleSelectPlanetRequest(SelectPlanetRequest request) {
        ArrayList<Planet> landablePlanet = request.getLandablePlanets();
        view.askSelectPlanetChoice(landablePlanet);


    }
// Dopo aver ricevuto una SelectPlanetRequest, chiedo al giocatore quale pianeta vuole scegliere.
// Quando il giocatore ha fatto la sua scelta, devo inviare sia il pianeta selezionato sia l'indice corrispondente,
// in modo che, quando ricevo un SelectPlanetUpdate, possa notificare a tutti i giocatori
// quale giocatore ha scelto quale pianeta (usando l'indice del pianeta).

    public void sendSelectPlanetResponse(Planet planet, int planetIndex) {
        SelectPlanetResponse response = new SelectPlanetResponse(planet, planetIndex);
        try {
            client.sendMessage(response);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Notifica: il giocatore (nome del giocatore) ha selezionato il pianeta x
    //Poi se selectingPlayerNickname uguale a mio nickname, va a chiedere all'utente come
    // vuole mettere i goods nella sua ship
    public void handleSelectPlanetUpdate(SelectedPlanetUpdate update) {
        String selectingPlayerNickname = update.getSelectingPlayerNickname();
        view.showGenericMessage("Player " + selectingPlayerNickname + " ha selezionato il pianeta " + update.getPlanetIndex());
        if (selectingPlayerNickname.equals(getNickname())) {
            Planet selectedPlanet = update.getSelectedPlanet();
            myModel.setSelectedPlanet(selectedPlanet);
            view.askLoadGoodChoice();
        }
    }

    public void handleLoadGoodChoice(String input) {
        if (input == null) return;

        switch (input.toLowerCase()) {
            case "l" -> view.askSelectGoodToLoad(myModel.getSelectedPlanet(), myModel.getMyInfo().getShip());
            case "d" -> view.askSelectGoodToDiscard(myModel.getSelectedPlanet(), myModel.getMyInfo().getShip());

            case "f" -> {
                view.showGenericMessage(" Caricamento merci completato.");
                try {
                    sendShipForGoodUpdate();
                } catch (Exception e) {
                    view.showGenericMessage("Errore durante l'invio della nave: " + e.getMessage());
                }
            }
            default -> {
                view.showGenericMessage(" Comando non riconosciuto. Usa L, D o F.");
            }
        }
    }


    public void placeMerci(int goodIndex, Good good, Position pos) {
        Ship ship = myModel.getMyInfo().getShip();
        Slot slot = ship.getShipBoard()[pos.getY()][pos.getX()];
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.playerLoadGood(good);
        myModel.getSelectedPlanet().getGoods().remove(goodIndex);
    }


    public void sendShipForGoodUpdate() throws IOException, ExecutionException, InterruptedException {
        ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
        client.sendMessage(update);
    }

    public ArrayList<Good> getDiscardPositionGoods(Position pos) {
        Ship ship = getMyShip();
        Slot slot = getSlot(ship, pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        ArrayList<Good> goodsInHold = hold.getGoods();
        return goodsInHold;
    }

    public void discardGood(int GoodIndex, Position pos) {
        Slot slot = getSlot(getMyShip(), pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.removeGood(hold.getGoods().get(GoodIndex));
    }

    public void handleActivateComponentRequest(ActivateComponentRequest request) {
        ActivatableComponent component = request.getActivatableComponentType();

        //Invoca metodo della view fare scegliere al giocatore i componenti da attivare e le batterie da usare
        try {
            view.chooseComponent(myModel.getMyInfo().getShip(), component);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleActivateComponentResponse(ActivatableComponent component, ArrayList<Position> componentPos, ArrayList<Position> battPos) {
        //Tornare lista di componenti e batterie

        //Inviare la response
        ActivateComponentResponse resp = new ActivateComponentResponse(component, componentPos, battPos);
        try {
            client.sendMessage(resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void hardleDiscardCrewMembersRequest(DiscardCrewMembersRequest request) {
        try {
            view.chooseDiscardCrew(myModel.getMyInfo().getShip(), request.getNumberOfCrewMembersToDiscard());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDiscardCrewMembersResponse(ArrayList<Position> housingPos) {
        DiscardCrewMembersResponse resp = new DiscardCrewMembersResponse(housingPos);
        try {
            client.sendMessage(resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleHeartbeatRequest(HeartbeatRequest heartbeatRequest) throws IOException, ExecutionException, InterruptedException {
        client.sendMessage(new HeartbeatResponse());
    }

    @NeedsToBeCompleted
    public void handlePlayerKickedUpdate(PlayerKickedUpdate playerKickedUpdate) {
        if (playerKickedUpdate.getNickname().equals(this.getNickname())) {
            //TODO: Torna alla scelta dei menù
            //Questo perché magari la connessione del client potrebbe cadere per più di 5 secondi, ma nel caso in cui tornasse su potrebbe ricevere tutti i messaggi arretrati.
            //Quindi dato che il PlayerKickedUpdate viene mandato anche a chi sta per essere kickato, questo è un caso da gestire
        } else {
            view.showGenericMessage("Player " + playerKickedUpdate.getNickname() + " got kicked out of the game!");
        }
    }

    //da chiamare nel visitor
    public void handleTronconiRequest() {
        //Lista di ship
        ArrayList<Ship> Tronconi = new ArrayList<>();
        try {
            view.chooseTroncone(Tronconi);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleTronconiResponse(int choice) {
        //Nuovo NetMessage
    }

    public AdventureCard getCurrentAdventureCard() {
        return currentAdventureCard;
    }

    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate) throws IOException, ExecutionException, InterruptedException {
        client.sendMessage(crewInitUpdate);
    }


    public ArrayList<Position> getOccupiedCargoHolds(Ship ship) {
        ArrayList<Position> cargoHolds = getCargoHolds(ship);
        ArrayList<Position> occupied = new ArrayList<>();

        for (Position pos : cargoHolds) {
            Slot slot = ship.getShipBoard()[pos.getY()][pos.getX()];
            if (slot != null && slot.getTile() != null) {
                Component c = slot.getTile().getMyComponent();
                if (c instanceof GenericCargoHolds hold && !hold.isEmpty()) {
                    occupied.add(pos);
                }
            }
        }
        return occupied;

    }

    public ArrayList<Position> getAvailableCargoHolds(Ship ship, Good good) {
        ArrayList<Position> cargoHolds = getCargoHolds(ship);
        ArrayList<Position> available = new ArrayList<>();

        for (Position pos : cargoHolds) {
            Slot slot = ship.getShipBoard()[pos.getY()][pos.getX()];
            if (slot != null && slot.getTile() != null) {
                Component c = slot.getTile().getMyComponent();
                if (c instanceof GenericCargoHolds hold && !hold.isFull()) {
                    if (good.getColor() == Color.RED) {
                        if (hold.isSpecial()) {
                            available.add(pos);
                        }
                    } else {
                        available.add(pos);

                    }
                }
            }
        }
        return available;
    }


    private ArrayList<Position> getCargoHolds(Ship ship) {
        return ship.getComponentPositionsFromName("GenericCargoHolds");
    }

    public void handlePlayerLostUpdate(PlayerLostUpdate update) {
        boolean isLandingEarly = update.isLandingEarly();


        String nickname = update.getNickname();
        if (nickname.equals(getNickname())) {
            myModel.setPlayerState(PlayerState.Spectating);
        }
        if (isLandingEarly) {
            view.showGenericMessage(" Il giocatore " + nickname + " ha lasciato la partita.");
        } else {
            view.showGenericMessage("il gicatore" + nickname + " è stato rimosso forzatamente dalla partita.");
        }
    }

    public Ship getMyShip() {
        return myModel.getMyInfo().getShip();
    }

    private Slot getSlot(Ship ship, Position pos) {
        Slot slot = ship.getShipBoard()[pos.getY()][pos.getX()];
        return slot;
    }
}
