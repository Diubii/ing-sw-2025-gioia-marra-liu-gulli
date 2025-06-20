package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;

public class AskPositionResponse extends NetworkMessage implements Serializable {


    private final int position;

    public AskPositionResponse(int id, int position) {
        super(id);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
