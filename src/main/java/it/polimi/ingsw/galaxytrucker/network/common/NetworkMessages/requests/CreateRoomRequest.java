package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class CreateRoomRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 53L;

    private final int maxPlayers;
    private final Boolean isLearningMatch;
    private final String nickName;

    public CreateRoomRequest(int maxPlayers, Boolean isLearningMatch, String nickName) {
        this.maxPlayers = maxPlayers;
        this.isLearningMatch = isLearningMatch;
        this.nickName = nickName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getNickName() {
        return nickName;
    }

    public Boolean getIsLearningMatch() {
        return isLearningMatch;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
