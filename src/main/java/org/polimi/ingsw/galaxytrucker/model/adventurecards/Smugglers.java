package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class Smugglers extends AdventureCard {
    private final int firePower;
    private int penalty;
    private ArrayList<Good> goods;
    public int getPenalty() {
        return penalty;
    }

    @JsonCreator
    public Smugglers(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("firePower") int firePower,
                         @JsonProperty("penalty")int penalty,
                         @JsonProperty("good") ArrayList<Good> goods)
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.firePower = firePower;
        this.penalty = penalty;
        this.goods = goods;

    }

    public Smugglers(int firePower){
        this.firePower = firePower;
    }

    public ArrayList<Good> getGoods() {
        return new ArrayList<Good>(goods);
    }
    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard, GameController gameController) {
        aca.activateSmugglers(this, p, flightBoard, gameController);
    }
}
