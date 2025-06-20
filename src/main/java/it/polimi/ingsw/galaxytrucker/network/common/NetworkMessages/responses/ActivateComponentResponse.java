package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ActivateComponentResponse extends NetworkMessage implements Serializable {
    private final ActivatableComponent activatableComponentType;
    private final ArrayList<Position> activatedComponentPositions;
    private final ArrayList<Position> batteriesPositions;

    @Serial
    private static final long serialVersionUID = 834756945642L;

    public ActivateComponentResponse(ActivatableComponent activatableComponentType, ArrayList<Position> activatedComponentPositions, ArrayList<Position> batteriesPositions) {
        this.activatableComponentType = activatableComponentType;
        this.activatedComponentPositions = activatedComponentPositions;
        this.batteriesPositions = batteriesPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public ActivatableComponent getActivatableComponentType() {
        return activatableComponentType;
    }

    public ArrayList<Position> getActivatedComponentPositions() {
        return activatedComponentPositions;
    }

    public ArrayList<Position> getBatteriesPositions() {
        return batteriesPositions;
    }
}
