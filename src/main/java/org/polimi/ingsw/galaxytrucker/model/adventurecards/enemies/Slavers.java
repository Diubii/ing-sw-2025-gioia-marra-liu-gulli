package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;

public class Slavers extends Enemy {
    private final int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Slavers(int penalty) {
        this.penalty = penalty;
    }

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateSlavers(this);
    }
}
