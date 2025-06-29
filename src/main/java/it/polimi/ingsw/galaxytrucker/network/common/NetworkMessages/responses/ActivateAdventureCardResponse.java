package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;
/**
 * Response message indicating whether the adventure card was successfully activated.
 */

public class ActivateAdventureCardResponse extends NetworkMessage implements Serializable {
    private final boolean activated;

    public ActivateAdventureCardResponse(boolean activated) {
        this.activated = activated;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isActivated() {
        return activated;
    }
}
