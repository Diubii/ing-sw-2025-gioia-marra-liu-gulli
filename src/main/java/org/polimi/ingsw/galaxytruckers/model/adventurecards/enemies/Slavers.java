package org.polimi.ingsw.galaxytruckers.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytruckers.model.adventurecards.abstracts.Enemy;
import org.polimi.ingsw.galaxytruckers.model.units.Player;

public class Slavers extends Enemy {
    private final int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Slavers(int penalty) {
        this.penalty = penalty;
    }

    public void activateEffect(Player player) {

    }
}
