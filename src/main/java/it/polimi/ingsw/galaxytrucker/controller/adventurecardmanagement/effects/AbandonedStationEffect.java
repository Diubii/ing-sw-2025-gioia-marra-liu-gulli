package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;
import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class AbandonedStationEffect {
    public static void start(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();

        System.out.println("DEBUG: AbandonedStationEffect.start()");
        if (player.getShip().getnCrew() >= abandonedStation.getRequiredCrewMembers()) {
            context.nextPhase();
            sendMessage(context, player, new ActivateAdventureCardRequest());
        } else { //Passo al prossimo giocatore
            System.out.println("[" + player.getNickName() + "] non ha abbastanza membri dell'equipaggio per attivare questa carta. Skippo");
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
        System.out.println("DEBUG: AbandonedStationEffect.receivedCardActivationResponse()");
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        if (activateAdventureCardResponse.isActivated()) {
            context.nextPhase();
            sendMessage(context, context.getCurrentPlayer(), new ShipUpdate(context.getCurrentPlayer().getShip(), context.getCurrentPlayer().getNickName()));
        } else {
            System.out.println("Rifiuto attivazione carta. Skippo ");
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

    public static void moveCurrentPlayer(CardContext context) {
        System.out.println("DEBUG: AbandonedStationEffect::moveCurrentPlayer");
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();
        movePlayer(context, context.getCurrentPlayer(), -abandonedStation.getDaysLost());

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
