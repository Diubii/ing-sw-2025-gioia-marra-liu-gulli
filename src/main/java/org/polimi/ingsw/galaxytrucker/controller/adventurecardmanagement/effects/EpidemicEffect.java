package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;

import java.util.ArrayList;

public abstract class EpidemicEffect {

    public static void check(CardContext context) {
        ArrayList<Position> housings = new ArrayList<>();
        Ship ship = context.getCurrentPlayer().getShip();

        //Prendo tutte le housing units
        housings.addAll(ship.getComponentPositionsFromName("CentralHousingUnit"));
        housings.addAll(ship.getComponentPositionsFromName("ModularHousingUnit"));

        for (Position housingUnitPosition : housings) {
            //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
            CentralHousingUnit housingUnit = (CentralHousingUnit) ship.getComponentFromPosition(housingUnitPosition);

            ArrayList<Pair<Position, Tile>> connectedHousingUnitTilesWithPositions = ship.getConnectedHousingUnitTiles(housingUnitPosition);
            for (Pair<Position, Tile> connectedHousingUnitTileWithPosition : connectedHousingUnitTilesWithPositions) {
                //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
                CentralHousingUnit connectedHousingUnit = (CentralHousingUnit) connectedHousingUnitTileWithPosition.getValue().getMyComponent();

                //Se entrambe le housingUnits hanno equipaggio, lo rimuovo da entrambe
                if (housingUnit.getNCrewMembers() != 0 && connectedHousingUnit.getNCrewMembers() != 0) {
                    housingUnit.removeCrewMember();
                    connectedHousingUnit.removeCrewMember();
                }

                //Se nella housing unit connessa non ci sono più membri la rimuovo dalle housing units da controllare
                if (connectedHousingUnit.getNCrewMembers() == 0)
                    housings.remove(connectedHousingUnitTileWithPosition.getKey());

                //Se nella housing unit che stiamo guardando non ci sono più membri, esco dal foreach
                if (housingUnit.getNCrewMembers() == 0)
                    break;
            }
        }

        if (context.getCurrentPlayer() != context.getCurrentRankedPlayers().getLast()) {
            //Eseguo ancora questo metodo con il giocatore successivo
            context.nextPlayer();
            context.executePhase();
        } else {
            //Execute CommonEffects::end
            context.nextPhase();
            context.executePhase();
        }
    }
}
