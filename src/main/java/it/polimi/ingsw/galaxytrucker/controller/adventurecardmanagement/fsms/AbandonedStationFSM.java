package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.AbandonedStationEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * FSM for handling the "Abandoned Station" adventure card.
 *
 * <p>Steps:</p>
 * <ul>
 *   <li>start – ask the player if they want to explore the station</li>
 *   <li>receivedCardActivationResponse – handle the player’s decision</li>
 *   <li>moveCurrentPlayer – move the player if they explore</li>
 * </ul>
 */
public class AbandonedStationFSM extends CardFSM {
    public AbandonedStationFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                AbandonedStationEffect::start,
                AbandonedStationEffect::receivedCardActivationResponse,
                AbandonedStationEffect::moveCurrentPlayer
        ));
    }
}
