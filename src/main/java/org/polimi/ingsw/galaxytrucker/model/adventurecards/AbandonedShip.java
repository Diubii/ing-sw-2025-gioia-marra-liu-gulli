package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This card introduces a scenario where humans, aliens, and credits are lost.
 */
public class AbandonedShip extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 999991L;
    /** The number of humans lost. */
    private final int crewMembersLost;
    /** The amount of credits lost or gained. */
    private final int credits;

    @JsonCreator
    public AbandonedShip(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("crewMembersLost") int crewMembersLost ,
                   @JsonProperty("credits")  int credits )
            {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.crewMembersLost = crewMembersLost;
        this.credits = credits;
        this.facultative = true;
    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        aca.visitAbandonedShip(this, rankedPlayers, lobbyManager);
    }

    public int getCrewMembersLost() {
        return crewMembersLost;
    }


    public int getCredits() {
        return credits;
    }
}
