package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class Slavers extends AdventureCard {
    private final int penalty;
    public int getPenalty() {
        return penalty;
    }

    @JsonCreator
    public Slavers(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("firePower") int firePower ,
                   @JsonProperty("penalty") int penalty) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.penalty = penalty;
    }

    public Slavers(int penalty) {
        this.penalty = penalty;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateSlavers(this, p, flightBoard);
    }
}
