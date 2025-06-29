package it.polimi.ingsw.galaxytrucker.network.common;

import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;


/**
 * Abstract base class for all network messages exchanged in the game.
 * Implements the visitor pattern for flexible message handling.
 */
public abstract class NetworkMessage implements Serializable {

    /** Static counter for assigning unique message IDs */
    private static int id = 0;
    /** ID assigned to this message instance */
    private int myId;


    /**
     * Constructs a message with a unique auto-incremented ID.
     */
    public NetworkMessage() {
        myId = id;
        id++;
    }

    /**
     * Constructs a message with a specified ID.
     * @param id the ID to assign to this message
     */
    public NetworkMessage(int id) {
        myId = id;

    }

    /**
     * Returns the ID of this message.
     * @return the message ID
     */
    public int getID() {
        return myId;
    }
    /**
     * Accepts a visitor for processing this message.
     * @param visitor the visitor to process the message
     * @param <T> the return type of the visitor
     * @return the result from the visitor
     */
    public abstract <T> T accept(NetworkMessageVisitorsInterface<T> visitor);
}
