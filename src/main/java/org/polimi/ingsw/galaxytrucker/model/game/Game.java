package org.polimi.ingsw.galaxytrucker.model.game;

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


    private GameState gameState;
    private GameState previousGameState;

    private Map<String, Player> playerMap = new HashMap<String, Player>();
    private Map<String, Ship> playerShip = new HashMap<String, Ship>();
    private HashSet<String> usedNicknames;


    private ArrayList<CardStack> decks = new ArrayList<CardStack>();
    private CardStack oneDeck;
    private CardStack twoDeck;
    private CardStack learningDeck;

    private FlightBoard flightBoard;
    private TileBunch tileBunch;

    /**Not yet implemented
     * Consider generating deck-related member variables
     * at the beginning.
     */


    public Game() {
        this.gameState = GameState.LOBBY;

        this.playerMap = null;
        this.playerShip = null;
        this.usedNicknames = null;

        this.decks = null;
        this.oneDeck = null;
        this.twoDeck = null;
        this.learningDeck = null;

        this.tileBunch = new TileBunch();
        this.flightBoard = null;


    }

    public void nextState (){
        if(gameState == GameState.PAUSED){
            System.out.println("Game is paused");
            return;
        }
        switch(gameState) {
            case LOBBY:
                gameState = GameState.BUILDING;
                break;
            case BUILDING:
                gameState = GameState.FLIGHT;
                break;
            case FLIGHT:
                gameState = GameState.ENDGAME;
                break;
            case ENDGAME:
                System.out.println("Game ended");
                return;

        }
    }
    public Ship getPlayerShip(String nickname) {
        return playerShip.get(nickname);
    }
    public boolean isNicknameUsed(String nickname) {
        return usedNicknames.contains(nickname);
    }

    public void addPlayer(Player player) {
        if(playerMap.size() >= nMaxPlayer || isNicknameUsed(player.getNickName())){
            return;
        }
        playerMap.put(player.getNickName(), player);
        usedNicknames.add(player.getNickName());
        return;
    }

    public void removePlayer(String nickname) {

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

    public GameState getGameState() {
        return gameState;
    }



}





