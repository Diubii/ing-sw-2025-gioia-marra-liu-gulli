package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

public class CombatZone extends AdventureCard {
    public CombatZone() {}

    public void activateEffect(AdventureCardActivator aca, Player p, FlightBoard flightBoard) {
        aca.activateCombatZone(this, p, flightBoard);
    }
}
