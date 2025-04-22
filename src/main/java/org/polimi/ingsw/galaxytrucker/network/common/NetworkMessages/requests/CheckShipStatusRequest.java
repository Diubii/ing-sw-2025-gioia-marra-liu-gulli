package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class CheckShipStatusRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 3091283L;

    private final ArrayList<Integer> removedTilesId = new ArrayList<>();

    public void addRemovedTileId(int id) {
        removedTilesId.add(id);
    }

    public void removeRemovedTileId(int id) {
        removedTilesId.remove(id);
    }

    public ArrayList<Integer> getRemovedTilesId() {
        return removedTilesId;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
