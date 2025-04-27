package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class GetFaceUpTilesResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 865493612L;

    private final ArrayList<Tile> faceUpTiles;

    public GetFaceUpTilesResponse(ArrayList<Tile> faceUpTiles) {
        this.faceUpTiles = faceUpTiles;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
        return null;
    }

    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }
}
