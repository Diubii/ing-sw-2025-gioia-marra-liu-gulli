package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

public class NicknameResponse extends NetworkMessage {
    private String response;
    public NicknameResponse(String response) {
        super();
        this.response = response;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
