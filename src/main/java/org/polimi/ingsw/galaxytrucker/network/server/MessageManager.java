package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
