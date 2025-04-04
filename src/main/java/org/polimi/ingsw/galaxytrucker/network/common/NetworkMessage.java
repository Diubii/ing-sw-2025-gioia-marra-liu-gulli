package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {
    public abstract void accept(NetworkMessageVisitor visitor);
}


