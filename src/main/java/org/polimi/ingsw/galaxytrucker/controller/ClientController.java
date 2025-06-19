package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
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
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.Network.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

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
    private Thread heartbeat;
    private AtomicBoolean isConnectionAlive = new AtomicBoolean(false);

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
    
    public void setCompletableFuture(CompletableFuture<NetworkMessage> completableFuture, int id) {
        this.completableFuture = completableFuture;
        this.pair = new Pair<>(id, completableFuture);
    }
    
    private final Boolean isSocket;

    public ClientController(View view, Boolean flag) {

        this.myModel = new ClientModel();
        this.isSocket = flag;
        this.view = view;

//        clientPhaseController.next();
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
    public void update(NetworkMessage message) {
        message.accept(messageVisitor);
    }


    public void connectToServer(String address, int port) throws IOException, NotBoundException {
        if (isSocket) {
            client = new ClientSocket(address, port);
            ((ClientSocket) client).create(address, port);
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket) client).receiveMessage();
        } else {
            client = new ClientRMI(port, this); // già fa addObserver
        }

        isConnectionAlive.set(true);
        heartbeat = getHeartbeatThread();
        heartbeat.start();
    }

    private Thread getHeartbeatThread() {
        Thread heartbeat = new Thread(() -> {
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            while (true) {
                try {
                    if(!safeSendMessage(heartbeatRequest)) return;
                    //System.out.println("[ClientController] Sent heartbeat.");
                    Thread.sleep(Duration.ofSeconds(1));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        heartbeat.setPriority(Thread.MAX_PRIORITY);
        return heartbeat;
    }

    public void handleServerInfo(SERVER_INFO info) {
        try {
            connectToServer(info.getAddress(), info.getPort());
        } catch (IOException | NotBoundException e) {
            view.showGenericMessage("Couldn't connect you to the specified server. Try again.",false);
            view.askServerInfo();
            return;
        }

        new Thread(() -> {
            try {
                view.askNickname();
            } catch (IOException | InterruptedException | ExecutionException e) {
                System.err.println("Errore nell'invio del nickname: " + e.getMessage());
            }
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

    public void handleNicknameInput(String nickname) throws IOException, ExecutionException, InterruptedException {
        if (!isNicknameLegal(nickname)) {
            view.showGenericMessage("Invalid nickname. It must  contain only letters, numbers, or underscores.",false);
            view.askNickname();
            return;
        }
        NicknameRequest request = new NicknameRequest(nickname);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if(!safeSendMessage(request)) return;

        new Thread(() -> {
            try {
                NicknameResponse response = (NicknameResponse) future.get();
                if ("VALID".equals(response.getResponse())) {
                    setNickname(nickname);
                    view.showGenericMessage("Nickname accepted.",false);
                    view.askJoinOrCreateRoom();
                } else {
                    view.showGenericMessage("Nickname rejected. Try again.",false);
                    view.askNickname();
                }
            } catch (Exception e) {
                view.showGenericMessage("Failed to receive nickname response: " + e.getMessage(),true);
            }
        }).start();

    }

    public void handleCreateOrJoinChoice(String choice) throws ExecutionException {
        switch (choice.toLowerCase()) {
            case "a" -> view.askCreateRoom();
            case "b" -> handleJoinRoomOptionsChoice();
            case "reset" -> {}
            default -> {
                view.showGenericMessage("Invalid choice.",false);
                view.askJoinOrCreateRoom();
            }
        }
    }

    public void handleCreateChoice(int maxPlayers, boolean isLearningMatch) {
        CreateRoomRequest request = new CreateRoomRequest(maxPlayers, isLearningMatch, getNickname());
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if(!safeSendMessage(request)) return;
        view.showGenericMessage("Room creation request sent.",false);

        new Thread(() -> {
            try {
                JoinRoomResponse response = (JoinRoomResponse) future.get();
                if (response.getOperationSuccess()) {
                    clientPhaseController.setPhase(PLAYER_PHASE.LOBBY);
                    myModel.getMyInfo().setColor(response.getColor());
                    myModel.getMyInfo().setShip(response.getMyShip());
                    myModel.getMyInfo().setNickName(getNickname());
                    myModel.setLearningMatch(response.getIsLearningMatch());
                    MenuManager.learningMatch = isLearningMatch;


                    myModel.getPlayerInfos().add(myModel.getMyInfo());
                    view.showPlayersLobby(myModel.getMyInfo(),myModel.getPlayerInfos());
                    view.showGenericMessage("Lobby creata con successo,in attesa di giocatori...",false);
                } else {
                    view.showGenericMessage("Room creation failed: " + response.getErrMess(),true);
                    view.askJoinOrCreateRoom();
                }
            } catch (Exception e) {
                view.showGenericMessage("Error while waiting for room creation response: " + e.getMessage(),true);
            }
        }).start();
    }

    public void handleJoinRoomOptionsChoice() {
        JoiniRoomOptionsRequest request = new JoiniRoomOptionsRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if(!safeSendMessage(request)) return;

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
            } catch (InterruptedException | ExecutionException e) {
                view.showGenericMessage("Error receiving room options: " + e.getMessage(),false);
            }
        }).start();
    }


    public void handleJoinChoice(int roomId) {
        JoinRoomRequest request = new JoinRoomRequest(roomId, getNickname());
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        if(!safeSendMessage(request)) return;

        try {
            JoinRoomResponse response = (JoinRoomResponse) future.get();
            if (response.getOperationSuccess()) {
                clientPhaseController.setPhase(PLAYER_PHASE.LOBBY);

                view.showGenericMessage("Successfully joined the lobby! Waiting for other players...",false);

                myModel.getMyInfo().setColor(response.getColor());
                myModel.getMyInfo().setShip(response.getMyShip());
                myModel.getMyInfo().setNickName(getNickname());
                myModel.setLearningMatch(response.getIsLearningMatch());

                synchronized (myModel.getPlayerInfos()) {
                    myModel.setPlayerInfos(response.getPlayerInfos());
                }

                MenuManager.learningMatch = response.getIsLearningMatch();


                view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());

            } else {
                view.showGenericMessage("Failed to join the lobby: " + response.getErrMess(),false);
                view.askRoomCode();
            }
        } catch (Exception e) {
            view.showGenericMessage("Error while waiting for join room response: " + e.getMessage(),false);
        }

    }


    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate) {

        synchronized (myModel.getPlayerInfos()) {
            myModel.setPlayerInfos(playerJoinedUpdate.getPlayersJoinedBefore());
        }

        view.showPlayerJoined(playerJoinedUpdate.getPlayerInfo());
        view.showPlayersLobby(myModel.getMyInfo(), myModel.getPlayerInfos());
    }

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {

        System.out.println("Ricevuto phase update in clientController: "+phaseUpdate.getState().toString());
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

    public void handleBuildingMenuChoice(String input) {

        switch (input) {
            case "menu", "?", "m" -> {
                view.toShowCurrentMenu();
                view.handleChoiceForPhase(phase);
            }
            case "a" -> view.askFetchShip();
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
                        view.showGenericMessage("Hai un tile  in mano, per favore posizionala prima.",false);
                        view.showBuildingMenu();
                    }

                } else {
                    view.showGenericMessage("You are not allowed to spy on the learningMatch!",false);
                    view.showBuildingMenu();
                }
                
            }
            case "c" -> {

                view.askShowFaceUpTiles();
                view.showBuildingMenu();
            }
            case "d" -> {
                if (currentTileInHand != null ) {
                    view.showGenericMessage("You already have a tile in hand! Place it or discard it before drawing a new one.",false);
                    view.showBuildingMenu();

                } else {
                    isPlaced = false;
                    view.askDrawTile();

                }

            }
            case "e" -> showTileInHand();
            case "f" -> {
                if(currentTileInHand ==null){
                    view.showGenericMessage("Before set rotation, you need to draw a tile ",false);
                    view.showBuildingMenu();
                }
                else{
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

            case "j" -> new Thread(() -> view.showTimerInfos()).start();
            case "reset" -> {}
            default -> 
                new Thread(() -> {
                    view.showGenericMessage("Invalid option \"" + input + "\". Please try again.",false);
                    view.showBuildingMenu();
                }).start();
        }

    }


    //case(a)
    public void sendShipUpdate() throws IOException, ExecutionException, InterruptedException {
        ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
        currentPosition = null;
        currentTileInHand = null;
        update.setOnlyFix(true);
        safeSendMessage(update);
    }


    public void handleFetchShip(String targetNickname) {

        boolean exists;
        exists = myModel.hasPlayerWithNickname(targetNickname);
        if (exists) {
            if(myModel.getMyInfo().getNickName().equals(targetNickname)){
                view.showShip(myModel.getMyInfo().getShip(),myModel.getMyInfo().getNickName());
                view.handleChoiceForPhase(phase);
            }
            else {
                Ship targetShip = myModel.getPlayerInfoByNickname(targetNickname).getShip();
                view.showShip(targetShip,targetNickname);
                view.handleChoiceForPhase(phase);
            }

        } else {
            view.showGenericMessage("No player with nickname " + targetNickname + " found. Please try again.",false);
            view.askFetchShip();
        }
    }


    public void handleShipUpdate(ShipUpdate update) {

//        System.out.println("Debug: handleShipUpdate");

        String owner = update.getNickName();
        Ship ship = update.getShipView();
        if(phase == GameState.FLIGHT ){
            if(update.getLoadMerci()== true){
                view.askLoadGoodChoice();
            }
//
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
                        view.showGenericMessage("Player with nickname " + owner + " not found.",false);
                    }
                }
            }
            if (update.getShouldDisplay()) {
                view.showShip(ship,owner);
                view.handleChoiceForPhase(phase);

            }
            if(view.autoShowUpdates()){
                view.showShip(ship,owner);
                System.out.println("Debug: stampo ultimo shipUpdate");
                ShipPrintUtils.printShip(ship);
            }

        } else {
            view.showGenericMessage("No ship belongs to this player.",false);
            view.handleChoiceForPhase(phase);
        }

    }

    //case (b)
    public Boolean viewAdventureCardDeck(int DeckID) {
        boolean allowed=false;
        ArrayList<CardDeck> cardDecks = myModel.getCardDecks();

        if (!cardDecks.isEmpty()) {

            if(cardDecks.size() <= DeckID){
                view.showGenericMessage("Numero del deck non valido",false);
            }
            else {
                CardDeck deck = cardDecks.get(DeckID);
                boolean spyable = deck.isSpyable();
                if (spyable) {
                    int colums = 3;
                    CardPrintUtils.printDeck(deck, colums);
                    DeckID++;
                    view.showGenericMessage("Deck  " + DeckID + " received successfully. ",false);
                    allowed = true;
                } else {
                    view.showGenericMessage("You are not allowed to spy on this deck!",false);
                }
            }


//
        } else {
            view.showGenericMessage("No card decks found.",false);
        }
        view.showBuildingMenu();
        return allowed;
    }

//case (c)


    public void handleFaceUpTileUpdate(FaceUpTileUpdate update) {

        ArrayList<Tile> faceUpTiles = update.getFaceUpTiles();
        synchronized (myModel.getFaceUpTiles()) {
            myModel.setFaceUpTiles(faceUpTiles);
        }
        view.handleFaceUpTilesUpdate();

    }

    public ArrayList<TimerInfo> getSynchTimerInfos(){

        AskTimerInfoRequest askTimerInfoRequest = new AskTimerInfoRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, askTimerInfoRequest.getID());
        final ArrayList<TimerInfo> timerInfos;

        if(!safeSendMessage(askTimerInfoRequest)) return null;
        try {
            TimerInfoResponse timerInfoResponse = (TimerInfoResponse) future.get();
            timerInfos = new ArrayList<>(timerInfoResponse.getTimerInfoList());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }



        return timerInfos;


    }


    //case(d) draw tile
    public void handleDrawFaceDownTile() {
        if(currentTileInHand!=null&&currentPosition!=null) {
            try {
                sendShipUpdate();
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        DrawTileRequest request = new DrawTileRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        if(!safeSendMessage(request)) return;

        new Thread(() -> {
            try {
                DrawTileResponse response = (DrawTileResponse) future.get();
                String error = response.getErrorMessage();

                switch (error) {
                    case "VALID" -> {

                        view.showTile(response.getTile());
                        ComponentNameVisitor visitor = new ComponentNameVisitor();
                        Component c = response.getTile().getMyComponent();
                        String name = c.accept(visitor);
                        int id = response.getTile().getId();
                        view.showGenericMessage("You drew a " + name + " tile." + "ID: " + id,false);
                        currentTileInHand = response.getTile();
                    }
                    case "EMPTY" -> view.showGenericMessage("The tile bunch is empty.",false);
                    case "INVALID_STATE" -> view.showGenericMessage("You cannot draw a tile right now.",false);
                    case null, default -> view.showGenericMessage("Unexpected response while drawing tile: " + error,false);
                }
                //Todo: MAI per la GUI ridisegnare tutto, quindi no chiamare così
                view.showBuildingMenu();

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    public void startChooseTile() {
        List<Tile> tiles = myModel.getFaceUpTiles();
        if (tiles == null || tiles.isEmpty()) {
            view.showGenericMessage("No face-up tiles available.",false);
            view.showBuildingMenu();
            return;
        }
        view.showGenericMessage("--Current face-up tiles--",false);

        view.showFaceUpTiles();
        view.askChooseTile();
    }

    public void handleChooseFaceUpTile(Tile tile) {

        DrawTileRequest request = new DrawTileRequest(tile);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        if(!safeSendMessage(request)) return;

        new Thread(() -> {

            try {
                DrawTileResponse response = (DrawTileResponse) future.get();
                String error = response.getErrorMessage();

                switch (error) {
                    case "VALID":
                        try {
                            sendShipUpdate();
                        } catch (IOException | ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Tile drawnTile = response.getTile();
                        if (drawnTile != null) {
                            view.showTile(drawnTile);
                            currentTileInHand = drawnTile;
                        }
                        break;
                    case "TAKEN":
                        view.showGenericMessage("The tile is chosen by another player.",false);
                        break;
                    case "INVALID_STATE":
                        view.showGenericMessage("You cannot draw a tile right now.",false);
                        break;
                    default:
                        view.showGenericMessage("Unexpected response while drawing tile(Face-Up): " + error,false);
                        break;
                }
            } catch (Exception e) {
                view.showGenericMessage("An error occurred while processing the response: " + e.getMessage(),false);

            }
            view.showBuildingMenu();
        }).start();


    }

    public void reclaimTile(){
        DrawTileRequest request = DrawTileRequest.reclaimLastTileRequest();
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());
        if(!safeSendMessage(request)) return;
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
                            currentPosition = null;
                        }
                        break;
                    case "FIXED":
                        view.showGenericMessage("The tile is fixed.",false);
                        break;
                    case "NO_TILE":
                        view.showGenericMessage("You don't have any reclaimable tile  ",false);
                }
            } catch (Exception e) {
                view.showGenericMessage("An error occurred while processing the response: " + e.getMessage(),false);

            }
            view.showBuildingMenu();
        }).start();

    }

public void handleDrawReservedTile (int slotIndex){
    DrawTileRequest request = DrawTileRequest.fromReservedSlot(slotIndex);
    CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
    setCompletableFuture(future, request.getID());
    if(!safeSendMessage(request)) return;

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
                        currentPosition = null;
                    }
                    break;
                case "NO_TILE_AT_INDEX":
                    view.showGenericMessage("no tile at index",false);
                    break;
            }
        } catch (Exception e) {
            view.showGenericMessage("An error occurred while processing the response: " + e.getMessage(),false);

        }
        view.showBuildingMenu();
    }).start();



}


    public void showTileInHand() {

        view.showTile(currentTileInHand);
        view.showGenericMessage("Tile in hand successfully.",false);
        view.showBuildingMenu();
    }

    // f
    public void rotateCurrentTile(int rotation) {
        currentTileInHand.rotate(rotation);
        view.showTile(currentTileInHand);
        view.showGenericMessage("Tile rotated successfully.",false);
        view.showBuildingMenu();

    }

//


    public void setTmpCurrentPosition(Position tmpCurrentPosition1) {
        this.tmpCurrentPosition = tmpCurrentPosition1;
    }

    public void resetCurrentPos() {
        currentPosition = null;
    }


    public void setCurrentPos(int x, int y) throws ExecutionException {
        Position pos = new Position(x, y);
        Ship ship = myModel.getMyInfo().getShip();

        if (!Util.inBoundaries(pos.getX(), pos.getY()) || ship.getInvalidPositions().contains(pos)) {
            Position toShowPosition = new Position(pos.getX()+4, pos.getY()+5);
            throw new IllegalArgumentException("Invalid Position" + toShowPosition);
        }

        currentPosition = pos;
    }

    // h
    public void handleTilePlacement() throws InvalidTilePosition {
        if ( currentTileInHand == null) {
            view.showGenericMessage("No tile selected.",false);
            view.showBuildingMenu();
            return;
        }
        //Todo Cambiare non deve fare modifica diretta e show diretto
//        myModel.getMyInfo().getShip().putTile(currentTileInHand, currentPosition);   //ship.putTile(tileInHand)
//        myModel.getMyInfo().getShip().setLastTile(currentTileInHand);               //ship.setLastTile
//        view.showShip(myModel.getMyInfo().getShip(),myModel.getMyInfo().getNickName());

        PlaceTileRequest request = new PlaceTileRequest(currentTileInHand, currentPosition);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if(!safeSendMessage(request)) return;

        new Thread(() -> {
            try {
                PlaceTileResponse response = (PlaceTileResponse) future.get();
                view.showGenericMessage(response.getMessage(),false);
                if(response.getMessage().equals("INVALID_STATE")){

                        currentPosition = tmpCurrentPosition;
                    view.showGenericMessage("You cannot place a tile right now.",false);
                }
                if(response.getMessage().equals("INVALID_POS")){

                    currentPosition = tmpCurrentPosition;

                    view.showGenericMessage("You cannot place a tile in that position. invalid pos",false);

                }
                if(response.getMessage().equals("OCCUPIED_POS")){

                        currentPosition = tmpCurrentPosition;

                    view.showGenericMessage("You cannot place a tile in that position. occupied pos",false);

                }
                if (response.getMessage().equals("VALID")) {

                    resetCurrentPos();
                    currentTileInHand = null;
                    isPlaced = true;
                    view.showTile(currentTileInHand);
                }


            } catch (Exception e) {
                view.showGenericMessage("Error during tile placement: " + e.getMessage(),false);
            } finally {

                view.showBuildingMenu();
            }

        }).start();

    }
    private void handlePlaceReservedTile(int slotIndex){
        if ( currentTileInHand == null) {
            view.showGenericMessage("No tile selected.",false);
            view.showBuildingMenu();
            return;
        }

        PlaceTileRequest request = new PlaceTileRequest(currentTileInHand, slotIndex);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getID());

        if(!safeSendMessage(request)) return;

        new Thread(() -> {
            try {
                PlaceTileResponse response = (PlaceTileResponse) future.get();
                view.showGenericMessage(response.getMessage(),false);

                if (response.getMessage().equals("VALID")) {

                    resetCurrentPos();
                    currentTileInHand = null;
                    isPlaced = true;
                    view.showTile(currentTileInHand);
                }
                else{
                    view.showGenericMessage("err PlaceReservedTile",false);
                }


            } catch (Exception e) {
                view.showGenericMessage("Error during tile placement: " + e.getMessage(),false);
            } finally {

                view.showBuildingMenu();
            }

        }).start();
    }


    //i
    public void sendDiscardRequest() {
        if (currentTileInHand == null) {
            view.showGenericMessage("No tile in hand to discard.",false);
            view.showBuildingMenu();

            return;
        }

        currentTileInHand.setRotation(0);
        DiscardTileRequest request = new DiscardTileRequest(currentTileInHand);

        if(!safeSendMessage(request)) return;
        currentTileInHand = null;
        currentPosition = null;
        view.showGenericMessage("Tile discarded successfully.",false);
        view.showBuildingMenu();
    }
    
    public void handlePickReservedTile(int slotIndex, boolean isPicking) {

//        Tile[] reservedTiles = myModel.getReservedTiles();
        Tile[] reservedTiles = getReservedTiles();

        Tile tile = reservedTiles[slotIndex];

        if (isPicking) {
            if (tile == null) {
                int toShowIndex =slotIndex+1;
                view.showGenericMessage("No reserved tile at slot " + toShowIndex + ".",false);
                view.showBuildingMenu();


            } else {
                handleDrawReservedTile (slotIndex);
//                currentTileInHand = tile;
//                myModel.getReservedTiles()[slotIndex] = null;
//                view.showGenericMessage("Tile picked successfully.");
//                view.showBuildingMenu();

            }
        }
        //placing
        else {
            if (tile == null) {
//                myModel.getReservedTiles()[slotIndex] = currentTileInHand;
//                currentTileInHand = null;
//                view.showGenericMessage("Tile reserved successfully.");
//                view.showBuildingMenu();
                handlePlaceReservedTile(slotIndex);

            } else {
                view.showGenericMessage("A tile is already reserved at slot " + slotIndex + ".",false);
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
        if(phase == GameState.FLIGHT && view.autoShowUpdates() == true){
            view.showFlightBoard(myModel.getFlightBoard(),myModel.getPlayerInfos(),myModel.getMyInfo());
        }

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
                case "menu", "m", "?" -> view.handleChoiceForPhase(phase);
                default -> {
                    view.showGenericMessage("Invalid option. Please try again.",false);
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

        if(!safeSendMessage(checkShipStatusRequest)) return;
        new Thread(() -> {
            try {
                CheckShipStatusResponse response = (CheckShipStatusResponse) future.get();
                boolean isValid = response.getIsValid();
                if (isValid) {
                    view.showWaitOtherPlayers();
                    view.showGenericMessage("Nave immacolata!",true);
                    return;

                } else {
                    view.showGenericMessage("La nave va ricontrollata",true);
                    myModel.getMyInfo().setShip(response.getShip());

                }
                view.showcheckShipMenu();

            } catch (ExecutionException | InterruptedException e) {
                view.showGenericMessage("Errore durante il controllo della nave",true);
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
                    view.showGenericMessage("Invalid option. Please try again.",false);
                    view.showembarkCrewMenu();
                }

            }

        }).start();
    }


    public void handleEndTurnUpdate(EndTurnUpdate update) {

        view.showGenericMessage("turn ended",false);
        if(update.isEndGame()){
            view.showGenericMessage("Game ended",false);
            return;
        }
        view.toShowCurrentMenu();
        if(myModel.getPlayerState() != PlayerState.Spectating){
            view.showGenericMessage("Il turno e' finito !", true);
            view.showFlightMenu();
        }
        else{
            handleReadyTurnRequest();
        }
    }

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
                        view.showGenericMessage("Invalid option. Please try again.",false);
                        view.handleChoiceForPhase(phase);
                    }
                }

        }).start();
    }

    public void handleEarlyLandingRequest() {
        EarlyLandingRequest request = new EarlyLandingRequest();
        safeSendMessage(request);
        view.showGenericMessage("Hai scelto l’atterraggio anticipato, ora guarda gli altri giocatori.",false);
        view.showYouAreNowSpectating();

    }
    public void handleReadyTurnRequest()  {
        ReadyTurnRequest request = new ReadyTurnRequest();
        safeSendMessage(request);
        view.showGenericMessage(" Devi aspettare che gli altri giocatori siano pronti.",false);

    }



    public void handleGameMessage(GameMessage gameMessage) {

        view.showGenericMessage(gameMessage.getMessage(),false);
    }

    public void handleMatchInfoUpdate(MatchInfoUpdate matchInfoUpdate) {

        int remainCards = matchInfoUpdate.getRemainingCards();
        String leaderNickname = matchInfoUpdate.getLeaderNickname();


        boolean amLeader = leaderNickname.equals(getNickname());
        myModel.setLeader(amLeader);
        view.showGenericMessage("Il giocatore: " + leaderNickname + " è il leader, rimangono: " + remainCards + "  carte.",false);
        if (amLeader) {
            view.askDrawCard();
        } else {
            view.showGenericMessage("Non sei leader per questo turno. Devi aspettare che il leader peschi la carta.",false);

        }

    }

    public void sendDrawAdventureCardRequest() {
        DrawAdventureCardRequest request = new DrawAdventureCardRequest();
        safeSendMessage(request);
    }

    public void handleDrawnAdventureCardUpdate(DrawnAdventureCardUpdate drawnAdventureCardUpdate) {
        view.forceReset();
        myModel.setCurrentAdventureCard(drawnAdventureCardUpdate.getCard());
        view.showCurrentAdventureCard();
    }

    public void handleActivateAdventureCardRequest(ActivateAdventureCardRequest ignoredActivateAdventureCardRequest) {
        view.askActivateAdventureCard();
    }

    public void sendActivateAdventureCardResponse(boolean confirm) {
        ActivateAdventureCardResponse response = new ActivateAdventureCardResponse(confirm);
        if(!safeSendMessage(response)) return;

        if (confirm && "AbandonedStation".equals(getCurrentAdventureCard().getName())){
            AbandonedStation abandonedStation = (AbandonedStation)  getCurrentAdventureCard();
            myModel.setUnplacedGoods(abandonedStation.getGoods());
            view.askLoadGoodChoice();
        }

    }

    //planet
    //Select Planet
    public void handleSelectPlanetRequest(SelectPlanetRequest request) {
        HashMap<Integer, Planet> landablePlanets = request.getLandablePlanets();
        view.askSelectPlanetChoice(landablePlanets);
    }
// Dopo aver ricevuto una SelectPlanetRequest, chiedo al giocatore quale pianeta vuole scegliere.
// Quando il giocatore ha fatto la sua scelta, devo inviare sia il pianeta selezionato sia l'indice corrispondente,
// in modo che, quando ricevo un SelectPlanetUpdate, possa notificare a tutti i giocatori
// quale giocatore ha scelto quale pianeta (usando l'indice del pianeta).

    public void sendSelectPlanetResponse(Planet planet, int planetIndex) {
        SelectPlanetResponse response = new SelectPlanetResponse(planet, planetIndex);
        safeSendMessage(response);
    }

    //Notifica: il giocatore (nome del giocatore) ha selezionato il pianeta x
    //Poi se selectingPlayerNickname uguale a mio nickname, va a chiedere all'utente come
    // vuole mettere i goods nella sua ship
    public void handleSelectPlanetUpdate(SelectedPlanetUpdate update) {
        String selectingPlayerNickname = update.getSelectingPlayerNickname();
        view.showGenericMessage("Player " + selectingPlayerNickname + " ha selezionato il pianeta " + update.getPlanetIndex(),false);
        if (selectingPlayerNickname.equals(getNickname())) {
            Planet selectedPlanet = update.getSelectedPlanet();
            myModel.setSelectedPlanet(selectedPlanet);
            myModel.setUnplacedGoods(selectedPlanet.getGoods());
        }
    }

    /**
     * Handles the choice for the loading goods phase, and tells the View what to show
     * @param input f
     */
    public void handleLoadGoodChoice(String input) {
        if (input == null) return;

        switch (input.toLowerCase()) {
            case "l" -> view.askSelectGoodToLoad(myModel.getUnplacedGoods(), myModel.getMyInfo().getShip());
            case "d" -> view.askSelectGoodToDiscard( myModel.getMyInfo().getShip());
            case "f" -> {
                view.showGenericMessage(" Caricamento merci completato.",false);
                try {
                    sendShipForGoodUpdate();
                } catch (Exception e) {
                    view.showGenericMessage("Errore durante l'invio della nave: " + e.getMessage(),false);
                }
            }
            default -> view.showGenericMessage(" Comando non riconosciuto. Usa L, D o F.",false);
        }
    }


    @NeedsToBeChecked("non modificare model in locale, creare una TempShip ")
    public void placeMerci(int goodIndex, Good good, Position pos) {
        Ship ship = myModel.getMyInfo().getShip();
        Slot slot = ship.getShipBoard()[pos.getX()][pos.getY()];
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.playerLoadGood(good);
        myModel.getSelectedPlanet().getGoods().remove(goodIndex);
    }


    public void sendShipForGoodUpdate() {
        ShipUpdate update = new ShipUpdate(myModel.getMyInfo().getShip(), myModel.getMyInfo().getNickName());
        safeSendMessage(update);
    }

    public ArrayList<Good> getDiscardPositionGoods(Position pos) {
        Ship ship = getMyShip();
        Slot slot = getSlot(ship, pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        return hold.getGoods();
    }

    public void discardGood(int GoodIndex, Position pos) {
        Slot slot = getSlot(getMyShip(), pos);
        GenericCargoHolds hold = (GenericCargoHolds) slot.getTile().getMyComponent();
        hold.removeGood(hold.getGoods().get(GoodIndex));
    }

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
            handleActivateComponentResponse(component,null,null);
        }
        else {
            //Invoca metodo della view fare scegliere al giocatore i componenti da attivare e le batterie da usare
            try {
                view.chooseComponent(myModel.getMyInfo().getShip(), component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleActivateComponentResponse(ActivatableComponent component, ArrayList<Position> componentPos, ArrayList<Position> battPos) {
        //Tornare lista di componenti e batterie

        //Inviare la response
        ActivateComponentResponse resp = new ActivateComponentResponse(component, componentPos, battPos);
        safeSendMessage(resp);
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
        safeSendMessage(resp);
    }
    
    @NeedsToBeCompleted
    public void handlePlayerKickedUpdate(PlayerKickedUpdate playerKickedUpdate) {
        if (playerKickedUpdate.getNickname().equals(this.getNickname())) {
            view.showGenericMessage("You've been kicked from the game!",false);
        } else {
            view.showGenericMessage(playerKickedUpdate.getNickname() + " got kicked out of the game!",false);
        }

        view.showGenericMessage("As " + playerKickedUpdate.getNickname() + " left the game, the game has ended prematurely and you'll have to start over.",false);
        backToMainMenu();
    }

    private void backToMainMenu(){
        view.askJoinOrCreateRoom();
        view.showGenericMessage("",false);
        PlayerInfo myInfo = new PlayerInfo();
        myInfo.setNickName(myModel.getMyInfo().getNickName());
        myModel = new ClientModel();
        myModel.setMyInfo(myInfo);

        phase = GameState.LOBBY;
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
        safeSendMessage(response);
    }

    public void sendCollectRewardsResponse(boolean confirm) {
       CollectRewardsResponse response = new CollectRewardsResponse(confirm);
       if(!safeSendMessage(response)) return;

       if (confirm && "Contrabbandieri".equals(getCurrentAdventureCard().getName())) {
            Smugglers smugglers = (Smugglers)  getCurrentAdventureCard();
            myModel.setUnplacedGoods(smugglers.getGoods());
            view.askLoadGoodChoice();
       }
    }

    public void handleCollectRewardsRequest(CollectRewardsRequest ignoredrequest){
        view.askCollectRewards();

    }
    public AdventureCard getCurrentAdventureCard() {
        return myModel.getCurrentAdventureCard();
    }

    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate) throws IOException, ExecutionException, InterruptedException {
        safeSendMessage(crewInitUpdate);
    }


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



    public void handleGameEndUpdate(GameEndUpdate update) {
        ArrayList<PlayerScore> scores = update.getScores();
        view.showEndGame(scores);
    }


    public void handlePlayerLostUpdate(PlayerLostUpdate update) {
        String nickname = update.getNickname();

        if (nickname.equals(getNickname())) {
            myModel.setPlayerState(PlayerState.Spectating);
            //Todo comunicare bene a giocatore che rimosso
            view.showYouAreNowSpectating();
        }

        String message = nickname + " ha perso: ";
        switch (update.getReason()){
            case PlayerLostReason.Quit -> message += "ha deciso di atterrare in anticipo.";
            case PlayerLostReason.NoCrewMembersLeft -> message += "non aveva più membri dell'equipaggio a disposizione.";
            case PlayerLostReason.Lapped -> message += "è stato doppiato.";
            case PlayerLostReason.ZeroEnginePower -> message += "non aveva potenza motrice.";
            default -> message += "le ragioni rimangono tutt'ora ignote.";
        }

        view.showGenericMessage(message, true);
    }

    public Ship getMyShip() {
        return myModel.getMyInfo().getShip();
    }

    private Slot getSlot(Ship ship, Position pos) {
        return ship.getShipBoard()[pos.getX()][pos.getY()];
    }

    private ArrayList<Position> getCargoHolds(Ship ship) {
        return ship.getComponentPositionsFromName("GenericCargoHolds");
    }

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
    
    //Todo : eliminare
    public Tile[] getReservedTiles() {
        return myModel.getReservedTiles();
    }

    //update per generic message
    public void completeFuture(NetworkMessage message) {
        int responseId = message.getID();
        if (completableFuture == null) {
            view.showGenericMessage("⚠️ CompletableFuture is null when receiving response with ID: " + responseId,false);
            return;
        }

        if (!pair.getKey().equals(responseId)) {
            view.showGenericMessage("⚠️ ID mismatch! Expected: " + pair.getKey() + ", but got: " + responseId,false);
            return;
        }
        if (completableFuture != null && pair.getKey().equals(message.getID())) {
            completableFuture.complete(message);
            completableFuture = null;
        } else {
            System.err.println("⚠️ Cannot complete future: ID mismatch or future was null");

        }
    }

    public void sendFlipRequest(ArrayList<TimerInfo> timerInfos) {
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
        safeSendMessage(flipTimerRequest);
    }

    public boolean safeSendMessage(NetworkMessage message) {
        if(!isConnectionAlive.get()) return false;

        try {
            client.sendMessage(message);
            return true;
        } catch (IOException e){
            if(isConnectionAlive.getAndSet(false)) {
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
