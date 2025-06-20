package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class AdventureCardExampleResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 434343L;

    private final String body;

    public AdventureCardExampleResponse(String body) {

        this.body = body;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getBody() {
        return body;
    }
}
