package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Activate component response.
 */
public class ActivateComponentResponse extends NetworkMessage implements Serializable {
    private final ActivatableComponent activatableComponentType;
    private final ArrayList<Position> activatedComponentPositions;
    private final ArrayList<Position> batteriesPositions;

    @Serial
    private static final long serialVersionUID = 834756945642L;

    /**
     * Instantiates a new Activate component response.
     *
     * @param activatableComponentType    the activatable component type
     * @param activatedComponentPositions the activated component positions
     * @param batteriesPositions          the batteries positions
     */
    public ActivateComponentResponse(ActivatableComponent activatableComponentType, ArrayList<Position> activatedComponentPositions, ArrayList<Position> batteriesPositions) {
        this.activatableComponentType = activatableComponentType;
        this.activatedComponentPositions = activatedComponentPositions;
        this.batteriesPositions = batteriesPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets activatable component type.
     *
     * @return the activatable component type
     */
    public ActivatableComponent getActivatableComponentType() {
        return activatableComponentType;
    }

    /**
     * Gets activated component positions.
     *
     * @return the activated component positions
     */
    public ArrayList<Position> getActivatedComponentPositions() {
        return activatedComponentPositions;
    }

    /**
     * Gets batteries positions.
     *
     * @return the batteries positions
     */
    public ArrayList<Position> getBatteriesPositions() {
        return batteriesPositions;
    }
}
