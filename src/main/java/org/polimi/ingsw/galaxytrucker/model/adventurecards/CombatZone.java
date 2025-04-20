package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class CombatZone extends AdventureCard {
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
                         @JsonProperty("projectiles") ArrayList<Projectile> projectiles)
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
    }

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateCombatZone(this, p, flightBoard);
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
}
