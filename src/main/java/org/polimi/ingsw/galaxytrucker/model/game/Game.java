package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.TileBunch;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;

import java.util.*;

/**
 *  Used for managing data,
 *  handling game state changes,
 *  monitoring game progress,
 *  and managing game players.
 */
public class Game {

    private int nMaxPlayer;
    private final Map<String, Player> playerMap;
//    private final Map<Player,Ship> playerShip;
    private final HashSet<String> usedNicknames;
    private final HashMap<Player,Integer> playerOrder;



    private Player gameHost;

    private boolean gameStarted;
    private boolean learningMatch;

    private ArrayList<CardDeck> decks;
    private CardDeck oneDeck;
    private CardDeck twoDeck;
    private CardDeck learningDeck;
    private CardDeck flightDeck;

    public FlightBoard flightBoard;
    private final TileBunch tileBunch;


    /**Not yet implemented
     * Consider generating deck-related member variables
     * at the beginning.
     */


    public Game() {
        this.nMaxPlayer = 4;
        this.playerMap = new HashMap<>();
//        this.playerShip = new HashMap<>();
        this.usedNicknames = new HashSet<>();
        this.playerOrder = new HashMap<>();
        this.learningMatch = false;

        this.decks = new ArrayList<>();

        this.tileBunch = new TileBunch();

    }

    /*
    * @authord nerd53
    *
    * creata funzione separata poiche non si sa se il gioco e' learningMatch fino a che il primo client non lo decide
    * */

    public void initFlightBoard(){
        this.flightBoard = new FlightBoard(new ArrayList<>(), learningMatch);

    }

    public void setLearningMatch(Boolean learningMatch){
        this.learningMatch = learningMatch;
    }

    public void setnMaxPlayer(Integer nMaxPlayer){
        this.nMaxPlayer = nMaxPlayer;
    }

    public boolean isNicknameUsed(String nickname) {
        return usedNicknames.contains(nickname);
    }



    public void addPlayer(Player player) throws TooManyPlayersException, PlayerAlreadyExistsException {
        if(playerMap.size() >= nMaxPlayer ){
            throw new TooManyPlayersException(nMaxPlayer);
        }

        if(isNicknameUsed(player.getNickName())){
            throw new PlayerAlreadyExistsException(player.getNickName());
        }
        playerMap.put(player.getNickName(), player);
        usedNicknames.add(player.getNickName());

//        playerShip.put(player,player.getShip());

    }

    public void removePlayer(String nickname) throws PlayerNotFoundException {
        Player player = playerMap.get(nickname);
        if(player == null){
            throw new PlayerNotFoundException(nickname);
        }
        playerMap.remove(nickname);
        usedNicknames.remove(nickname);
//        playerShip.remove(player);

    }

    public void startCardEffect(AdventureCard card, Player leader) {

    }
    public void generatelvtwoDeckes(){

    }

    public void reorderPlayer(){

    }


    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(playerMap.values());
    }

//    public Ship getPlayerShip(Player player) {
//
//        return playerShip.get(player);
//    }

    public Player getPlayer(String nickname) {
        return playerMap.get(nickname);
    }

    public int getNumPlayers() {
        return playerMap.size();
    }


    public TileBunch getTileBunch() {
        return tileBunch;
    }

    public Integer getMaxPlayers(){
        return nMaxPlayer;
    }

    public Boolean getIsLearningMatch(){
        return learningMatch;
    }


    public CardDeck getFlightDeck(){
        return flightDeck;
    }

    public HashMap<Player,Integer> getPlayerOrder(){
        return playerOrder;
    }

    public Player getActivePlayer(){

        Player myPlayer = null ;
        int i = 0;
        for (Player player : playerOrder.keySet()) {

            //il player in posizione 1 e' il primo
            if (playerOrder.get(player) == 1) {
                myPlayer = player;
            }
        }

        return myPlayer;
    }

    public FlightBoard getFlightBoard(){
        return flightBoard;
    }
}





