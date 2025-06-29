package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request message sent from the client to the server to indicate
 * that the player wants to activate the current adventure card.
 */
public class ActivateAdventureCardRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 328742390732L;

    public ActivateAdventureCardRequest() {
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
