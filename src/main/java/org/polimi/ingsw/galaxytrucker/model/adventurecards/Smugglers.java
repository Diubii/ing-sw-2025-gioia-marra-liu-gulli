package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Smugglers extends AdventureCard {
    @Serial
    private static final long serialVersionUID = 888888889L;
    private final int firePower;
    private int penalty;
    private ArrayList<Good> goods;
    public int getPenalty() {
        return penalty;
    }

    @JsonCreator
    public Smugglers(@JsonProperty("id") int id,
                         @JsonProperty("level") int level,
                         @JsonProperty("daysLost") int daysLost,
                         @JsonProperty("name") String name,
                         @JsonProperty("learningFlight") boolean learningFlight,
                         @JsonProperty("firePower") int firePower,
                         @JsonProperty("penalty")int penalty,
                         @JsonProperty("good") ArrayList<Good> goods)
    {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.affectsAll = false;
        this.firePower = firePower;
        this.penalty = penalty;
        this.goods = goods;

    }

    public Smugglers(int firePower){
        this.firePower = firePower;
    }

    public ArrayList<Good> getGoods() {
        return new ArrayList<Good>(goods);
    }
    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        aca.visitSmugglers(this, rankedPlayers, lobbyManager);
    }
    public String[] accept(AdventureCardPrintVisitorInterface visitor){
        return visitor.visit(this);
    }
}
