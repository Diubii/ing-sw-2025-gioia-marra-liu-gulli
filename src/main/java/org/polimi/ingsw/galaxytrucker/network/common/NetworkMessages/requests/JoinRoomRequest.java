package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, IOException, InvalidTilePosition {
        return visitor.visit(this);
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickName() {
        return nickName;
    }
}