package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serializable;

public class NICKNAME_RESPONSE extends NetworkMessage implements Serializable {
    private boolean available;

    public NICKNAME_RESPONSE(boolean available) {
        this.available = available;
    }

    public void accept(NetworkMessageVisitor visitor) {
        visitor.getNicknameResponse(this);
    }
}
