package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.EpidemicEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EpidemicFSM extends CardFSM {
    @Override
    public ArrayList<Consumer<CardContext>> initPhases() {
        return new ArrayList<>(
                List.of(
                        EpidemicEffect::check
                )
        );
    }
}
