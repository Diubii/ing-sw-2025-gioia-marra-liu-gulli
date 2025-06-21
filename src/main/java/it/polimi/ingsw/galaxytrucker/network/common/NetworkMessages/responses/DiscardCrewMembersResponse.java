package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Discard crew members response.
 */
public class DiscardCrewMembersResponse extends NetworkMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 5563957029L;

    private final ArrayList<Position> housingPositions;

    /**
     * Instantiates a new Discard crew members response.
     *
     * @param housingPositions the housing positions
     */
    public DiscardCrewMembersResponse(ArrayList<Position> housingPositions) {
        this.housingPositions = housingPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * Gets housing positions.
     *
     * @return the housing positions
     */
    public ArrayList<Position> getHousingPositions() {
        return housingPositions;
    }
}
