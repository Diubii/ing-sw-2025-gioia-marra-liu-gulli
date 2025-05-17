package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleCannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameController {

    private GameState gameState;
    private final LobbyManager game;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
    CompletableFuture cardDrawn;
    private Boolean gameEndedEarly = false;
    private CardDeck cardDeckTest = new CardDeck(true);
    private Iterator<Player> rankedPlayersIterator;
    private CardContext currentCardContext;

    public CardContext getCurrentCardContext() {
        return currentCardContext;
    }

    public CardDeck getCardDeckTest() {
        return cardDeckTest;
    }

    public void setCardDeckTest(CardDeck cardDeckTest) {
        this.cardDeckTest = cardDeckTest;
    }

    public int getnCompletedShips() {
        synchronized (ncsLock) {
            return nCompletedShips;
        }
    }

    public void addCompletedShip() {
        synchronized (ncsLock) {
            nCompletedShips++;
        }
    }

    public GameController(LobbyManager game) {
        this.game = game;
        gameState = GameState.LOBBY;
    }

    private final Object gameStateLock = new Object();

    public GameState getGameState() {
        synchronized (gameStateLock) {
            return gameState;
        }
    }

    public void nextState() {
        synchronized (gameStateLock) {
            switch (gameState) {
                case LOBBY -> gameState = GameState.BUILDING_START;
                case BUILDING_START -> gameState = GameState.BUILDING_TIMER;
                case BUILDING_TIMER -> gameState = GameState.BUILDING_END;
                case BUILDING_END -> gameState = GameState.SHIP_CHECK;
                case SHIP_CHECK -> gameState = GameState.CREW_INIT;
                case CREW_INIT -> gameState = GameState.FLIGHT;
            }
        }
    }

    public void startFlight() throws ExecutionException, InterruptedException, IOException {
        cardDeckTest = Util.createTestDeck();
        game.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));
//        handleTurnBeforeDrawnCard();
    }

//    public void handleTurnBeforeDrawnCard() {
//        ArrayList<Player> rankedPlayers = getRankedPlayers();
//
//        MatchInfoUpdate miu;
//        if (!rankedPlayers.isEmpty()) {
//            miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), cardDeckTest.getSize());
//            rankedPlayersIterator = rankedPlayers.iterator();
//        } else {
//            miu = new MatchInfoUpdate("", game.getRealGame().getFlightDeck().getSize());
//        }
//
//        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(miu)); //Mando match info update
//    }

    public void sendMatchInfoUpdate() {
        ArrayList<Player> rankedPlayers = getRankedPlayers();

        MatchInfoUpdate miu;
        if (!rankedPlayers.isEmpty()) {
            miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), cardDeckTest.getSize());
            rankedPlayersIterator = rankedPlayers.iterator();
        } else {
            miu = new MatchInfoUpdate("", game.getRealGame().getFlightDeck().getSize());
        }

        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(miu));

    }


    public void handleTurn() {
        AdventureCard drawnAdventureCard = getCardDeckTest().pop();

        DrawnAdventureCardUpdate drawnAdventureCardUpdate = new DrawnAdventureCardUpdate(drawnAdventureCard);
        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(drawnAdventureCardUpdate)); //Mando drawnAdventureCardUpdate a tutti i player

        CardContext context = new CardContext(game, drawnAdventureCard);
        currentCardContext = context;
        context.executePhase();
    }

    public void handleEndTurn(){
        clearPlayersWithNoCrew();
        clearLappedPlayers();

        CardDeck cardDeck = getCardDeckTest();
        EndTurnUpdate etu = new EndTurnUpdate();
        //inviare end turn update
        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty() || cardDeck.getSize() == 0) {
            etu.setEndGame(true);
            game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(etu));
            handleEndGame();
            return;
        }
        else{
            etu.setEndGame(false);
            game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(etu));
        }

        game.resetReadyPlayers();
        currentCardContext = null;
    }


    public void handleEndGame() {
        List<PlayerScore> scores = calculateScores();
        GameEndUpdate geu = new GameEndUpdate(new ArrayList<>(scores));
        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(geu));
//        game.setFinished(true);
    }

    public List<PlayerScore> calculateScores() {
        List<Player> players = game.getRealGame().getPlayers();

        int minExposed = players.stream()
                .mapToInt(p -> p.getShip().getnExposedConnector())
                .min()
                .orElse(Integer.MAX_VALUE);

        return players.stream()
                .map(player -> {
                    int bestLooking = player.getShip().getnExposedConnector() == minExposed ? 2 : 0;
                    int finishOrder = calculateFinishOrderScore(player);
                    int reward = calculateGoodRewardScore(player);
                    int losses = calculateLossesScore(player);
                    int credits = player.getNCredits();

                    return new PlayerScore(
                            player.getNickName(),
                            bestLooking,
                            finishOrder,
                            reward,
                            losses,
                            credits
                    );
                })
                .sorted(Comparator.comparingInt(PlayerScore::getTotalScore).reversed())
                .toList();
    }
    private int calculateFinishOrderScore(Player player) {
        int score = 0;
        ArrayList<Player> activePlayers = getRankedPlayers();
        if(!activePlayers.contains(player)){
            return score;
        }
        else{
            int playerIndex = activePlayers.indexOf(player);
            int nPlayers = game.getRealGame().getPlayers().size();
            score= nPlayers - playerIndex;
            return score;
        }

    }
    private int calculateGoodRewardScore(Player player) {
        ArrayList<Good> goods = player.getShip().getGoodsOnShipBoard();
        int score = 0;
        for(Good good : goods){
            score += good.getValue();
        }
        double tmpScore = 0;
        if(PlayerState.Spectating == player.getPlayerState()){
            tmpScore = score * 0.5;
            return (int) Math.ceil(tmpScore);
        }

        return score;

    }
    private int calculateLossesScore(Player player) {
        int score = 0;
        int LossesScore = player.getShip().getDestroyedTiles();
        score += LossesScore;
        return score;
    }

    public void removePlayerFromGame(String nickname, boolean isLandingEarly) {
        game.getRealGame().getPlayer(nickname).setPlayerState(PlayerState.Spectating);
        game.getRealGame().getFlightBoard().removePlayer(game.getPlayerColors().get(nickname));

        FlightBoardUpdate fbu = new FlightBoardUpdate(game.getRealGame().getFlightBoard());
        PlayerLostUpdate plu = new PlayerLostUpdate(nickname, isLandingEarly);
        game.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(plu);
            ch.sendMessage(fbu);
        }); //Notifichiamo i client che un player ha perso e aggiorniamo la flight board

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
            handleEndGame();
//            completeCardDrawn();
        }
    }

    public void kickPlayerFromGame(String nickname) throws PlayerNotFoundException {
        game.getPlayerColors().remove(nickname);
        game.getRealGame().getFlightBoard().removePlayer(game.getPlayerColors().get(nickname));
        game.getRealGame().removePlayer(nickname);

        FlightBoardUpdate fbu = new FlightBoardUpdate(game.getRealGame().getFlightBoard());
        PlayerKickedUpdate pku = new PlayerKickedUpdate(nickname);
        game.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(pku);
            ch.sendMessage(fbu);
        }); //Notifichiamo i client che un player è stato kickato e aggiorniamo la flight board
        game.removePlayerHandler(nickname);

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
//            completeCardDrawn();

        }
    }

    /**
     * Removes the first tile hit by the projectile if the conditions to destroy it are met.
     *
     * @param projectile The projectile.
     * @param diceRoll   Result of the dice rolled by the player.
     * @author Alessandro Giuseppe Gioia
     */
    @NeedsToBeCompleted("Controllare per tronconi. Un po' scettico sul fatto che il messaggio debba essere mandato da questo metodo.")
    public Tile reactToProjectile(Player targetPlayer, Projectile projectile, int diceRoll) {
        Ship ship = targetPlayer.getShip();
        Position pos = ship.getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll);
        Tile destroyedTile = null;

        if (pos == null) return null;

        boolean aTileHasBeenDestroyed = false;
        String message = "Tile distrutta in posizione [" + pos.getX() + "," + pos.getY() + "]";

        if (projectile.getType() == ProjectileType.CannonFire) {
            if (projectile.getSize() == ProjectileSize.Big) {
                destroyedTile = ship.getTileFromPosition(pos);
                ship.removeTile(pos, false);
                aTileHasBeenDestroyed = true;
            } else if (projectile.getSize() == ProjectileSize.Little) {
                if (!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())) {
                    destroyedTile = ship.getTileFromPosition(pos);
                    ship.removeTile(pos, false);
                    aTileHasBeenDestroyed = true;
                }
            }
        } else if (projectile.getType() == ProjectileType.Meteor) {
            if (projectile.getSize() == ProjectileSize.Big) {
                if (!protectWithFirstAvailableCannon(ship, projectile.getDirection())) {
                    destroyedTile = ship.getTileFromPosition(pos);
                    ship.removeTile(pos, false);
                    aTileHasBeenDestroyed = true;
                }
            } else if (projectile.getSize() == ProjectileSize.Little) {
                ArrayList<Connector> tileConnectors = ship.getShipBoard()[pos.getY()][pos.getX()].getTile().getSides();
                int index = -1;

                switch (projectile.getDirection()) {
                    case UP -> index = 0;
                    case RIGHT -> index = 1;
                    case DOWN -> index = 2;
                    case LEFT -> index = 3;
                }

                if (tileConnectors.get(index) != Connector.EMPTY) { //Se non è un lato liscio
                    if (!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())) { //Se non c'è uno shield disponibile a proteggere
                        destroyedTile = ship.getTileFromPosition(pos);
                        ship.removeTile(pos, false);
                        aTileHasBeenDestroyed = true;
                    }
                }
            }
        }

        if (aTileHasBeenDestroyed) {
            game.getPlayerHandlers().get(targetPlayer.getNickName()).sendMessage(new GameMessage(message));
        }

        return destroyedTile;
    }

    /**
     * Finds the first available charged shield oriented according to the direction from which the projectile will come from and protects
     * the ship with it, discharging it.
     *
     * @param direction The direction which the projectile will come from.
     * @return {@code true} if the ship is protected, {@code false} if it is not.
     * @author Alessandro Giuseppe Gioia
     */
    private boolean protectWithFirstAvailableCannon(Ship ship, ProjectileDirection direction) {
        for (Position cannonPos : ship.getComponentPositionsFromName("Cannon")) {
            Cannon cannon = (Cannon) ship.getComponentFromPosition(cannonPos);
            if (cannon.getRotation() == direction.ordinal()) {
                return true;
            }
        }

        for (Position cannonPos : ship.getComponentPositionsFromName("DoubleCannon")) {
            DoubleCannon doubleCannon = (DoubleCannon) ship.getComponentFromPosition(cannonPos);
            if (doubleCannon.getRotation() == direction.ordinal() && doubleCannon.isCharged()) {
                doubleCannon.setCharged(false);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first available charged shield oriented according to the direction from which the projectile will come from and protects
     * the ship with it, discharging it.
     *
     * @param direction The direction which the projectile will come from.
     * @return {@code true} if the ship is protected, {@code false} if it is not.
     * @author Alessandro Giuseppe Gioia
     */
    private boolean protectWithFirstAvailableCorrectlyOrientedChargedShield(Ship ship, ProjectileDirection direction) {
        for (Position shieldPos : ship.getComponentPositionsFromName("Shield")) {
            Shield shield = (Shield) ship.getComponentFromPosition(shieldPos);
            if (shield.isCharged() && shield.getProtectedSides().contains(direction)) {
                shield.setCharged(false);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Player> getRankedPlayers() {
        return new ArrayList<>(game.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).sorted(Comparator.comparingInt(Player::getPlacement)).toList()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti //Prendiamo i giocatori che stanno giocando
    }

    public Player nextPlayer() {
        return rankedPlayersIterator.next();
    }

    public void clearPlayersWithNoCrew(){
        for(Player player : game.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).toList()) {
            if(player.getShip().getnCrew() == 0){
                removePlayerFromGame(player.getNickName(), false);
            }
        }
    }

    public void clearLappedPlayers() {
        for(Color color : game.getRealGame().getFlightBoard().getRankedPlayers()) {
            if(game.getRealGame().getFlightBoard().isPlayerLapped(color)){
                removePlayerFromGame(game.getNicknameFromColor(color), false);
            }
        }
    }
}
