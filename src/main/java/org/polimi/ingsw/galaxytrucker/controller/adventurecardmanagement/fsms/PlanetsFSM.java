package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.PlanetsEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class PlanetsFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        PlanetsEffect::sendSelectPlanetRequest,
                        PlanetsEffect::receivedSelectPlanetResponse,
                        PlanetsEffect::movePlayers
                )
        );
    }
}
