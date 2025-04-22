package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.Color;

public class FlightBoardMapSlot {

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
