package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Join room response.
 */
public class JoinRoomResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 112L;
    private String errMess;
    private Boolean operationSuccess;
    private Color color;
    private Ship myShip;
    private Boolean isLearningMatch;
    private ArrayList<PlayerInfo> playerInfos;

    /**
     * Instantiates a new Join room response.
     *
     * @param id the id
     */
    public JoinRoomResponse(int id) {
        super(id);
    }

    /**
     * Gets my ship.
     *
     * @return the my ship
     */
    public Ship getMyShip() {
        return myShip;
    }

    /**
     * Sets my ship.
     *
     * @param myShip the my ship
     */
    public void setMyShip(Ship myShip) {
        this.myShip = myShip;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets operation success.
     *
     * @return the operation success
     */
    public Boolean getOperationSuccess() {
        return operationSuccess;
    }

    /**
     * Gets err mess.
     *
     * @return the err mess
     */
    public String getErrMess() {
        return errMess;
    }

    /**
     * Sets err mess.
     *
     * @param errMess the err mess
     */
    public void setErrMess(String errMess) {
        this.errMess = errMess;
    }

    /**
     * Sets operation success.
     *
     * @param operationSuccess the operation success
     */
    public void setOperationSuccess(Boolean operationSuccess) {
        this.operationSuccess = operationSuccess;
    }

    /**
     * Gets is learning match.
     *
     * @return the is learning match
     */
    public Boolean getIsLearningMatch() {
        return isLearningMatch;
    }

    /**
     * Sets is learning match.
     *
     * @param isLearningMatch the is learning match
     */
    public void setIsLearningMatch(Boolean isLearningMatch) {
        this.isLearningMatch = isLearningMatch;
    }

    /**
     * Gets player infos.
     *
     * @return the player infos
     */
    public ArrayList<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    /**
     * Sets player infos.
     *
     * @param playerInfos the player infos
     */
    public void setPlayerInfos(ArrayList<PlayerInfo> playerInfos) {
        this.playerInfos = playerInfos;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
