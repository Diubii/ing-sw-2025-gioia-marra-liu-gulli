package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;

public class Slavers extends Enemy {
    private final int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Slavers(int penalty) {
        this.penalty = penalty;
    }

    public void activateEffect(AdventureCardActivator aca, Player p, FlightBoard flightBoard) {
        aca.activateSlavers(this, p, flightBoard);
    }
}
