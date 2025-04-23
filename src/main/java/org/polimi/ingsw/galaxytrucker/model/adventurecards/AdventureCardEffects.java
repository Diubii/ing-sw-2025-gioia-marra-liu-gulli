package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.util.ArrayList;

public class AdventureCardEffects implements AdventureCardActivator {


    @Override
    public void activateAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){
//        player.addCredits(abandonedShip.getCredits());
//        flightBoard.moveBoard(player.getNickName(), abandonedShip.getDaysLost());
    }

    @Override
    public void activateAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){
//        flightBoard.moveBoard(player.getNickName(), abandonedStation.getDaysLost());
    }

    @Override
    public void activateCombatZone(CombatZone combatZone, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }

    @Override
    public void activateEpidemic(Epidemic epidemic, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }

    @Override
    public void activateMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }

    @Override
    public void activateOpenSpace(OpenSpace openSpace, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){
        //flightBoard.moveBoard(player.getNickName(), player.getShip().calcLiveEngineStrength());


        ArrayList<Color> rankedPlayerColors = flightBoard.getRankedPlayers();
//        ArrayList<String> nickNames = new ArrayList<>();
//
//        int i = 0;
//
//        gameController.getMyGame().getPlayerColors().entrySet().stream().filter(pair -> {
//            if (pair.getValue().equals(rankedPlayerColors.get(i))){
//                nickNames.add(pair.getKey());
//                return true;
//            }else return false;
//
//        }).toList();


        /*
        *
        * 1) for (clienthandler in ClientHandlers)
        *   {
        *
        *  enginePower... request =  new enginePowerRequest()
        *
        *       CompletableFuture<> future = new ...;
        *       futuresList.add(future, request.getId())
        *       clientHandler.sendMessage(request) //deve avere l'id super()
        *
        *
        * //nel handleFirePOwerResponse andara aggioranata la ship del Player
        * //perche bella response ci sono: lista (TileId/Posizioni) batterie usate e dei motori attivati.
        *       enginePowerrResponse response = (enginePowerResponse) future.get();
        *
        * `     int enginePower = reponse.getEnginepower();
        *
        *
        *         //LOGICA
        *
        *         flightBoard.movePLayer(player, enginePower)
        *
        *   }
        *
        * */


        /*
        deletedPLayers = new ArrayList()
        *
        * for (players(colori dei token) (dall'ultimo)){
        *
        *       if (flightBoard.isPlayerOverLapped(player)) {
        *
        *               playerLooses(player)
                    deletedPLayer.add(player)
        *       }
        * }

            colors.removeAll(deletedPLayers);
        * */






    }

    @Override
    public void activatePlanets(Planets planets, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){
    }

    @Override
    public void activateStardust(Stardust stardust, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){
        //player.getShip().calcExposedConnectors();
    }

    @Override
    public void activatePirates(Pirates pirates, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }

    @Override
    public void activateSlavers(Slavers slavers, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }

    @Override
    public void activateSmugglers(Smugglers smugglers, ArrayList<Player> player, FlightBoard flightBoard, GameController gameController){

    }
}