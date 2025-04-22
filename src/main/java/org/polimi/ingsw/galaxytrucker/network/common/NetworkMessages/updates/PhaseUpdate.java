package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class PhaseUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 712363L;

    private final GameState state;

    public PhaseUpdate(GameState state) {

        this.state = state;
    }

    public GameState getState() {
        return state;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }
}
