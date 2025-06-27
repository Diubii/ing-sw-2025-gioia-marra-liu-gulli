package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedShip;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DiscardCrewMembersRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class AbandonedShipEffect {
    public static void start(CardContext context) {
//        System.out.println("DEBUG: Entered AbandonedShipEffect.start()");//
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
        Player player = context.getCurrentPlayer();


        if (player.getShip().getnCrew() >= abandonedShip.getRequiredCrewMembers()) {
            ActivateAdventureCardRequest activateAdventureCardRequest = new ActivateAdventureCardRequest();
            context.nextPhase();

            sleepSafe(600);

            sendMessage(context, player, activateAdventureCardRequest);
        } else {
//            System.out.println("[" + player.getNickName() + "] non ha abbastanza membri dell'equipaggio per attivare questa carta. Skippo");
            GameMessage gameMessage = new GameMessage("Non hai abbastanza membri dell'equipaggio per attivare questa carta.");

            sleepSafe(600);

            sendMessage(context, player, gameMessage);
            //Passiamo al prossimo giocatore
            if(context.currentPlayerIsLast()){
                context.goToEndPhase();
            }
            else{
                context.nextPlayer();
            }
            context.executePhase();
        }
    }

    public static void receivedCardActivationResponse(CardContext context) {
//        System.out.println("DEBUG: Entered receivedCardActivationResponse");
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        if (activateAdventureCardResponse.isActivated()) {
            AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();
            context.nextPhase();

           sleepSafe(600);

            sendMessage(context, context.getCurrentPlayer(), new DiscardCrewMembersRequest(abandonedShip.getRequiredCrewMembers()));
        } else {
//            System.out.println("Rifiuto attivazione carta. Skippo ");
            if(context.currentPlayerIsLast()){

                context.goToEndPhase();
            }
            else{
                context.resetFSM();
                context.nextPlayer();
            }
            context.executePhase();
        }
    }

    public static void crewDiscarded(CardContext context) {
//        System.out.println("DEBUG: Entered crewDiscarded");
        Player player = context.getCurrentPlayer();
        AbandonedShip abandonedShip = (AbandonedShip) context.getAdventureCard();

        DiscardCrewMembersResponse discardCrewMembersResponse = (DiscardCrewMembersResponse) context.getIncomingNetworkMessage();

        Utils.discardCrewMembers(context,context.getCurrentPlayer(), discardCrewMembersResponse, abandonedShip.getRequiredCrewMembers());

        //Broadcasto nuova nave


        player.addCredits(abandonedShip.getCredits()); //Accredito crediti

        broadcastGameMessage(context,"Il giocatore " + player.getNickName()+ " ha guadagnato " + abandonedShip.getCredits()+ " punti");
        sleepSafe(600);

        movePlayer(context, player, -abandonedShip.getDaysLost()); //Sposto il player
        //Execute CommonEffects::end

        context.nextPhase();
        context.executePhase();
    }
}
