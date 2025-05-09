package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;

import java.util.HashMap;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.movePlayer;
import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.sendMessage;

public abstract class OpenSpaceEffect {
    private final static HashMap<LobbyManager, HashMap<String, Integer>> playerToPowerMapPerGame = new HashMap<>();

    public static void doubleEnginesActivationRequest(CardContext context) {
        sendMessage(context, context.getCurrentPlayer(), new ActivateComponentRequest(ActivatableComponent.DoubleEngine));
        context.nextPhase();
    }

    public static void doubleEnginesActivated(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Player player = context.getCurrentPlayer();

        int playerEnginePower = player.getShip().calculateEnginePower();
        HashMap<String, Integer> playerToPowerMap;

        //Populating HashMap
        if (playerToPowerMapPerGame.get(game) != null) {
            playerToPowerMap = playerToPowerMapPerGame.get(game);
        } else {
            playerToPowerMap = new HashMap<>();
            playerToPowerMapPerGame.put(game, playerToPowerMap);
        }
        playerToPowerMap.put(player.getNickName(), playerEnginePower);

        movePlayer(context, player, playerEnginePower);

        if (player == context.getCurrentRankedPlayers().getLast()) {
            context.nextPhase();
        }
        else{
            context.nextPlayer();
            context.previousPhase();
        }

        context.executePhase();
    }

    public static void finalCheck(CardContext context) {
        LobbyManager game = context.getCurrentGame();

        HashMap<String, Integer> playerToPowerMap = playerToPowerMapPerGame.get(game);

        playerToPowerMap.forEach((nickname, power) -> {
            if (power == 0) {
                try {
                    game.getGameController().removePlayerFromGame(nickname, false);
                } catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Cleanup
        playerToPowerMapPerGame.get(game).forEach((nickname, ignoredPower) -> playerToPowerMap.remove(nickname));
        playerToPowerMapPerGame.remove(game);

        //Execute CommonEffects::end
        context.nextPhase();
        context.executePhase();
    }
}
