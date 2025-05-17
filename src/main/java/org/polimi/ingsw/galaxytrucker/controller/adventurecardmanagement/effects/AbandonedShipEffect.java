package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedShip;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;
import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class AbandonedShipEffect {
    public static void start(CardContext context) {
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        if (player.getShip().getnCrew() >= abandonedShip.getRequiredCrewMembers()) {
            ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
            context.nextPhase();
            sendMessage(context, player, activateAdventureCardRequest);
        } else {
            GameMessage gameMessage = new GameMessage("Non hai abbastanza membri dell'equipaggio per attivare questa carta.");
            sendMessage(context, player, gameMessage);

            //Passiamo al prossimo giocatore
            context.nextPlayer();
            context.executePhase();
        }
    }

    public static void receivedCardActivationResponse(CardContext context) {
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        if (activateAdventureCardResponse.isActivated()) {
            AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
            context.nextPhase();
            sendMessage(context, context.getCurrentPlayer(), new DiscardCrewMembersRequest(abandonedShip.getRequiredCrewMembers()));
        } else {
            context.resetFSM();
            context.nextPlayer();
            context.executePhase();
        }
    }

    public static void crewDiscarded(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();

        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();

        Utils.discardCrewMembers(context.getCurrentPlayer(), discardCrewMembersResponse, abandonedShip.getRequiredCrewMembers());

        //Broadcasto nuova nave
        ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
        Utils.broadcast(context, shipUpdate);

        player.addCredits(abandonedShip.getCredits()); //Accredito crediti
        movePlayer(context, player, -abandonedShip.getDaysLost()); //Sposto il player

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
