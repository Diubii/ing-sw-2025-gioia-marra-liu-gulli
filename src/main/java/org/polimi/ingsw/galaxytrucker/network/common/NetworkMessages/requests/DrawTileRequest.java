package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class DrawTileRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 533L;
    private Tile tile;
    private boolean needLastTile;


    public DrawTileRequest() {
        this.tile = null;
        this.needLastTile = false;
    }


    public DrawTileRequest(Tile tile) {
        //pesco da faceup tiles
        this.tile = tile;
        this.needLastTile = false;
    }

    public static DrawTileRequest reclaimLastTileRequest() {
        DrawTileRequest req = new DrawTileRequest();
        req.needLastTile = true;
        return req;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }


    public Tile getTile() {
        return tile;
    }
    public boolean isNeedLastTile() {
        return needLastTile;
    }
}
