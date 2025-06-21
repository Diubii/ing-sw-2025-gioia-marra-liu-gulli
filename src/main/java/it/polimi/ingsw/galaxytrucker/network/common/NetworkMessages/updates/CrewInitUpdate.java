package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;
import javafx.util.Pair;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Crew init update.
 */
public class CrewInitUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 947463L;

    /**
     * The Crew pos.
     */
    ArrayList<Pair<Position, AlienColor>> crewPos = new ArrayList<>();

    /**
     * Add crew pos.
     *
     * @param crewPos the crew pos
     */
    public void addCrewPos(Pair<Position, AlienColor> crewPos) {
        this.crewPos.add(crewPos);
    }

    /**
     * Gets crew pos.
     *
     * @return the crew pos
     */
    public ArrayList<Pair<Position, AlienColor>> getCrewPos() {
        return crewPos;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
