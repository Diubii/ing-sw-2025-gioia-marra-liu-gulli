package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;

import java.util.ArrayList;
import java.util.Collections;

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

    private int getSize(){
        return cards.size();
    }

    /**
     * Adds a card to che current {@link CardDeck}
     * @param card The card to add
     */
    public void addCard(AdventureCard card) {
        cards.addFirst(card);
    }

    /**
     * Pops the first {@link AdventureCard} from the stack.
     * @return The popped {@link AdventureCard}
     */
    public AdventureCard pop() {
        AdventureCard popped = cards.getFirst();
        cards.remove(popped);
        return popped;
    }

    /**
     * Merges a CardStack to the current one while clearing it.
     * Returns itself to allow chaining.
     * @author Alessandro Giuseppe Gioia
     * @param cd The {@link CardDeck}
     * @return {@link CardDeck}
    */
    public CardDeck merge(CardDeck cd) {
        while(cd.getSize()!=0){
            cards.addFirst(cd.pop());
        }
        return this;
    }

    /**
     * Shuffles the CardStack.
     * @author Alessandro Giuseppe Gioia
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }

    /**
     * Clears the CardStack.
     * @author Alessandro Giuseppe Gioia
     */
    public void clear(){
        cards.clear();
    }
}
