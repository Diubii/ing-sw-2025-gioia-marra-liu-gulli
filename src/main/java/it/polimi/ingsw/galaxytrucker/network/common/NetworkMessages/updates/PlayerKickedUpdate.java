package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Player kicked update.
 */
public class PlayerKickedUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 13975924237534L;

    private final String nickname;

    /**
     * Instantiates a new Player kicked update.
     *
     * @param nickname the nickname
     */
    public PlayerKickedUpdate(String nickname) {
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
}
