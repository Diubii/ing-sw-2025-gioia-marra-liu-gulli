package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class MatchInfoUpdate extends NetworkMessage implements Serializable {

    private final String leaderNickname;
    private final int remainingCards;

    public MatchInfoUpdate(String leaderNickname, int remainingCards) {
        this.leaderNickname = leaderNickname;
        this.remainingCards = remainingCards;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getLeaderNickname() {
        return leaderNickname;
    }

    public int getRemainingCards() {
        return remainingCards;
    }
}
