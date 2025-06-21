package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.DiscardCrewMembersResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.FlightBoardUpdate;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageCouplingVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        flightBoard.movePlayer(game.getPlayerColors().get(player.getNickName()), steps, player);
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
            if(centralHousingUnit.getNCrewMembers() > 0 && numberOfCrewMembersToBeDiscarded > 0) {
                centralHousingUnit.removeCrewMember();
                numberOfCrewMembersToBeDiscarded--;
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
//        CompletableFuture<NetworkMessage> future =z//        ClientHandler clientHandler = lobbyManager.getPlayerHandlers().get(player.getNickName()); //Prendo il ClientHandler associato al player
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
            return player
                    .getShip()
                    .getComponentPositionsFromName("Shield")
                    .stream()
                    .anyMatch(p -> ((Shield) player.getShip()
                            .getComponentFromPosition(p))
                            .getProtectedSides()
                            .contains(projectile.getDirection()));
    }

    protected static boolean playerCanDefendThemselvesWithASingleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.Big) return false;

        Ship ship = player.getShip();
        ProjectileDirection direction = projectile.getDirection();

        for (Position cannonPos : ship.getComponentPositionsFromName("Cannon")) {
            Cannon cannon = (Cannon) ship.getComponentFromPosition(cannonPos);
            ProjectileDirection directionCannon = ProjectileDirection.fromRotation(cannon.getRotation());

            if (!directionCannon.equals(direction)) continue;

            if (direction == ProjectileDirection.UP) {
                if (diceRoll == cannonPos.getX()) return true;
            } else if (direction == ProjectileDirection.DOWN) {
                if (Math.abs(cannonPos.getX() - diceRoll) <= 1) return true;
            } else {
                if (Math.abs(cannonPos.getY() - diceRoll) <= 1) return true;
            }
        }
        return false;
    }

    protected static boolean playerCanDefendThemselvesWithADoubleCannon(Player player, Projectile projectile, int diceRoll) {
        if (projectile.getType() != ProjectileType.Meteor || projectile.getSize() != ProjectileSize.Big) return false;
        Ship ship = player.getShip();
        ProjectileDirection direction = projectile.getDirection();

        for (Position cannonPos : ship.getComponentPositionsFromName("DoubleCannon")) {
            DoubleCannon doubleCannon = (DoubleCannon) ship.getComponentFromPosition(cannonPos);
            ProjectileDirection directionDoubleCannon = ProjectileDirection.fromRotation(doubleCannon.getRotation());

            if (!directionDoubleCannon.equals(direction)) continue;
            if (!doubleCannon.isCharged()) continue;

            if (direction == ProjectileDirection.UP) {
                if (diceRoll == cannonPos.getX()) {
                    return true;
                }
            } else if (direction == ProjectileDirection.DOWN) {
                if (Math.abs(cannonPos.getX() - diceRoll) <= 1) {
                    return true;
                }
            } else {
                if (Math.abs(cannonPos.getY() - diceRoll) <= 1) {
                    return true;
                }
            }
        }
        return false;
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

    public static ArrayList<Good> getAndRemoveMostValuableGoods(CardContext context,Player player, int penalty ) {
        //red, yellow, green, blue
        Ship ship = player.getShip();
        ArrayList<Good> goodsToDiscard = new ArrayList<>();
        Map<Color, ArrayList<Position>> goodPositions = new HashMap<>();

        goodPositions.put(Color.RED, new ArrayList<>());
        goodPositions.put(Color.BLUE, new ArrayList<>());
        goodPositions.put(Color.GREEN, new ArrayList<>());
        goodPositions.put(Color.YELLOW, new ArrayList<>());

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("GenericCargoHolds");

        for (Position pos : storagePos) {
            GenericCargoHolds hold = (GenericCargoHolds) ship.getComponentFromPosition(pos);
            if (hold.isEmpty()) {
                continue;
            }

            for (Good good : hold.getGoods()) {
                Color color = good.getColor();
                goodPositions.get(color).add(pos);
            }
        }

        //dopo averle, parto dalla piu importante


        List<Color> priority = List.of(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);

        int index = 0;

        while (goodsToDiscard.size() < penalty && index < priority.size()) {
            Color currentColor = priority.get(index);
            List<Position> positions = goodPositions.get(currentColor);

            if (positions.isEmpty()) {
                index++;
                continue;
            }

            Position pos = positions.remove(0);
            GenericCargoHolds hold = (GenericCargoHolds) ship.getComponentFromPosition(pos);

            hold.removeGood(currentColor);

            ShipUpdate shipUpdate = new ShipUpdate(ship,player.getNickName());
            broadcast(context, shipUpdate);

            goodsToDiscard.add(new Good(currentColor));

        }

        return goodsToDiscard;

    }

    public static void removeBatteries(CardContext context,Player player, int batteryToDiscard) {
        if (batteryToDiscard <= 0) return;
        Ship ship = player.getShip();

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("BatterySlot");

        int removed = 0;

        for (Position pos : storagePos) {
            BatterySlot batterySlot = (BatterySlot) ship.getComponentFromPosition(pos);

            while (batterySlot.getBatteriesLeft() > 0 && removed < batteryToDiscard) {
                boolean success = batterySlot.removeBattery();
                if (success) {
                    removed++;
                    ShipUpdate shipUpdate = new ShipUpdate(ship,player.getNickName());
                    broadcast(context, shipUpdate);
                } else {
                    break;
                }
            }

            if (removed >= batteryToDiscard) {
                break; //Done
            }
        }
    }

    public static void resetDoubleCannon(Player player) {

        Ship ship = player.getShip();
        ArrayList<Position> doubleCannonPos = ship.getComponentPositionsFromName("DoubleCannon");

        if (doubleCannonPos.isEmpty()) {
            return;
        }
        for (Position pos : doubleCannonPos) {
           DoubleCannon doubleCannon = (DoubleCannon) ship.getComponentFromPosition(pos);
           doubleCannon.setCharged(false);
        }

    }

    public static void resetShield(Player player) {

        Ship ship = player.getShip();
        ArrayList<Position>  shieldPos = ship.getComponentPositionsFromName("Shield");

        if (shieldPos.isEmpty()) {
            return;
        }
        for (Position pos : shieldPos) {
            Shield shield = (Shield) ship.getComponentFromPosition(pos);
            shield.setCharged(false);
        }

    }
    public static void resetDoubleEngine(Player player) {

        Ship ship = player.getShip();
        ArrayList<Position>  doubleEnginePos = ship.getComponentPositionsFromName("DoubleEngine");

        if (doubleEnginePos.isEmpty()) {
            return;
        }
        for (Position pos : doubleEnginePos) {
            DoubleEngine doubleEngine = (DoubleEngine) ship.getComponentFromPosition(pos);
            doubleEngine.setCharged(false);
        }

    }





}
