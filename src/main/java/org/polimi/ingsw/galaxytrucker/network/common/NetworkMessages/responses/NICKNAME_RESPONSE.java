package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public class NICKNAME_RESPONSE extends NetworkMessage {
    private String response;
    public NICKNAME_RESPONSE(String response) {
        super(NetworkMessageType.NICKNAME_RESPONSE);
        this.response = response;
    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
