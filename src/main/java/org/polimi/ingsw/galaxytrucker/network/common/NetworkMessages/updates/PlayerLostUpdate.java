package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class PlayerLostUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 13975924237534L;

    private final String nickname;
    private final boolean isLandingEarly;

    public PlayerLostUpdate(String nickname, boolean isLandingEarly) {
        this.nickname = nickname;
        this.isLandingEarly = isLandingEarly;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isLandingEarly() {
        return isLandingEarly;
    }
}
