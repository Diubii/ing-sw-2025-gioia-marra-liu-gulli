package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serializable;
import java.util.ArrayList;

public class AskPositionUpdate extends NetworkMessage implements Serializable {

    ArrayList<Integer> validPositions;

    public AskPositionUpdate(ArrayList<Integer> validPositions) {

        super();
        this.validPositions = validPositions;
    }

    public ArrayList<Integer> getValidPositions() {
        return validPositions;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition {
        return visitor.visit(this);
    }
}
