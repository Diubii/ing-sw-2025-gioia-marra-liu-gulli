package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.PLAYER_PHASE;

public class ClientPhaseController {
    private final ClientController controller;
    private PLAYER_PHASE phase;
    public ClientPhaseController(ClientController controller) {
        this.controller = controller;
        phase = PLAYER_PHASE.START;
    }

    public PLAYER_PHASE getPhase() {
        return phase;
    }

    public void nextPhase(){
        switch (phase){
            case START:
                phase = PLAYER_PHASE.SERVER_INFO;
                break;

            case SERVER_INFO:
                phase = PLAYER_PHASE.NICKNAME_REQUEST;
                break;

                case NICKNAME_REQUEST:
                    phase = PLAYER_PHASE.NUM_PLAYERS_REQ;
                    break;

                case NUM_PLAYERS_REQ:
                    phase = PLAYER_PHASE.LOBBY;
                    break;

        }    }






}
