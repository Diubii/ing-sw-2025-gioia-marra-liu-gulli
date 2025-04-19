package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class Smugglers extends AdventureCard {
    private int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Smugglers(){}

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateSmugglers(this, p, flightBoard);
    }
}
