package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * Response message used to indicate which trunk (segment) of a ship has been selected
 * by a player after a ship break event.
 */
public class AskTrunkResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 96243145242157961L;

    private final int trunkIndex;
    private final String playerNickname;

    public AskTrunkResponse(int trunkIndex, String playerNickname) {
        this.trunkIndex = trunkIndex;
        this.playerNickname = playerNickname;
    }

    public int getTrunkIndex() {
        return trunkIndex;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
