package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class AskTrunkResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 96243145242157961L;

    private final int trunkIndex;

    public AskTrunkResponse(int trunkIndex) {
        this.trunkIndex = trunkIndex;
    }

    public int getTrunkIndex() {
        return trunkIndex;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
