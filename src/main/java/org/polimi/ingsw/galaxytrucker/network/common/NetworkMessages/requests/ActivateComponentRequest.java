package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class ActivateComponentRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 87698987L;

    private final ActivatableComponent activatableComponentType;

    public ActivateComponentRequest(ActivatableComponent activatableComponentType) {
        this.activatableComponentType = activatableComponentType;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException {
        return visitor.visit(this);
    }

    public ActivatableComponent getActivatableComponentType() {
        return activatableComponentType;
    }
}
