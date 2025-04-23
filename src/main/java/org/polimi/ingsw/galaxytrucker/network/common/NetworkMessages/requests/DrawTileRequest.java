package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class DrawTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 533L;
    private  Tile tile;
    private Integer tileId;

    public DrawTileRequest() {
        this.tile = null;
    }

    public DrawTileRequest(Tile tile) {
        //pesco da faceup tiles
        this.tile = tile;
    }

    public DrawTileRequest(int tileId) {
        this.tileId = tileId;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
        return visitor.visit(this);
    }

    public int getTileId() {
        return tileId;
    }

    public Tile getTile() {
        return tile;
    }
}
