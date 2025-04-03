package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;

public abstract class NetworkMessage {
    public abstract void accept(NetworkMessageVisitor visitor);
}


