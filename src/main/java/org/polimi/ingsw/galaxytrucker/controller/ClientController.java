package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Observer {

    private  Client client;
    ClientPhaseController clientPhaseController;
    private final ExecutorService taskQueue;
    private View view;
    ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    private final CompletableFuture<Void> nicknameAsked = new CompletableFuture<>();
    private final ExecutorService viewExecutor = Executors.newSingleThreadExecutor();

    private  Boolean isHost = false;
    private Boolean isSocket = false;


    public ClientController(View view, Boolean flag) {
        this.isSocket = flag;
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
        if (message.accept(new ComponentNameVisitor()).equals("SERVER_INFO")) {

            int port = ((SERVER_INFO) message).getPort();
            String address = ((SERVER_INFO) message).getAddress();

            if (isSocket) {
                client = new ClientSocket(address, port);
                ((ClientSocket) client).create(address, port);
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
            } else { //sto in RMI


                client = new ClientRMI(port, this);

                clientPhaseController.nextPhase();
                new Thread(() -> {
                    try {
                        view.askNickname();
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            }


        }

        if (message.accept(new ComponentNameVisitor()).equals("LOBBY_INFO")) {

            LOBBY_INFO mess = (LOBBY_INFO) message;

            nicknameAsked.thenRunAsync(() -> {
                if (mess.getIsFirst()) {
                    isHost = true;
                    try {
                        view.askMaxPlayers(); // viene eseguito solo dopo che askNickname è finito
                        clientPhaseController.nextPhase();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
//                System.out.println(clientPhaseController.getPhase());
            }, viewExecutor); // eseguiamo in un thread separato
        }



        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_REQUEST")) {
//            System.out.println("[+]NICKNAME_REQUEST SENT : SENT : " + ((NICKNAME_REQUEST)message).getNickname());
            client.sendMessage(message);
        }

        if (message.accept(new ComponentNameVisitor()).equals("NUM_PLAYERS_REQUEST")) {
//            System.out.println("[+]NICKNAME_REQUEST SENT : SENT : " + ((NICKNAME_REQUEST)message).getNickname());
            client.sendMessage(message);
        }

        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_RESPONSE")) {
            NICKNAME_RESPONSE nicknameResponse = (NICKNAME_RESPONSE) message;
            if (nicknameResponse.getResponse().equals("VALID")){
//                System.out.println("[+]VALID!");
                clientPhaseController.nextPhase();
//                new Thread(() -> {
//                    view.showGenericMessage("NICKNAME VALID");
//                }).start();

            } else {

                System.out.println(TuiColor.RED + "INVALID NICKNAME!" + TuiColor.RESET);
                new Thread(() -> {
                    try {
                        view.askNickname();
                        nicknameAsked.complete(null);// Segnalo che ho finito
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
