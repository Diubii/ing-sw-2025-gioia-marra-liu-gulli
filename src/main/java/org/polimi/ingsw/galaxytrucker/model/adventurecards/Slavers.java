package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitorInterface;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Slavers extends AdventureCard {


    @Serial
    private static final long serialVersionUID = 99434216L;

    private final int penalty;
    private final int firePower;



    /** The amount of credits lost or gained. */
    private final int credits;
    public int getPenalty() {
        return penalty;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonCreator
    public Slavers(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("firePower") int firePower ,
                   @JsonProperty("penalty") int penalty,
                   @JsonProperty("credits") int credits,
                   @JsonProperty("affectsAll") boolean affectsAll) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.penalty = penalty;
        this.credits = credits;
        this.affectsAll = false;

    }

    public Slavers(int penalty, int firePower,int credits) {
        this.penalty = penalty;
        this.firePower = firePower;
        this.credits = credits;
        this.affectsAll = false;
    }

    /**
     * Gets the firepower of the enemy.
     *
     * @return The firepower value.
     */
    public int getFirePower() {
        return firePower;
    }

    public int getCredits() {
        return credits;
    }

    public void activateEffect(AdventureCardVisitorsInterface aca, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) {
        aca.visitSlavers(this, rankedPlayers, lobbyManager);
    }

    @Override
    public String[] accept(AdventureCardPrintVisitorInterface visitor) {
        return  visitor.visit(this);
    }
}