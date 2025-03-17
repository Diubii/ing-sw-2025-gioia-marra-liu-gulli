package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.enums.GameStateType;
import org.polimi.ingsw.galaxytrucker.exceptions.IllegalStateOperationException;
import org.polimi.ingsw.galaxytrucker.model.CardStack;
import org.polimi.ingsw.galaxytrucker.model.units.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.units.Player;
import org.polimi.ingsw.galaxytrucker.model.units.Ship;
import org.polimi.ingsw.galaxytrucker.model.units.TileBunch;

import java.util.*;

/**
 *  Used for managing data,
 *  handling game state changes,
 *  monitoring game progress,
 *  and managing game players.
 */
public class Game {

    private static final int nMaxPlayer = 4;

    private final int gameID;
    private GameState currentState;

    private Map<String, Player> playerMap;
    private Map<String, Ship> playerShip;
    private HashSet<String> usedNicknames;


    private ArrayList<CardStack> decks;
    private CardStack oneDeck;
    private CardStack twoDeck;
    private CardStack learningDeck;

    private FlightBoard flightBoard;
    private TileBunch tileBunch;

    /**
     * Not yet implemented
     * Consider generating deck-related member variables
     * at the beginning.
     */


    public Game(int gameid) {

        this.gameID = gameid;

        this.playerMap = new HashMap<>();
        this.playerShip = new HashMap<>();
        this.usedNicknames = new HashSet<>();

        this.decks = new ArrayList<>();


        this.tileBunch = new TileBunch();
        this.flightBoard = new FlightBoard(new ArrayList<>(), 1);

        this.currentState = new LobbyState();

    }


    public boolean isNicknameUsed(String nickname) {
        return usedNicknames.contains(nickname);
    }

    public void addPlayer(Player player) {
        if (playerMap.size() >= nMaxPlayer || isNicknameUsed(player.getNickName())) {
            return;
        }
        playerMap.put(player.getNickName(), player);
        usedNicknames.add(player.getNickName());
        return;
    }

    public void removePlayer(String nickname) {

    }

    private GameState createStateByType(GameStateType type) {
        switch (type) {
            case LOBBY:
                return new LobbyState();
            case BUILDING:
                return new BuildingState();
            case FLIGHT:
                return new FlightState();
            case PAUSED:
                return new PausedState();
            case ENDGAME:
                return new EndGameState();
            default:
                throw new IllegalArgumentException("unknown GameStateType ");
        }
    }

    public void changeState(GameStateType newStateType) throws IllegalStateOperationException {

        if (currentState != null) {
            currentState.exit();
        }

        currentState = createStateByType(newStateType);
        currentState.enter(this);
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public Player getPlayer(String nickname) {
        return playerMap.get(nickname);
    }

    public int getNumPlayers() {
        return playerMap.size();
    }


    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }


    public GameState getCurrentState() {
        return currentState;
    }

    public int getGameID() {
        return gameID;
    }


    public Ship getPlayerShip(String nickname) {
        return playerShip.get(nickname);
    }

}

