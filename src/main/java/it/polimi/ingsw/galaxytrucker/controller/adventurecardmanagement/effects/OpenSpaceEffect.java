package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.PlayerLostReason;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code OpenSpaceEffect} class handles the logic for resolving the "Open Space" adventure card,
 * where players must rely on their engine power to progress.
 * <p>
 * Each player moves forward based on their current engine power, which may include activated double engines.
 * Players with zero total engine power are eliminated from the game at the end of the effect.
 * <p>
 *  The effect includes:
 * <ul>
 *   <li>Computing and applying movement based on engine power.</li>
 *   <li>Tracking engine power per player for post-effect evaluation.</li>
 *   <li>Eliminating players who fail to meet the engine power requirement.</li>
 * </ul>

 */
public abstract class OpenSpaceEffect {
    private final static HashMap<LobbyManager, HashMap<String, Integer>> playerToPowerMapPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, ArrayList<Player>> originRankedPlayers = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> playerIndex= new HashMap<>();


    /**
     * Handles the logic when a player has activated double engines.
     * Calculates total engine power, moves the player, updates their ship,
     * and advances to the next player or the next phase.
     *
     * @param context the {@link CardContext} containing the current game state and player info.
     */
    public static void doubleEnginesActivated(CardContext context) {
//        System.out.println("DEBUG: OpenSpaceEffect.doubleEnginesActivated()");
        LobbyManager game = context.getCurrentGame();
        Player player = context.getCurrentPlayer();

        int playerEnginePower = player.getShip().calculateEnginePower();
//        System.out.println("DEBUG: playerEnginePower = " + playerEnginePower);
        HashMap<String, Integer> playerToPowerMap;


        //Populating HashMap
        if (playerToPowerMapPerGame.get(game) != null) {
            playerToPowerMap = playerToPowerMapPerGame.get(game);
        } else {
            playerToPowerMap = new HashMap<>();
            playerToPowerMapPerGame.put(game, playerToPowerMap);
        }
        playerToPowerMap.put(player.getNickName(), playerEnginePower);
        playerToPowerMapPerGame.put(game, playerToPowerMap);

        originRankedPlayers.computeIfAbsent(game, k -> context.getCurrentRankedPlayers());
        playerIndex.computeIfAbsent(game, k -> 0);

        movePlayer(context, player, playerEnginePower);

        resetDoubleEngine(player);
        ShipUpdate shipUpdate =new ShipUpdate(player.getShip(),player.getNickName());
        broadcast(context, shipUpdate);

        if (player == originRankedPlayers.get(game).getLast()) {
            context.nextPhase();
        } else {
            playerIndex.put(game, playerIndex.get(game) + 1);
            int nextPlayerIndex = playerIndex.get(game);
            context.setCurrentPlayer(originRankedPlayers.get(game).get(nextPlayerIndex));
            context.previousPhase();
        }

        context.executePhase();
    }

    /**
     * Performs a final evaluation after all players have moved.
     * Eliminates players with zero engine power and resets the effect’s state.
     * Proceeds to the next game phase.
     *
     * @param context the {@link CardContext} representing the current adventure phase.
     */

    public static void finalCheck(CardContext context) {
//        System.out.println("DEBUG: OpenSpaceEffect.finalCheck()");
        LobbyManager game = context.getCurrentGame();

        HashMap<String, Integer> playerToPowerMap = playerToPowerMapPerGame.get(game);

        playerToPowerMap.forEach((nickname, power) -> {
            if (power == 0) {

                broadcastGameMessage(context, "Il giocatore " + nickname + " è stato eliminato per potenza motrice pari a zero!");
                sleepSafe(600);

//                System.out.println("DEBUG " + nickname + " removed from game");
                game.getGameController().removePlayerFromGame(nickname, PlayerLostReason.ZeroEnginePower);
            }
        });
      resetState(game);
      context.nextPhase();
      context.executePhase();
    }

    /**
     * Resets internal data structures used for tracking engine power and turn order
     * after the effect is resolved or the game ends.
     *
     * @param game the {@link LobbyManager} representing the current game session.
     */
    private static void resetState(LobbyManager game) {

        playerToPowerMapPerGame.remove(game);
        //Execute CommonEffects::end
        originRankedPlayers.remove(game);
        playerIndex.remove(game);

    }
}
