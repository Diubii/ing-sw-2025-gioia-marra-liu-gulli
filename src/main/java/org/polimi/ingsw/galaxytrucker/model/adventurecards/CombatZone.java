package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;

import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CombatZone extends AdventureCard {

    @Serial
    private static final long serialVersionUID = 999929L;
    private final int crewMembersLost;
    private final int goodsLost;
    private final ArrayList<Projectile> projectiles;

    public CombatZone(int crewMembersLost, int goodsLost, ArrayList<Projectile> projectiles) {
        this.crewMembersLost = crewMembersLost;
        this.goodsLost = goodsLost;
        this.projectiles = projectiles;
    }

    @JsonCreator
    public CombatZone(   @JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("crewMembersLost") int crewMembersLost,
                         @JsonProperty("goodsLost") int goodsLost,
                         @JsonProperty("projectiles") ArrayList<Projectile> projectiles,
                         @JsonProperty("affectsAll") boolean affectsAll)
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.crewMembersLost = crewMembersLost;
        this.goodsLost = goodsLost;
        this.projectiles = projectiles;
        this.facultative = true;

    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException {
        aca.visitCombatZone(this, rankedPlayers, lobbyManager);
    }

    public int getCrewMembersLost() {
        return crewMembersLost;
    }

    public int getGoodsLost() {
        return goodsLost;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
