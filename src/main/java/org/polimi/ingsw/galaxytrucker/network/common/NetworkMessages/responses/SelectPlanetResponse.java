package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class SelectPlanetResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 274424698798L;

    private final Planet selectedPlanet;

    public SelectPlanetResponse(Planet selectedPlanet) {
        this.selectedPlanet = selectedPlanet;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
        return visitor.visit(this);
    }

    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }
}
