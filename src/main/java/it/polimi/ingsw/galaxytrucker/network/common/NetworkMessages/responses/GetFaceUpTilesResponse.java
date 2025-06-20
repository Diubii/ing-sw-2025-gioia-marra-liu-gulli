package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class GetFaceUpTilesResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 865493612L;

    private final ArrayList<Tile> faceUpTiles;

    public GetFaceUpTilesResponse(ArrayList<Tile> faceUpTiles) {
        this.faceUpTiles = faceUpTiles;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return null;
    }

    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }
}
