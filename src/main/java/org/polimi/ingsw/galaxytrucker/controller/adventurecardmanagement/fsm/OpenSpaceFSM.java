package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.OpenSpaceEffects;
import org.polimi.ingsw.galaxytrucker.enums.CardPhase;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class OpenSpaceFSM extends CardFSM {
    public OpenSpaceFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                OpenSpaceEffects::doubleEnginesActivationRequest,
                OpenSpaceEffects::doubleEnginesActivated,
                OpenSpaceEffects::finalCheck
        ));
    }
}
