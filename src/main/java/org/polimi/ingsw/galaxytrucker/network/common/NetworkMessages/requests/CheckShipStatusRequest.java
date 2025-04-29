package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CheckShipStatusRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 3091283L;

    private  ArrayList<Integer> removedTilesId = new ArrayList<>();

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

    public CheckShipStatusRequest(){
        super();
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws ExecutionException, InterruptedException {
        return visitor.visit(this);
    }
}
