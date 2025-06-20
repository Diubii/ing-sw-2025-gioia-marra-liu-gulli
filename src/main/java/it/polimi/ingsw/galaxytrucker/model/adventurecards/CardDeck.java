package it.polimi.ingsw.galaxytrucker.model.adventurecards;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a deck of adventure cards in the game.
 * The stack contains a collection of {@link AdventureCard} objects and may be
 * either spyable or not, depending on the deck's configuration.
 */
public class CardDeck implements Serializable {

    @Serial
    private static final long serialVersionUID = 19819109L;

    /**
     * The list of cards in the deck.
     */
    private final ArrayList<AdventureCard> cards;
    /**
     * Indicates whether the card deck is spyable (can be observed by players).
     */
    public final boolean spyable;

    /**
     * Checks if the card deck is spyable (can be observed).
     *
     * @return {@code true} if the deck is spyable, otherwise {@code false}.
     */
    public boolean isSpyable() {
        return spyable;
    }

    /**
     * Constructs a {@link CardDeck} with the specified list of cards and spyable status.
     *
     * @param cards   A list of {@link AdventureCard} objects in the deck.
     * @param spyable A boolean value indicating if the stack is spyable.
     */
    public CardDeck(ArrayList<AdventureCard> cards, boolean spyable) {
        this.cards = cards;
        this.spyable = spyable;
    }

    /**
     * Constructs an empty {@link CardDeck} with a spyable status.
     *
     * @param spyable A boolean value indicating if the stack is spyable.
     */
    public CardDeck(boolean spyable) {
        this.spyable = spyable;
        this.cards = new ArrayList<>();
    }

    public int getSize() {
        return cards.size();
    }


    /**
     * Adds a card to che current {@link CardDeck}
     *
     * @param card The card to add
     */
    public void addCard(AdventureCard card) {
        cards.addFirst(card);
    }

    /**
     * Pops the first {@link AdventureCard} from the stack.
     *
     * @return The popped {@link AdventureCard}
     */
    public AdventureCard pop() {
        AdventureCard popped = cards.getFirst();
        cards.remove(popped);
        return popped;
    }

    /**
     * Merges a {@link CardDeck} to the current one while clearing it.
     * Returns itself to allow chaining.
     *
     * @param cd The {@link CardDeck}
     * @return {@link CardDeck}
     * @author Alessandro Giuseppe Gioia
     */
    public CardDeck merge(CardDeck cd) {
        for (AdventureCard c : cd.getCards()) {
            cards.addFirst(c);
        }
        return this;
    }

    /**
     * Shuffles the CardDeck.
     *
     * @author Alessandro Giuseppe Gioia
     */
    public void shuffle() {
//        Collections.shuffle(cards);
    }

    /**
     * Clears the CardDeck.
     *
     * @author Alessandro Giuseppe Gioia
     */
    public void clear() {
        cards.clear();
    }

    /**
     * Returns the first AdventureCard without popping it.
     *
     * @return {@link AdventureCard}
     */
    public AdventureCard peek() {
        return cards.getFirst();
    }

    /**
     * <b>For deck building purposes.</b>
     * <br>
     * This method, if the first card is not level two, searches for the first level two card in the deck and puts it on top.
     */
    public void putFirstLvl2CardOnTop() {
        if (this.peek().getLevel() != 2) {
            AdventureCard toBeFirst = cards.stream().filter(card -> card.getLevel() == 2).findFirst().get();
            cards.remove(toBeFirst);
            cards.addFirst(toBeFirst);
        }
    }

    public ArrayList<AdventureCard> getCards() {
        return cards;
    }
}
