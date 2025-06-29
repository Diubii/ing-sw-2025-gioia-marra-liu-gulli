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

/**
 * The {@code PlanetsEffect} class handles the logic for resolving the "Planets" adventure card,
 * where players are given the opportunity to land on planets and collect resources.
 *
 * <p>This effect proceeds through the following phases:
 * <ul>
 *   <li>Planet Selection: Players take turns selecting unoccupied planets from the list.</li>
 *   <li>Ship Update: Landed players send updated ship states reflecting collected goods (e.g., cargo).</li>
 *   <li>Movement Penalty: Landed players move backward a specified number of days after interaction.</li>
 * </ul>
 */

public abstract class PlanetsEffect {

    private final static HashMap<LobbyManager, ArrayList<Player>> landedPlayers = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> landedPlayerIndex = new HashMap<>();
    private final static HashMap<LobbyManager,Integer> finishedPlayer = new HashMap<>();

    /**
     * Sends a {@link SelectPlanetRequest} to the current player with the list of unoccupied planets.
     * Once the request is sent, advances to the next phase to await the player's response.
     *
     * @param context the {@link CardContext} containing current player and card state.
     */
    public static void sendSelectPlanetRequest(CardContext context) {
        Player player =context.getCurrentPlayer();
//        System.out.println(player.getNickName() + " DEBUG: PlanetsEffect sendSelectPlanetRequest");
        Planets planets = (Planets) context.getAdventureCard();
            HashMap<Integer, Planet> notOccupiedPlanets = new HashMap<>();

            int i=1;
            for(Planet planet : planets.getPlanets()) {
                if(!planet.isOccupied()) notOccupiedPlanets.put(i, planet);
                i++;
            }

            SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
            context.nextPhase();

            broadcastGameMessage(context, "Il giocatore " + player.getNickName() + " sta scegliendo un pianeta...");
            sleepSafe(100);

            sendMessage(context, context.getCurrentPlayer(), selectPlanetRequest);
    }


    /**
     * Handles the player's response to planet selection.
     * Marks the selected planet as occupied and adds the player to the list of landed players.
     * If unoccupied planets remain, passes control to the next player;
     * otherwise continues to the send ship update phase.
     *
     * @param context the {@link CardContext} containing the selection response and game state.
     */
    public static void receivedSelectPlanetResponse(CardContext context) {
        LobbyManager game = context.getCurrentGame();
//        System.out.println(player.getNickName() + " DEBUG: PlanetsEffect receivedSelectPlanetResponse");
        Planets planets = (Planets) context.getAdventureCard();
        SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) context.getIncomingNetworkMessage();
        Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
        landedPlayers.putIfAbsent(context.getCurrentGame(), new ArrayList<>());
        if (selectedPlanet != null) {
            planets.getPlanets().get(selectPlanetResponse.getPlanetIndex() - 1).setOccupied(true);
            SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(context.getCurrentPlayer().getNickName(), selectedPlanet, selectPlanetResponse.getPlanetIndex());
            broadcast(context, selectedPlanetUpdate);
            landedPlayers.get(context.getCurrentGame()).add(context.getCurrentPlayer());
        }

        if (!planets.getPlanets()
                .stream()
                .allMatch(Planet::isOccupied) && context.getCurrentPlayer() != context.getCurrentRankedPlayers().getLast()) {

            context.nextPlayer();
            context.previousPhase();
            context.executePhase();
        }
        else{
            if(landedPlayers.get(game).isEmpty()){
                context.goToEndPhase();
                context.executePhase();
                return;
            }

            context.nextPhase();
            landedPlayerIndex.putIfAbsent(game,0);
            context.setCurrentPlayer(landedPlayers.get(game).get(landedPlayerIndex.get(game)));
            context.executePhase();

        }
    }

    /**
     * Sends a {@link ShipUpdate} request to each landed player so they can submit updated ship state
     * reflecting resources collected from the planet.
     * @param context the {@link CardContext} used to retrieve the landed players and current game.
     */
    public static void sendShipupdate(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        ArrayList<Player>  landedPlayersList= landedPlayers.get(game);
        context.nextPhase();

      for( Player p : landedPlayersList){
//          System.out.println("ShipUpdate send Player  "+ p.getNickName());
          ShipUpdate shipUpdate =new ShipUpdate(p.getShip(),p.getNickName());
          shipUpdate.setLoadMerci(true);
          sendMessage(context,p,shipUpdate);
      }
    }

    /**
     * Processes an incoming {@link ShipUpdate} message from a player who landed on a planet.
     * Broadcasts the updated ship to all clients.
     * When all landed players have responded,advances to the next phase.
     *
     * @param context the {@link CardContext} containing the ship update and network state.
     */
    public static void receiveShipUpdate(CardContext context) {
        ShipUpdate shipUpdate = (ShipUpdate) context.getIncomingNetworkMessage();
//        System.out.println(shipUpdate.getNickName() + " DEBUG: PlanetsEffect receiveShipUpdate");
        LobbyManager game = context.getCurrentGame();
        finishedPlayer.compute(game, (k, v) -> v == null ? 1 : v + 1);
        broadcast(context,shipUpdate);

        if(landedPlayers.get(game).size() == finishedPlayer.get(game)){
            context.nextPhase();
            context.executePhase();
        }

    }


    /**
     * Applies movement penalties to all players who landed on planets.
     * Each affected player is moved backward by the number of days defined in the {@link Planets} card.
     * The internal state is cleared after processing.
     *
     * @param context the {@link CardContext} representing the current phase and game state.
     */
    public static void movePlayers(CardContext context) {
//        System.out.println( " DEBUG: PlanetsEffect movePlayers");
        ArrayList<Player> currentGameLandedPlayers = landedPlayers.get(context.getCurrentGame());
        if(currentGameLandedPlayers != null) {
            Planets planets = (Planets) context.getAdventureCard();
            for (Player player : currentGameLandedPlayers.reversed()) {
                movePlayer(context, player, -planets.getDaysLost());
            }

            landedPlayers.remove(context.getCurrentGame());
        }

        //Execute CommonEffects::end
        resetState(context.getCurrentGame());
        context.nextPhase();
        context.executePhase();
    }
    /**
     * Clears internal tracking maps related to landed players and ship updates for the given game session.
     *
     * @param game the {@link LobbyManager} identifying the game to reset.
     */
    private static void resetState(LobbyManager game) {
        landedPlayers.remove(game);
        landedPlayerIndex.remove(game);
        finishedPlayer.remove(game);
    }
}
