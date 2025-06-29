package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.*;
import it.polimi.ingsw.galaxytrucker.model.*;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleCannon;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import it.polimi.ingsw.galaxytrucker.model.game.Game;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import java.util.concurrent.ExecutionException;

public class GameController {

    private GameState gameState;
    private final LobbyManager game;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
    private CardDeck cardDeckTest = new CardDeck(true);
    private Iterator<Player> rankedPlayersIterator;
    private CardContext currentCardContext;
    private boolean gameAlreadyEnded;
    /**
     * Returns the current card context in use by the game controller.
     *
     * @return The current CardContext instance.
     */
    public CardContext getCurrentCardContext() {
        return currentCardContext;
    }


    public CardDeck getCardDeckTest() {
        return cardDeckTest;
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
                case BUILDING_START -> gameState = GameState.BUILDING_END;
                case BUILDING_END -> gameState = GameState.SHIP_CHECK;
                case SHIP_CHECK -> gameState = GameState.CREW_INIT;
                case CREW_INIT -> gameState = GameState.FLIGHT;
            }
        }
    }

    public void startFlight() throws ExecutionException, InterruptedException, IOException {
//        cardDeckTest = Util.createLearningDeck();
        cardDeckTest = Util.createTestDeck();
        game.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));
//        handleTurnBeforeDrawnCard();
    }


    public void sendMatchInfoUpdate() {
        ArrayList<Player> rankedPlayers = getRankedPlayers();

        MatchInfoUpdate miu;
        if (!rankedPlayers.isEmpty()) {
            miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), cardDeckTest.getSize());
            rankedPlayersIterator = rankedPlayers.iterator();
        } else {
            miu = new MatchInfoUpdate("", 0);
        }

        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(miu));

    }


    public void handleTurn() {
        AdventureCard drawnAdventureCard = getCardDeckTest().pop();

        if(getPlayingPlayers().size() == 1){
            while(drawnAdventureCard.getName().equals("Zona Guerra")){
                drawnAdventureCard = getCardDeckTest().pop();
            }
        }

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
            if(!gameAlreadyEnded) {
                gameAlreadyEnded = true;
                handleEndGame();
            }
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
                    int bestLooking = (player.getShip().getnExposedConnector() == minExposed && getRankedPlayers().contains(player)) ? 2 : 0;
                    int finishOrder = calculateFinishOrderScore(player);
                    double reward = calculateGoodRewardScore(player);
                    int losses = calculateLossesScore(player);
                    int credits = player.getNCredits();

                    System.out.println(bestLooking);
                    System.out.println(finishOrder);
                    System.out.println(reward);
                    System.out.println(losses);
                    System.out.println(credits);


                    return new PlayerScore(
                            player.getNickName(),
                            bestLooking,
                            finishOrder,
                            reward,
                            losses,
                            credits
                    );
                })
                .sorted(Comparator.comparingDouble(PlayerScore::getTotalScore).reversed())
                .toList();
    }
    private int calculateFinishOrderScore(Player player) {
        int score = 0;
        ArrayList<Player> activePlayers = getRankedPlayers();
        if (activePlayers.contains(player)) {
            int playerIndex = activePlayers.indexOf(player);
            System.out.println(playerIndex + " playIndex");
            int nPlayers = game.getRealGame().getPlayers().size();
            System.out.println(nPlayers + " nPlayers");
            score = nPlayers - playerIndex;
        }
        return score;

    }
    private float calculateGoodRewardScore(Player player) {
        ArrayList<Good> goods = player.getShip().getGoodsOnShipBoard();
        float score = 0.0f;

        for (Good good : goods) {
            score += good.getValue();
        }

        if (PlayerState.Spectating.equals(player.getPlayerState())) {
            float tmpScore = score * 0.5f;
            return (float) Math.ceil(tmpScore);
        }

        return score;
    }

    private int calculateLossesScore(Player player) {
        int score = 0;
//        int LossesScore = player.getShip().getDestroyedTiles();
        int numToAdd = 0;
        Ship ship = player.getShip();
        for (Tile t: ship.getAsideTiles()){
            if (t != null){
                numToAdd++;
            }
        }
        int LossesScore = player.getShip().getLostTiles();
        score += LossesScore + numToAdd;
        return score;
    }

    public void removePlayerFromGame(String nickname, PlayerLostReason reason) {
        game.getRealGame().getPlayer(nickname).setPlayerState(PlayerState.Spectating);
        game.getRealGame().getFlightBoard().removePlayer(game.getPlayerColors().get(nickname));

        FlightBoardUpdate fbu = new FlightBoardUpdate(game.getRealGame().getFlightBoard());
        PlayerLostUpdate plu = new PlayerLostUpdate(nickname, reason);
        game.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(plu);
            ch.sendMessage(fbu);
        }); //Notifichiamo i client che un player ha perso e aggiorniamo la flight board

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
            if(!gameAlreadyEnded) {
                gameAlreadyEnded = true;
                handleEndGame();
            }
//            completeCardDrawn();
        }
    }

    @NeedsToBeCompleted("Fare TODO")
    public void kickPlayerFromGame(String nickname) {
        game.getPlayerColors().remove(nickname);
        Game realGame = game.getRealGame();

        FlightBoard flightBoard = realGame.getFlightBoard();
        realGame.removePlayer(nickname);

        final FlightBoardUpdate fbu;
        if(flightBoard == null) {
            fbu = null;
        }
        else{
            flightBoard.removePlayer(game.getPlayerColors().get(nickname));
            fbu = new FlightBoardUpdate(game.getRealGame().getFlightBoard());
        }

        PlayerKickedUpdate pku = new PlayerKickedUpdate(nickname);
        game.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(pku);
            if(fbu != null) ch.sendMessage(fbu);
        }); //Notifichiamo i client che un player è stato kickato e aggiorniamo la flight board
        game.removePlayerHandler(nickname);

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
//            completeCardDrawn();
            //TODO
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

        if (pos == null) return null;

        Tile destroyedTile = null;
        boolean aTileHasBeenDestroyed = false;
        String message = "Tile distrutta in posizione" + pos;

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
                if (!protectWithFirstAvailableCannon(ship, projectile.getDirection(),diceRoll)) {
                    destroyedTile = ship.getTileFromPosition(pos);
                    ship.removeTile(pos, false);
                    aTileHasBeenDestroyed = true;
                }
            } else if (projectile.getSize() == ProjectileSize.Little) {
                ArrayList<Connector> tileConnectors = ship.getShipBoard()[pos.getX()][pos.getY()].getTile().getSides();
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
    private boolean protectWithFirstAvailableCannon(Ship ship, ProjectileDirection direction,int diceRoll) {


        for (Position cannonPos : ship.getComponentPositionsFromName("Cannon")) {
            Cannon cannon = (Cannon) ship.getComponentFromPosition(cannonPos);
            ProjectileDirection directionCannon = ProjectileDirection.fromRotation(cannon.getRotation());

            if (direction.equals(ProjectileDirection.UP)) {
                if (directionCannon.equals(ProjectileDirection.UP) && diceRoll == cannonPos.getX()) {
                    return true;
                }
            }
            else if(direction.equals(ProjectileDirection.DOWN)){
               if(directionCannon.equals(ProjectileDirection.DOWN) && (Math.abs(cannonPos.getX() - diceRoll) <= 1)) {
                   return true;
               }
            }
            else{
                if(directionCannon.equals(direction) && (Math.abs(cannonPos.getY() - diceRoll) <= 1)) {
                    return true;
                }
            }
        }

        for (Position cannonPos : ship.getComponentPositionsFromName("DoubleCannon")) {
            DoubleCannon doubleCannon = (DoubleCannon) ship.getComponentFromPosition(cannonPos);
            ProjectileDirection directionDoubleCannon = ProjectileDirection.fromRotation(doubleCannon.getRotation());

            if(doubleCannon.isCharged()) {
                if (direction.equals(ProjectileDirection.UP)) {
                    if (directionDoubleCannon.equals(ProjectileDirection.UP) && diceRoll == cannonPos.getX()) {
                        doubleCannon.setCharged(false);
                        return true;
                    }
                } else if (direction.equals(ProjectileDirection.DOWN)) {
                    if (directionDoubleCannon.equals(ProjectileDirection.DOWN) && (Math.abs(cannonPos.getX() - diceRoll) <= 1)) {
                        doubleCannon.setCharged(false);
                        return true;
                    }
                } else {
                    if (directionDoubleCannon.equals(direction) && (Math.abs(cannonPos.getY() - diceRoll) <= 1)) {
                       doubleCannon.setCharged(false);
                        return true;
                    }
                }
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


    /**
     * Retrieves a list of ranked players based on their placement in the game.
     *
     * @return An ArrayList containing the ranked players.
     */
    public ArrayList<Player> getRankedPlayers() {

//        return new ArrayList<>(getPlayingPlayers().stream().sorted(Comparator.comparingInt(Player::getPlacement)).toList()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti //Prendiamo i giocatori che stanno giocando
        ArrayList<Color> rankedColors = game.getRealGame().getFlightBoard().getRankedPlayers();
        ArrayList<Player> rankedPlayers = new ArrayList<>();
        for (Color color : rankedColors) {
            String nickname = game.getNicknameFromColor(color);
            Player player = game.getRealGame().getPlayer(nickname);
            if (player != null && player.getPlayerState() == PlayerState.Playing) {
                rankedPlayers.add(player);
            }
        }

        return rankedPlayers;

    }

    /**
     * Gets the list of players who are currently active in the game.
     *
     * @return A List of players whose state is 'Playing'.
     */
    public List<Player> getPlayingPlayers(){
        return game.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).toList();
    }


    /**
     * Removes players from the game who have no remaining crew members on their ship.
     * Players are removed with the reason 'NoCrewMembersLeft'.
     */
    public void clearPlayersWithNoCrew(){
        for(Player player : getPlayingPlayers()) {
            Ship ship = player.getShip();
            int nCrewAndAlien = ship.getnCrew();
            int nCrew = nCrewAndAlien - ship.getNBrownAlien()-ship.getNPurpleAlien();
            if(nCrew == 0){
                removePlayerFromGame(player.getNickName(), PlayerLostReason.NoCrewMembersLeft);
            }
        }
    }

    /**
     * Removes players from the game who have been lapped by other players on the flight board.
     * Players are removed with the reason 'Lapped'.
     */
    public void clearLappedPlayers() {
        for(Color color : game.getRealGame().getFlightBoard().getRankedPlayers()) {
            if(game.getRealGame().getFlightBoard().isPlayerLapped(color)){
                removePlayerFromGame(game.getNicknameFromColor(color), PlayerLostReason.Lapped);
            }
        }
    }
}
