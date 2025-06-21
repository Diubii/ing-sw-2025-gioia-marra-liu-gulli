package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

/**
 * The type Face up tile update.
 */
public class FaceUpTileUpdate extends NetworkMessage {
    @Serial
    private static final long serialVersionUID = 43850L;

    private ArrayList<Tile> faceUpTiles;

    /**
     * Instantiates a new Face up tile update.
     */
    public FaceUpTileUpdate() {

        super();
        this.faceUpTiles = new ArrayList<>();
    }

    /**
     * Instantiates a new Face up tile update.
     *
     * @param id          the id
     * @param faceUpTiles the face up tiles
     */
    public FaceUpTileUpdate(int id, ArrayList<Tile> faceUpTiles) {
        super(id);
        this.faceUpTiles = new ArrayList<>(faceUpTiles);
    }

    /**
     * Gets face up tiles.
     *
     * @return the face up tiles
     */
    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }

    /**
     * Sets face up tiles.
     *
     * @param faceUpTiles the face up tiles
     */
    public void setFaceUpTiles(ArrayList<Tile> faceUpTiles) {
        this.faceUpTiles = new ArrayList<>(faceUpTiles);
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
