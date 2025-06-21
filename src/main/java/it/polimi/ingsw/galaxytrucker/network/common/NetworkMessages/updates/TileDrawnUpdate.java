package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Tile drawn update.
 */
public class TileDrawnUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 328586923L;

    private final int tileId;

    /**
     * Instantiates a new Tile drawn update.
     *
     * @param tileId the tile id
     */
    public TileDrawnUpdate(int tileId) {
        this.tileId = tileId;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets tile id.
     *
     * @return the tile id
     */
    public int getTileId() {
        return tileId;
    }
}
