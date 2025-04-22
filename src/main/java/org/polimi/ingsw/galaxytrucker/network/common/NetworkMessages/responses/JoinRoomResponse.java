package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class JoinRoomResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 112L;
    private String errMess;

    private Boolean operationSuccess;


    private Color color;

    public JoinRoomResponse(String errMess, Boolean operationSuccess, int id) {
        super(id);
        this.errMess = errMess;
        this.operationSuccess = operationSuccess;
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

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }
}
