package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * The type Select planet request.
 */
public class SelectPlanetRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 98769876L;

    private final HashMap<Integer, Planet> landablePlanets;

    /**
     * Instantiates a new Select planet request.
     *
     * @param landablePlanets the landable planets
     */
    public SelectPlanetRequest(HashMap<Integer, Planet> landablePlanets) {
        this.landablePlanets = landablePlanets;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets landable planets.
     *
     * @return the landable planets
     */
    public HashMap<Integer, Planet> getLandablePlanets() {
        return landablePlanets;
    }
}
