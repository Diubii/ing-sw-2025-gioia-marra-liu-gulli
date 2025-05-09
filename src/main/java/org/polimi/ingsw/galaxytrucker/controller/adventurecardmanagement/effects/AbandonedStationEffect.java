package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;
import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;

public abstract class AbandonedStationEffect {
    public static void start(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();

        if (player.getShip().getnCrew() >= abandonedStation.getRequiredCrewMembers()) {
            sendMessage(context, player, new ActivateAdventureCardRequest());
            context.nextPhase();
        } else { //Passo al prossimo giocatore
            context.nextPlayer();
            context.executePhase();
        }
    }

    public static void receivedCardActivationResponse(CardContext context) {
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        if (activateAdventureCardResponse.isActivated()) {
            context.incrementExpectedNumberOfNetworkMessages(NetworkMessageType.ShipUpdate);
            context.nextPhase();
        } else {
            context.resetFSM();
            context.nextPlayer();
            context.executePhase();
        }
    }

    public static void moveCurrentPlayer(CardContext context) {
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();
        movePlayer(context, context.getCurrentPlayer(), -abandonedStation.getDaysLost());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
