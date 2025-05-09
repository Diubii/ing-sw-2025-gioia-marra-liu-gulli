package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;

public class CheckShipStatusResponse extends NetworkMessage implements Serializable {

    private final Ship ship;
    private final Boolean isValid;

    public CheckShipStatusResponse(Ship ship, Boolean isValid, int id) {
        super(id);
        this.ship = ship;
        this.isValid = isValid;
    }

    public Ship getShip() {
        return ship;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
