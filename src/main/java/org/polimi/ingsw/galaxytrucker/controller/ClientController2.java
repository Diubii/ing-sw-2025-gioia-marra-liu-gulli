package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DrawTileResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomResponse;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
//import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PlayerJoinedUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.TileDrawnUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.View2;
import org.polimi.ingsw.galaxytrucker.visitors.ClientNetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController2 implements Observer {


    ClientNetworkMessageVisitor messageVisitor = new ClientNetworkMessageVisitor(this);

    private final Map<Integer, CompletableFuture<NetworkMessage>> futureMap = new HashMap<>();
    private Client client;
    private final ExecutorService taskQueue;
    private View2 view;
    ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    private final CompletableFuture<Void> nicknameAsked = new CompletableFuture<>();
    private final ExecutorService viewExecutor = Executors.newSingleThreadExecutor();

    private Boolean isHost = false;


    private Boolean isSocket = false;
    private Position currentTilePosition;
    private CompletableFuture<NetworkMessage> completableFuture;



    private String nickname = "";

    private Tile currentTileInHand = null;
    private Ship currentShip = null;

    private  List<AdventureCard> deck2;
    private  List<AdventureCard> deck3;
    private  List<AdventureCard> deck4;
    private Pair<Integer, CompletableFuture<NetworkMessage>> pair;


    public ClientController2(View2 view, Boolean flag) {
        this.isSocket = flag;
        this.view = view;
//        clientPhaseController = new ClientPhaseController(this);
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


    // Metodo update(NetworkMessage) con completamento delle future
    public void update(NetworkMessage message) throws IOException, InvalidTilePosition, TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
//        int id = message.getId();
//
//        CompletableFuture<NetworkMessage> future = futureMap.remove(id);
//        if (future != null) {
//            future.complete(message);
//            return;
//        }


        
        if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.JoinRoomResponse)){
            NicknameResponse nicknameResponse = (NicknameResponse) message;


            if (completableFuture != null && pair.getKey().equals(message.getId())) {
                completableFuture.complete(nicknameResponse);
                completableFuture = null;
                new Thread(() -> {
                    view.showGenericMessage("RESPONS ID: " + message.getId());
                }).start();

                return;

            }
        }



        // Gestione diretta se non è una risposta attesa
        if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.ServerInfo)) {
            handleServerInfo((SERVER_INFO) message);
        } else {
            try {
                message.accept(messageVisitor);
            } catch (Exception e) {
                System.err.println("[Error] The message was not processed correctly: " + e.getMessage());
            }
        }

        if (message.accept(new NetworkMessageNameVisitor()).equals(NetworkMessageType.NicknameResponse)){
            handleNicknameResponse((NicknameResponse) message);

        }



    }

    // Metodo di debug per sapere se una response è già stata gestita
//    public boolean solvedByFuture(int id) {
//        boolean solved = !futureMap.containsKey(id);
//        System.out.println("[FUTURE] Message ID " + id + " already handled? " + solved);
//        return solved;
//    }



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


    public void handleServerInfo(SERVER_INFO info) throws IOException {

        try {
            connectToServer(info.getAddress(), info.getPort());
        } catch (IOException e) {


            //view.showGenericMessage(" Failed to connect to server: " + e.getMessage());
            // si traduce in

            new Thread(() -> {

                view.showGenericMessage(" Failed to connect to server: " + e.getMessage());
            });
            return;
        }
        try {
            view.askNickname();
        } catch (Exception e) {
            System.err.println("Errore nell'invio del nickname: " + e.getMessage());
        }

    }
    //update per generic message


    public void handleNicknameResponse(NicknameResponse message) {

        NicknameResponse nicknameResponse = (NicknameResponse) message;


        if (completableFuture != null && pair.getKey().equals(message.getId())) {
            completableFuture.complete(nicknameResponse);
            completableFuture = null;

        }
        if ("VALID".equals(message.getResponse())) {
            new Thread(() -> {


                view.showGenericMessage(" Nickname accepted!");

                view.askJoinOrCreateRoom();
            });

        } else {
            new Thread(() -> {
                view.showGenericMessage(" Nickname already in use. Please try another one.");
                try {
                    view.askNickname();
                } catch (Exception e) {
                    System.err.println("Errore nel reinserire il nickname: " + e.getMessage());
                }
            }).start();
        }
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void handleJoinRoomOptionsResponse(JoinRoomOptionsResponse response) {
        List<LobbyInfo> lobbies = response.getLobbyInfos();
        view.showLobbies(lobbies);

        if(lobbies.isEmpty()) {
            view.askJoinOrCreateRoom();
        }
        else{
            view.askRoomCode();
        }
    }


    public void handleJoinRoomResponse(JoinRoomResponse response) {
        boolean success = response.getOperationSuccess();
        String errMessage = response.getErrMess();
        if (!success) {
            view.showGenericMessage(errMessage);
        }
        else{
            view.showGenericMessage("Joined room successfully! Waiting for other players...");
        }

    }

    public void handlePlayerJoinedUpdate(PlayerJoinedUpdate playerJoinedUpdate){
        HashMap<String, Color>playerInfo = playerJoinedUpdate.getPlayerInfo();
        view.showPlayerJoined(playerInfo);

    }

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate){

        view.handlePhaseUpdate(phaseUpdate);

    }


    public void handleDrawTileResponse(DrawTileResponse response) {
        int tileID = response.getTile().getId();
        ComponentNameVisitor visitor = new ComponentNameVisitor();
        String tileType =visitor.visit(response.getTile().getMyComponent());

    }


    public void handleTileDrawnUpdate(TileDrawnUpdate update) {
        int tileID = update.getTileId();
        view.showTileTaken(tileID);
    }
    public void handleIfNotHandled(NetworkMessage msg) {

        view.showGenericMessage(" Error: No response ");
    }


@NeedsToBeCompleted
//    public void handleDecksUpdate(DecksUpdate update) {
//        deck2 = update.getDeck2();
//        deck3 = update.getDeck3();
//        deck4 = update.getDeck4();
//    }

    @Override
    public void update(String message) {

        System.out.println("[+]" + message);
    }
    public Tile getCurrentTileInHand() {
        return currentTileInHand;
    }
    public Client getClient() {
        return client;
    }

    // Metodo expectResponse corretto e generico
//    public <T extends NetworkMessage> CompletableFuture<T> expectResponse(int messageId) {
//        CompletableFuture<T> future = new CompletableFuture<>();
//        // Cast sicuro tramite raw type con soppressione
//        @SuppressWarnings("unchecked")
//        CompletableFuture<T> castedFuture =  future;
//        futureMap.put(messageId, (CompletableFuture<NetworkMessage>) castedFuture);
//        return future;
//    }


    public void setView(View2 v) {
        this.view = v;
    }

    public void setCurrentTilePosition(Position currentTilePosition) {
        this.currentTilePosition = currentTilePosition;
    }


    public String getNickname() {
        return this.nickname;
    }

    public Position getCurrentTilePosition() {
        return currentTilePosition;
    }

    public View2 getView() {
        return view;
    }
    public List<AdventureCard> getDeck4() {
        return deck4;
    }

    public List<AdventureCard> getDeck2() {
        return deck2;
    }

    public List<AdventureCard> getDeck3() {
        return deck3;
    }

    public Ship getCurrentShip() {
        return currentShip;
    }

    public void setCurrentTileInHand(Tile currentTileInHand) {
        this.currentTileInHand = currentTileInHand;
    }

    public void setCurrentShip(Ship currentShip) {
        this.currentShip = currentShip;
    }


    public CompletableFuture<NetworkMessage> getCompletableFuture(){
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<NetworkMessage> completableFuture, int id){
        this.completableFuture = completableFuture;
        this.pair = new Pair<>(id, completableFuture);
    }

    public void completeFuture(NetworkMessage message){
        if (completableFuture != null) {
            completableFuture.complete(message);
        }
    }

    public CompletableFuture<NetworkMessage> expectResponse(int id) {
        return null;
    }
}