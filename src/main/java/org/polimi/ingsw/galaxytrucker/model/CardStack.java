package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;

import java.util.ArrayList;

public class CardStack {
    private ArrayList<AdventureCard> cards;
    public final boolean spyable;

    public ArrayList<AdventureCard> getCards() {
        return cards;
    }
    public boolean isSpyable() {
        return spyable;
    }

    public CardStack(ArrayList<AdventureCard> cards, boolean spyable) {
        this.cards = cards;
        this.spyable = spyable;
    }

    public void addCard(AdventureCard card) {

    }

    //TODO: cambia il tipo da void a quello commentato
    public void /*AdventureCard*/ pop() {

    }
    public void /*CardStack*/ merge(CardStack cs) {}
    public void /*CardStack*/ shuffle(){}
}
