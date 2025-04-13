package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

public class NicknameResponse extends NetworkMessage {
    private String response;
    public NicknameResponse(String response) {
        super();
        this.response = response;
    }

    public NetworkMessageType accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
