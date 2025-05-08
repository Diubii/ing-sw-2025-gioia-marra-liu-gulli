package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm;

import org.polimi.ingsw.galaxytrucker.enums.CardPhase;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class AbandonedShipFSM extends CardFSM {
    public AbandonedShipFSM() {
        super();
    }

    @Override
    public ArrayList<Consumer<LobbyManager>> initPhases() {
        return new ArrayList<>(Arrays.asList(
                CardPhase.Start,
                CardPhase.CardActivated,
                CardPhase.CrewDiscarded,
                CardPhase.End
        ));
    }


}
