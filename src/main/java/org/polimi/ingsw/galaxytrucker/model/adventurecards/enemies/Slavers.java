package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;
import org.polimi.ingsw.galaxytrucker.model.Player;

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
