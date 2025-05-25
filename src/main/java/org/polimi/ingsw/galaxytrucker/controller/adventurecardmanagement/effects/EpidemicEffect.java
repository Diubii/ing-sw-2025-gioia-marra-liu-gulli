package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class EpidemicEffect {

    public static void check(CardContext context) {
        ArrayList<Position> housings = new ArrayList<>();
        Ship ship = context.getCurrentPlayer().getShip();

        //Prendo tutte le housing units
        housings.addAll(ship.getComponentPositionsFromName("CentralHousingUnit"));
        housings.addAll(ship.getComponentPositionsFromName("ModularHousingUnit"));

        HashSet<String> processedPairs = new HashSet<>(); //per evitare una seconda eliminazione


        for (Position housingUnitPosition : housings) {

            //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
            CentralHousingUnit housingUnit = (CentralHousingUnit) ship.getComponentFromPosition(housingUnitPosition);
            if(housingUnit.getNCrewMembers() == 0 ) {
                continue;
            }
            ArrayList<Pair<Position, Tile>> connectedHousingUnitTilesWithPositions = ship.getConnectedHousingUnitTiles(housingUnitPosition);

            for (Pair<Position, Tile> connectedHousingUnitTileWithPosition : connectedHousingUnitTilesWithPositions) {
                //Dynamic binding, anche se faccio il cast a CentralHousingUnit il tipo dinamico potrebbe essere ModularHousingUnit
                CentralHousingUnit connectedHousingUnit = (CentralHousingUnit) connectedHousingUnitTileWithPosition.getValue().getMyComponent();
                Position connectedPos = connectedHousingUnitTileWithPosition.getKey();

                if(connectedHousingUnit == null || connectedHousingUnit.getNCrewMembers() == 0 ) {
                    continue;
                }
                String key = createSortedKey(housingUnitPosition, connectedPos);
                if(processedPairs.contains(key)) {
                    continue;
                }
                //Se entrambe le housingUnits hanno equipaggio, lo rimuovo da entrambe
                if (housingUnit.getNCrewMembers() > 0 && connectedHousingUnit.getNCrewMembers() > 0) {
                    housingUnit.removeCrewMember();
                    connectedHousingUnit.removeCrewMember();
                    processedPairs.add(key);
                }

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


    private static String createSortedKey(Position p1, Position p2) {
        if (p1.getX() < p2.getX() || (p1.getX() == p2.getX() && p1.getY() < p2.getY())) {
            return p1.getX() + "," + p1.getY() + "-" + p2.getX() + "," + p2.getY();
        } else {
            return p2.getX() + "," + p2.getY() + "-" + p1.getX() + "," + p1.getY();
        }
    }

}
