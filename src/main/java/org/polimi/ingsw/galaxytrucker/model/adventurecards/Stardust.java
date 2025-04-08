package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

public class Stardust extends AdventureCard {
    public Stardust(){}

    public void activateEffect(AdventureCardActivator aca, Player p, FlightBoard flightBoard){
        aca.activateStardust(this, p, flightBoard);
    }
}
