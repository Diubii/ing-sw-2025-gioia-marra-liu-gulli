package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

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
