package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.AbandonedShipEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

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
