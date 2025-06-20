package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class ActivateComponentRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 87698987L;

    private final ActivatableComponent activatableComponentType;

    public ActivateComponentRequest(ActivatableComponent activatableComponentType) {
        this.activatableComponentType = activatableComponentType;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public ActivatableComponent getActivatableComponentType() {
        return activatableComponentType;
    }
}
