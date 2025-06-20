package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class AskPositionUpdate extends NetworkMessage implements Serializable {

    ArrayList<Integer> validPositions;

    //test
    public String nickname;

    public AskPositionUpdate(ArrayList<Integer> validPositions) {

        super();
        this.validPositions = validPositions;
    }

    public ArrayList<Integer> getValidPositions() {
        return validPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
