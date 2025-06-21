package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Decks update.
 */
public class DecksUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 7293817L;

    private ArrayList<CardDeck> decks = new ArrayList<>();
    private CardDeck flightDeck = null;

    /**
     * Gets decks.
     *
     * @return the decks
     */
    public ArrayList<CardDeck> getDecks() {
        return decks;
    }

    /**
     * Sets decks.
     *
     * @param decks the decks
     */
    public void setDecks(ArrayList<CardDeck> decks) {
        this.decks = decks;
    }

    /**
     * Gets flight deck.
     *
     * @return the flight deck
     */
    public CardDeck getFlightDeck() {
        return flightDeck;
    }

    /**
     * Sets flight deck.
     *
     * @param flightDeck the flight deck
     */
    public void setFlightDeck(CardDeck flightDeck) {
        this.flightDeck = flightDeck;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
