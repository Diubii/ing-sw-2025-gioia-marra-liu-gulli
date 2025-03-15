package org.polimi.ingsw.galaxytruckers.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytruckers.model.adventurecards.abstracts.Enemy;
import org.polimi.ingsw.galaxytruckers.model.units.Player;

public class Smugglers extends Enemy {
    private int penalty;
    public int getPenalty() {
        return penalty;
    }

    public Smugglers(){}

    public void activateEffect(Player player) {
    }
}
