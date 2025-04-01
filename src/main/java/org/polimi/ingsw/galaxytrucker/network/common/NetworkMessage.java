package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;

public class NetworkMessage {

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


