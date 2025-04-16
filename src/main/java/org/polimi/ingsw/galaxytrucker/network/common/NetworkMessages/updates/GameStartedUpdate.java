package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serializable;

public class GameStartedUpdate extends NetworkMessage implements Serializable {
    @Override
    public void accept(ServerController serverController, ClientHandler clientHandler) {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }
}
