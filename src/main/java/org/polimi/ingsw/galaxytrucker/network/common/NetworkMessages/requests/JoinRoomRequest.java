package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitorInterface;

import java.io.Serial;
import java.io.Serializable;

public class JoinRoomRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 54L;
    private final int roomId;
    private final String nickName;

    public JoinRoomRequest(int roomId, String nickName) {
        this.roomId = roomId;
        this.nickName = nickName;
    }

    public NetworkMessageType accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickName() {
        return nickName;
    }
}
