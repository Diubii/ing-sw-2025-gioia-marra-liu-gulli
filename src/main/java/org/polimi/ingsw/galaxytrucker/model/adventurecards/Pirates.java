package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class Pirates extends AdventureCard {
    private final ArrayList<Projectile> cannonFires;
    public ArrayList<Projectile> getCannonFires() {
        return cannonFires;
    }

    @JsonCreator
    public Pirates(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("firePower") int firePower ,
                   @JsonProperty("cannonFires") ArrayList<Projectile> cannonFires) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.cannonFires = cannonFires;
    }

    public Pirates(ArrayList<Projectile> cannonFires) {
        this.cannonFires = cannonFires;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activatePirates(this, p, flightBoard);
    }
}
