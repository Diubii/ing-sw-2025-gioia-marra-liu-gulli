package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;
import javafx.util.Pair;

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
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
