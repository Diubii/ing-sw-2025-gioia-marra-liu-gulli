package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;

import java.io.Serial;
import java.util.ArrayList;
/**
 * Represents an Abandoned Station adventure card.
 * The card allows the player to gain or lose goods based on the card's effect.
 */
public class AbandonedStation extends AdventureCard {
    @Serial
    private static final long serialVersionUID = 99199L;

    /** A list of goods associated with the abandoned station. */
    private final ArrayList<Good> goods;



    private final int crewMemebers;

    public int getCrewMemebers() {
        return crewMemebers;
    }

    /**
     * Gets the list of goods found at the abandoned station.
     *
     * @return A list of {@link Good} objects associated with the abandoned station.
     */
    public ArrayList<Good> getGoods() { return goods; }

    @JsonCreator
    public AbandonedStation(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("goods")   ArrayList<Good> goods,
                         @JsonProperty("crewMembers")   int crewMemebers)
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.goods = goods;
        this.crewMemebers = crewMemebers;
    }


    /**
     * Activates the effect of the Abandoned Station card by triggering the effect
     * through the given {@link AdventureCardActivator}.
     *
     * @param aca         The activator responsible for triggering the Abandoned Station's effect.
     * @param flightBoard
     */
    public void activateEffect(AdventureCardActivator aca,ArrayList<Player> player, FlightBoard flightBoard, GameController gameController) {
        aca.activateAbandonedStation(this,player , flightBoard,  gameController);
    }

    @Override
    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> player, FlightBoard flightBoard) {

    }

    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
