package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

public class SERVER_INFO extends NetworkMessage {

    private final String address;
    private final int port;


    public SERVER_INFO(String address, int port) {
        super();
        this.address = address;
        this.port = port;

    }
    public String getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) {
        NetworkMessageVisitor.visit(this, serverController, clientHandler);
    }



}
