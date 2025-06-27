package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Join room request.
 */
public class JoinRoomRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 54L;
    private final int roomId;
    private final String nickName;
    private final boolean subscribedToTimerUpdates;

    /**
     * Instantiates a new Join room request.
     *
     * @param roomId   the room id
     * @param nickName the nick name
     */
    public JoinRoomRequest(int roomId, String nickName, boolean subscribedToTimerUpdates) {

        super();
        this.roomId = roomId;
        this.nickName = nickName;
        this.subscribedToTimerUpdates = subscribedToTimerUpdates;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets room id.
     *
     * @return the room id
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    public boolean isSubscribedToTimerUpdates() {
        return subscribedToTimerUpdates;
    }
}