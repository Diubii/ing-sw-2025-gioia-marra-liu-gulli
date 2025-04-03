package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.PLAYER_PHASE;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.LOBBY_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Observer {

    private  Client client;
    ClientPhaseController clientPhaseController;
    private final ExecutorService taskQueue;
    private View view;
    ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    private  Boolean isFirst;


    public ClientController(View view) {
        this.view = view;
        clientPhaseController = new ClientPhaseController(this);
        clientPhaseController.nextPhase();
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
    public void update(NetworkMessage message) throws IOException, ExecutionException {
        //da aggiungere l'if per RMI: uguale
//        System.out.println(message.accept(new ComponentNameVisitor()));
        if (message.accept(new ComponentNameVisitor()).equals("SERVER_INFO")) {
//            System.out.println("[+]SERVER_INFO SENT");
            client = new ClientSocket(((SERVER_INFO)message).getAddress(),((SERVER_INFO)message).getPort());
            ((ClientSocket)client).create(((SERVER_INFO)message).getAddress(),((SERVER_INFO)message).getPort() );
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket) client).receiveMessage();
            clientPhaseController.nextPhase();


            new Thread(() -> {
                try {
                    view.askNickname();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();


        }

        if (message.accept(new ComponentNameVisitor()).equals("LOBBY_INFO") && clientPhaseController.getPhase().equals(PLAYER_PHASE.NICKNAME_REQUEST)) {

            LOBBY_INFO mess = (LOBBY_INFO) message;
            if (mess.getIsFirst()){
                new Thread(() -> {
                    try {
                        view.askMaxPlayers();
                    } catch (ExecutionException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

        }


        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_REQUEST")) {
//            System.out.println("[+]NICKNAME_REQUEST SENT : SENT : " + ((NICKNAME_REQUEST)message).getNickname());
            client.sendMessage(message);
        }

        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_RESPONSE")) {
            NICKNAME_RESPONSE nicknameResponse = (NICKNAME_RESPONSE) message;
            if (nicknameResponse.getResponse().equals("VALID")){
//                System.out.println("[+]VALID!");
                clientPhaseController.nextPhase();
                new Thread(() -> {
                    view.showGenericMessage("NICKNAME VALID");
                }).start();

            }
        }
    }


    //update per generic message

    @Override
    public void update(String message){

        System.out.println("[+]"+message);
    }

    public Client getClient() {
        return client;
    }

    public void setView(View v){
        this.view = v;
    }


}
