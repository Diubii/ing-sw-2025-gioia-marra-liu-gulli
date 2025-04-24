package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.io.Serial;
import java.util.ArrayList;

public class Planets extends AdventureCard {


    @Serial
    private static final long serialVersionUID = 9994343L;

    private final ArrayList<Planet> planets;
    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    @JsonCreator
    public Planets(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("planets") ArrayList<Planet> planets)

    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.planets = planets;

    }

    public Planets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard, GameController gameController){
        aca.activatePlanets(this, p, flightBoard, gameController);
    }
}
