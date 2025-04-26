package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;

import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Epidemic extends AdventureCard {
    @Serial
    private static final long serialVersionUID = 919999L;
    public Epidemic(){}

    @JsonCreator
    public Epidemic(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("affectsAll") boolean affectsAll)
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = true;

    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        aca.visitEpidemic(this, rankedPlayers, lobbyManager);
    }
    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
