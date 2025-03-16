package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;
import org.polimi.ingsw.galaxytrucker.model.units.Player;

public class Smugglers extends Enemy {
    private int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Smugglers(){}

    public void activateEffect(Player player) {
    }
}
