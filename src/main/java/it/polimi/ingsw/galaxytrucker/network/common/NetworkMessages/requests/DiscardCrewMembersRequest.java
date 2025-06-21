package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Discard crew members request.
 */
public class DiscardCrewMembersRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 5563957030L;

    private final int numberOfCrewMembersToDiscard;

    /**
     * Instantiates a new Discard crew members request.
     *
     * @param numberOfCrewMembersToDiscard the number of crew members to discard
     */
    public DiscardCrewMembersRequest(int numberOfCrewMembersToDiscard) {
        this.numberOfCrewMembersToDiscard = numberOfCrewMembersToDiscard;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets number of crew members to discard.
     *
     * @return the number of crew members to discard
     */
    public int getNumberOfCrewMembersToDiscard() {
        return numberOfCrewMembersToDiscard;
    }
}
