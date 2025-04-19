package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.ArrayList;
/**
 * Represents an Abandoned Station adventure card.
 * The card allows the player to gain or lose goods based on the card's effect.
 */
public class AbandonedStation extends AdventureCard {
    /** A list of goods associated with the abandoned station. */
    private final ArrayList<Good> goods;
    /**
     * Gets the list of goods found at the abandoned station.
     *
     * @return A list of {@link Good} objects associated with the abandoned station.
     */
    public ArrayList<Good> getGoods() { return goods; }
    /**
     * Constructs an AbandonedStation adventure card with a specified list of goods.
     *
     * @param goods A list of {@link Good} objects representing the goods at the abandoned station.
     */
    public AbandonedStation(ArrayList<Good> goods) {
        this.goods=goods;
    }
    /**
     * Activates the effect of the Abandoned Station card by triggering the effect
     * through the given {@link AdventureCardActivator}.
     *
     * @param aca         The activator responsible for triggering the Abandoned Station's effect.
     * @param flightBoard
     */
    public void activateEffect(AdventureCardActivator aca,ArrayList<Player> player, FlightBoard flightBoard) {
        aca.activateAbandonedStation(this,player , flightBoard);
    }
}
