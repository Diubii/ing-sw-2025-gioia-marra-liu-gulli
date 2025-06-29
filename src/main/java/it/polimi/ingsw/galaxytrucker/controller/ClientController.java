package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.enums.*;
import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.PlayerScore;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Smugglers;
import it.polimi.ingsw.galaxytrucker.model.essentials.*;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.network.client.Client;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import it.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkingUtils;
import it.polimi.ingsw.galaxytrucker.observer.Observer;
import it.polimi.ingsw.galaxytrucker.view.Tui.MenuManager;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.CardPrintUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.Network.ClientNetworkMessageVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import javafx.util.Pair;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/*
  Copyright (c) Politecnico di Milano - Ingegneria del Software
  <p>
  This is the ClientController class responsible for handling all client-side logic,
  communication with the server (via Socket or RMI), and view interaction.
  It operates under the MVC pattern with a synchronized ClientModel and
  acts as an Observer for server-side updates.
 */


/**
 * The type Client controller.
 */
public class ClientController implements Observer {


    private Client client;

    private View view;
    private CompletableFuture<NetworkMessage> completableFuture;
    private Pair<Integer, CompletableFuture<NetworkMessage>> pair;
    private ClientModel myModel;
    private final NetworkMessageVisitorsInterface<Void> messageVisitor = new ClientNetworkMessageVisitor(this);
    private Tile currentTileInHand = null;
    private Position currentPosition;
    private Position tmpCurrentPosition;
    private final ClientPhaseController clientPhaseController = new ClientPhaseController(this);
    private final AtomicBoolean isConnectionAlive = new AtomicBoolean(false);
    private GameState phase = GameState.LOBBY;
    private boolean isPlaced = false;
    private final Boolean isSocket;
    private ScheduledFuture<?> heartbeatTask;

    /**
     * Returns the current game phase.
     *
     * @return the current game state
     */
    public GameState getPhase() {
        return phase;
    }


    /**
     * Gets my model.
     *
     * @return the my model
     */
    public ClientModel getMyModel() {
        return myModel;
    }

    /**
     * Sets completable future.
     *
     * @param completableFuture the completable future
     * @param id                the id
     */
    public void setCompletableFuture(CompletableFuture<NetworkMessage> completableFuture, int id) {
        this.completableFuture = completableFuture;
        this.pair = new Pair<>(id, completableFuture);
    }

    /**
     * Constructs a new ClientController with the given view and connection type.
     *
     * @param view the client-side view (TUI/GUI)
     * @param flag true if using Socket, false for RMI
     */
    public ClientController(View view, Boolean flag) {
        this.myModel = new ClientModel();
        this.isSocket = flag;
        this.view = view;

        InputStream in = System.in;
        System.setIn(new FilterInputStream(in) {
            @Override
            public void close() throws IOException {
                System.out.println("⚠️ QUALCOSA STA CHIUDENDO System.in!");
                super.close();
            }
        });
    }


    /**
     * Receives and processes messages from the server using the visitor pattern.
     * @param message the message received
     */
    @Override
    public void update(NetworkMessage message) {
        message.accept(messageVisitor);
    }

    /**
     * Scheduler for managing periodic tasks such as the heartbeat mechanism.
     * This uses a single-threaded executor to ensure heartbeat signals are sent sequentially.
     * Thread is named "HeartbeatThread" and set to maximum priority.
     */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("HeartbeatThread");
        t.setPriority(Thread.MAX_PRIORITY);
        t.setDaemon(false);
        return t;
    });

    /**
     * Connects the client to the specified server using either Socket or RMI.
     * Starts the heartbeat to ensure connection remains alive.
     *
     * @param address the server address
     * @param port    the server port
     * @throws IOException       if connection fails
     * @throws NotBoundException if RMI lookup fails
     */
    public void connectToServer(String address, int port) throws IOException, NotBoundException {
        if (isSocket) {
            client = new ClientSocket(address, port);
            ((ClientSocket) client).create(address, port);
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket) client).receiveMessage();
        } else {
            System.setProperty("java.rmi.server.hostname", NetworkingUtils.getLocalIP());
            client = new ClientRMI(address, port, this); // già fa addObserver
        }

        isConnectionAlive.set(true);
        heartbeatTask = scheduler.scheduleAtFixedRate(getHeartbeatTask(), 0, 1, TimeUnit.SECONDS); //Invia subito e poi ogni secondo
    }

    /**
     * Creates a Runnable task that periodically sends a heartbeat request to the server.
     * <p>
     * The heartbeat is used to check the connection status with the server.
     * If the send fails, it logs an error and the connection may be marked as lost.
     *
     * @return a Runnable that sends a {@link HeartbeatRequest} to the server
     */
    private Runnable getHeartbeatTask() {
        return (() -> {
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            /*boolean success =*/ safeSendMessage(heartbeatRequest);
            //System.out.println("Sent heartbeat: " + System.currentTimeMillis());
                //System.out.println("[ClientController] Sent heartbeat.");
//

        });
    }

    /**
     * Handle server info.
     *
     * @param info the info
     */
    public void handleServerInfo(SERVER_INFO info) {
        try {
            connectToServer(info.getAddress(), info.getPort());
        } catch (IOException | NotBoundException e) {
            view.showGenericMessage("Couldn't connect you to the specified server. Try again.", false);
            view.askServerInfo();
            return;
        }

        new Thread(() -> {
            view.askNickname();
        }).start();
    }

    /**
     * Handles the user input for a nickname.
     * Validates the nickname format locally before sending a {@link NicknameRequest}
     * to the server.
     *
     * @param nickname the nickname input provided by the user
     */

    private boolean isNicknameLegal(String nickname) {
        //no accetta nickname == null o la string vuota
        if (nickname == null || nickname.trim().isEmpty()) return false;
        //nickname deve corrispondere a una stringa che contiene solo lettere (a-z o A-Z), numeri (0-9) o il carattere di sottolineatura (_) e che ha almeno un carattere.
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
    public void handleNicknameInput(String nickname) {
        if (!isNicknameLegal(nickname)) {
            view.showGenericMessage("Invalid nickname. It must contain only letters, numbers, or underscores.", false);
            view.askNickname();
            return;
        }
        NicknameRequest request = new NicknameRequest(nickname);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        setNickname(nickname);

        safeSendMessage(request);
    }

    /**
     * Handles the server's response to a {@link NicknameRequest}.
     * <p>
     * If the nickname is valid, the user proceeds to the next step (joining or creating a room).
     * Otherwise, the user is prompted to try a different nickname.
     *
     * @param response the response from the server indicating whether the nickname was accepted
     */
    public void handleNicknameResponse(NicknameResponse response) {
        if ("VALID".equals(response.getResponse())) {
            view.showGenericMessage("Nickname accepted.", false);
            view.askJoinOrCreateRoom();
        } else {
            setNickname(null);
            view.showGenericMessage("Nickname rejected. Try again.", false);
            view.askNickname();
        }
    }

    /**
     * Handles user's choice to create or join a room.
     *
     * @param choice user's input
     * @throws ExecutionException if execution fails
     */
    public void handleCreateOrJoinChoice(String choice) throws ExecutionException {
        switch (choice.toLowerCase()) {
            case "a" -> view.askCreateRoom();
            case "b" -> handleJoinRoomOptionsChoice();
            case "reset" -> {}
            default -> {
                view.showGenericMessage("Invalid choice.", false);
                view.askJoinOrCreateRoom();
            }
        }
    }

    /**
     * Sends a CreateRoomRequest to the server with specified options.
     *
     * @param maxPlayers      number of players allowed
     * @param isLearningMatch true if learning match
     */
    public void handleCreateChoice(int maxPlayers, boolean isLearningMatch) {
        CreateRoomRequest request = new CreateRoomRequest(maxPlayers, isLearningMatch, getNickname(), view.getViewType() == ViewType.GUI);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if (!safeSendMessage(request)) return;
        view.showGenericMessage("Room creation request sent.", false);
    }

    /**
     * Handles the server's response to a {@link JoinRoomRequest}.
     * <p>
     * If the join is successful, the client's model is updated with room details (such as ship,
     * player color, learning match flag), and the lobby view is shown.
     * If the join fails, an error message is shown and the user is prompted to try again.
     *
     * @param response the response from the server indicating success or failure of the room join operation
     */
    public void handleJoinRoomResponse(JoinRoomResponse response) {
        if (response.getOperationSuccess()) {
            clientPhaseController.setPhase(PLAYER_PHASE.LOBBY);
            myModel.getMyInfo().setColor(response.getColor());
            myModel.getMyInfo().setShip(response.getMyShip());
            myModel.getMyInfo().setNickName(getNickname());
            myModel.setLearningMatch(response.getIsLearningMatch());
            MenuManager.learningMatch = response.getIsLearningMatch();

            ArrayList<PlayerInfo> playerInfos = response.getPlayerInfos();
            if(playerInfos == null) playerInfos = new ArrayList<>();
            synchronized (myModel.getPlayerInfos()) {
                myModel.getPlayerInfos().add(myModel.getMyInfo());
                myModel.setPlayerInfos(playerInfos);
            }

            view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());
            view.showGenericMessage("Lobby joinata con successo, in attesa di giocatori...", false);
        } else {
            view.showGenericMessage("Room join failed: " + response.getErrMess(), true);
            view.askJoinOrCreateRoom();
        }
    }

    /**
     * Handle join room options choice.
     */
    public void handleJoinRoomOptionsChoice() {
        JoiniRoomOptionsRequest request = new JoiniRoomOptionsRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        safeSendMessage(request);
    }

    /**
     * Handles the server's response to a {@link JoiniRoomOptionsRequest}.
     * <p>
     * Displays the list of available lobbies (rooms) to the user.
     * If lobbies are available, the user is prompted to enter a room code to join.
     *
     * @param response the response containing a list of available lobbies
     */
    public void handleJoinRoomOptionsResponse(JoinRoomOptionsResponse response) {
        List<LobbyInfo> lobbies = response.getLobbyInfos();

        if (lobbies.isEmpty()) {
            view.showLobbies(lobbies);
        } else {
            view.showLobbies(lobbies);
            view.askRoomCode();
        }
    }

    /**
     * Sends a JoinRoomRequest to the server with the selected room ID.
     *
     * @param roomId room ID to join
     */
    public void handleJoinChoice(int roomId) {
        JoinRoomRequest request = new JoinRoomRequest(roomId, getNickname(), view.getViewType() == ViewType.GUI);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        safeSendMessage(request);
    }

    /**
     * Handle player joined update.
     *
     * @param playerJoinedUpdate the player joined update
     */
    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate) {

        synchronized (myModel.getPlayerInfos()) {
            myModel.setPlayerInfos(playerJoinedUpdate.getPlayersJoinedBefore());
        }

        view.showPlayerJoined(playerJoinedUpdate.getPlayerInfo());
        view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());
    }

    /**
     * Handle phase update.
     *
     * @param phaseUpdate the phase update
     */
    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {

        //System.out.println("Ricevuto phase update in clientController: " + phaseUpdate.getState().toString());
        phase = phaseUpdate.getState();

        if (phase.equals(GameState.FLIGHT)) myModel.setPlayerState(PlayerState.Playing);
        if (phase.equals(GameState.BUILDING_END)) {
            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.FINISH_BUILDING)) {
                return;
            }
            if (clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING_TIMER) || clientPhaseController.getPhase().equals(PLAYER_PHASE.BUILDING)) {

                view.forceReset();

                clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);

                FinishBuildingRequest finishBuildingRequest = new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile());
                finishBuildingRequest.name = getNickname();

                safeSendMessage(finishBuildingRequest);

                return;
            }
            view.handlePhaseUpdate(phaseUpdate);
            return;

        }
        clientPhaseController.handlePhaseUpdate(phaseUpdate);
        view.handlePhaseUpdate(phaseUpdate);
    }

    /**
     * Handles user input during the building phase menu.
     * <p>
     * Depending on the input, this method routes the command to different sub-menus or actions such as:
     * drawing a tile, rotating/placing/discarding a tile, fetching ships, showing adventure decks, etc.
     * Invalid or premature commands are handled with user feedback.
     *
     * Supported commands include:
     * <ul>
     *     <li>"a" - fetch and view a ship</li>
     *     <li>"b" - view adventure decks (non-learning match only)</li>
     *     <li>"c" - show face-up tiles</li>
     *     <li>"d" - draw a face-down tile</li>
     *     <li>"e" - show tile in hand</li>
     *     <li>"f" - rotate tile in hand</li>
     *     <li>"g" - start tile placement process</li>
     *     <li>"h" - discard tile in hand</li>
     *     <li>"i" - finish building</li>
     *     <li>"j" - ask for timer info</li>
     *     <li>"menu", "?", "m" - show current menu</li>
     *     <li>"reset" - no-op reset</li>
     * </ul>
     *
     * @param input the user's menu command input
     */
    public void handleBuildingMenuChoice(String input) {

        switch (input) {
            case "menu", "?", "m" -> {
                view.toShowCurrentMenu();
                view.handleChoiceForPhase(phase);
            }
            case "a" -> view.askFetchShip();
            case "b" -> {
                if (!myModel.isLearningMatch()) {

                    if (isPlaced || currentTileInHand == null) {
                        sendShipUpdate();
                        view.askViewAdventureDecks();
                    } else {
                        view.showGenericMessage("Hai una tile in mano, per favore posizionala prima.", false);
                        view.showBuildingMenu();
                    }

                } else {
                    view.showGenericMessage("You are not allowed to spy on the learningMatch!", false);
                    view.showBuildingMenu();
                }

            }
            case "c" -> {

                view.askShowFaceUpTiles();
                view.showBuildingMenu();
            }
            case "d" -> {
                if (currentTileInHand != null) {
                    view.showGenericMessage("You already have a tile in hand! Place it or discard it before drawing a new one.", false);
                    view.showBuildingMenu();

                } else {
                    isPlaced = false;
                    view.askDrawTile();

                }

            }
            case "e" -> showTileInHand();
            case "f" -> {
                if (currentTileInHand == null) {
                    view.showGenericMessage("Before setting a rotation, you need to draw a tile.", false);
                    view.showBuildingMenu();
                } else {
                    view.askRotation();
                }

            }
            case "g" -> view.askTilePlacement();
            case "h" -> sendDiscardRequest();
            case "i" -> {
                clientPhaseController.setPhase(PLAYER_PHASE.FINISH_BUILDING);

                FinishBuildingRequest finishBuildingRequest = new FinishBuildingRequest(myModel.getMyInfo().getShip(), myModel.getMyInfo().getShip().getLastTile());
                finishBuildingRequest.name = getNickname();

                safeSendMessage(finishBuildingRequest);
            }

            case "j" -> sendAskTimerInfoRequest();
            case "reset" -> {}
            default ->
                new Thread(() -> {
                    view.showGenericMessage("Invalid option \"" + input + "\". Please try again.",false);
                    view.showBuildingMenu();
                }).start();
        }

    }

    /**
     * Handles user input during the "finished building" menu phase.
     * <p>
     * This phase occurs after a player finishes building their ship and is waiting for others.
     * The player is limited to non-modifying actions such as:
     * viewing their ship, inspecting adventure decks (if allowed), or checking timer status.
     *
     * Supported commands include:
     * <ul>
     *     <li>"a" - fetch and view a ship</li>
     *     <li>"b" - view adventure decks (only if not a learning match)</li>
     *     <li>"c" - show face-up tiles</li>
     *     <li>"j" - request timer info</li>
     *     <li>"menu", "?", "m" - show the current menu</li>
     *     <li>"reset" - no-op</li>
     * </ul>
     *
     * @param input the user's menu command input
     */
    public void handleFinishedBuildingMenuChoice(String input){
        switch (input) {
            case "menu", "?", "m" -> {
                view.toShowCurrentMenu();
                view.handleChoiceForPhase(phase);
            }
            case "a" -> view.askFetchShip();
            case "b" -> {
                if (!myModel.isLearningMatch()) {

                    if (isPlaced || currentTileInHand == null) {
                        sendShipUpdate();
                        view.askViewAdventureDecks();
                    } else {
                        view.showGenericMessage("Hai una tile in mano, per favore posizionala prima.", false);
                        view.showFinishedBuildingMenu();
                    }

                } else {
                    view.showGenericMessage("You are not allowed to spy on the learningMatch!", false);
                    view.showFinishedBuildingMenu();
                }

            }
            case "c" -> {

                view.askShowFaceUpTiles();
                view.showFinishedBuildingMenu();
            }

            case "j" -> sendAskTimerInfoRequest();
            case "reset" -> {}
            default ->
                    new Thread(() -> {
                        view.showGenericMessage("Invalid option \"" + input + "\". Please try again.",false);
                        view.showFinishedBuildingMenu();
                    }).start();
        }
    }


    /**
     * Sends the current ship state to the server for update.
     */
    public void sendShipUpdate() {
        if(myModel.getMyInfo().getShip().getLastTile()!=null) {
            ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
            currentPosition = null;
            currentTileInHand = null;
            update.setOnlyFix(true);
            safeSendMessage(update);
        }
    }


    /**
     * Handles a request to fetch and display the ship of a given player by nickname.
     * <p>
     * If the nickname matches the client's own, their own ship is shown.
     * If the nickname belongs to another player in the lobby or match, that player's ship is shown.
     * If the nickname is not found, an error is shown and the user is prompted again.
     *
     * @param targetNickname the nickname of the player whose ship should be displayed
     */
    public void handleFetchShip(String targetNickname) {

        boolean exists;
        exists = myModel.hasPlayerWithNickname(targetNickname);
        if (exists) {
            if (myModel.getMyInfo().getNickName().equals(targetNickname)) {
                view.showShip(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
                view.handleChoiceForPhase(phase);
            } else {
                Ship targetShip = myModel.getPlayerInfoByNickname(targetNickname).getShip();
                view.showShip(targetShip, targetNickname);
                view.handleChoiceForPhase(phase);
            }

        } else {
            view.showGenericMessage("No player with nickname " + targetNickname + " found. Please try again.", false);
            view.askFetchShip();
        }
    }


    /**
     * Handle the received {@link ShipUpdate}.
     *
     * @param update the update
     */
    public void handleShipUpdate(ShipUpdate update) {
        String owner = update.getNickName();
        Ship ship = update.getShipView();

        if (phase == GameState.FLIGHT) {
            if (update.getLoadMerci() == true) {
                view.askLoadGoodChoice();
            }
        }

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
                        view.showGenericMessage("Player with nickname " + owner + " not found.", false);
                    }
                }
            }
            if (update.getShouldDisplay()) {
                view.autoShowShipInTui(ship,owner);
            }
            if (view.autoShowUpdates()) {
                view.showShip(ship, owner);
                System.out.println("Debug: stampo ultimo shipUpdate");
                ShipPrintUtils.printShip(ship);
            }
        } else {
            view.showGenericMessage("No ship belongs to this player.", false);
            view.handleChoiceForPhase(phase);
        }

    }

    /**
     * View adventure card deck boolean.
     *
     * @param DeckID the deck id
     * @return the boolean
     */
    public Boolean viewAdventureCardDeck(int DeckID) {
        boolean allowed = false;
        ArrayList<CardDeck> cardDecks = myModel.getCardDecks();

        if (!cardDecks.isEmpty()) {
            if (cardDecks.size() <= DeckID) {
                view.showGenericMessage("Numero del deck non valido", false);
            } else {
                CardDeck deck = cardDecks.get(DeckID);
                boolean spyable = deck.isSpyable();
                if (spyable) {
                    int colums = 3;
                    CardPrintUtils.printDeck(deck, colums);
                    DeckID++;
                    view.showGenericMessage("Deck  " + DeckID + " received successfully. ", false);
                    allowed = true;
                } else {
                    view.showGenericMessage("You are not allowed to spy on this deck!", false);
                }
            }
        } else {
            view.showGenericMessage("No card decks found.", false);
        }
        view.showBuildingMenu();
        return allowed;
    }


    /**
     * Handle face up tile update.
     *
     * @param update the update
     */
    public void handleFaceUpTileUpdate(FaceUpTileUpdate update) {
        ArrayList<Tile> faceUpTiles = update.getFaceUpTiles();
        synchronized (myModel.getFaceUpTiles()) {
            myModel.setFaceUpTiles(faceUpTiles);
        }
        view.handleFaceUpTilesUpdate();
    }

    /**
     * Sends an {@link AskTimerInfoRequest}
     */
    public void sendAskTimerInfoRequest(){
        AskTimerInfoRequest askTimerInfoRequest = new AskTimerInfoRequest();
        safeSendMessage(askTimerInfoRequest);
    }

    /**
     * Handles the server's response containing timer information for the building phase.
     * <p>
     * Updates the client's model with the latest timer states and displays them to the user.
     * If this is the final timer update (i.e., all hourglasses have been flipped),
     * the game transitions to the {@link GameState#BUILDING_END} phase automatically.
     *
     * @param timerInfoResponse the timer information response from the server
     */
    public void handleTimerInfoResponse(TimerInfoResponse timerInfoResponse) {
        myModel.getTimerInfos().clear();
        myModel.getTimerInfos().addAll(timerInfoResponse.getTimerInfoList());
        view.showTimerInfos(myModel.getTimerInfos());

        if (timerInfoResponse.getLast()) {
            handlePhaseUpdate(new PhaseUpdate(GameState.BUILDING_END));
        }
    }
    /**
     * Determines whether the player is allowed to flip an hourglass during the building phase.
     * <p>
     * A player can flip an hourglass only if:
     * <ul>
     *     <li>No other timer is currently active (i.e., no timer is in {@link TimerStatus#STARTED})</li>
     *     <li>Fewer than two timers have been flipped, or the player is in the {@code FINISH_BUILDING} phase</li>
     * </ul>
     *
     * @return {@code true} if flipping an hourglass is allowed, {@code false} otherwise
     */

    public boolean canFlipHourglass(){
        boolean oneActive = false;
        int numFlipped = 0;

        synchronized (myModel.getTimerInfos()) {
            for (TimerInfo timerInfo : myModel.getTimerInfos()) {
                if (timerInfo.getTimerStatus().equals(TimerStatus.STARTED)) {
                    oneActive = true;
                }
                if (timerInfo.isFlipped()) {
                    numFlipped++;
                }
            }
        }

        return !oneActive && (numFlipped != 2 || clientPhaseController.getPhase().equals(PLAYER_PHASE.FINISH_BUILDING));
    }

    /**
     * Sends a DrawTileRequest to the server and handles the response.
     * For both face-down and face-up tile selections.
     */
    public void handleDrawFaceDownTile() {

        if (currentTileInHand == null || isPlaced) {
            sendShipUpdate();
        }
        DrawTileRequest request = new DrawTileRequest();
        safeSendMessage(request);
    }

    /**
     * Processes the response to a {@link DrawTileRequest}.
     * <p>
     * If successful, stores and shows the drawn tile.
     * Otherwise, displays an error message based on the response code.
     *
     * @param response the server's response to the draw request
     */
    public void handleDrawTileResponse(DrawTileResponse response) {
        String error = response.getErrorMessage();

        switch (error) {
            case "VALID" -> {
                Tile drawnTile = response.getTile();
                if (drawnTile != null) {
                    view.showTile(drawnTile);
                    ComponentNameVisitor visitor = new ComponentNameVisitor();
                    Component c = drawnTile.getMyComponent();
                    String name = c.accept(visitor);
                    int id = drawnTile.getId();
                    currentTileInHand = drawnTile;
                    currentPosition = null;
                    view.showGenericMessage("You drew a " + name + " tile." + "ID: " + id, false);
                }
            }
            case "EMPTY" -> view.showGenericMessage("The tile bunch is empty.", false);
            case "INVALID_STATE" -> view.showGenericMessage("You cannot draw a tile right now.", false);
            case "FIXED" -> view.showGenericMessage("The tile is fixed.", false);
            case "NO_TILE" -> view.showGenericMessage("You don't have any reclaimable tile.", false);
            case "NO_TILE_AT_INDEX" -> view.showGenericMessage("No tile at that index.", false);
            case null, default -> view.showGenericMessage("Unexpected response while drawing tile: " + error, false);
        }
        view.showBuildingMenu();
    }


    /**
     * Start choose tile.
     */
    public void startChooseTile() {
        List<Tile> tiles = myModel.getFaceUpTiles();
        if (tiles == null || tiles.isEmpty()) {
            view.showGenericMessage("No face-up tiles available.", false);
            view.showBuildingMenu();
            return;
        }
        view.showGenericMessage("--Current face-up tiles--", false);

        view.showFaceUpTiles();
        view.askChooseTile();
    }

    /**
     * Handle choose face up tile.
     *
     * @param tile the tile
     */
    public void handleChooseFaceUpTile(Tile tile) {

        DrawTileRequest request = new DrawTileRequest(tile);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        safeSendMessage(request);
    }

    /**
     * Reclaim tile.
     */
    public void reclaimTile(){
        DrawTileRequest request = DrawTileRequest.reclaimLastTileRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        safeSendMessage(request);
    }

    /**
     * Handle draw reserved tile.
     *
     * @param slotIndex the slot index
     */
    public void handleDrawReservedTile (int slotIndex){
        DrawTileRequest request = DrawTileRequest.fromReservedSlot(slotIndex);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        safeSendMessage(request);
}


    /**
     * Shows the tile in hand.
     */
    public void showTileInHand() {
        view.showTile(currentTileInHand);
        //view.showGenericMessage("Tile in hand showed successfully.", false);
        view.showBuildingMenu();
    }

    /**
     * Rotates the current tile in hand and shows it on the view.
     *
     * @param rotation number of clockwise rotations to apply
     */
    public void rotateCurrentTile(int rotation) {
        currentTileInHand.rotate(rotation);
        view.showTile(currentTileInHand);
        view.showGenericMessage("Tile rotated successfully.", false);
        view.showBuildingMenu();

    }

    /**
     * Sets tmp current position.
     *
     * @param tmpCurrentPosition1 the tmp current position 1
     */
    public void setTmpCurrentPosition(Position tmpCurrentPosition1) {
        this.tmpCurrentPosition = tmpCurrentPosition1;
    }

    /**
     * Reset current pos.
     */
    public void resetCurrentPos() {
        currentPosition = null;
    }


    /**
     * Sets current pos.
     *
     * @param x the x
     * @param y the y
     */
    public void setCurrentPos(int x, int y)  {
        Position pos = new Position(x, y);
        Ship ship = myModel.getMyInfo().getShip();

        if (!Util.inBoundaries(pos.getX(), pos.getY()) || ship.getInvalidPositions().contains(pos)) {

            throw new IllegalArgumentException("Invalid Position" + pos.toOffsetString());
        }

        currentPosition = pos;
    }

    /**
     * Sends a PlaceTileRequest to the server to place the current tile.
     */
    public void handleTilePlacement() {
        if (currentTileInHand == null ) {
            view.showGenericMessage("No tile selected.", false);
            view.showBuildingMenu();
            return;
        }
        //Toglie preventivamente tile dalla mano per evitare bug se click rapidi
        Tile appInHand = currentTileInHand;
        currentTileInHand = null;
        isPlaced = true;
        view.showTile(currentTileInHand);

        PlaceTileRequest request = new PlaceTileRequest(appInHand, currentPosition);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        safeSendMessage(request);
    }

    /**
     * Processes the response to a tile placement request.
     * <p>
     * Updates local state based on placement result and shows feedback to the user.
     *
     * @param response the server's response to the tile placement
     */
    public void handlePlaceTileResponse(PlaceTileResponse response) {
        view.showGenericMessage(response.getMessage(), false);

        switch (response.getMessage()) {
            case "INVALID_STATE" -> {
                currentPosition = tmpCurrentPosition;
                view.showGenericMessage("You cannot place a tile right now.", false);
            }
            case "INVALID_POS" -> {
                currentPosition = tmpCurrentPosition;
                view.showGenericMessage("You cannot place a tile in that position. invalid pos", false);
            }
            case "OCCUPIED_POS" -> {
                currentPosition = tmpCurrentPosition;
                view.showGenericMessage("You cannot place a tile in that position. occupied pos", false);
            }
            case "VALID" -> {
                resetCurrentPos();
                currentTileInHand = null;
                isPlaced = true;
                view.showTile(null);
            }
            default -> {
                isPlaced = false;
                view.showTile(currentTileInHand);
            }
        }

        view.showBuildingMenu();
    }

    /**
     * Sends a request to place the current tile into a reserved slot.
     *
     * @param slotIndex the index of the reserved slot
     */

    private void handlePlaceReservedTile(int slotIndex) {
        if (currentTileInHand == null) {
            view.showGenericMessage("No tile selected.", false);
            view.showBuildingMenu();
            return;
        }
        PlaceTileRequest request = new PlaceTileRequest(currentTileInHand, slotIndex);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        safeSendMessage(request);
    }


    /**
     * Sends a request to discard the current tile in hand.
     * <p>
     * If no tile is held, displays a warning instead.
     */
    public void sendDiscardRequest() {
        if (currentTileInHand == null) {
            view.showGenericMessage("No tile in hand to discard.", false);
            view.showBuildingMenu();

            return;
        }
        int initRotation = currentTileInHand.getRotation();
        currentTileInHand.rotate(-initRotation);
        DiscardTileRequest request = new DiscardTileRequest(currentTileInHand);

        if (!safeSendMessage(request)) return;
        currentTileInHand = null;
        currentPosition = null;
        view.showGenericMessage("Tile discarded successfully.", false);
        view.showBuildingMenu();
    }


    /**
     * Handles picking from or placing a tile into a reserved slot.
     *
     * @param slotIndex the reserved slot index
     * @param isPicking {@code true} to pick a tile from the slot, {@code false} to place into it
     */
    public void handlePickReservedTile(int slotIndex, boolean isPicking) {

        Tile[] reservedTiles = getReservedTiles();
        Tile tile = reservedTiles[slotIndex];

        if (isPicking) {
            if (tile == null) {
                int toShowIndex = slotIndex + 1;
                view.showGenericMessage("No reserved tile at slot " + toShowIndex + ".", false);
                view.showBuildingMenu();
            } else {
                handleDrawReservedTile(slotIndex);
            }
        }

        else {
            if (tile == null) {
                handlePlaceReservedTile(slotIndex);
            } else {
                view.showGenericMessage("A tile is already reserved at slot " + slotIndex + ".", false);
                view.showBuildingMenu();
            }
        }
    }



    /**
     * Returns whether there is a tile currently in hand.
     *
     * @return true if a tile is in hand
     */
    public boolean hasTileInHand() {
        return this.getCurrentTileInHand() != null;
    }

    /**
     * Handle decks update.
     *
     * @param decksUpdate the decks update
     */
    public void handleDecksUpdate(DecksUpdate decksUpdate) {

        myModel.setCardDecks(decksUpdate.getDecks());

    }

    /**
     * Handle flight board update.
     *
     * @param flightBoardUpdate the flight board update
     */
    public void handleFlightBoardUpdate(FlightBoardUpdate flightBoardUpdate) {

        myModel.setFlightBoard(flightBoardUpdate.getFlightBoard());
        if (phase == GameState.FLIGHT && view.autoShowUpdates() == true) {
            view.showFlightBoard(myModel.getFlightBoard(), myModel.getPlayerInfos(), myModel.getMyInfo());
        }

    }

    /**
     * Handle ask position update.
     *
     * @param askPositionUpdate the ask position update
     */
    public void handleAskPositionUpdate(AskPositionUpdate askPositionUpdate) {
        new Thread(() -> view.askFlightBoardPosition(askPositionUpdate.getValidPositions(), askPositionUpdate.getID())).start();
    }


    /**
     * Gets is socket.
     *
     * @return <code>true</code> if the chosen communication protocol is socket.
     */
    public boolean getIsSocket() {
        return isSocket;
    }


    /**
     * Handles user input from the check-ship menu during the building phase.
     *
     * @param input the user's selected option
     */
    public void handleCheckShipChoice(String input) {
        new Thread(() -> {

            switch (input) {
                case "a" -> {
                    view.showShip(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
                    view.showCheckShipMenu();
                }
                case "b" -> {
                    if (myModel.getMyInfo().getShip().remainingTiles() > 0) {
                        view.askRemoveTile(myModel.getMyInfo().getShip());
                    } else {
                        System.out.println("OPTION DISABLED< YOU HAVE NO TILE");
                        view.showCheckShipMenu();
                    }
                }
                case "c" -> handleCheckShipRequest();
                case "menu", "m", "?" -> view.handleChoiceForPhase(phase);
                default -> {
                    view.showGenericMessage("Invalid option. Please try again.", false);
                    view.showCheckShipMenu();
                }

            }


        }).start();

    }


    /**
     * Handles check ship request.
     */
    public void handleCheckShipRequest() {
        CheckShipStatusRequest checkShipStatusRequest = new CheckShipStatusRequest();
        checkShipStatusRequest.setRemovedTilesId(myModel.getTilesToRemove());

        safeSendMessage(checkShipStatusRequest);
    }

    /**
     * Handles the response to a ship validity check.
     * <p>
     * Updates the ship state if invalid and shows appropriate feedback to the user.
     *
     * @param response the server's validation result for the player's ship
     */
    public void handleCheckShipStatusResponse(CheckShipStatusResponse response) {
        boolean isValid = response.getIsValid();
        if (isValid) {
            view.showWaitOtherPlayers();
            view.showGenericMessage("Nave immacolata!", true);
            return;

        } else {
            view.showGenericMessage("La nave va ricontrollata", true);
            myModel.getMyInfo().setShip(response.getShip());

        }
        view.showCheckShipMenu();
    }

    /**
     * Handles user input during the embark crew menu.
     *
     * @param string the selected menu option
     */
    public void handleEmbarkCrewMenu(String string) {
        new Thread(() -> {

            switch (string) {
                case "a" -> {
                    view.showShip(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
                    view.showEmbarkCrewMenu();
                }

                case "b" -> {
                    try {
                        view.chooseCrew(myModel.getMyInfo().getShip());

                    } catch (ExecutionException | InterruptedException | IOException | InvalidTilePosition |
                             TooManyPlayersException | PlayerAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }
                }

                case "menu", "m", "?" -> view.handleChoiceForPhase(phase);
                default -> {
                    view.showGenericMessage("Invalid option. Please try again.", false);
                    view.showEmbarkCrewMenu();
                }

            }

        }).start();
    }

    /**
     * Handles the end of a turn, displays appropriate messages and next menu.
     *
     * @param update EndTurnUpdate from the server
     */
    public void handleEndTurnUpdate(EndTurnUpdate update) {

        view.showGenericMessage("Turn ended", false);
        if (update.isEndGame()) {
            view.showGenericMessage("Game ended", false);
            return;
        }
        view.toShowCurrentMenu();
        if (myModel.getPlayerState() != PlayerState.Spectating) {
            view.showGenericMessage("Il turno è finito!", false);
            view.showFlightMenu();
        } else {
            handleReadyTurnRequest();
        }
    }

    /**
     * Handle flight menu choice.
     *
     * @param input the input
     * @throws RuntimeException the runtime exception
     */
    public void handleFlightMenuChoice(String input) throws RuntimeException {
        new Thread(() -> {
            switch (input) {
                case "RESET" -> {
                }
                case "a" -> view.askFetchShip();
                case "b" -> {
                    view.showFlightBoard(myModel.getFlightBoard(), myModel.getPlayerInfos(), myModel.getMyInfo());
                    view.handleChoiceForPhase(phase);
                }
                case "c" -> handleEarlyLandingRequest();
                case "d" -> handleReadyTurnRequest();
                case "menu", "m", "?" -> view.handleChoiceForPhase(phase);
                default -> {
                    view.showGenericMessage("Invalid option. Please try again.", false);
                    view.handleChoiceForPhase(phase);
                }
            }

        }).start();
    }

    /**
     * Sends a request for early landing during the flight phase.
     * <p>
     * Marks the player as spectating for the remainder of the round.
     */
    public void handleEarlyLandingRequest() {
        EarlyLandingRequest request = new EarlyLandingRequest();
        safeSendMessage(request);
        view.showGenericMessage("Hai scelto l’atterraggio anticipato, ora guarda gli altri giocatori.", false);
        view.showYouAreNowSpectating();

    }

    /**
     * Sends a request to signal that the player is ready for the next turn.
     */
    public void handleReadyTurnRequest() {
        ReadyTurnRequest request = new ReadyTurnRequest();
        safeSendMessage(request);
        view.showGenericMessage(" Devi aspettare che gli altri giocatori siano pronti.", false);

    }


    /**
     * Handles game message.
     *
     * @param gameMessage the game message
     */
    public void handleGameMessage(GameMessage gameMessage) {

        view.showGenericMessage(gameMessage.getMessage(), false);
    }

    /**
     * Handles a match info update at the start of a round.
     * <p>
     * Displays the leader and remaining adventure cards.
     * Prompts the leader to draw a card.
     *
     * @param matchInfoUpdate the update containing round metadata
     */
    public void handleMatchInfoUpdate(MatchInfoUpdate matchInfoUpdate) {

        int remainCards = matchInfoUpdate.getRemainingCards();
        String leaderNickname = matchInfoUpdate.getLeaderNickname();


        boolean amLeader = leaderNickname.equals(getNickname());
        myModel.setLeader(amLeader);
        view.showGenericMessage("Il giocatore: " + leaderNickname + " è il leader, rimangono: " + remainCards + "  carte.", false);
        if (amLeader) {
            view.askDrawCard();
        } else {
            view.showGenericMessage("Non sei leader per questo turno. Devi aspettare che il leader peschi la carta.", false);
        }

    }

    /**
     * Sends draw adventure card request.
     */
    public void sendDrawAdventureCardRequest() {
        DrawAdventureCardRequest request = new DrawAdventureCardRequest();
        safeSendMessage(request);
    }

    /**
     * Handles drawn adventure card update.
     *
     * @param drawnAdventureCardUpdate the drawn adventure card update
     */
    public void handleDrawnAdventureCardUpdate(DrawnAdventureCardUpdate drawnAdventureCardUpdate) {
        view.forceReset();
        myModel.setCurrentAdventureCard(drawnAdventureCardUpdate.getCard());
        view.showCurrentAdventureCard();
    }

    /**
     * Handles activate adventure card request.
     *
     * @param ignoredActivateAdventureCardRequest the ignored activate adventure card request
     */
    public void handleActivateAdventureCardRequest(ActivateAdventureCardRequest ignoredActivateAdventureCardRequest) {
        view.askActivateAdventureCard();
    }

    /**
     * Send activate adventure card response.
     *
     * @param confirm the confirmation
     */
    public void sendActivateAdventureCardResponse(boolean confirm) {
        ActivateAdventureCardResponse response = new ActivateAdventureCardResponse(confirm);
        if (!safeSendMessage(response)) return;

        if (confirm && "AbandonedStation".equals(getCurrentAdventureCard().getName())) {
            AbandonedStation abandonedStation = (AbandonedStation) getCurrentAdventureCard();
            myModel.setUnplacedGoods(abandonedStation.getGoods());
            view.askLoadGoodChoice();
        }

    }

    /**
     * Handle select planet request.
     *
     * @param request the request
     */

    public void handleSelectPlanetRequest(SelectPlanetRequest request) {
        HashMap<Integer, Planet> landablePlanets = request.getLandablePlanets();
        view.askSelectPlanetChoice(landablePlanets);
    }

    /**
     * Send select planet response.
     *
     * @param planet      the planet
     * @param planetIndex the planet index
     */
    public void sendSelectPlanetResponse(Planet planet, int planetIndex) {
        SelectPlanetResponse response = new SelectPlanetResponse(planet, planetIndex);
        safeSendMessage(response);
    }
    /**
     * Handles a planet selection update broadcasted by the server.
     * <p>
     * If the update is for this player, stores the selected planet and its goods.
     *
     * @param update the planet selection update
     */
    public void handleSelectPlanetUpdate(SelectedPlanetUpdate update) {
        String selectingPlayerNickname = update.getSelectingPlayerNickname();
        view.showGenericMessage("Player " + selectingPlayerNickname + " ha selezionato il pianeta " + update.getPlanetIndex(), false);
        if (selectingPlayerNickname.equals(getNickname())) {
            Planet selectedPlanet = update.getSelectedPlanet();
            myModel.setSelectedPlanet(selectedPlanet);
            myModel.setUnplacedGoods(selectedPlanet.getGoods());
        }
    }
    /**
     * Handles user input during the goods loading phase.
     *
     * @param input the user's choice: "l" to load, "d" to discard, "f" to finish
     */
    public void handleLoadGoodChoice(String input) {
        if (input == null) return;

        switch (input.toLowerCase()) {
            case "l" -> view.askSelectGoodToLoad(myModel.getUnplacedGoods(), myModel.getMyInfo().getShip());
            case "d" -> view.askSelectGoodToDiscard(myModel.getMyInfo().getShip());
            case "f" -> {
                view.showGenericMessage(" Caricamento merci completato.", false);
                try {
                    sendShipForGoodUpdate();
                } catch (Exception e) {
                    view.showGenericMessage("Errore durante l'invio della nave: " + e.getMessage(), false);
                }
            }
            default -> view.showGenericMessage(" Comando non riconosciuto. Usa L, D o F.", false);
        }
    }

    /**
     * Places a good into the specified cargo hold position on the ship.
     *
     * @param goodIndex index of the good in the planet's goods list
     * @param good      the good to place
     * @param pos       the position of the cargo hold on the ship
     */
    public void placeMerci(int goodIndex, Good good, Position pos) {
        Ship ship = myModel.getMyInfo().getShip();
        Slot slot = ship.getShipBoard()[pos.getX()][pos.getY()];
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.playerLoadGood(good);
        myModel.getSelectedPlanet().getGoods().remove(goodIndex);
    }


    /**
     * Sends the updated ship state (after loading goods) to the server.
     */
    public void sendShipForGoodUpdate() {
        ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
        safeSendMessage(update);
    }

    /**
     * Gets discard position goods.
     *
     * @param pos the pos
     * @return the discard position goods
     */
    public ArrayList<Good> getDiscardPositionGoods(Position pos) {
        Ship ship = getMyShip();
        Slot slot = getSlot(ship, pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        return hold.getGoods();
    }

    /**
     * Discard good.
     *
     * @param GoodIndex the good index
     * @param pos       the pos
     */
    public void discardGood(int GoodIndex, Position pos) {
        Slot slot = getSlot(getMyShip(), pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.removeGood(hold.getGoods().get(GoodIndex));
    }

    /**
     * Handle activate component request.
     *
     * @param request the request
     */
    public void handleActivateComponentRequest(ActivateComponentRequest request) {
        ActivatableComponent component = request.getActivatableComponentType();
        String componentName = switch (component) {
            case ActivatableComponent.DoubleCannon -> "DoubleCannon";
            case ActivatableComponent.DoubleEngine -> "DoubleEngine";
            case ActivatableComponent.Shield -> "Shield";
        };

        Ship myShip = getMyShip();
        ArrayList<Position> activateComponentPosition = myShip.getComponentPositionsFromName(componentName);
        if (activateComponentPosition == null || activateComponentPosition.isEmpty()) {
            handleActivateComponentResponse(component, null, null);
        } else {
            //Invoca metodo della view fare scegliere al giocatore i componenti da attivare e le batterie da usare
            try {
                view.chooseComponent(myModel.getMyInfo().getShip(), component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handle activate component response.
     *
     * @param component    the component
     * @param componentPos the component pos
     * @param battPos      the battery pos
     */
    public void handleActivateComponentResponse(ActivatableComponent component, ArrayList<Position> componentPos, ArrayList<Position> battPos) {
        ActivateComponentResponse resp = new ActivateComponentResponse(component, componentPos, battPos);
        safeSendMessage(resp);
    }

    /**
     * Handle discard crew members request.
     *
     * @param request the request
     */
    public void handleDiscardCrewMembersRequest(DiscardCrewMembersRequest request) {
        try {
            view.chooseDiscardCrew(myModel.getMyInfo().getShip(), request.getNumberOfCrewMembersToDiscard());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle discard crew members response.
     *
     * @param housingPos the housing pos
     */
    public void handleDiscardCrewMembersResponse(ArrayList<Position> housingPos) {
        DiscardCrewMembersResponse resp = new DiscardCrewMembersResponse(housingPos);
        safeSendMessage(resp);
    }

    /**
     * Handle player kicked update.
     *
     * @param playerKickedUpdate the player kicked update
     */

    public void handlePlayerKickedUpdate(PlayerKickedUpdate playerKickedUpdate) {
        if (playerKickedUpdate.getNickname().equals(this.getNickname())) {
            view.showGenericMessage("You've been kicked from the game!", false);
            //System.err.println("QUESTO!!!");
        } else {
            view.showGenericMessage(playerKickedUpdate.getNickname() + " got kicked out of the game!", false);
        }
        view.showGenericMessage("As " + playerKickedUpdate.getNickname() + " left the game, it has ended prematurely and you'll have search for another one.", true);
        backToMainMenu();
    }

    /**
     * Resets the client state and returns the user to the main menu.
     * <p>
     * Preserves the player's nickname.
     */
    private void backToMainMenu() {
        view.askJoinOrCreateRoom();
        view.showGenericMessage("", false);
        PlayerInfo myInfo = new PlayerInfo();
        myInfo.setNickName(myModel.getMyInfo().getNickName());
        myModel = new ClientModel();
        myModel.setMyInfo(myInfo);

        phase = GameState.LOBBY;
    }

    /**
     * Handles the server's request for the player to choose a ship trunk after damage.
     *
     * @param askTrunkRequest the request containing available ship trunks to choose from
     */

    public void handleAskTrunkRequest(AskTrunkRequest askTrunkRequest) {
        ArrayList<Ship> Trunks = askTrunkRequest.getTrunks();
        try {
            view.chooseTroncone(Trunks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle trunk response.
     *
     * @param choice the choice
     */
    public void handleTrunkResponse(int choice) {
        AskTrunkResponse response = new AskTrunkResponse(choice, myModel.getMyInfo().getNickName());
        safeSendMessage(response);
    }

    /**
     * Sends the player's response to a reward collection prompt.
     * <p>
     * If confirmed and the reward includes goods (e.g. from Smugglers or Abandoned Station),
     * prompts the player to load them.
     *
     * @param confirm {@code true} to collect rewards, {@code false} to skip
     */
    public void sendCollectRewardsResponse(boolean confirm) {
        CollectRewardsResponse response = new CollectRewardsResponse(confirm);
        if (!safeSendMessage(response)) return;

        if (confirm && "Contrabbandieri".equals(getCurrentAdventureCard().getName())) {
            Smugglers smugglers = (Smugglers) getCurrentAdventureCard();
            myModel.setUnplacedGoods(smugglers.getGoods());
            view.askLoadGoodChoice();
        }
    }

    /**
     * Handle collect rewards request.
     *
     * @param ignoredrequest the ignoredrequest
     */
    public void handleCollectRewardsRequest(CollectRewardsRequest ignoredrequest){
        view.askCollectRewards();
    }

    /**
     * Gets current adventure card.
     *
     * @return the current adventure card
     */
    public AdventureCard getCurrentAdventureCard() {
        return myModel.getCurrentAdventureCard();
    }

    /**
     * Handle crew init update.
     *
     * @param crewInitUpdate the crew init update
     */
    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate) {
        safeSendMessage(crewInitUpdate);
    }


    /**
     * Gets occupied cargo holds.
     *
     * @param ship the ship
     * @return the occupied cargo holds
     */
    public ArrayList<Position> getOccupiedCargoHolds(Ship ship) {
        ArrayList<Position> cargoHolds = getCargoHolds(ship);
        ArrayList<Position> occupied = new ArrayList<>();

        for (Position pos : cargoHolds) {
            Slot slot = ship.getShipBoard()[pos.getX()][pos.getY()];
            if (slot != null && slot.getTile() != null) {
                Component c = slot.getTile().getMyComponent();
                if (c instanceof GenericCargoHolds hold && !hold.isEmpty()) {
                    occupied.add(pos);
                }
            }
        }
        return occupied;

    }


    /**
     * Handle game end update.
     *
     * @param update the update
     */
    public void handleGameEndUpdate(GameEndUpdate update) {
        ArrayList<PlayerScore> scores = update.getScores();
        view.showEndGame(scores);
    }


    /**
     * Handles the event when a player is eliminated during flight.
     * <p>
     * If it's the local player, switches to spectating mode.
     * Shows a message describing the reason for the loss.
     *
     * @param update the update containing the player's nickname and reason for elimination
     */
    public void handlePlayerLostUpdate(PlayerLostUpdate update) {
        String nickname = update.getNickname();

        if (nickname.equals(getNickname())) {
            myModel.setPlayerState(PlayerState.Spectating);
            view.showYouAreNowSpectating();
        }

        String message = nickname + " ha perso: ";
        switch (update.getReason()) {
            case PlayerLostReason.Quit -> message += "ha deciso di atterrare in anticipo.";
            case PlayerLostReason.NoCrewMembersLeft ->
                    message += "non aveva più membri dell'equipaggio a disposizione.";
            case PlayerLostReason.Lapped -> message += "è stato doppiato.";
            case PlayerLostReason.ZeroEnginePower -> message += "non aveva potenza motrice.";
            default -> message += "le ragioni rimangono tutt'ora ignote.";
        }
        view.showGenericMessage(message, true);
    }

    /**
     * Returns the player's ship.
     *
     * @return the current Ship
     */
    public Ship getMyShip() {
        return myModel.getMyInfo().getShip();
    }

    /**
     * Sets the player's ship and sends the updated state to the server.
     *
     * @param ship the ship to assign to the local player
     */
    public void setShip(Ship ship) {
        myModel.getMyInfo().setShip(ship);
        sendShipUpdate();
    }

    /**
     * Returns the slot at the given position on the ship.
     *
     * @param ship the ship to access
     * @param pos  the position on the ship's grid
     * @return the slot at the specified position
     */
    private Slot getSlot(Ship ship, Position pos) {
        return ship.getShipBoard()[pos.getX()][pos.getY()];
    }

    /**
     * Returns the positions of all generic cargo holds on the given ship.
     *
     * @param ship the ship to inspect
     * @return a list of positions containing generic cargo holds
     */
    private ArrayList<Position> getCargoHolds(Ship ship) {
        return ship.getComponentPositionsFromName("GenericCargoHolds");
    }

    /**
     * Gets available cargo holds.
     *
     * @param ship the ship
     * @param good the good
     * @return the available cargo holds
     */
    public ArrayList<Position> getAvailableCargoHolds(Ship ship, Good good) {
        ArrayList<Position> cargoHolds = getCargoHolds(ship);
        ArrayList<Position> available = new ArrayList<>();

        for (Position pos : cargoHolds) {
            Slot slot = ship.getShipBoard()[pos.getX()][pos.getY()];
            if (slot != null && slot.getTile() != null) {
                Component c = slot.getTile().getMyComponent();
                if (c instanceof GenericCargoHolds hold && !hold.isFull()) { //TODO: Rimuovere instanceof
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


    /**
     * Gets client.
     *
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets view.
     *
     * @param v the v
     */
    public void setView(View v) {
        this.view = v;
    }

    /**
     * Gets view.
     *
     * @return the view
     */
    public View getView() {
        return view;
    }


    /**
     * Sets nickname.
     *
     * @param nickname the nickname
     */
    public void setNickname(String nickname) {

        this.myModel.getMyInfo().setNickName(nickname);
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return myModel.getMyInfo().getNickName();
    }

    /**
     * Returns the tile currently in hand.
     *
     * @return current Tile
     */
    public Tile getCurrentTileInHand() {
        return this.currentTileInHand;
    }

    /**
     * Returns the current position selected on the board.
     *
     * @return selected Position
     */
    public Position getCurrentPosition() {
        return currentPosition;
    }


    /**
     * Get reserved tiles tile [ ].
     *
     * @return the tile [ ]
     */
    public Tile[] getReservedTiles() {
        return myModel.getReservedTiles();
    }

    /**
     * Sends a FlipTimerRequest for the first available OFF timer.
     */
    public void sendFlipRequest() {
        FlipTimerRequest flipTimerRequest = new FlipTimerRequest();
        safeSendMessage(flipTimerRequest);
    }

    /**
     * Safely sends a message to the server. Handles disconnections.
     *
     * @param message message to send
     * @return true if send was successful
     */
    public boolean safeSendMessage(NetworkMessage message) {
        if (!isConnectionAlive.get()) return false;

        try {
            client.sendMessage(message);
            return true;
        } catch (IOException e) {
            if (isConnectionAlive.getAndSet(false)) {

                if(heartbeatTask != null && !heartbeatTask.isCancelled()) {
                    heartbeatTask.cancel(true);
                }

                myModel = new ClientModel();
                MenuManager.clearConsole();
                view.forceReset();
                view.showGenericMessage("Connessione al server persa, verrai riportato alla schermata di connessione.", true);
                new Thread(() -> view.askServerInfo()).start();
            }
            return false;
        }
    }
}
