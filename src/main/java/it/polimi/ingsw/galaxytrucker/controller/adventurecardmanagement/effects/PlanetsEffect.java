package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Planets;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.SelectedPlanetUpdate;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class PlanetsEffect {

    private final static HashMap<LobbyManager, ArrayList<Player>> landedPlayers = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> landedPlayerIndex = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> finishedPlayer = new HashMap<>();

    public static void sendSelectPlanetRequest(CardContext context) {
        Player player =context.getCurrentPlayer();
        System.out.println(player.getNickName() + " DEBUG: PlanetsEffect sendSelectPlanetRequest");
        Planets planets = (Planets) context.getAdventureCard();
            HashMap<Integer, Planet> notOccupiedPlanets = new HashMap<>();

            int i=1;
            for(Planet planet : planets.getPlanets()) {
                if(!planet.isOccupied()) notOccupiedPlanets.put(i, planet);
                i++;
            }

            SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
            context.nextPhase();
            sendMessage(context, context.getCurrentPlayer(), selectPlanetRequest);
    }

    public static void receivedSelectPlanetResponse(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Player player =context.getCurrentPlayer();
        System.out.println(player.getNickName() + " DEBUG: PlanetsEffect receivedSelectPlanetResponse");
        Planets planets = (Planets) context.getAdventureCard();
        SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) context.getIncomingNetworkMessage();
        Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
        landedPlayers.putIfAbsent(context.getCurrentGame(), new ArrayList<>());
        if (selectedPlanet != null) {
            planets.getPlanets().get(selectPlanetResponse.getPlanetIndex() - 1).setOccupied(true);
            SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(context.getCurrentPlayer().getNickName(), selectedPlanet, selectPlanetResponse.getPlanetIndex());
            broadcast(context, selectedPlanetUpdate);

//            context.incrementExpectedNumberOfNetworkMessages(NetworkMessageType.ShipUpdate);
//            ShipUpdate shipUpdate =new ShipUpdate(player.getShip(),player.getNickName());
//            sendMessage(context,player,shipUpdate);


            landedPlayers.get(context.getCurrentGame()).add(context.getCurrentPlayer());
        }

        if (!planets.getPlanets()
                .stream()
                .allMatch(Planet::isOccupied) && context.getCurrentPlayer() != context.getCurrentRankedPlayers().getLast()) {
            //Mando il selectPlanetRequest al prossimo player
            context.nextPlayer();

            context.previousPhase();
            context.executePhase();
        }
        else{ //Se tutti hanno scelto o i pianeti sono tutti occupati
            if(landedPlayers.get(game).size() == 0){
                context.goToEndPhase();
                context.executePhase();
                return;
            }

            context.nextPhase();

            landedPlayerIndex.putIfAbsent(game,0);
            context.setCurrentPlayer(landedPlayers.get(game).get(landedPlayerIndex.get(game)));

            context.executePhase();
            //Controllo se non siamo in attesa di ShipUpdates
        }
    }

    public static void sendShipupdate(CardContext context) {


        LobbyManager game = context.getCurrentGame();
        ArrayList<Player>  landedPlayersList= landedPlayers.get(game);
        context.nextPhase();
      for( Player p : landedPlayersList){
          System.out.println("ShipUpdate send Player  "+ p.getNickName());
          ShipUpdate shipUpdate =new ShipUpdate(p.getShip(),p.getNickName());
          shipUpdate.setLoadMerci(true);
          sendMessage(context,p,shipUpdate);

      }


    }

    public static void receiveShipUpdate(CardContext context) {

        ShipUpdate shipUpdate = (ShipUpdate) context.getIncomingNetworkMessage();

        System.out.println(shipUpdate.getNickName() + " DEBUG: PlanetsEffect receiveShipUpdate");

        LobbyManager game = context.getCurrentGame();

        finishedPlayer.putIfAbsent(context.getCurrentGame(),0);
        int finishedPlayerIndex = finishedPlayer.get(game);
        finishedPlayer.put(game,finishedPlayerIndex+1);

        broadcast(context,shipUpdate);

        if(landedPlayers.get(game).size() == finishedPlayer.get(game)){

            context.nextPhase();
            context.executePhase();
        }


    }


    public static void movePlayers(CardContext context) {
        System.out.println( " DEBUG: PlanetsEffect movePlayers");
        ArrayList<Player> currentGameLandedPlayers = landedPlayers.get(context.getCurrentGame());
        if(currentGameLandedPlayers != null) {
            Planets planets = (Planets) context.getAdventureCard();
            for (Player player : currentGameLandedPlayers.reversed()) {
                movePlayer(context, player, -planets.getDaysLost());
            }

            landedPlayers.remove(context.getCurrentGame());
        }

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
