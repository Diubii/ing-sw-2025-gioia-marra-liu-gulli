package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

public class MessageManager {

    private final ServerController serverController;


    public MessageManager(ServerController serverController) {
        this.serverController = serverController;
    }
    //logica

    public void handle(NetworkMessage message, ClientHandler clientHandler) {
        NetworkMessageVisitor nmv = new NetworkMessageVisitor(serverController, clientHandler);
        message.accept(nmv);
    }
}
