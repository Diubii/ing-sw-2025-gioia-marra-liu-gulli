package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
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
