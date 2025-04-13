package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.io.Serial;
import java.io.Serializable;

public class TileDrawnResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 522L;
    private final Tile tile;
    private  String errorMessage;

    public TileDrawnResponse(Tile tile) {

        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
;

}
