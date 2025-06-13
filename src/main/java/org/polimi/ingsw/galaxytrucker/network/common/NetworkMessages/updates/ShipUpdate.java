package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.annotations.TemporaryType;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

public class ShipUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 44662262421L;

    @TemporaryType(temporaryType = "Ship", actualType = "ShipView")
    private final Ship shipView;

    private final String nickName;
    private Boolean onlyFix = false;
    private Boolean shouldDisplay = false;
    private Boolean loadMerci = false;

    public ShipUpdate(Ship shipView, String nickName) {
        this.shipView = shipView;
        this.nickName = nickName;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    public Ship getShipView() {
        return shipView;
    }

    public String getNickName() {
        return nickName;
    }

    public Boolean getOnlyFix() {
        return onlyFix;
    }

    public void setOnlyFix(Boolean onlyFix) {
        this.onlyFix = onlyFix;
    }

    public Boolean getShouldDisplay() {
        return shouldDisplay;
    }

    public void setLoadMerci(Boolean loadMerci) {
        this.loadMerci = loadMerci;
    }

    public Boolean getLoadMerci() {
        return loadMerci;
    }

    public void setShouldDisplay(Boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
    }
}
