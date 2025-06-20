package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class PlaceTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43848L;

    private final Tile tile;
    private final Position pos;
    private final boolean toReserved;
    private final int reservedSlotIndex;


    public PlaceTileRequest(Tile tile, Position pos) {
        super();
        this.tile = tile;
        this.pos = pos;
        this.toReserved = false;
        this.reservedSlotIndex = -1;
    }

    public PlaceTileRequest(Tile tile, int reservedSlotIndex) {
        super();
        this.tile = tile;
        this.pos = null;
        this.toReserved = true;
        this.reservedSlotIndex = reservedSlotIndex;
    }


    public Position getPos() {
        return pos;
    }

    public boolean isToReserved() {
        return toReserved;
    }

    public int getReservedSlotIndex() {
        return reservedSlotIndex;
    }
    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public Tile getTile() {
        return tile;
    }
}
