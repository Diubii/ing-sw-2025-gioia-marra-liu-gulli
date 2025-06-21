package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Sell goods response.
 */
public class SellGoodsResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 528658960523L;

    private final boolean accepted;

    /**
     * Instantiates a new Sell goods response.
     *
     * @param accepted choice
     * @param id       the id
     */
    public SellGoodsResponse(boolean accepted, int id) {
        super(id);
        this.accepted = accepted;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return null;
    }

    /**
     * Is accepted boolean.
     *
     * @return the boolean
     */
    public boolean isAccepted() {
        return accepted;
    }
}
