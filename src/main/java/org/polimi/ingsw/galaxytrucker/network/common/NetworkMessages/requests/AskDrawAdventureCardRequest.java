package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;


import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class AskDrawAdventureCardRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3287423907322938L;


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

}
