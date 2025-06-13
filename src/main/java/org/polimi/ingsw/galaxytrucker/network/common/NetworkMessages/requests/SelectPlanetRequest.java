package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
