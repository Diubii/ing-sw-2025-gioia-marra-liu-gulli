package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serial;
import java.io.Serializable;

public class JoinRoomResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 112L;
    private String errMess;

    private  Boolean operationSuccess;
    public JoinRoomResponse(String errMess, Boolean operationSuccess) {
        this.errMess = errMess;
        this.operationSuccess = operationSuccess;
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

}
