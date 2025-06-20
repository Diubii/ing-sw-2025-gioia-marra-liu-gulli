package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class SelectPlanetResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 274424698798L;

    private final Planet selectedPlanet;
    private final Integer planetIndex;

    public SelectPlanetResponse(Planet selectedPlanet, Integer planetIndex) {

        this.selectedPlanet = selectedPlanet;
        this.planetIndex = planetIndex;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }

    public Integer getPlanetIndex() {
        return planetIndex;
    }
}
