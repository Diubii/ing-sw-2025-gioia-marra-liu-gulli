package it.polimi.ingsw.galaxytrucker.network.client;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.IOException;


/**
 * Represents a generic client in the Galaxy Trucker network architecture.
 * Provides the ability to send messages to the server.
 */
public interface Client {
    /**
     * Sends a {@link NetworkMessage} to the server.
     *
     * @param message the message to send
     * @throws IOException if a communication error occurs
     */
    void sendMessage(NetworkMessage message) throws IOException;
}
