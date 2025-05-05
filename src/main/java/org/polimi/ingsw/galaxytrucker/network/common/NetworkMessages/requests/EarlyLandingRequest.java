package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class EarlyLandingRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3287423907322938L;


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException {
        return visitor.visit(this);
    }
}
