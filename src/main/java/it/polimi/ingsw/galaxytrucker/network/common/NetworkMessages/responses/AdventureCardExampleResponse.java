package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Adventure card example response.
 */
public class AdventureCardExampleResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 434343L;

    private final String body;

    /**
     * Instantiates a new Adventure card example response.
     *
     * @param body the body
     */
    public AdventureCardExampleResponse(String body) {

        this.body = body;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }
}
