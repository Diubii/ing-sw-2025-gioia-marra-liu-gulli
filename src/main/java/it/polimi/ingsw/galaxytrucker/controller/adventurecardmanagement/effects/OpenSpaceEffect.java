package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.PlayerLostReason;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.HashMap;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;

public abstract class OpenSpaceEffect {
    private final static HashMap<LobbyManager, HashMap<String, Integer>> playerToPowerMapPerGame = new HashMap<>();

    public static void doubleEnginesActivated(CardContext context) {
        System.out.println("DEBUG: OpenSpaceEffect.doubleEnginesActivated()");
        LobbyManager game = context.getCurrentGame();
        Player player = context.getCurrentPlayer();

        int playerEnginePower = player.getShip().calculateEnginePower();
        System.out.println("DEBUG: playerEnginePower = " + playerEnginePower);
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
        movePlayer(context, player, playerEnginePower);

        if (player == context.getCurrentRankedPlayers().getLast()) {
            context.nextPhase();
        } else {
            context.nextPlayer();
            context.previousPhase();
        }

        context.executePhase();
    }

    public static void finalCheck(CardContext context) {
        System.out.println("DEBUG: OpenSpaceEffect.finalCheck()");
        LobbyManager game = context.getCurrentGame();

        HashMap<String, Integer> playerToPowerMap = playerToPowerMapPerGame.get(game);

        playerToPowerMap.forEach((nickname, power) -> {
            if (power == 0) {
                System.out.println("DEBUG " + nickname + " removed from game");
                game.getGameController().removePlayerFromGame(nickname, PlayerLostReason.ZeroEnginePower);
            }
        });

        //Cleanup


        playerToPowerMapPerGame.get(game).clear();
        playerToPowerMapPerGame.remove(game);


        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
