package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
