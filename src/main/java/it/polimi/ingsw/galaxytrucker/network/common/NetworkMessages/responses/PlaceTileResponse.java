package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Place tile response.
 */
public class PlaceTileResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 4383506L;

    private String message;

    /**
     * Instantiates a new Place tile response.
     *
     * @param message the message
     * @param id      the id
     */
    public PlaceTileResponse(String message, int id) {

        super(id);
        this.message = message;
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

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
