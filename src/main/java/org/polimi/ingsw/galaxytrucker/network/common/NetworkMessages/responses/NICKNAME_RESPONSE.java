package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public class NICKNAME_RESPONSE extends NetworkMessage {
    private String response;
    public NICKNAME_RESPONSE(String response) {
        this.response = response;
    }

    public void accept(NetworkMessageVisitor visitor) {
        visitor.getNicknameResponse(this);
    }
}
