package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Collect rewards response.
 */
public class CollectRewardsResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 713582355231856L;

    private final boolean wantsToCollect;

    /**
     * Instantiates a new Collect rewards response.
     *
     * @param wantsToCollect the wants to collect
     */
    public CollectRewardsResponse(boolean wantsToCollect) {
        this.wantsToCollect = wantsToCollect;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Does want to collect boolean.
     *
     * @return the boolean
     */
    public boolean doesWantToCollect() {
        return wantsToCollect;
    }
}
