package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class DiscardTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43849L;

    private final Tile tile;

    public DiscardTileRequest(Tile tile) {
        this.tile = tile;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public Tile getTile() {
        return tile;
    }
}
