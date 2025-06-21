package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;

/**
 * The type Nickname response.
 */
public class NicknameResponse extends NetworkMessage {
    @Serial
    private static final long serialVersionUID = 43870435536632L;

    private String response;

    /**
     * Instantiates a new Nickname response.
     *
     * @param response the response
     * @param id       the id
     */
    public NicknameResponse(String response, int id) {
        super(id);
        this.response = response;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets response.
     *
     * @param response the response
     */
    public void setResponse(String response) {
        this.response = response;
    }
}
