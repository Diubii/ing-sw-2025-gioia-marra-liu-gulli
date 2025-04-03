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

    private  ClientSocket clientSocket;
    ClientPhaseController clientPhaseController;
    private View view;

    public ClientController(View view) {
        this.view = view;
        clientPhaseController = new ClientPhaseController(this);
    }

    @Override
    public void update(NetworkMessage message) throws IOException {

    }

    public ClientSocket getClientSocket() {
        return clientSocket;
    }
}
