package it.polimi.ingsw.galaxytrucker.model.essentials;

import it.polimi.ingsw.galaxytrucker.enums.Color;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a single slot (tile) on the Flight Board.
 * <p>
 * Each slot can optionally:
 * <ul>
 *   <li>Hold a player's token (position on the track).</li>
 *   <li>Be marked as a starting position (with an order).</li>
 *   <li>Be associated with a 2D position (if visual mapping is used).</li>
 * </ul>
 */
public class FlightBoardMapSlot implements Serializable {


    @Serial
    private static final long serialVersionUID = 356098306830L;

    private Color playerToken = Color.EMPTY;
    private Boolean isStartingPos;
    private Integer startingPos;
    private Position position;


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Color getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(Color playerToken) {
        this.playerToken = playerToken;
    }

    public Boolean getIsStartingPos() {
        return isStartingPos;
    }

    public void setIsStartingPos(Boolean isStartingPos) {
        this.isStartingPos = isStartingPos;
    }

    public Integer getStartingPos() {
        return startingPos;
    }

    public void setStartingPos(Integer startingPos) {
        this.startingPos = startingPos;
    }
}
