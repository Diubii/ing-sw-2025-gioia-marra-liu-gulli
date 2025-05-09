package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.MeteorSwarmEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class MeteorSwarmFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                MeteorSwarmEffect::sendActivateComponentRequests,
                MeteorSwarmEffect::unleashTheMeteorSwarm
        ));
    }
}
