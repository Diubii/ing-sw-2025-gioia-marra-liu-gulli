package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class PlaceTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43848L;

    private final Tile tile;
    private final Position pos;

    public PlaceTileRequest(Tile tile, Position pos) {

        super();

        this.tile = tile;

        this.pos = pos;
    }

    public Position getPos() {
        return pos;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition {
        return visitor.visit(this);
    }

    public Tile getTile() {
        return tile;
    }
}
