package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Nickname request.
 */
public class NicknameRequest extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1062869860895077647L;

    private String nickname;

    /**
     * Instantiates a new Nickname request.
     */
    public NicknameRequest() {
        super(); // o un default valido
    }

    /**
     * Instantiates a new Nickname request.
     *
     * @param nickname the nickname
     */
    public NicknameRequest(String nickname) {
        super();
        this.nickname = nickname;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }


    /**
     * Sets nickname.
     *
     * @param nickname the nickname
     */
// Setter necessari per deserializzazione
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
