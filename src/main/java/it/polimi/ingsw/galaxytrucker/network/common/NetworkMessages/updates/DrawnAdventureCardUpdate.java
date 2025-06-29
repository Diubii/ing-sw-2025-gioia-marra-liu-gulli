package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;

/**
 * Network update sent when a new AdventureCard is drawn.
 */
public class DrawnAdventureCardUpdate extends NetworkMessage {
    @Serial
    private static final long serialVersionUID = 328742390734L;

    private final AdventureCard card;

    public DrawnAdventureCardUpdate(AdventureCard card) {
        this.card = card;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public AdventureCard getCard() {
        return card;
    }
}
