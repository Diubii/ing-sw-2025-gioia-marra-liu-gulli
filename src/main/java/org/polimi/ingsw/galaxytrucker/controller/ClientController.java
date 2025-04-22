package org.polimi.ingsw.galaxytrucker.controller;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.JoinRoomRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomResponse;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageNameVisitor;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Observer {

    private Client client;
    ClientPhaseController clientPhaseController;
    private final ExecutorService taskQueue;
    private View view;
    ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    private final CompletableFuture<Void> nicknameAsked = new CompletableFuture<>();
    private final ExecutorService viewExecutor = Executors.newSingleThreadExecutor();

    private CompletableFuture<NetworkMessage> completableFuture;
    private Pair<Integer, CompletableFuture<NetworkMessage>> pair;

    private ClientModel myModel = new ClientModel();


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

    public void completeFuture(NetworkMessage message){
        if (completableFuture != null) {
            completableFuture.complete(message);
        }
    }

    private Boolean isHost = false;
    private Boolean isSocket = false;

    public ClientController(View view, Boolean flag) {

        this.myModel = new ClientModel();
        this.isSocket = flag;
        this.view = view;
        clientPhaseController = new ClientPhaseController(this);
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
//        da aggiungere l'if per RMI: uguale
        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.ServerInfo)) {

        int port = ((SERVER_INFO) message).getPort();
        String address = ((SERVER_INFO) message).getAddress();

        if (isSocket) {
            client = new ClientSocket(address, port);
            ((ClientSocket) client).create(address, port);
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket) client).receiveMessage();
//            clientPhaseController.nextPhase();


            new Thread(() -> {
                try {
                    view.askNickname();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else { //sto in RMI


            client = new ClientRMI(port, this);

//            clientPhaseController.nextPhase();
            new Thread(() -> {
                try {
                    view.askNickname();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }


        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.NicknameResponse)){

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

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.NicknameRequest)){

            new Thread(() -> {
                view.showGenericMessage("ID: REQ -> " + message.getId());
            }).start();
            client.sendMessage(message);
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.JoinRoomOptionsRequest)){
            client.sendMessage(message);
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.CreateRoomRequest)){
            client.sendMessage(message);
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.JoinRoomOptionsResponse)){

            JoinRoomOptionsResponse joinRoomOptionsResponse = (JoinRoomOptionsResponse) message;

            if (completableFuture != null && pair.getKey().equals(message.getId())) {
                completableFuture.complete(joinRoomOptionsResponse);
                completableFuture = null;
                new Thread(() -> {
                    view.showGenericMessage("ID: RESP -> " + joinRoomOptionsResponse.getId());
                }).start();

                return;

            }
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.JoinRoomRequest)){
            client.sendMessage(message);
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.JoinRoomResponse)){

            JoinRoomResponse joinRoomResponse = (JoinRoomResponse) message;

            if (completableFuture != null && pair.getKey().equals(message.getId())) {
                completableFuture.complete(joinRoomResponse);
                completableFuture = null;
                new Thread(() -> {
                    view.showGenericMessage("RESPONS ID: " + message.getId());
                }).start();

                return;

            }
        }

        if (message.accept( new NetworkMessageNameVisitor()).equals(NetworkMessageType.PhaseUpdate)){

            clientPhaseController.handlePhaseUpdate((PhaseUpdate) message);
        }


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
}
