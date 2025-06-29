package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerController;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;


/**
 * Handles incoming {@link NetworkMessage} instances from clients.
 * This class uses the Visitor pattern to dispatch message processing
 * to the appropriate handler based on message type.
 */
public class MessageManager {

    private final ServerController serverController;

    /**
     * Constructs a new MessageManager with a reference to the server controller.
     *
     * @param serverController the controller responsible for managing server-side logic.
     */

    public MessageManager(ServerController serverController) {
        this.serverController = serverController;
    }

    /**
     * Handles an incoming network message from a client.
     * The message is processed using a {@link NetworkMessageVisitor}.
     *
     * @param message       the message to handle.
     * @param clientHandler the client that sent the message.
     */

    public void handle(NetworkMessage message, ClientHandler clientHandler) {
        NetworkMessageVisitor nmv = new NetworkMessageVisitor(serverController, clientHandler);
        message.accept(nmv);
    }
}
