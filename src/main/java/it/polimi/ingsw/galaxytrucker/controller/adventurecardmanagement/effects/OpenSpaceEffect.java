package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.PlayerLostReason;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class OpenSpaceEffect {
    private final static HashMap<LobbyManager, HashMap<String, Integer>> playerToPowerMapPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, ArrayList<Player>> originRankedPlayers = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> playerIndex= new HashMap<>();

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


      resetState(game);
        context.nextPhase();
        context.executePhase();
    }

    private static void resetState(LobbyManager game) {

        playerToPowerMapPerGame.remove(game);
        //Execute CommonEffects::end
        originRankedPlayers.remove(game);
        playerIndex.remove(game);

    }
}
