package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serializable;
import java.rmi.RemoteException;

public class NICKNAME_RESPONSE extends NetworkMessage implements Serializable {
    private boolean available;

    public NICKNAME_RESPONSE(boolean available) {
        this.available = available;
    }

    public void accept(NetworkMessageVisitor visitor) throws RemoteException {
        visitor.getNicknameResponse(this);
    }
}
