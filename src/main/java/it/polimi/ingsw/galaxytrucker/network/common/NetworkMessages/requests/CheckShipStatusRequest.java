package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Check ship status request.
 */
public class CheckShipStatusRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 3091283L;

    private ArrayList<Integer> removedTilesId = new ArrayList<>();

    /**
     * Add removed tile id.
     *
     * @param id the id
     */
    public void addRemovedTileId(int id) {
        removedTilesId.add(id);
    }

    /**
     * Sets removed tiles id.
     *
     * @param removedTilesId the removed tiles id
     */
    public void setRemovedTilesId(ArrayList<Integer> removedTilesId) {
        this.removedTilesId = removedTilesId;
    }

    /**
     * Remove removed tile id.
     *
     * @param id the id
     */
    public void removeRemovedTileId(int id) {
        removedTilesId.remove(id);
    }

    /**
     * Gets removed tiles id.
     *
     * @return the removed tiles id
     */
    public ArrayList<Integer> getRemovedTilesId() {
        return removedTilesId;
    }

    /**
     * Instantiates a new Check ship status request.
     */
    public CheckShipStatusRequest() {
        super();
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
