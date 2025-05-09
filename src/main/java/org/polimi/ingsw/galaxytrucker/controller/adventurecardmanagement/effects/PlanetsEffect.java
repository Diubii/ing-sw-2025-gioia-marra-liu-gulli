package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Planets;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.SelectPlanetRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.SelectPlanetResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.SelectedPlanetUpdate;

import java.util.ArrayList;
import java.util.HashMap;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class PlanetsEffect {

    private final static HashMap<LobbyManager, ArrayList<Player>> landedPlayers = new HashMap<>();

    public static void sendSelectPlanetRequest(CardContext context) {
        Planets planets = (Planets) context.getAdventureCard();
            ArrayList<Planet> notOccupiedPlanets = new ArrayList<>(planets.getPlanets().stream().filter(planet -> !planet.isOccupied()).toList());
            SelectPlanetRequest selectPlanetRequest = new SelectPlanetRequest(notOccupiedPlanets);
            sendMessage(context, context.getCurrentPlayer(), selectPlanetRequest);
            context.nextPhase();

    }

    public static void receivedSelectPlanetResponse(CardContext context) {
        Planets planets = (Planets) context.getAdventureCard();
        SelectPlanetResponse selectPlanetResponse = (SelectPlanetResponse) context.getIncomingNetworkMessage();
        Planet selectedPlanet = selectPlanetResponse.getSelectedPlanet();
        if (selectedPlanet != null) {
            planets.getPlanets().get(selectPlanetResponse.getPlanetIndex()).setOccupied(true);
            SelectedPlanetUpdate selectedPlanetUpdate = new SelectedPlanetUpdate(context.getCurrentPlayer().getNickName(), selectedPlanet, selectPlanetResponse.getPlanetIndex());
            broadcast(context, selectedPlanetUpdate);

            context.incrementExpectedNumberOfNetworkMessages(NetworkMessageType.ShipUpdate);

            landedPlayers.putIfAbsent(context.getCurrentGame(), new ArrayList<>());
            landedPlayers.get(context.getCurrentGame()).add(context.getCurrentPlayer());
        }

        if (!planets.getPlanets().stream().allMatch(Planet::isOccupied) && context.getCurrentPlayer() != context.getCurrentRankedPlayers().getLast()) {
            //Mando il selectPlanetRequest al prossimo player
            context.nextPlayer();
            context.previousPhase();
            context.executePhase();
        }
        else{ //Se tutti hanno scelto o i pianeti sono tutti occupati
            context.nextPhase();

            //Controllo se non siamo in attesa di ShipUpdates
            if (context.getExpectedNumberOfNetworkMessagesPerType().get(NetworkMessageType.ShipUpdate) == 0) {
                context.executePhase();
            }
        }
    }

    public static void movePlayers(CardContext context) {
        Planets planets = (Planets) context.getAdventureCard();
        for (Player player : landedPlayers.get(context.getCurrentGame()).reversed()) {
            movePlayer(context, player, -planets.getDaysLost());
        }

        landedPlayers.remove(context.getCurrentGame());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
