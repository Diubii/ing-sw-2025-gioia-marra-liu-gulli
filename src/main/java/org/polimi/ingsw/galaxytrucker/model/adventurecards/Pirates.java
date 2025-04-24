package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;

import java.util.ArrayList;

public class Pirates extends AdventureCard {
    private final ArrayList<Projectile> cannonFires;
    private final int firePower;
    /** The amount of credits lost or gained. */
    private final int credits;

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
                   @JsonProperty("cannonFires") ArrayList<Projectile> cannonFires,
                   @JsonProperty("credits") int credits) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.cannonFires = cannonFires;
        this.credits = credits;
    }

    public Pirates(ArrayList<Projectile> cannonFires, int firePower, int credits) {
        this.cannonFires = cannonFires;
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


    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activatePirates(this, p, flightBoard);
    }

    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
