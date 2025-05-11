package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.CommonEffects;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.OpenSpaceEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class OpenSpaceFSM extends CardFSM {
    public OpenSpaceFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                CommonEffects::sendDoubleCannonsActivationRequest,
                OpenSpaceEffect::doubleEnginesActivated,
                OpenSpaceEffect::finalCheck
        ));
    }
}
