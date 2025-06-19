package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.enums.PlayerLostReason;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class PlayerLostUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 13975924237534L;

    private final String nickname;
    private final PlayerLostReason reason;

    public PlayerLostUpdate(String nickname, PlayerLostReason reason) {
        this.nickname = nickname;
        this.reason = reason;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getNickname() {
        return nickname;
    }

    public PlayerLostReason getReason() {
        return reason;
    }
}
