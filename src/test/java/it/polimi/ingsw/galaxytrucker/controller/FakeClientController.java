package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitor;

/**
 * A test-oriented implementation of {@link ClientController} that
 * processes messages using a {@link NetworkMessageVisitor} without actual network transmission.
 */
public class FakeClientController extends ClientController {
    private final NetworkMessageVisitor visitor;

    /**
     * Constructs a FakeClientController for test purposes.
     *
     * @param view           the associated view instance
     * @param flag           a boolean flag passed to the superclass
     * @param serverController the server-side controller logic
     * @param clientHandler  the simulated client connection handler
     */
    public FakeClientController(View view, Boolean flag, ServerController serverController, ClientHandler clientHandler) {
        super(view, flag);
        visitor = new NetworkMessageVisitor(serverController, clientHandler);
    }
    /**
     * Simulates sending a network message by directly accepting a visitor.
     *
     * @param message the network message to process
     * @return always returns {@code true} to indicate successful handling
     */

    @Override
    public boolean safeSendMessage(NetworkMessage message) {
        message.accept(visitor);
        return true;
    }
}
