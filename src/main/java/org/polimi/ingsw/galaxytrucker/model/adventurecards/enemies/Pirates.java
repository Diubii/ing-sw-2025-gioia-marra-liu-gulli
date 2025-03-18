package org.polimi.ingsw.galaxytrucker.model.adventurecards.enemies;

import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.Enemy;
import org.polimi.ingsw.galaxytrucker.model.Player;

import java.util.ArrayList;

public class Pirates extends Enemy {
    private final ArrayList<Projectile> cannonFires;
    public ArrayList<Projectile> getCannonFires() {
        return cannonFires;
    }

    public Pirates(ArrayList<Projectile> cannonFires) {
        this.cannonFires = cannonFires;
    }

    public void activateEffect(Player player) {

    }
}
