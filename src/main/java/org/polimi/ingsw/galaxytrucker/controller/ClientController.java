package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
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
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PlayerJoinedUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
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

    private ClientModel myModel = new ClientModel();
    private final NetworkMessageVisitorsInterface<Void> messageVisitor = new ClientNetworkMessageVisitor(this);
    private Tile currentTileInHand;
    private Ship currentShip;
    private Position currentPosition;

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
        setCompletableFuture(future, request.getId());

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

    public void handleCreateOrJoinChoice(String choice) {
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
        setCompletableFuture(future, request.getId());

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
        setCompletableFuture(future, request.getId());

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
        setCompletableFuture(future, request.getId());
        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send join room request.");
            return;
        }

        new Thread(() -> {
            try {
                JoinRoomResponse response = (JoinRoomResponse) future.get();
                if (response.getOperationSuccess()) {
                    view.showGenericMessage("Successfully joined the lobby! Waiting for other players...");
                } else {
                    view.showGenericMessage("Failed to join the lobby: " + response.getErrMess());
                    view.askRoomCode();
                }
            } catch (Exception e) {
                view.showGenericMessage("Error waiting for join room response: " + e.getMessage());
                view.askRoomCode();
            }
        }).start();
    }






    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate){
                HashMap<String, Color> playerInfo = playerJoinedUpdate.getPlayerInfo();
                view.showPlayerJoined(playerInfo);

    }

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate){

        view.handlePhaseUpdate(phaseUpdate);

    }

    public void handleBuildingMenuChoice(String input) {
        new Thread(() -> {

            switch (input) {
                case "a" -> view.askFetchShip();
//            case "b" -> viewAdventureDecks();
//            case "c" -> client.sendMessage(new ShowFaceUpTilesRequest());
              case "d" -> view.askDrawTile();
              case "e" -> showTileInHand();
              case "f" -> view.askRotation();
              case "g" -> view.askPosition();
              case "h" -> view.askTilePlacement();
//            case "i" -> client.sendMessage(new DiscardTileRequest(currentTileInHand));
//            case "j" -> client.sendMessage(new FinishBuildingRequest(currentShip, currentTileInHand));
                default -> {
                    new Thread(() -> {
                    view.showGenericMessage("Invalid option. Please try again.");
                    view.showBuildingMenu();}).start();
                }
            }
        }).start();
    }

    public void handleFetchShip(String targetNickname){

    FetchShipRequest request = new FetchShipRequest(targetNickname);
    try {
        client.sendMessage(request);
        view.showGenericMessage("Request sent. Waiting for server...");
    } catch (Exception e) {
        view.showGenericMessage("Failed to send request: " + e.getMessage());
    }
    }

    @NeedsToBeCompleted
    // metodo nel view o altri class per stampare ship
    public void handleShipUpdate(ShipUpdate update){
            String owner = update.getNickName();
            Ship ship = update.getShipView();
     if(owner != null) {
         view.showGenericMessage("Ship belongs to: " + owner);
//            view.showShip(ship);
     }
     else{
         view.showGenericMessage("No ship belongs to this player.");
     }

    }


    public void handleTilePlacement(Boolean confirm){
        if (currentPosition == null || currentTileInHand == null) {
            view.showGenericMessage("No tile or position selected.");
            return;
        }

        PlaceTileRequest request = new PlaceTileRequest(currentTileInHand, currentPosition);
        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        setCompletableFuture(future, request.getId());

        try {
            client.sendMessage(request);
        } catch (IOException | ExecutionException | InterruptedException e) {
            view.showGenericMessage("Failed to send tile placement: " + e.getMessage());
            return;
        }

        new Thread(() -> {
            try {
                PlaceTileResponse response = (PlaceTileResponse) future.get();
                view.showGenericMessage(response.getMessage());
            } catch (Exception e) {
                view.showGenericMessage("Error during tile placement: " + e.getMessage());
            }
        }).start();

    }
    public void completeFuture(NetworkMessage message) {
        int responseId = message.getId();
        if (completableFuture == null) {
            view.showGenericMessage("⚠️ CompletableFuture is null when receiving JoinRoomResponse with ID: " + responseId);
            return;
        }

        if (!pair.getKey().equals(responseId)) {
            view.showGenericMessage("⚠️ ID mismatch! Expected: " + pair.getKey() + ", but got: " + responseId);
            return;
        }
        if (completableFuture != null && pair.getKey().equals(message.getId())) {
            completableFuture.complete(message);
            completableFuture = null;
        } else {
            System.err.println("⚠️ Cannot complete future: ID mismatch or future was null");

        }
    }
    public void showTileInHand() {
        view.showTile(currentTileInHand);
    }
    public void rotateCurrentTile(int rotation) {
        currentTileInHand.rotate(rotation);

    }

    public void moveCurrentTile(int x, int y) {
        Position pos = new Position(x, y);
        if(currentShip.getInvalidPositions().contains(pos)||currentShip.getShipBoard()[pos.getY()][pos.getX()].getTile() != null) {
          new Thread(() -> {
              view.showGenericMessage("Invalid position. Please try again.");
              view.askPosition();
          }).start();
        }
        else{
            currentPosition = pos;
        }

    }
    public void moveTile() {

    }

    //update per generic message

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
    private Tile getCurrentTileInHand(){
        return this.currentTileInHand;
    }

    public boolean hasTileInHand() {
        return this.getCurrentTileInHand() != null;
    }
}
