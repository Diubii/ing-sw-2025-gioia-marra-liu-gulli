package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class JoiniRoomOptionsRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 69L;

    public JoiniRoomOptionsRequest(){
        super();
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, ExecutionException, InterruptedException {
        return visitor.visit(this);
    }
}
