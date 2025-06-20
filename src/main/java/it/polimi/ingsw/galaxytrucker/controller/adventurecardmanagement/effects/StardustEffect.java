package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;

public class StardustEffect {
    public static void effect(CardContext context) {
        for (Player player : context.getCurrentGame().getGameController().getRankedPlayers().reversed()) { //Si parte dall'ultimo, stavolta uso la lista del game controller perché quella del context viene aggiornata durante il foreach
            movePlayer(context, player, -player.getShip().getnExposedConnector());
        }
        //Execute CommonEffects::end
        context.goToEndPhase();
        context.executePhase();
    }
}
