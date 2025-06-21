package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Draw tile response.
 */
public class DrawTileResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 522L;
    private final Tile tile;
    private String errorMessage;

    /**
     * Instantiates a new Draw tile response.
     *
     * @param tile the tile
     * @param id   the id
     */
    public DrawTileResponse(Tile tile, int id) {
        super(id);
        this.tile = tile;
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
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
