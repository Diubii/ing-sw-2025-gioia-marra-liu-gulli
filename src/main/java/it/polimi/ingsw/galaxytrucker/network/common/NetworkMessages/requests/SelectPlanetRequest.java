package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class SelectPlanetRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 98769876L;

    private final HashMap<Integer, Planet> landablePlanets;

    public SelectPlanetRequest(HashMap<Integer, Planet> landablePlanets) {
        this.landablePlanets = landablePlanets;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public HashMap<Integer, Planet> getLandablePlanets() {
        return landablePlanets;
    }
}
