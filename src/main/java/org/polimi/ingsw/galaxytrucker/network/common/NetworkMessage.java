package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.NetworkMessageVisitor;

import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class NetworkMessage implements Serializable {
    public abstract void accept(NetworkMessageVisitor visitor) throws RemoteException;
}


