package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
        return visitor.visit(this);
    }
}
