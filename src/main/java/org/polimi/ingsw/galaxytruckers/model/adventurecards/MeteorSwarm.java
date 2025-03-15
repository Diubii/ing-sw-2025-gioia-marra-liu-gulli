package org.polimi.ingsw.galaxytruckers.model.adventurecards;

import org.polimi.ingsw.galaxytruckers.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytruckers.model.Projectile;
import org.polimi.ingsw.galaxytruckers.model.units.Player;

import java.util.ArrayList;

public class MeteorSwarm extends AdventureCard {
    private final ArrayList<Projectile> meteors;
    public ArrayList<Projectile> getMeteors() {
        return meteors;
    }

    public MeteorSwarm(ArrayList<Projectile> meteors) {
        this.meteors = meteors;
    }

    public void activateEffect(Player player){

    }
}
