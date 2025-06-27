package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateAdventureCardRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.ActivateAdventureCardResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class AbandonedStationEffect {
    public static void start(CardContext context) {
        Player player = context.getCurrentPlayer();
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();

//        System.out.println("DEBUG: AbandonedStationEffect.start()");
        if (player.getShip().getnCrew() >= abandonedStation.getRequiredCrewMembers()) {
            //
            broadcastGameMessage(context,"Il giocatore "+ player.getNickName() +"ha abbastanza membri dell'equipaggio e sta decidendo se raccogliere la ricompensa...");
            //
            sleepSafe(600);

            context.nextPhase();
            sendMessage(context, player, new ActivateAdventureCardRequest());
        } else { //Passo al prossimo giocatore
            //
//            System.out.println( player.getNickName()  + " non ha abbastanza membri dell'equipaggio per attivare questa carta. Skippo");
            broadcastGameMessage(context,  player.getNickName() + " non ha abbastanza membri dell'equipaggio per attivare questa carta.");
            //
            sleepSafe(600);
            //
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
//        System.out.println("DEBUG: AbandonedStationEffect.receivedCardActivationResponse()");
        ActivateAdventureCardResponse activateAdventureCardResponse = (ActivateAdventureCardResponse) context.getIncomingNetworkMessage();
        Player currentPlayer = context.getCurrentPlayer();

        if (activateAdventureCardResponse.isActivated()) {
            broadcastGameMessage(context,"Il giocatore " + currentPlayer.getNickName() + " ha accettato la ricompensa. In attesa che carichi la propria astronave...");

            sleepSafe(600);

            context.nextPhase();
            sendMessage(context, context.getCurrentPlayer(), new ShipUpdate(context.getCurrentPlayer().getShip(), context.getCurrentPlayer().getNickName()));
        } else {


//            System.out.println("Rifiuto attivazione carta. Skippo ");
            broadcastGameMessage(context,"Il giocatore " + currentPlayer.getNickName() + " rifiuta di attivare l'effetto della carta.");

            sleepSafe(600);

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
//        System.out.println("DEBUG: AbandonedStationEffect::moveCurrentPlayer");
        AbandonedStation abandonedStation = (AbandonedStation) context.getAdventureCard();

        broadcastGameMessage(context,"Il giocatore " + context.getCurrentPlayer().getNickName() + " deve muoversi indietro di " +context.getAdventureCard().getDaysLost() +" caselle.");
        sleepSafe(600);

        movePlayer(context, context.getCurrentPlayer(), -abandonedStation.getDaysLost());
        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
