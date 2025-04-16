package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;

import java.util.ArrayList;

public class Smugglers extends Enemy {
    private int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Smugglers(){}

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateSmugglers(this, p, flightBoard);
    }
}
