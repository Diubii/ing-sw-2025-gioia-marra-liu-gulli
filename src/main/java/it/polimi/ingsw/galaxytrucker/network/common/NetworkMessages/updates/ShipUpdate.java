package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.annotations.TemporaryType;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Ship update.
 */
public class ShipUpdate extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 44662262421L;

    @TemporaryType(temporaryType = "Ship", actualType = "ShipView")
    private final Ship shipView;

    private final String nickName;
    private Boolean onlyFix = false;
    private Boolean shouldDisplay = false;
    private Boolean loadMerci = false;

    /**
     * Instantiates a new Ship update.
     *
     * @param shipView the ship view
     * @param nickName the nick name
     */
    public ShipUpdate(Ship shipView, String nickName) {
        this.shipView = shipView;
        this.nickName = nickName;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets ship view.
     *
     * @return the ship view
     */
    public Ship getShipView() {
        return shipView;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Gets only fix.
     *
     * @return the only fix
     */
    public Boolean getOnlyFix() {
        return onlyFix;
    }

    /**
     * Sets only fix.
     *
     * @param onlyFix the only fix
     */
    public void setOnlyFix(Boolean onlyFix) {
        this.onlyFix = onlyFix;
    }

    /**
     * Gets should display.
     *
     * @return the should display
     */
    public Boolean getShouldDisplay() {
        return shouldDisplay;
    }

    /**
     * Sets load merci.
     *
     * @param loadMerci the load merci
     */
    public void setLoadMerci(Boolean loadMerci) {
        this.loadMerci = loadMerci;
    }

    /**
     * Gets load merci.
     *
     * @return the load merci
     */
    public Boolean getLoadMerci() {
        return loadMerci;
    }

    /**
     * Sets should display.
     *
     * @param shouldDisplay the should display
     */
    public void setShouldDisplay(Boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
    }
}
