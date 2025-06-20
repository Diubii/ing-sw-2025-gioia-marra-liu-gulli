package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class CheckShipStatusRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 3091283L;

    private ArrayList<Integer> removedTilesId = new ArrayList<>();

    public void addRemovedTileId(int id) {
        removedTilesId.add(id);
    }

    public void setRemovedTilesId(ArrayList<Integer> removedTilesId) {
        this.removedTilesId = removedTilesId;
    }

    public void removeRemovedTileId(int id) {
        removedTilesId.remove(id);
    }

    public ArrayList<Integer> getRemovedTilesId() {
        return removedTilesId;
    }

    public CheckShipStatusRequest() {
        super();
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
