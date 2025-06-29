package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request sent by a client to indicate that the player wants to land early
 * during the flight phase of the game.
 */
public class EarlyLandingRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3287423907322938L;


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
