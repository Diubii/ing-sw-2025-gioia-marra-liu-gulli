package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Game message.
 */
public class GameMessage extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 23389426523346L;

    private String message;

    /**
     * Instantiates a new Game message.
     */
    public GameMessage() {
    }

    /**
     * Instantiates a new Game message.
     *
     * @param message the message
     */
    public GameMessage(String message) {
        this.message = message;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
