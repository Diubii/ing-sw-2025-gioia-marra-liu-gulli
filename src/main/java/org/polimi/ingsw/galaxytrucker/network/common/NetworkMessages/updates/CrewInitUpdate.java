package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class CrewInitUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 947463L;

    ArrayList<Pair<Position, AlienColor>> crewPos = new ArrayList<>();

    public void addCrewPos(Pair<Position, AlienColor> crewPos) {
        this.crewPos.add(crewPos);
    }

    public ArrayList<Pair<Position, AlienColor>> getCrewPos() {
        return crewPos;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition {
        return visitor.visit(this);
    }
}
