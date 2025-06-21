package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type End turn update.
 */
public class EndTurnUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 72637L;
    private boolean endGame;

    /**
     * Instantiates a new End turn update.
     */
    public EndTurnUpdate() {

    }

    /**
     * Is end game boolean.
     *
     * @return the boolean
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * Sets end game.
     *
     * @param endGame the end game
     */
    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
