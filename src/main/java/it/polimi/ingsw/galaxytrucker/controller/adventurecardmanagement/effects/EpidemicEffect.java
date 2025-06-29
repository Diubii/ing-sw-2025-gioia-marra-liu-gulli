package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code EpidemicEffect} class implements the logic for resolving the effect of the
 * "Epidemic" adventure card. This effect removes crew members from housing units in pairs
 * that are connected and both occupied.
 */
public abstract class EpidemicEffect {
    /**
     * Applies the epidemic effect to the current player's ship.
     * <p>
     * Iterates through all crewed housing units and removes one crew member from each
     * of any pair of connected units that both have crew.
     * @param context the {@link CardContext} providing the current player and game state.
     */

    public static void check(CardContext context) {
        ArrayList<Position> housings = new ArrayList<>();
        Player currentPlayer = context.getCurrentPlayer();
        Ship ship = context.getCurrentPlayer().getShip();

        
        broadcastGameMessage(context,"Il giocatore "+ currentPlayer.getNickName() +" sta subendo l’effetto della carta e gli equipaggi nelle cabine idonee verranno rimossi");
        sleepSafe(600);


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
            broadcast(context, new ShipUpdate(ship,currentPlayer.getNickName()));
            //Eseguo ancora questo metodo con il giocatore successivo

            broadcastGameMessage(context,"Effetto concluso. Tocca al prossimo giocatore.");
            sleepSafe(600);

            context.nextPlayer();
            context.executePhase();
        } else {
            broadcast(context, new ShipUpdate(ship,currentPlayer.getNickName()));
            broadcastGameMessage(context,"Effetto concluso.");
            sleepSafe(600);

            //Execute CommonEffects::end
            context.nextPhase();
            context.executePhase();
        }
    }


    /**
     * Utility method for generating a unique, sorted string key for a pair of positions.
     * <p>
     * Ensures consistent ordering of position pairs to avoid processing the same pair twice.
     *
     * @param p1 the first {@link Position}
     * @param p2 the second {@link Position}
     * @return a lexicographically sorted string key representing the pair.
     */
    private static String createSortedKey(Position p1, Position p2) {
        if (p1.getX() < p2.getX() || (p1.getX() == p2.getX() && p1.getY() < p2.getY())) {
            return p1.getX() + "," + p1.getY() + "-" + p2.getX() + "," + p2.getY();
        } else {
            return p2.getX() + "," + p2.getY() + "-" + p1.getX() + "," + p1.getY();
        }
    }

}
