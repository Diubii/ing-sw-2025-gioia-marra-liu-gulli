package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class CombatZone extends AdventureCard {
    public CombatZone() {}

    public void activateEffect(AdventureCardActivator aca, ArrayList<Player> p, FlightBoard flightBoard) {
        aca.activateCombatZone(this, p, flightBoard);
    }
}
