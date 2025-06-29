package it.polimi.ingsw.galaxytrucker.network.client;

import it.polimi.ingsw.galaxytrucker.enums.PlayerState;
import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;

import java.util.ArrayList;

/**
 * The {@code ClientModel} class represents the client-side model for the Galaxy Trucker game.
 * It holds the current state of the player and game elements relevant to the client.
 *
 * <p>This includes information such as the player's ship, game state, flight board, deck of adventure cards,
 * player list, and current timers.</p>
 *
 * <p>It provides getters and setters to modify the model during gameplay and sync it with server updates.</p>
 */
public class ClientModel {


    /**
     * Constructs a new {@code ClientModel} and initializes the player info list.
     */
    public ClientModel() {
        this.myInfo = new PlayerInfo();
        this.playerInfos = new ArrayList<>();
    }


    private ArrayList<Integer> tilesToRemove = new ArrayList<>();
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> playerInfos;
    private FlightBoard flightBoard;
    private ArrayList<CardDeck> cardDecks = new ArrayList<>();
    private ArrayList<Tile> faceUpTiles = new ArrayList<>();
    private boolean isLeader;
    private Planet selectedPlanet;
    private PlayerState playerState;
    private boolean isLearningMatch;
    private ArrayList<Good> unplacedGoods = new ArrayList<>();
    private AdventureCard currentAdventureCard;
    private final ArrayList<TimerInfo> timerInfos = new ArrayList<>();

    /**
     * Gets the current {@link PlayerState} of the local player.
     *
     * @return the current player state
     */
    public PlayerState getPlayerState() {
        return playerState;
    }
    /**
     * Sets the current state of the player.
     *
     * @param playerState the new player state
     */
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }
    /**
     * Returns the list of tile IDs to be removed.
     *
     * @return list of tile IDs
     */
    public ArrayList<Integer> getTilesToRemove() {
        return tilesToRemove;
    }


    /**
     * Sets the list of tile IDs to be removed.
     *
     * @param tilesToRemove list of tile IDs
     */
    public void setTilesToRemove(ArrayList<Integer> tilesToRemove) {
        this.tilesToRemove = tilesToRemove;
    }


    /**
     * Adds a tile ID to the list of tiles to remove.
     *
     * @param tileID the ID of the tile to remove
     */
    public void addTileToRemove(Integer tileID) {
        this.tilesToRemove.add(tileID);
    }


    /**
     * Gets the list of card decks available in the game.
     *
     * @return list of card decks
     */
    public ArrayList<CardDeck> getCardDecks() {
        return cardDecks;
    }

    /**
     * Sets the card decks used in the game.
     *
     * @param cardDecks list of card decks
     */
    public void setCardDecks(ArrayList<CardDeck> cardDecks) {
        this.cardDecks = cardDecks;
    }

    /**
     * Returns the current flight board.
     *
     * @return the flight board
     */
    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    /**
     * Sets the flight board.
     *
     * @param flightBoard the flight board
     */
    public void setFlightBoard(FlightBoard flightBoard) {
        this.flightBoard = flightBoard;
    }

    /**
     * Sets the local player's information.
     *
     * @param myInfo the player info
     */
    public void setMyInfo(PlayerInfo myInfo) {
        this.myInfo = myInfo;
    }
    /**
     * Gets the local player's information.
     *
     * @return player info
     */
    public PlayerInfo getMyInfo() {
        return myInfo;
    }


    /**
     * Sets the list of all players' information.
     *
     * @param playerInfos list of player info
     */
    public void setPlayerInfos(ArrayList<PlayerInfo> playerInfos) {
        this.playerInfos = playerInfos;
    }

    /**
     * Gets the list of all players' information.
     *
     * @return list of player info
     */
    public ArrayList<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    /**
     * Gets the face-up tiles currently available.
     *
     * @return list of face-up tiles
     */
    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }
    /**
     * Sets the face-up tiles currently available.
     *
     * @param faceUpTiles list of face-up tiles
     */
    public void setFaceUpTiles(ArrayList<Tile> faceUpTiles) {

        this.faceUpTiles = faceUpTiles;
    }

    /**
     * Gets the player's reserved tiles (aside tiles).
     *
     * @return array of reserved tiles
     */

    public Tile[] getReservedTiles() {
        if (myInfo != null && myInfo.getShip() != null) {
            return myInfo.getShip().getAsideTiles();
        } else {
            return new Tile[2];
        }
    }


    /**
     * Gets the player info for a specific nickname.
     *
     * @param nickname the nickname of the player
     * @return the corresponding player info, or {@code null} if not found
     */
    public PlayerInfo getPlayerInfoByNickname(String nickname) {
        if (myInfo != null && myInfo.getNickName().equals(nickname)) {
            return myInfo;
        }

        synchronized (playerInfos) {
            return playerInfos.stream()
                    .filter(info -> info.getNickName().equals(nickname))
                    .findFirst()
                    .orElse(null);
        }
    }



    /**
     * Sets whether this client is the match leader.
     *
     * @param isLeader {@code true} if leader, {@code false} otherwise
     */
    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    /**
     * Checks whether this client is the match leader.
     *
     * @return {@code true} if leader, {@code false} otherwise
     */
    public boolean isLeader() {
        return isLeader;
    }


    /**
     * Checks whether the model contains a player with the given nickname.
     *
     * @param nickname the nickname to check
     * @return {@code true} if present, {@code false} otherwise
     */
    public boolean hasPlayerWithNickname(String nickname) {
        return getPlayerInfoByNickname(nickname) != null;
    }

    /**
     * Gets the currently selected planet.
     *
     * @return the selected planet
     */
    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }

    /**
     * Sets the selected planet.
     *
     * @param selectedPlanet the selected planet
     */
    public void setSelectedPlanet(Planet selectedPlanet) {
        this.selectedPlanet = selectedPlanet;
    }

    /**
     * Checks whether the current match is in learning mode.
     *
     * @return {@code true} if learning mode, {@code false} otherwise
     */
    public boolean isLearningMatch() {
        return isLearningMatch;
    }

    /**
     * Sets whether the match is in learning mode.
     *
     * @param learningMatch {@code true} to enable learning mode
     */
    public void setLearningMatch(boolean learningMatch) {
        isLearningMatch = learningMatch;
    }


    /**
     * Gets the current adventure card being processed.
     *
     * @return the current adventure card
     */
    public AdventureCard getCurrentAdventureCard() {
        return currentAdventureCard;
    }

    /**
     * Sets the current adventure card.
     *
     * @param currentAdventureCard the adventure card to set
     */
    public void setCurrentAdventureCard(AdventureCard currentAdventureCard) {
        this.currentAdventureCard = currentAdventureCard;
    }
    /**
     * Gets the list of goods that have not yet been placed.
     *
     * @return list of unplaced goods
     */
    public ArrayList<Good> getUnplacedGoods() {
        return unplacedGoods;
    }

    /**
     * Sets the list of goods that have not yet been placed.
     *
     * @param unplacedGoods list of unplaced goods
     */
    public void setUnplacedGoods(ArrayList<Good> unplacedGoods) {
        this.unplacedGoods = unplacedGoods;
    }

    /**
     * Gets the timer information for each timer on the board.
     *
     * @return list of timer info
     */
    public ArrayList<TimerInfo> getTimerInfos() {
        return timerInfos;
    }
}
