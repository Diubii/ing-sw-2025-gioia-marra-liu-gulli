package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;

/**
 * The type Check ship status response.
 */
public class CheckShipStatusResponse extends NetworkMessage implements Serializable {

    private final Ship ship;
    private final Boolean isValid;

    /**
     * Instantiates a new Check ship status response.
     *
     * @param ship    the ship
     * @param isValid the validity
     * @param id      the id
     */
    public CheckShipStatusResponse(Ship ship, Boolean isValid, int id) {
        super(id);
        this.ship = ship;
        this.isValid = isValid;
    }

    /**
     * Gets ship.
     *
     * @return the ship
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Gets is valid.
     *
     * @return the is valid
     */
    public Boolean getIsValid() {
        return isValid;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
