package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.SmugglersEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM (Finite State Machine) for managing the gameplay sequence of the "Smugglers" adventure card.
 * <p>
 * This FSM coordinates the steps for handling cannon activation, firepower comparison,
 * optional reward collection, and ship updates after the encounter.
 */
public class SmugglersFSM extends CardFSM{
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        CommonEffects::sendDoubleCannonsActivationRequest,
                        SmugglersEffect::firePowerCheck,
                        SmugglersEffect::receivedRewardsCollectionResponse,
                        SmugglersEffect::receivedShipUpdate
                )
        );
    }
}
