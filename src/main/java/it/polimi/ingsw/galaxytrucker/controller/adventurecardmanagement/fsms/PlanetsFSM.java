package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.PlanetsEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM (Finite State Machine) for handling the logic of the "Planets" adventure card.
 * <p>
 * This FSM guides players through planet selection, reward collection, and ship updates.
 */
public class PlanetsFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        PlanetsEffect::sendSelectPlanetRequest,
                        PlanetsEffect::receivedSelectPlanetResponse,
                        PlanetsEffect::sendShipupdate,
                        PlanetsEffect::receiveShipUpdate,
                        PlanetsEffect::movePlayers
                )
        );
    }
}
