package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Place tile request.
 */
public class PlaceTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43848L;

    private final Tile tile;
    private final Position pos;
    private final boolean toReserved;
    private final int reservedSlotIndex;


    /**
     * Instantiates a new Place tile request.
     *
     * @param tile the tile
     * @param pos  the pos
     */
    public PlaceTileRequest(Tile tile, Position pos) {
        super();
        this.tile = tile;
        this.pos = pos;
        this.toReserved = false;
        this.reservedSlotIndex = -1;
    }

    /**
     * Instantiates a new Place tile request.
     *
     * @param tile              the tile
     * @param reservedSlotIndex the reserved slot index
     */
    public PlaceTileRequest(Tile tile, int reservedSlotIndex) {
        super();
        this.tile = tile;
        this.pos = null;
        this.toReserved = true;
        this.reservedSlotIndex = reservedSlotIndex;
    }


    /**
     * Gets pos.
     *
     * @return the pos
     */
    public Position getPos() {
        return pos;
    }

    /**
     * Is to reserved boolean.
     *
     * @return the boolean
     */
    public boolean isToReserved() {
        return toReserved;
    }

    /**
     * Gets reserved slot index.
     *
     * @return the reserved slot index
     */
    public int getReservedSlotIndex() {
        return reservedSlotIndex;
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
