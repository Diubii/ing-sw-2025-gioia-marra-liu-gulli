package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Flip timer request.
 */
public class FlipTimerRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43232442849L;

    /**
     * Gets timer index.
     *
     * @return the timer index
     */
    public int getTimerIndex() {
        return timerIndex;
    }

    /**
     * Sets timer index.
     *
     * @param timerIndex the timer index
     */
    public void setTimerIndex(int timerIndex) {
        this.timerIndex = timerIndex;
    }

    private int timerIndex;

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
