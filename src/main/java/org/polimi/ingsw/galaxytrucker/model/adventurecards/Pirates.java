package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.io.Serial;
import java.util.ArrayList;

public class Pirates extends AdventureCard {


    @Serial
    private static final long serialVersionUID = 32233232L;


    private final ArrayList<Projectile> cannonFires;
    private final int firePower;
    /** The amount of credits lost or gained. */
    private final int credits;

    public ArrayList<Projectile> getCannonFires() {
        return cannonFires;
    }

    @JsonCreator
    public Pirates(@JsonProperty("id") int id,
                   @JsonProperty("level") int level,
                   @JsonProperty("daysLost") int daysLost,
                   @JsonProperty("name") String name,
                   @JsonProperty("learningFlight") boolean learningFlight,
                   @JsonProperty("firePower") int firePower ,
                   @JsonProperty("cannonFires") ArrayList<Projectile> cannonFires,
                   @JsonProperty("credits") int credits) {
        this.id = id;
        this.level = level;
        this.daysLost = daysLost;
        this.name = name;
        this.learningFlight = learningFlight;
        this.firePower = firePower;
        this.cannonFires = cannonFires;
        this.credits = credits;
    }

    public Pirates(ArrayList<Projectile> cannonFires, int firePower, int credits) {
        this.cannonFires = cannonFires;
        this.firePower = firePower;
        this.credits = credits;
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
        aca.visitPirates(this, rankedPlayers, lobbyManager);
    }
}
