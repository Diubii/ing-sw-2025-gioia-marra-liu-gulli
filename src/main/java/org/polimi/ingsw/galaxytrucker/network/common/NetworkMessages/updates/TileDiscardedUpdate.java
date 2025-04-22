package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class TileDiscardedUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 438506757L;

    private final Tile tile;

    public TileDiscardedUpdate(Tile tile) {
        this.tile = tile;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }

    public Tile getTile() {
        return tile;
    }
}
