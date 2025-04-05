package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

import java.io.Serial;
import java.io.Serializable;

public class NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private NetworkMessageType type;

    public NetworkMessage(final NetworkMessageType type) {
        this.type = type;
    }

    public NetworkMessageType getType() {
        return type;
    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }
}
