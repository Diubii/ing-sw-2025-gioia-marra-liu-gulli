package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.model.Player;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;

public class StardustEffect {
    public static void effect(CardContext context) {
        for (Player player : context.getCurrentGame().getGameController().getRankedPlayers().reversed()) { //Si parte dall'ultimo, stavolta uso la lista del game controller perché quella del context viene aggiornata durante il foreach
            movePlayer(context, player, -player.getShip().getnExposedConnector());
        }
    }
}
