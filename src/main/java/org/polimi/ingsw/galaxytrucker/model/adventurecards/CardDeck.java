package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;

import java.util.ArrayList;
/**
 * Represents a stack of adventure cards in the game.
 * The stack contains a collection of {@link AdventureCard} objects and may be
 * either spyable or not, depending on the stack's configuration.
 */
public class CardDeck {
    /** The list of cards in the stack. */
    private ArrayList<AdventureCard> cards;
    /** Indicates whether the card stack is spyable (can be observed by players). */
    public final boolean spyable;

    /**
     * Checks if the card stack is spyable (can be observed).
     *
     * @return {@code true} if the stack is spyable, otherwise {@code false}.
     */
    public boolean isSpyable() {
        return spyable;
    }

    /**
     * Constructs a CardStack with the specified list of cards and spyable status.
     *
     * @param cards A list of {@link AdventureCard} objects in the stack.
     * @param spyable A boolean value indicating if the stack is spyable.
     */
    public CardDeck(ArrayList<AdventureCard> cards, boolean spyable) {
        this.cards = cards;
        this.spyable = spyable;
    }

    /**
     * Adds a new card to the stack.
     *
     * @param card The {@link AdventureCard} to add to the stack.
     */
    public void addCard(AdventureCard card) {

    }

    //TODO: cambia il tipo da void a quello commentato
    /**
     * Removes and returns the top card from the stack.
     * (Method to be implemented)
     *
     * @return The removed {@link AdventureCard} from the top of the stack.
     */
    public void /*AdventureCard*/ pop() {

    }


    public void /*CardStack*/ merge(CardDeck cs) {}

    /**
     * Shuffles the cards in the stack.
     * (Method to be implemented)
     *
     * @return A new shuffled {@link CardDeck}.
     */
    public void /*CardStack*/ shuffle(){}

    /**
     * Gets the list of cards in the card stack.
     *
     * @return A list of {@link AdventureCard} objects in the stack.
     */
    public ArrayList<AdventureCard> getCards() {
        return cards;
    }
}
