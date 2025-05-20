package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class FlipTimerRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43232442849L;

    public int getTimerIndex() {
        return timerIndex;
    }

    public void setTimerIndex(int timerIndex) {
        this.timerIndex = timerIndex;
    }

    private int timerIndex;

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
