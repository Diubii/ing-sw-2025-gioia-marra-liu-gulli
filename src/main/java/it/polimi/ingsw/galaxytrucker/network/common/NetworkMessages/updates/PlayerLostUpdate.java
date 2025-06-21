package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.enums.PlayerLostReason;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Player lost update.
 */
public class PlayerLostUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 13975924237534L;

    private final String nickname;
    private final PlayerLostReason reason;

    /**
     * Instantiates a new Player lost update.
     *
     * @param nickname the nickname
     * @param reason   the reason
     */
    public PlayerLostUpdate(String nickname, PlayerLostReason reason) {
        this.nickname = nickname;
        this.reason = reason;
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
     * Gets reason.
     *
     * @return the reason
     */
    public PlayerLostReason getReason() {
        return reason;
    }
}
