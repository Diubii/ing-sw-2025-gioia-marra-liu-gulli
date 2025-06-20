package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests;

import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class AskTrunkRequest extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 834562902135L;

    private final ArrayList<Ship> trunks;

    public AskTrunkRequest(ArrayList<Ship> trunks) {
        this.trunks = trunks;
    }

    public ArrayList<Ship> getTrunks() {
        return trunks;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
