package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Tile discarded update.
 */
public class TileDiscardedUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 438506757L;

    private final Tile tile;

    /**
     * Instantiates a new Tile discarded update.
     *
     * @param tile the tile
     */
    public TileDiscardedUpdate(Tile tile) {
        this.tile = tile;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets tile.
     *
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }
}
