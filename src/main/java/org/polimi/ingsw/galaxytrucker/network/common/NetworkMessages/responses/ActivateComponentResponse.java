package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ActivateComponentResponse extends NetworkMessage implements Serializable {
    private final ActivatableComponent activatableComponentType;
    private final ArrayList<Position> activatedDoubleEnginesPositions;
    private final ArrayList<Position> batteriesPositions;

    @Serial
    private static final long serialVersionUID = 834756945642L;

    public ActivateComponentResponse(ActivatableComponent activatableComponentType, ArrayList<Position> activatedDoubleEnginesPositions, ArrayList<Position> batteriesPositions) {
        this.activatableComponentType = activatableComponentType;
        this.activatedDoubleEnginesPositions = activatedDoubleEnginesPositions;
        this.batteriesPositions = batteriesPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public ActivatableComponent getActivatableComponentType() {
        return activatableComponentType;
    }

    public ArrayList<Position> getActivatedDoubleEnginesPositions() {
        return activatedDoubleEnginesPositions;
    }

    public ArrayList<Position> getBatteriesPositions() {
        return batteriesPositions;
    }
}
