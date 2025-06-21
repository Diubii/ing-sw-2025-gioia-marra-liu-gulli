package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Selected planet update.
 */
public class SelectedPlanetUpdate extends NetworkMessage implements Serializable {
    @Serial
    private final static long serialVersionUID = 45695230598L;

    private final String selectingPlayerNickname; //Così gli altri player possono visualizzare nome e colore sul pianeta scelto nella GUI perlomeno
    private final Planet selectedPlanet;
    private final int planetIndex;

    /**
     * Instantiates a new Selected planet update.
     *
     * @param selectingPlayerNickname the selecting player nickname
     * @param selectedPlanet          the selected planet
     * @param planetIndex             the planet index
     */
    public SelectedPlanetUpdate(String selectingPlayerNickname, Planet selectedPlanet, int planetIndex) {
        this.selectingPlayerNickname = selectingPlayerNickname;
        this.selectedPlanet = selectedPlanet;
        this.planetIndex = planetIndex;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets selecting player nickname.
     *
     * @return the selecting player nickname
     */
    public String getSelectingPlayerNickname() {
        return selectingPlayerNickname;
    }

    /**
     * Gets selected planet.
     *
     * @return the selected planet
     */
    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }

    /**
     * Gets planet index.
     *
     * @return the planet index
     */
    public int getPlanetIndex() {
        return planetIndex;
    }
}
