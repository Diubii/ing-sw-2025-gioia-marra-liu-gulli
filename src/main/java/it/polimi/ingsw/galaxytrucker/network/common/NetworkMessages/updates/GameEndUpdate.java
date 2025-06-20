package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.PlayerScore;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class GameEndUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 72637L;
    private final ArrayList<PlayerScore> scores;

    public GameEndUpdate(ArrayList<PlayerScore> scores) {
        this.scores = scores;
    }

    public ArrayList<PlayerScore> getScores() {
        return scores;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
