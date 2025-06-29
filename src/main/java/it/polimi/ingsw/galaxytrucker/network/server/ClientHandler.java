package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.util.UUID;


/**
 * Represents a generic client handler interface used by the server
 * to manage communication with an individual client.
 */
public interface ClientHandler {
    /**
     * Returns the unique identifier of the client.
     *
     * @return the UUID associated with the client.
     */
    UUID getClientID();
    /**
     * Sends a network message to the associated client.
     *
     * @param message the {@link NetworkMessage} to be sent.
     */
    void sendMessage(NetworkMessage message);
}
