package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class JoiniRoomOptionsRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 69L;

    public NetworkMessageType accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }
}
