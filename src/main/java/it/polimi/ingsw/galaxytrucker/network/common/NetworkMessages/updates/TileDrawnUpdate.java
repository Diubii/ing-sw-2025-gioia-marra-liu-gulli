package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class TileDrawnUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 328586923L;

    private final int tileId;

    public TileDrawnUpdate(int tileId) {
        this.tileId = tileId;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public int getTileId() {
        return tileId;
    }
}
