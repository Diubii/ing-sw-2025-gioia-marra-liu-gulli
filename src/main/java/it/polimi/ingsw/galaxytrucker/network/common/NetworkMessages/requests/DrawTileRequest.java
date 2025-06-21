package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Draw tile request.
 */
public class DrawTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 533L;
    private Tile tile;
    private boolean needLastTile;
    private boolean fromReserved;
    private int reservedSlotIndex = -1;


    /**
     * Instantiates a new Draw tile request.
     */
    public DrawTileRequest() {
        this.tile = null;
        this.needLastTile = false;
    }


    /**
     * Instantiates a new Draw tile request.
     *
     * @param tile the tile
     */
    public DrawTileRequest(Tile tile) {
        //pesco da faceup tiles
        this.tile = tile;
        this.needLastTile = false;
    }

    /**
     * Reclaim last tile request draw tile request.
     *
     * @return the draw tile request
     */
    public static DrawTileRequest reclaimLastTileRequest() {
        DrawTileRequest req = new DrawTileRequest();
        req.needLastTile = true;
        return req;
    }

    /**
     * From reserved slot draw tile request.
     *
     * @param index the index
     * @return the draw tile request
     */
    public static DrawTileRequest fromReservedSlot(int index) {
        DrawTileRequest req = new DrawTileRequest();
        req.fromReserved = true;
        req.reservedSlotIndex = index;
        return req;
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

    /**
     * Is need last tile boolean.
     *
     * @return the boolean
     */
    public boolean isNeedLastTile() {
        return needLastTile;
    }

    /**
     * Is from reserved boolean.
     *
     * @return the boolean
     */
    public boolean isFromReserved() {
        return fromReserved;
    }

    /**
     * Gets reserved slot index.
     *
     * @return the reserved slot index
     */
    public int getReservedSlotIndex() {
        return reservedSlotIndex;
    }
}
