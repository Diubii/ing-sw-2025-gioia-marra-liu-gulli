package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Planets extends AdventureCard {


    @Serial
    private static final long serialVersionUID = 9994343L;

    private final ArrayList<Planet> planets;
    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    @JsonCreator
    public Planets(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("planets") ArrayList<Planet> planets,
                          @JsonProperty("affectsAll") boolean affectsAll)

    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.planets = planets;
        this.facultative = true;
    }

    public Planets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager){
        aca.visitPlanets(this, rankedPlayers, lobbyManager);
    }
    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
