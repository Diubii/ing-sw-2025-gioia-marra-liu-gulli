package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitor;

import java.util.concurrent.ExecutionException;

public class MessageManager {

    private final ServerController serverController;


    public MessageManager(ServerController serverController) {
        this.serverController = serverController;
    }
    //logica

    public void handle(NetworkMessage message, ClientHandler clientHandler) throws ExecutionException, InterruptedException {
        NetworkMessageVisitor nmv = new NetworkMessageVisitor(serverController, clientHandler);
        try {
            message.accept(nmv);
        } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
            System.out.println(e.getMessage());
        } catch (InvalidTilePosition e) {
            throw new RuntimeException(e);
        }
    }
}
