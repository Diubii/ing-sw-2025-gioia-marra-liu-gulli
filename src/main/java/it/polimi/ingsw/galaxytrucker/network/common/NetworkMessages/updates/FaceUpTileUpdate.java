package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

public class FaceUpTileUpdate extends NetworkMessage {
    @Serial
    private static final long serialVersionUID = 43850L;

    private ArrayList<Tile> faceUpTiles;

    public FaceUpTileUpdate() {

        super();
        this.faceUpTiles = new ArrayList<>();
    }

    public FaceUpTileUpdate(int id, ArrayList<Tile> faceUpTiles) {
        super(id);
        this.faceUpTiles = new ArrayList<>(faceUpTiles);
    }

    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }

    public void setFaceUpTiles(ArrayList<Tile> faceUpTiles) {
        this.faceUpTiles = new ArrayList<>(faceUpTiles);
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
