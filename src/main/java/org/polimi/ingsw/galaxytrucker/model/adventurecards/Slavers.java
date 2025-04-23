package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class Slavers extends AdventureCard {
    private final int penalty;
    private final int firePower;



    /** The amount of credits lost or gained. */
    private final int credits;
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
                   @JsonProperty("penalty") int penalty,
                   @JsonProperty("credits") int credits) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.penalty = penalty;
        this.credits = credits;
    }

    public Slavers(int penalty, int firePower,int credits) {
        this.penalty = penalty;
        this.firePower = firePower;
        this.credits = credits;
    }

    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }

    public int getCredits() {
        return credits;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard, GameController gameController) {
        aca.activateSlavers(this, p, flightBoard, gameController);
    }
}
