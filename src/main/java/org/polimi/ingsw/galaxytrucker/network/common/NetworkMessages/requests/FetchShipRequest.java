package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class FetchShipRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43847L;

    private final String targetNickname;

    public FetchShipRequest(String targetNickname) {

        super();
        this.targetNickname = targetNickname;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public int getId() {
        return this.getID();
    }
}
