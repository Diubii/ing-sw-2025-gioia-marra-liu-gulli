package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.MeteorSwarm;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.HashMap;
import java.util.Random;

import static org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

public abstract class MeteorSwarmEffect {
    private final static HashMap<LobbyManager, Integer> projectileCounters = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> diceRolls = new HashMap<>();

    public static void sendActivateComponentRequests(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int diceRoll = new Random().nextInt(2, 13);
        diceRolls.put(game, diceRoll);

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);

        broadcastGameMessage(context, "Stai per essere colpito da un " + projectile.getType().name() + " da " + projectile.getDirection().name() + ", indice " + diceRoll + "!");

        ActivateComponentRequest activateShieldsRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
        ActivateComponentRequest activateDoubleCannonsRequest = new ActivateComponentRequest(ActivatableComponent.DoubleCannon);

        if (projectile.getSize() == ProjectileSize.Little) {
            for (Player player : context.getCurrentRankedPlayers()) {
                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null && playerCanDefendThemselvesWithAShield(player, projectile)) {
                    sendGameMessage(context, player, "Puoi difenderti con uno scudo!");
                    sendMessage(context, player, activateShieldsRequest);
                }
            }
        } else if (projectile.getSize() == ProjectileSize.Big) {
            for (Player player : context.getCurrentRankedPlayers()) {
                if (player.getShip().getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll) != null) {
                    if (playerCanDefendThemselvesWithASingleCannon(player, projectile, diceRoll)) { //Prima controllo se si può difendere con un cannone singolo
                        sendGameMessage(context, player, "Ti proteggerà un cannone singolo!");
                    } else if (playerCanDefendThemselvesWithADoubleCannon(player, projectile, diceRoll)) { //Altrimenti faccio ricorso a un eventuale cannone doppio
                        sendGameMessage(context, player, "Puoi difenderti con un cannone doppio!");
                        sendMessage(context, player, activateDoubleCannonsRequest);
                    }
                }
            }
        }

        context.nextPhase();
    }

    public static void unleashTheMeteorSwarm(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        MeteorSwarm meteorSwarm = (MeteorSwarm) context.getAdventureCard();

        int projectileIndex = projectileCounters.computeIfAbsent(game, _ -> 0);
        Projectile projectile = meteorSwarm.getMeteors().get(projectileIndex);

        int diceRoll = diceRolls.get(game);

        for (Player player : context.getCurrentRankedPlayers()) {
            game.getGameController().reactToProjectile(player, projectile, diceRoll);

            ShipUpdate shipUpdate = new ShipUpdate(player.getShip(), player.getNickName());
            broadcast(context, shipUpdate);
        }

        //FSM management
        if (projectileIndex < meteorSwarm.getMeteors().size() - 1) {
            context.resetFSM();
            context.executePhase();
        } else { //Finito
            projectileCounters.remove(game);
            diceRolls.remove(game);

            context.nextPhase();
        }
    }
}
