package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class MeteorSwarm extends AdventureCard {
    private final ArrayList<Projectile> meteors;
    public ArrayList<Projectile> getMeteors() {
        return meteors;
    }

    public MeteorSwarm(ArrayList<Projectile> meteors) {
        this.meteors = meteors;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard){
        aca.activateMeteorSwarm(this, p, flightBoard);
    }
}
