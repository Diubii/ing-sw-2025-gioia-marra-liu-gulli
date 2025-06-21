package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Ask position update.
 */
public class AskPositionUpdate extends NetworkMessage implements Serializable {

    /**
     * The Valid positions.
     */
    ArrayList<Integer> validPositions;

    /**
     * The Nickname.
     */
//test
    public String nickname;

    /**
     * Instantiates a new Ask position update.
     *
     * @param validPositions the valid positions
     */
    public AskPositionUpdate(ArrayList<Integer> validPositions) {

        super();
        this.validPositions = validPositions;
    }

    /**
     * Gets valid positions.
     *
     * @return the valid positions
     */
    public ArrayList<Integer> getValidPositions() {
        return validPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
