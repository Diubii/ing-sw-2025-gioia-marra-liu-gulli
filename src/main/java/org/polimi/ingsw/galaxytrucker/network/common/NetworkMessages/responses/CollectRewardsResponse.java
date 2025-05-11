package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class CollectRewardsResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 713582355231856L;

    private final boolean wantsToCollect;

    public CollectRewardsResponse(boolean wantsToCollect) {
        this.wantsToCollect = wantsToCollect;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public boolean doesWantToCollect() {
        return wantsToCollect;
    }
}
