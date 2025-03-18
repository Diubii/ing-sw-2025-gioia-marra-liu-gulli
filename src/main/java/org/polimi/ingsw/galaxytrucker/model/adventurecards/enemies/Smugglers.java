package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;

public class Smugglers extends Enemy {
    private int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Smugglers(){}

    public void activateEffect(AdventureCardActivator aca) {
        aca.activateSmugglers(this);
    }
}
