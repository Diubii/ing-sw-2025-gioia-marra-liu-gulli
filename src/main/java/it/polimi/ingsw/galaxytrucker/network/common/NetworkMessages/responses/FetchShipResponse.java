package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Fetch ship response.
 */
public class FetchShipResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 52928352963L;

    private final String targetNickname;

    private final Ship targetShipView;

    /**
     * Instantiates a new Fetch ship response.
     *
     * @param targetNickname the target nickname
     * @param targetShipView the target ship view
     */
    public FetchShipResponse(String targetNickname, Ship targetShipView) {
        this.targetNickname = targetNickname;
        this.targetShipView = targetShipView;
    }

    /**
     * Instantiates a new Fetch ship response.
     *
     * @param targetNickname the target nickname
     * @param targetShipView the target ship view
     * @param id             the id
     */
    public FetchShipResponse(String targetNickname, Ship targetShipView, int id) {
        super(id);
        this.targetNickname = targetNickname;
        this.targetShipView = targetShipView;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets target nickname.
     *
     * @return the target nickname
     */
    public String getTargetNickname() {
        return targetNickname;
    }

    /**
     * Gets target ship view.
     *
     * @return the target ship view
     */
    public Ship getTargetShipView() {
        return targetShipView;
    }
}
