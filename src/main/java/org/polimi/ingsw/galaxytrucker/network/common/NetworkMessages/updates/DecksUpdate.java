package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DecksUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 7293817L;

    private ArrayList<CardDeck> decks = new ArrayList<>();
    private CardDeck flightDeck = null;

    public ArrayList<CardDeck> getDecks() {
        return decks;
    }

    public void setDecks(ArrayList<CardDeck> decks) {
        this.decks = decks;
    }

    public CardDeck getFlightDeck() {
        return flightDeck;
    }

    public void setFlightDeck(CardDeck flightDeck) {
        this.flightDeck = flightDeck;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
