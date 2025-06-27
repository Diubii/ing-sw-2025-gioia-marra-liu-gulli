package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Create room request.
 */
public class CreateRoomRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 53L;

    private final int maxPlayers;
    private final Boolean isLearningMatch;
    private final String nickName;
    private final boolean subscribedToTimerUpdates;

    /**
     * Instantiates a new Create room request.
     *
     * @param maxPlayers      the max players
     * @param isLearningMatch the is learning match
     * @param nickName        the nick name
     */
    public CreateRoomRequest(int maxPlayers, Boolean isLearningMatch, String nickName, boolean subscribedToTimerUpdates) {
        this.maxPlayers = maxPlayers;
        this.isLearningMatch = isLearningMatch;
        this.nickName = nickName;
        this.subscribedToTimerUpdates = subscribedToTimerUpdates;
    }

    /**
     * Gets max players.
     *
     * @return the max players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Gets is learning match.
     *
     * @return the is learning match
     */
    public Boolean getIsLearningMatch() {
        return isLearningMatch;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isSubscribedToTimerUpdates() {
        return subscribedToTimerUpdates;
    }
}
