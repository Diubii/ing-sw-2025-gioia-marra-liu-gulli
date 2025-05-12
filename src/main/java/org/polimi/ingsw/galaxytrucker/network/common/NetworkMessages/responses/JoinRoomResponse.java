package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class JoinRoomResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 112L;
    private String errMess;
    private Boolean operationSuccess;
    private Color color;
    private Ship myShip;
    private Boolean isLearningMatch;

    public JoinRoomResponse(String errMess, Boolean operationSuccess, int id) {
        super(id);
        this.errMess = errMess;
        this.operationSuccess = operationSuccess;
    }

    public Ship getMyShip() {
        return myShip;
    }

    public void setMyShip(Ship myShip) {
        this.myShip = myShip;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Boolean getOperationSuccess() {
        return operationSuccess;
    }

    public String getErrMess() {
        return errMess;
    }

    public void setErrMess(String errMess) {
        this.errMess = errMess;
    }

    public void setOperationSuccess(Boolean operationSuccess) {
        this.operationSuccess = operationSuccess;
    }

    public Boolean getIsLearningMatch() {
        return isLearningMatch;
    }

    public void setIsLearningMatch(Boolean isLearningMatch) {
        this.isLearningMatch = isLearningMatch;
    }
    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
