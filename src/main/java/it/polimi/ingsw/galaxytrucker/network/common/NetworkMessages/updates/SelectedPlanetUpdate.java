package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class SelectedPlanetUpdate extends NetworkMessage implements Serializable {
    @Serial
    private final static long serialVersionUID = 45695230598L;

    private final String selectingPlayerNickname; //Così gli altri player possono visualizzare nome e colore sul pianeta scelto nella GUI perlomeno
    private final Planet selectedPlanet;
    private final int planetIndex;

    public SelectedPlanetUpdate(String selectingPlayerNickname, Planet selectedPlanet, int planetIndex) {
        this.selectingPlayerNickname = selectingPlayerNickname;
        this.selectedPlanet = selectedPlanet;
        this.planetIndex = planetIndex;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getSelectingPlayerNickname() {
        return selectingPlayerNickname;
    }

    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }

    public int getPlanetIndex() {
        return planetIndex;
    }
}
