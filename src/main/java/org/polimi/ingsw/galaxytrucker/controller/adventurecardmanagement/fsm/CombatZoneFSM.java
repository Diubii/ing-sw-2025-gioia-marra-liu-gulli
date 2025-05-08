package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.OpenSpace;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class CombatZoneFSM extends CardFSM{
    public CombatZoneFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<LobbyManager>> initPhases() {
        return new ArrayList<>(
                Arrays.asList(

                )
        );
    }
}
