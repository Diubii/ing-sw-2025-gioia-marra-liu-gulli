package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.enums.PLAYER_PHASE;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;

public class ClientPhaseController {
    private final ClientController controller;
    private GameState phase;

    public ClientPhaseController(ClientController controller) {
        this.controller = controller;
        phase = GameState.LOBBY;
    }

    public GameState getPhase() {
        return phase;
    }


    public void handlePhaseUpdate(PhaseUpdate update) {
        switch (update.getState()){

            case BUILDING_START -> {
                new Thread(() -> {
                    controller.getView().showGenericMessage("NEW GAME PHASE " + update.getState());
                }).start();

            }
        }
    }


}
