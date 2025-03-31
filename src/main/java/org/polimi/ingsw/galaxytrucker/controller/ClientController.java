package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Observer {

    private  Client client;
    ClientPhaseController clientPhaseController;
    private final ExecutorService taskQueue;
    private View view;


    public ClientController(View view) {
        this.view = view;
        clientPhaseController = new ClientPhaseController(this);
        taskQueue = Executors.newSingleThreadExecutor();
    }





    @Override
    public void update(NetworkMessage message) throws IOException {
        //da aggiungere l'if per RMI: uguale
        System.out.println(message.accept(new ComponentNameVisitor()));
        if (message.accept(new ComponentNameVisitor()).equals("SERVER_INFO")) {
            System.out.println("SERVER_INFO SENT");
            client = new ClientSocket(((SERVER_INFO)message).getAddress(),((SERVER_INFO)message).getPort());
            ((ClientSocket)client).create(((SERVER_INFO)message).getAddress(),((SERVER_INFO)message).getPort() );
            ((ClientSocket) client).addObserver(this);
            ((ClientSocket) client).receiveMessage();

        }


        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_REQUEST")) {
            client.sendMessage(message);
        }

        if (message.accept(new ComponentNameVisitor()).equals("NICKNAME_RESPONSE")) {
            NICKNAME_RESPONSE nicknameResponse = (NICKNAME_RESPONSE) message;
            if (nicknameResponse.getResponse().equals("VALID")){
                clientPhaseController.handle();

            }
        }
    }

    public Client getClient() {
        return client;
    }


}
