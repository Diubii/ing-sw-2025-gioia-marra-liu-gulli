package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.io.Serial;
import java.io.Serializable;

public class JoiniRoomOptionsRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 69L;

    public void accept(ServerController serverController, ClientHandler clientHandler) {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }
}
