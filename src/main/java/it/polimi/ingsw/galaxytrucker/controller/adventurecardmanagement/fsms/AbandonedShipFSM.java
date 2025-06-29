package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.AbandonedShipEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM for handling the "Abandoned Ship" adventure card.
 *
 * <p>This FSM includes three main steps:</p>
 * <ul>
 *   <li>start – ask the player if they want to board the abandoned ship</li>
 *   <li>receivedCardActivationResponse – handle the player’s choice</li>
 *   <li>crewDiscarded – remove crew members if the player boards the ship</li>
 * </ul>
 */
public class AbandonedShipFSM extends CardFSM {

    public AbandonedShipFSM() {
        super();
    }


    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                AbandonedShipEffect::start,
                AbandonedShipEffect::receivedCardActivationResponse,
                AbandonedShipEffect::crewDiscarded
        ));
    }
}
