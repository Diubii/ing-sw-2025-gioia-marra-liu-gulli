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
import java.util.List;

import java.util.concurrent.ExecutionException;

public class GameController {

    private GameState gameState;
    private final LobbyManager game;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
    private CardDeck cardDeckTest = new CardDeck(true);
    private CardContext currentCardContext;
    private boolean gameAlreadyEnded;
    private CardDeck flightDeck;
    /**
     * Returns the current card context in use by the game controller.
     *
     * @return The current CardContext instance.
     */
    public CardContext getCurrentCardContext() {
        return currentCardContext;
    }


    /**
     * Returns the current adventure card deck.
     *
     * @return the {@link CardDeck} in use
     */

    public CardDeck getCardDeckTest() {
        return cardDeckTest;
    }

    /**
     * Returns the number of players who have completed their ships.
     * Thread-safe.
     *
     * @return number of completed ships
     */
    public int getnCompletedShips() {
        synchronized (ncsLock) {
            return nCompletedShips;
        }
    }
    /**
     * Increments the count of completed ships by one.
     * Thread-safe.
     */

    public void addCompletedShip() {
        synchronized (ncsLock) {
            nCompletedShips++;
        }
    }

    /**
     * Constructs a new GameController for the given game lobby.
     *
     * @param game the LobbyManager managing the current match
     */
    public GameController(LobbyManager game) {
        this.game = game;
        gameState = GameState.LOBBY;
    }

    private final Object gameStateLock = new Object();

    /**
     * Returns the current global game state.
     * <p>
     * Thread-safe access using {@code gameStateLock}.
     *
     * @return the current {@link GameState}
     */
    public GameState getGameState() {
        synchronized (gameStateLock) {
            return gameState;
        }
    }

    /**
     * Advances the game to the next {@link GameState} in the predefined sequence.
     * <p>
     * Thread-safe transition using {@code gameStateLock}.
     * The sequence is: LOBBY → BUILDING_START → BUILDING_END → SHIP_CHECK → CREW_INIT → FLIGHT.
     */
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

    /**
     * Starts the flight phase by initializing the card deck and setting all players to the 'Playing' state.
     *
     * @throws ExecutionException   if task execution fails
     * @throws InterruptedException if thread is interrupted
     * @throws IOException          if deck initialization fails
     */
    public void startFlight() throws ExecutionException, InterruptedException, IOException {
//        cardDeckTest = Util.createLearningDeck();
        flightDeck = game.getRealGame().createFlightDeck(game.getRealGame().getDecks());
        game.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));

    }


    /**
     * Sends a {@link MatchInfoUpdate} to all players with the current leader and the number of remaining adventure cards.
     * <p>
     * If no ranked players are available, an empty update is sent.
     */
    public void sendMatchInfoUpdate() {
        ArrayList<Player> rankedPlayers = getRankedPlayers();
        MatchInfoUpdate miu;
        if (!rankedPlayers.isEmpty()) {
            miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), flightDeck.getSize());
        } else {
            miu = new MatchInfoUpdate("", 0);
        }
        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(miu));

    }


    /**
     * Executes a single turn in the flight phase.
     * <p>
     * Draws the next adventure card, broadcasts it to players, and executes its effect.
     */
    public void handleTurn() {
        AdventureCard drawnAdventureCard = flightDeck.pop();

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

    /**
     * Ends the current turn, checks for end-game conditions, and resets turn state.
     * <p>
     * Also removes invalid players and prepares for the next turn if the game continues.
     */
    public void handleEndTurn(){
        clearPlayersWithNoCrew();
        clearLappedPlayers();

        EndTurnUpdate etu = new EndTurnUpdate();
        //inviare end turn update
        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty() || flightDeck.getSize() == 0) {
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


    /**
     * Handles end-of-game procedures by calculating final scores and notifying all players.
     * <p>
     * Sends a {@link GameEndUpdate} containing the ranked player scores.
     */
    public void handleEndGame() {
        List<PlayerScore> scores = calculateScores();
        GameEndUpdate geu = new GameEndUpdate(new ArrayList<>(scores));
        game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(geu));

    }

    /**
     * Calculates the final score for each player at the end of the game.
     * <p>
     * Scores include bonuses for ship appearance, finish order, rewards from goods,
     * penalties from tile losses, and collected credits.
     *
     * @return a sorted list of {@link PlayerScore} in descending order of total score
     */
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
                    int losses = -calculateLossesScore(player);
                    int credits = player.getNCredits();

//                    System.out.println(bestLooking);
//                    System.out.println(finishOrder);
//                    System.out.println(reward);
//                    System.out.println(losses);
//                    System.out.println(credits);


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

    /**
     * Calculates the score based on the player's finish order in the flight phase.
     *
     * @param player the player to evaluate
     * @return the finish order score, or 0 if the player is not ranked
     */
    private int calculateFinishOrderScore(Player player) {
        int score = 0;
        ArrayList<Player> activePlayers = getRankedPlayers();
        if (activePlayers.contains(player)) {
            int playerIndex = activePlayers.indexOf(player);

            int nPlayers = game.getRealGame().getPlayers().size();

            score = nPlayers - playerIndex;
        }
        return score;

    }

    /**
     * Calculates the total value of goods on the player's ship.
     * <p>
     * Spectating players earn only 50% of the value (rounded up).
     *
     * @param player the player whose goods are evaluated
     * @return the total reward score from goods
     */
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

    /**
     * Calculates penalty points based on how many tiles were destroyed on the player's ship.
     *
     * @param player the player to evaluate
     * @return the total penalty points
     */
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

    /**
     * Removes a player from the game due to a loss condition and notifies all clients.
     * <p>
     * The player is marked as {@link PlayerState#Spectating} and removed from the flight board.
     * Sends a {@link PlayerLostUpdate} and {@link FlightBoardUpdate} to all players.
     * If no players remain, triggers game end.
     *
     * @param nickname the nickname of the player to remove
     * @param reason   the reason the player lost (e.g., lapped, no crew, early landing)
     */
    public void removePlayerFromGame(String nickname, PlayerLostReason reason) {
        game.getRealGame().getPlayer(nickname).setPlayerState(PlayerState.Spectating);
        game.getRealGame().getFlightBoard().removePlayer(game.getPlayerColors().get(nickname));

        FlightBoardUpdate fbu = new FlightBoardUpdate(game.getRealGame().getFlightBoard());
        PlayerLostUpdate plu = new PlayerLostUpdate(nickname, reason);
        game.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(plu);
            ch.sendMessage(fbu);
        });

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {

            if(!gameAlreadyEnded) {
                gameAlreadyEnded = true;
                handleEndGame();
            }
        }
    }

    /**
     * Kicks a player from the game and notifies all clients.
     * <p>
     * Also removes the player from the flight board and player handler map.
     *
     * @param nickname the player to kick
     */
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
        });
        game.removePlayerHandler(nickname);

        if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
//            completeCardDrawn();
        }
    }

    /**
     * Handles the effect of a projectile hitting a player's ship.
     * <p>
     * Determines the tile impacted by the projectile based on its type, size, and direction,
     * and applies damage if the tile is not protected (by shield or cannon).
     *
     * @param targetPlayer the player whose ship is being targeted
     * @param projectile   the incoming projectile
     * @param diceRoll     the dice roll determining the projectile's trajectory
     * @return the tile that was destroyed, or {@code null} if no tile was removed
     */

    public Tile reactToProjectile(Player targetPlayer, Projectile projectile, int diceRoll) {

        Ship ship = targetPlayer.getShip();
        Position pos = ship.getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll);

        if (pos == null) return null;

        Tile destroyedTile = null;

        if (projectile.getType() == ProjectileType.CannonFire) {
            if (projectile.getSize() == ProjectileSize.Big) {
                destroyedTile = ship.getTileFromPosition(pos);
                ship.removeTile(pos, false);

            } else if (projectile.getSize() == ProjectileSize.Little) {
                if (!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())) {
                    destroyedTile = ship.getTileFromPosition(pos);
                    ship.removeTile(pos, false);
                }
            }
        } else if (projectile.getType() == ProjectileType.Meteor) {
            if (projectile.getSize() == ProjectileSize.Big) {
                if (!protectWithFirstAvailableCannon(ship, projectile.getDirection(),diceRoll)) {
                    destroyedTile = ship.getTileFromPosition(pos);
                    ship.removeTile(pos, false);
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

    public CardDeck getFlightDeck() {
        return flightDeck;
    }
}
