package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.enums.GameState;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Phase update.
 */
public class PhaseUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 712363L;

    private final GameState state;

    /**
     * Instantiates a new Phase update.
     *
     * @param state the state
     */
    public PhaseUpdate(GameState state) {

        this.state = state;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public GameState getState() {
        return state;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
