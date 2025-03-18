package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.Player;

import java.util.ArrayList;

public class Planets extends AdventureCard {
    private final ArrayList<Planet> planets;
    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public Planets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public void activateEffect(Player player){

    }
}
