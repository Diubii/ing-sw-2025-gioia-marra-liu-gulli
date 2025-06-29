package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code StardustEffect} class handles the resolution of the "Stardust" adventure card.
 *
 * <p>Each player is penalized based on the number of exposed connectors on their ship.
 * Players are moved backward on the travel track accordingly.
 *
 * <p>The order of execution starts from the last player in the ranked list, and moves
 * in reverse order to the first.
 */
public class StardustEffect {
    /**
     * Applies the stardust effect to all players.
     * Each player is moved backward by a number of days equal to their ship's exposed connectors.
     * The operation is applied in reverse player ranking order.
     *
     * @param context the {@link CardContext} containing game and player information.
     */
    public static void effect(CardContext context) {
        for (Player player : context.getCurrentGame().getGameController().getRankedPlayers().reversed()) { //Si parte dall'ultimo, stavolta uso la lista del game controller perché quella del context viene aggiornata durante il foreach
            movePlayer(context, player, -player.getShip().getnExposedConnector());

            sleepSafe(600);
        }
        //Execute CommonEffects::end
        context.goToEndPhase();
        context.executePhase();
    }
}
