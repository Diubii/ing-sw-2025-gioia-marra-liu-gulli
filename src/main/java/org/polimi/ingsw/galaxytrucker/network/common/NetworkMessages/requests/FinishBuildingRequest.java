package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class FinishBuildingRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 43870L;

    private final Ship ship;
    private final Tile lastTile;
    public String name;

    public FinishBuildingRequest(Ship ship, Tile lastTile) {
        this.ship = ship;
        this.lastTile = lastTile;
    }

    public Ship getShip() {
        return ship;
    }

    public Tile getLastTile() {
        return lastTile;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
        return visitor.visit(this);
    }
}
