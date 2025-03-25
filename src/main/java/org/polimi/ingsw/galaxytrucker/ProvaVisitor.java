package org.polimi.ingsw.galaxytrucker;

import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedShip;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Stardust;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.abstracts.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class ProvaVisitor {
    public static void main(String[] args) {
        AdventureCard stardust = new Stardust();
        AdventureCard abandonedShip = new AbandonedShip(0 ,0 ,0 );
        ArrayList<AdventureCard> acs = new ArrayList<>();

        acs.add(stardust);
        acs.add(abandonedShip);

        AdventureCardActivator aca = new AdventureCardEffects();
        FlightBoard fb = new FlightBoard(null, true);

        for(AdventureCard ac : acs){
            ac.activateEffect(aca, new Player("o",0 ,0 , true), fb);
        }
    }
}
