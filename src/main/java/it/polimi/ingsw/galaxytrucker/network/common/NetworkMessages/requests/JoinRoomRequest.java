package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class JoinRoomRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 54L;
    private final int roomId;
    private final String nickName;

    public JoinRoomRequest(int roomId, String nickName) {

        super();
        this.roomId = roomId;
        this.nickName = nickName;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickName() {
        return nickName;
    }
}