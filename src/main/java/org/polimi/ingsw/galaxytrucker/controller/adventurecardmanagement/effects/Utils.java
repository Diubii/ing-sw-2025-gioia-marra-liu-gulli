package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.FlightBoardUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageCouplingVisitor;

/**
 * This class includes common shared methods among the cards' effects methods.
 */
public abstract class Utils {
    private final static NetworkMessageCouplingVisitor networkMessageCouplingVisitor = new NetworkMessageCouplingVisitor();

    /**
     * Moves a specific player in the game's flight board and sends an update to all clients.
     *
     * @param context
     * @param player
     * @param steps
     */
    protected static void movePlayer(CardContext context, Player player, int steps) {
        LobbyManager game = context.getCurrentGame();

        FlightBoard flightBoard = game.getRealGame().getFlightBoard();
        flightBoard.movePlayer(game.getPlayerColors().get(player.getNickName()), steps);
        FlightBoardUpdate fbu = new FlightBoardUpdate(flightBoard);
        String direction = steps < 0 ? "backwards" : "forwards";
        String message = "Player " + player.getNickName() + " moved " + Math.abs(steps) + " steps " + direction + "!";
        broadcast(context, new GameMessage(message));
        broadcast(context, fbu);
        context.setCurrentRankedPlayers(context.getCurrentGame().getGameController().getRankedPlayers()); //Aggiorno i currentRankedPlayers del context
    }

    protected static void discardCrewMembers(Player player, DiscardCrewMembersResponse discardCrewMembersResponse, int numberOfCrewMembersToBeDiscarded) {
        for (Position position : discardCrewMembersResponse.getHousingPositions()) { //Per ogni posizione (assumo posizioni duplicate per scartare più volte dalla stessa housing unit)
            Component housingUnit = player.getShip().getComponentFromPosition(position); //Prendo la housingUnit dalla position data

            CentralHousingUnit centralHousingUnit = (CentralHousingUnit) housingUnit;
            while(centralHousingUnit.getNCrewMembers() > 0 && numberOfCrewMembersToBeDiscarded > 0) {
                centralHousingUnit.removeCrewMember();
            }
        }
    }

    /**
     * Sends a NetworkMessage to a specific player.
     *
     * @param context
     * @param player
     * @param message
     */
    protected static void sendMessage(CardContext context, Player player, NetworkMessage message) {
        context.incrementExpectedNumberOfNetworkMessages(message.accept(networkMessageCouplingVisitor));
        context.getCurrentGame().getPlayerHandlers().get(player.getNickName()).sendMessage(message);
    }


//    protected static void sendMessageAndDeferGetResponse(LobbyManager lobbyManager, Player player, NetworkMessage message, ArrayList<CompletableFuture<NetworkMessage>> futures) {
//        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
//        ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
//        lobbyManager.addPendingResponse(future, message.getID()); //Notifico che sono in attesa di una risposta
//        clientHandler.sendMessage(message); //Mando la richiesta di attivare eventuali motori doppi
//        futures.add(future);
//    }

    protected static void sendGameMessage(CardContext context, Player player, String message) {
        GameMessage gameMessage = new GameMessage(message);
        gameMessage.setMessage(message);
        context.getCurrentGame().getPlayerHandlers().get(player.getNickName()).sendMessage(gameMessage);
    }

    protected static void broadcastGameMessage(CardContext context, String message) {
        broadcast(context, new GameMessage(message));
    }

    protected static void broadcast(CardContext context, NetworkMessage message) {
        LobbyManager lobbyManager = context.getCurrentGame();
        lobbyManager.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(message));
    }

    protected static void broadcastExcept(CardContext context, NetworkMessage message, Player player) {
        LobbyManager lobbyManager = context.getCurrentGame();
        ClientHandler excludedPlayerClientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName());
        lobbyManager.getPlayerHandlers().values().forEach(ch -> { if(ch != excludedPlayerClientHandler) ch.sendMessage(message); });
    }

    protected static void broadcastExcept(CardContext context, NetworkMessage message, ClientHandler excludedPlayerClientHandler) {
        LobbyManager lobbyManager = context.getCurrentGame();
        lobbyManager.getPlayerHandlers().values().forEach(ch -> { if(ch != excludedPlayerClientHandler) ch.sendMessage(message); });
    }

    /**
     * Checks if a player has a shield orientated the same way of the incoming projectile. Does not check for same row/column.
     *
     * @param player
     * @param projectile
     * @author Alessandro Giuseppe Gioia
     */
    protected static boolean playerCanDefendThemselvesWithAShield(Player player, Projectile projectile) {
        if (projectile.getSize() == ProjectileSize.Big) return false;
        else
            return player.getShip().getComponentPositionsFromName("Shield").stream().anyMatch(p -> ((Shield) player.getShip().getComponentFromPosition(p)).getProtectedSides().contains(projectile.getDirection()));
    }

    protected static boolean playerCanDefendThemselvesWithASingleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.Big) return false;
        else return player.getShip().getComponentPositionsFromName("Cannon").stream().anyMatch(p -> {
            Cannon c = (Cannon) player.getShip().getComponentFromPosition(p);
            if (c.getRotation() == projectile.getDirection().ordinal()) {
                return projectile.getDirection() != ProjectileDirection.UP || (projectile.getDirection() == ProjectileDirection.UP && p.getX() == diceRoll);
            } else return false;
        });
    }

    protected static boolean playerCanDefendThemselvesWithADoubleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.Big) return false;
        else
            return player.getShip().getComponentPositionsFromName("DoubleCannon").stream().anyMatch(p -> {
                DoubleCannon c = (DoubleCannon) player.getShip().getComponentFromPosition(p);
                if (c.getRotation() == projectile.getDirection().ordinal()) {
                    return projectile.getDirection() != ProjectileDirection.UP || (projectile.getDirection() == ProjectileDirection.UP && p.getX() == diceRoll);
                } else return false;
            });
    }

    protected static int getCorrectedDiceRoll(int diceRoll, ProjectileDirection direction){
        switch (direction) {
            case UP, DOWN -> {
                return diceRoll - 4;
            }
            case LEFT, RIGHT -> {
                return diceRoll - 5;
            }

            default -> throw new IllegalArgumentException("Invalid direction");
        }
    }
}
