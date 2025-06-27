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


/**
 * Manages the overall game flow and state transitions in the Galaxy Trucker game.
 * Handles phases such as building, flight, and end-game scoring, along with player elimination logic.
 * Coordinates network communication via LobbyManager and executes adventure card effects.
 */
public class GameController {

    private GameState gameState;
    private final LobbyManager game;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
    private CardDeck cardDeckTest = new CardDeck(true);
    private Iterator<Player> rankedPlayersIterator;
    private CardContext currentCardContext;
    private boolean gameAlreadyEnded;
    private final Object gameStateLock = new Object();


        /**
         * Returns the current card context being used by the game controller to manage adventure card execution.
         *
         * @return The current CardContext instance.
         */
        public CardContext getCurrentCardContext() {
            return currentCardContext;
        }

        /**
         * Retrieves the current deck of adventure cards used during gameplay.
         *
         * @return The current CardDeck instance.
         */
        public CardDeck getCardDeckTest() {
            return cardDeckTest;
        }

        /**
         * Gets the number of completed ships that have been successfully built by players.
         *
         * @return The count of completed ships.
         */
        public int getnCompletedShips() {
            synchronized (ncsLock) {
                return nCompletedShips;
            }
        }

        /**
         * Increments the count of completed ships when a player finishes constructing their ship.
         */
        public void addCompletedShip() {
            synchronized (ncsLock) {
                nCompletedShips++;
            }
        }

        /**
         * Initializes the game controller with a reference to the lobby manager and sets the initial game state to LOBBY.
         *
         * @param game The LobbyManager managing connections and player sessions.
         */
        public GameController(LobbyManager game) {
            this.game = game;
            gameState = GameState.LOBBY;
        }

        /**
         * Retrieves the current game state (e.g., LOBBY, BUILDING_START, FLIGHT).
         *
         * @return The current GameState enum value.
         */
        public GameState getGameState() {
            synchronized (gameStateLock) {
                return gameState;
            }
        }

        /**
         * Advances the game to the next phase based on the current state.
         * Ensures thread-safe state transitions using synchronization.
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
         * Starts the flight phase by initializing the adventure card deck and setting all eligible players to 'Playing' state.
         *
         * @throws ExecutionException   If an error occurs during asynchronous execution.
         * @throws InterruptedException If the operation is interrupted.
         * @throws IOException          If an I/O error occurs.
         */
        public void startFlight() throws ExecutionException, InterruptedException, IOException {
            cardDeckTest = Util.createTestDeck();
            game.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));
        }

        /**
         * Sends match information updates to all connected clients, including the current leading player and remaining card count.
         */
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

        /**
         * Executes a game turn by drawing an adventure card, notifying clients, and initiating card effect processing.
         */
        public void handleTurn() {
            AdventureCard drawnAdventureCard = getCardDeckTest().pop();

            if(getPlayingPlayers().size() == 1){
                while(drawnAdventureCard.getName().equals("Zona Guerra")){
                    drawnAdventureCard = getCardDeckTest().pop();
                }
            }

            DrawnAdventureCardUpdate drawnAdventureCardUpdate = new DrawnAdventureCardUpdate(drawnAdventureCard);
            game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(drawnAdventureCardUpdate));

            CardContext context = new CardContext(game, drawnAdventureCard);
            currentCardContext = context;
            context.executePhase();
        }

        /**
         * Processes the end of a turn by checking for game termination conditions and resetting ready states.
         */
        public void handleEndTurn(){
            clearPlayersWithNoCrew();
            clearLappedPlayers();

            CardDeck cardDeck = getCardDeckTest();
            EndTurnUpdate etu = new EndTurnUpdate();

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

        /**
         * Handles the finalization of the game by calculating scores and sending game-end updates to clients.
         */
        public void handleEndGame() {
            List<PlayerScore> scores = calculateScores();
            GameEndUpdate geu = new GameEndUpdate(new ArrayList<>(scores));
            game.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(geu));
        }

        /**
         * Calculates final player scores based on multiple factors: ship design, finish order, cargo value, losses, and credits.
         *
         * @return A list of PlayerScore objects sorted by total score in descending order.
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
                    .sorted(Comparator.comparingDouble(PlayerScore::getTotalScore).reversed())
                    .toList();
        }

        /**
         * Calculates the score contribution based on a player's finishing position in the race.
         *
         * @param player The player whose score is being calculated.
         * @return The score contribution from finishing position.
         */
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

        /**
         * Calculates the score contribution from goods collected on the ship.
         * Spectating players receive half points rounded up.
         *
         * @param player The player whose score is being calculated.
         * @return The score contribution from cargo value.
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
         * Calculates the score deduction based on tiles destroyed during gameplay.
         *
         * @param player The player whose score is being calculated.
         * @return The score contribution from losses.
         */
        private int calculateLossesScore(Player player) {
            int score = 0;
            int LossesScore = player.getShip().getDestroyedTiles();
            score += LossesScore;
            return score;
        }

        /**
         * Removes a player from the game due to specified reasons (e.g., no crew left or lapped).
         *
         * @param nickname The nickname of the player to be removed.
         * @param reason   The reason why the player is being removed.
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
         * Kicks a player from the game, removing them from all relevant data structures and notifying other clients.
         *
         * @param nickname The nickname of the player to be kicked.
         */
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
            });
            game.removePlayerHandler(nickname);

            if (game.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            }
        }

        /**
         * Reacts to a projectile hitting a ship by potentially destroying a tile based on type and shield protection.
         *
         * @param targetPlayer The player whose ship is being attacked.
         * @param projectile   The projectile object containing type, size, and direction.
         * @param diceRoll     Result of the dice roll determining impact location.
         * @return The destroyed Tile if applicable, otherwise null.
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

                    if (tileConnectors.get(index) != Connector.EMPTY) {
                        if (!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())) {
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
         * Checks if a cannon can protect the ship from incoming projectiles based on position and orientation.
         *
         * @param ship        The ship being protected.
         * @param direction   Direction from which the projectile is coming.
         * @param diceRoll    Impact position determined by dice roll.
         * @return True if the ship is protected by a cannon, false otherwise.
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
         * Checks if a charged shield oriented correctly can protect the ship from incoming projectiles.
         *
         * @param ship      The ship being protected.
         * @param direction Direction from which the projectile is coming.
         * @return True if the ship is protected by a shield, false otherwise.
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
         * Retrieves a list of players currently ranked on the flight board who are still actively playing.
         *
         * @return An ArrayList containing the ranked players still in the game.
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
         * Gets a list of players who are currently active in the game (i.e., not spectating).
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