package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.PlayerScore;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Smugglers;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
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
import org.polimi.ingsw.galaxytrucker.view.Tui.MenuManager;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.CardPrintUtils;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.Network.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
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

    public GameState getPhase() {
        return phase;
    }

    public void setPhase(GameState phase) {
        this.phase = phase;
    }

    private GameState phase = GameState.LOBBY;
    private boolean isPlaced = false;

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
            //TODO: gestire crash server
            System.err.println("[Error] The message was not processed correctly: " + e.getMessage());
            //e.printStackTrace();
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
                    MenuManager.learningMatch = isLearningMatch;


                    myModel.getPlayerInfos().add(myModel.getMyInfo());
                    view.showPlayersLobby(myModel.getMyInfo(),myModel.getPlayerInfos());
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
                    view.showLobbies(lobbies);
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
                myModel.setLearningMatch(response.getIsLearningMatch());
                MenuManager.learningMatch = response.getIsLearningMatch();


                view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());

            } else {
                view.showGenericMessage("Failed to join the lobby: " + response.getErrMess());
                view.askRoomCode();
            }
        } catch (Exception e) {
            view.showGenericMessage("Error while waiting for join room response: " + e.getStackTrace());
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
               view.toShowCurrentMenu();
               view.handleChoiceForPhase(phase);
                break;
            }
            case "a" -> {
                view.askFetchShip();
                break;
            }
            case "b" -> {
                if (!myModel.isLearningMatch()) {

                    if(isPlaced || currentTileInHand == null) {
                        try {
                            view.askViewAdventureDecks();
                            sendShipUpdate();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        view.showGenericMessage("Hai un tile  in mano, per favore posizionala prima.");
                        view.showBuildingMenu();
                    }

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
                    isPlaced = false;
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

            case "k" -> {
                //devo far vedere i risultati dei TimerInfo e far appararire un menu
//                if (!myModel.isLearningMatch())
//                {
                new Thread(() -> {
                    view.showTimerInfos();

                }).start();
//                }

                    break;
            }
            case "reset" -> {
                break;
            }
            default -> {
                new Thread(() -> {
                    view.showGenericMessage("Invalid option \"" + input + "\". Please try again.");
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
            if(myModel.getMyInfo().getNickName().equals(targetNickname)){
                view.showShip(myModel.getMyInfo().getShip(),myModel.getMyInfo().getNickName());
                view.handleChoiceForPhase(phase);
                return;
            }
            else {
                Ship targetShip = myModel.getPlayerInfoByNickname(targetNickname).getShip();
                view.showShip(targetShip,targetNickname);
                view.handleChoiceForPhase(phase);
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
                view.showShip(ship,owner);
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

    public ArrayList<TimerInfo> getSynchTimerInfos(){

        AskTimerInfoRequest askTimerInfoRequest = new AskTimerInfoRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, askTimerInfoRequest.getID());
        final ArrayList<TimerInfo> timerInfos = new ArrayList<>();

        try {
            client.sendMessage(askTimerInfoRequest);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send request: " + e.getMessage() + e.getStackTrace());
        }
            try {
                TimerInfoResponse timerInfoResponse = (TimerInfoResponse) future.get();
                timerInfos.addAll(timerInfoResponse.getTimerInfoList());

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }



        return timerInfos;


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
        Position pos = new Position(x, y);
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
                    isPlaced = true;
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




    @Override
    public void update(String message) {

        System.out.println("[+]" + message);
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
                    view.showShip(myModel.getMyInfo().getShip(),myModel.getMyInfo().getNickName());
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
                    view.showShip(myModel.getMyInfo().getShip(),myModel.getMyInfo().getNickName());
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
        if(update.isEndGame()){
            view.showGenericMessage("Game ended");
            return;
        }
        view.toShowCurrentMenu();
        view.showFlightMenu();
    }

    public void handleFlightMenuChoice(String input) throws RuntimeException {
        new Thread(() -> {
                switch (input) {
                    case "RESET" -> {
                        return;

                    }
                    case "a" -> {
                        view.askFetchShip();

                        break;
                    }

                    case "b" -> {
                        view.showFlightBoard(myModel.getFlightBoard(), myModel.getPlayerInfos(), myModel.getMyInfo());
                        view.handleChoiceForPhase(phase);
                        break;
                    }

                    case "c" ->
                            handleEarlyLandingRequest();

                    case "d" ->
                            handleReadyTurnRequest();

                    case "menu", "m", "?" -> {
                        view.handleChoiceForPhase(phase);
                    }
                    default -> {
                        view.showGenericMessage("Invalid option. Please try again.");
                        view.handleChoiceForPhase(phase);
                    }
                }

        }).start();
    }

    public void handleEarlyLandingRequest() {
        EarlyLandingRequest request = new EarlyLandingRequest();
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        view.showGenericMessage("Hai scelto l’atterraggio anticipato, ora guarda gli altri giocatori.");

    }
    public void handleReadyTurnRequest()  {
        ReadyTurnRequest request = new ReadyTurnRequest();
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        view.showGenericMessage(" Devi aspettare che gli altri giocatori siano pronti.");

    }



    public void handleGameMessage(GameMessage gameMessage) {

        view.showGenericMessage(gameMessage.getMessage());
    }

    public void handleMatchInfoUpdate(MatchInfoUpdate matchInfoUpdate) {

        int remainCards = matchInfoUpdate.getRemainingCards();
        String leaderNickname = matchInfoUpdate.getLeaderNickname();


        boolean amLeader = leaderNickname.equals(getNickname());
        myModel.setLeader(amLeader);
        view.showGenericMessage("Il giocatore: " + leaderNickname + " è il leader, rimangono: " + remainCards + "  carte.");
        if (amLeader) {
            view.askDrawCard();
        } else {
            view.showGenericMessage("No sei il leader a questo turno. Dovresti aspettare il leader pesca la carta.");

        }

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
        myModel.setCurrentAdventureCard(drawnAdventureCardUpdate.getCard());



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

        if (confirm == true && "AbandonedStation".equals(getCurrentAdventureCard().getName())){
            AbandonedStation abandonedStation = (AbandonedStation)  getCurrentAdventureCard();
            myModel.setUnplacedGoods(abandonedStation.getGoods());
            view.askLoadGoodChoice();
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
        int fakeplanetIndex = update.getPlanetIndex()+1;
        view.showGenericMessage("Player " + selectingPlayerNickname + " ha selezionato il pianeta " + fakeplanetIndex);
        if (selectingPlayerNickname.equals(getNickname())) {
            Planet selectedPlanet = update.getSelectedPlanet();
            myModel.setSelectedPlanet(selectedPlanet);
            myModel.setUnplacedGoods(selectedPlanet.getGoods());
            view.askLoadGoodChoice();
        }
    }

    public void handleLoadGoodChoice(String input) {
        if (input == null) return;

        switch (input.toLowerCase()) {
            case "l" -> view.askSelectGoodToLoad(myModel.getUnplacedGoods(), myModel.getMyInfo().getShip());
            case "d" -> view.askSelectGoodToDiscard( myModel.getMyInfo().getShip());
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

    public void handleDiscardCrewMembersRequest(DiscardCrewMembersRequest request) {
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
    public void handleAskTrunkRequest(AskTrunkRequest askTrunkRequest) {
        //Lista di ship
        ArrayList<Ship> Trunks = askTrunkRequest.getTrunks();
        try {
            view.chooseTroncone(Trunks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleTrunkResponse(int choice) {
        AskTrunkResponse response = new AskTrunkResponse(choice-1);
        try {
            client.sendMessage(response);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCollectRewardsResponse(boolean confirm) {
       CollectRewardsResponse response = new CollectRewardsResponse(confirm);
        try {
            client.sendMessage(response);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        if (confirm && "Smugglers".equals(getCurrentAdventureCard().getName())) {
            Smugglers smugglers = (Smugglers)  getCurrentAdventureCard();
            myModel.setUnplacedGoods(smugglers.getGoods());
            view.askLoadGoodChoice();
        }
    }

    public void handleCollectRewardsRequest(CollectRewardsRequest request){
        view.askCollectRewards();

    }
    public AdventureCard getCurrentAdventureCard() {
        return myModel.getCurrentAdventureCard();
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



    public void handleGameEndUpdate(GameEndUpdate update) {
        ArrayList<PlayerScore> scores = update.getScores();
        view.showEndGame(scores);
    }


    public void handlePlayerLostUpdate(PlayerLostUpdate update) {
        boolean isLandingEarly = update.isLandingEarly();
        String nickname = update.getNickname();

        if (nickname.equals(getNickname())) {
            myModel.setPlayerState(PlayerState.Spectating);
        }
        if (isLandingEarly) {
            view.showGenericMessage("Il giocatore " + nickname + " ha lasciato la partita.");
        } else {
            view.showGenericMessage("il giocatore " + nickname + " è stato rimosso forzatamente dalla partita.");
        }
    }

    public Ship getMyShip() {
        return myModel.getMyInfo().getShip();
    }

    private Slot getSlot(Ship ship, Position pos) {
        Slot slot = ship.getShipBoard()[pos.getY()][pos.getX()];
        return slot;
    }

    private ArrayList<Position> getCargoHolds(Ship ship) {
        return ship.getComponentPositionsFromName("GenericCargoHolds");
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
    public Tile[] getReservedTiles() {
        return myModel.getReservedTiles();
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

    public void sendFlipRequest(ArrayList<TimerInfo> timerInfos) throws IOException, ExecutionException, InterruptedException {
        //trovo quello che si puo flippare

        int index = 0;

        for (TimerInfo timerInfo: timerInfos){
            if (timerInfo.getTimerStatus().equals(TimerStatus.OFF)){
                index = timerInfo.getIndex();
                break;
            }
        }

        FlipTimerRequest flipTimerRequest = new FlipTimerRequest();
        flipTimerRequest.setTimerIndex(index);
        client.sendMessage(flipTimerRequest);





    }
}
