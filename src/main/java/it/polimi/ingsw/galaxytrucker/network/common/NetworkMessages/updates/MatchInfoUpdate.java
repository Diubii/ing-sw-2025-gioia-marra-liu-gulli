package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;

/**
 * The type Match info update.
 */
public class MatchInfoUpdate extends NetworkMessage implements Serializable {

    private final String leaderNickname;
    private final int remainingCards;

    /**
     * Instantiates a new Match info update.
     *
     * @param leaderNickname the leader nickname
     * @param remainingCards the remaining cards
     */
    public MatchInfoUpdate(String leaderNickname, int remainingCards) {
        this.leaderNickname = leaderNickname;
        this.remainingCards = remainingCards;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets leader nickname.
     *
     * @return the leader nickname
     */
    public String getLeaderNickname() {
        return leaderNickname;
    }

    /**
     * Gets remaining cards.
     *
     * @return the remaining cards
     */
    public int getRemainingCards() {
        return remainingCards;
    }
}
