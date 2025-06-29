package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.PiratesEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM (Finite State Machine) responsible for handling the phase sequence of the "Pirates" adventure card.
 * <p>
 * This FSM defines the steps a player must go through when encountering Pirates:
 * including cannon activation, combat resolution, reward decisions, and post-combat effects.
 */
public class PiratesFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(
                        CommonEffects::sendDoubleCannonsActivationRequest,
                        PiratesEffect::firePowerCheck,
                        PiratesEffect::receivedRewardsCollectionResponse,
                        PiratesEffect::cannonaitsStart,
                        PiratesEffect::cannonaitsFire,
                        PiratesEffect::cannonaitsTrunks
                )
        );
    }
}
