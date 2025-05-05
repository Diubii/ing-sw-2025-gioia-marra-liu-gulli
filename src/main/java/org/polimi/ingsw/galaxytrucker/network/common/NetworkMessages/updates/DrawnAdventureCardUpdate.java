package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.util.concurrent.ExecutionException;

public class DrawnAdventureCardUpdate extends NetworkMessage {
    @Serial
    private static final long serialVersionUID = 328742390734L;

    private final AdventureCard card;

    public DrawnAdventureCardUpdate(AdventureCard card) {
        this.card = card;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException {
        return visitor.visit(this);
    }

    public AdventureCard getCard() {
        return card;
    }
}
