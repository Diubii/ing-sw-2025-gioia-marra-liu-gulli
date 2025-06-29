package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request message sent from a client to the server to draw the next adventure card.
 * This is typically used during the flight phase, where players need to proceed
 * through the adventure deck one card at a time.
 */
public class DrawAdventureCardRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 43850L;

    public DrawAdventureCardRequest() {


    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
