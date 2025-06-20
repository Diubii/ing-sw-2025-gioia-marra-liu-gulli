package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class EndTimerUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 72637L;

    private final int timerSeconds;

    public EndTimerUpdate(int timerSeconds) {

        this.timerSeconds = timerSeconds;
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
