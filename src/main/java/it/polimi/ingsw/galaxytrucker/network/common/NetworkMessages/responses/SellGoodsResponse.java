package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class SellGoodsResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 528658960523L;

    private final boolean accepted;

    public SellGoodsResponse(boolean accepted, int id) {
        super(id);
        this.accepted = accepted;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return null;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
