package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;

public class Epidemic extends AdventureCard {
    public Epidemic(){}

    public void activateEffect(AdventureCardActivator aca, Player p, FlightBoard flightBoard) {
        aca.activateEpidemic(this, p, flightBoard);
    }
}
