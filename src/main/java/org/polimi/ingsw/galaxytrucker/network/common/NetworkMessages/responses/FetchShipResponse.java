package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.annotations.TemporaryType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class FetchShipResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 52928352963L;

    private final String targetNickname;

    @TemporaryType(temporaryType = "Ship", actualType = "ShipView")
    private final Ship targetShipView;

    @TemporaryType(temporaryType = "Ship", actualType = "ShipView")
    public FetchShipResponse(String targetNickname, Ship targetShipView) {
        this.targetNickname = targetNickname;
        this.targetShipView = targetShipView;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public Ship getTargetShipView() {
        return targetShipView;
    }
}
