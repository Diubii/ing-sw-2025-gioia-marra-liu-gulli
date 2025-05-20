package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class AskTimerInfoRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123123131L; // Unique serialization ID

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public AskTimerInfoRequest() {
        super(); // Call to the parent class constructor
    }
}
