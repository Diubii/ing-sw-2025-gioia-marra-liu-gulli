package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.enums.PLAYER_PHASE;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;

public class ClientPhaseController {
    private final ClientController controller;
    private PLAYER_PHASE phase;

    public ClientPhaseController(ClientController controller) {
        this.controller = controller;
        phase = PLAYER_PHASE.START;
    }

    public synchronized PLAYER_PHASE getPhase() {
        return phase;
    }

    public synchronized void nextPhase() {
        switch (phase){
            case START: phase = PLAYER_PHASE.LOBBY;

            case LOBBY: phase = PLAYER_PHASE.BUILDING;

            case BUILDING: phase = PLAYER_PHASE.FINISH_BUILDING;
        }
    }


    public synchronized void setPhase(PLAYER_PHASE phase) {
        this.phase = phase;
    }

    public synchronized void handlePhaseUpdate(PhaseUpdate update) {
        switch (update.getState()){

            case BUILDING_START -> {
                phase = PLAYER_PHASE.BUILDING;
            }

            case BUILDING_TIMER -> {

                if (phase == PLAYER_PHASE.FINISH_BUILDING) {return;}
                phase = PLAYER_PHASE.BUILDING_TIMER;
            }

            case CREW_INIT -> phase = PLAYER_PHASE.CREW_CHOICE;



        }
    }


}
