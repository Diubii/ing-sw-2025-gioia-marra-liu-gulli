package org.polimi.ingsw.galaxytrucker.model.game;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Hourglass;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.TileBunch;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;

import java.io.IOException;
import java.util.*;

/**
 * Used for managing data,
 * handling game state changes,
 * monitoring game progress,
 * and managing game players.
 */
public class Game {

    private int nMaxPlayer;
    private final Map<String, Player> playerMap;
    //    private final Map<Player,Ship> playerShip;
    private final HashSet<String> usedNicknames;
    private final HashMap<Player, Integer> playerOrder;

    public ArrayList<TimerInfo> getTimerInfos() {
        return timerInfos;
    }

    public void setTimerInfos(ArrayList<TimerInfo> timerInfos) {
        this.timerInfos = timerInfos;
    }

    private ArrayList<TimerInfo> timerInfos = new ArrayList<>();


    private Player gameHost;

    private boolean gameStarted;
    private boolean learningMatch;

    private ArrayList<CardDeck> Decks;
    private CardDeck flightDeck;


    public FlightBoard flightBoard;
    private final TileBunch tileBunch;
    private Hourglass hourglass;


    /**
     * Not yet implemented
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

        this.Decks = new ArrayList<>();

        this.tileBunch = new TileBunch();
        setupTimerInfos();

    }

    /*
     * @authord nerd53
     *
     * creata funzione separata poiche non si sa se il gioco e' learningMatch fino a che il primo client non lo decide
     * */

    public ArrayList<CardDeck> getDecks() {
        return Decks;
    }

    private void setupTimerInfos(){

        if (!learningMatch){
            timerInfos.add(new TimerInfo(0,0,false));
            timerInfos.add(new TimerInfo(1,0,false));
            timerInfos.add(new TimerInfo(2,0,false));
        }

    }

    public CardDeck getFlightDeck() {
        return flightDeck;
    }


    public void initFlightBoard() {
        this.flightBoard = new FlightBoard(learningMatch);
    }

    public void setFlightBoard(FlightBoard flightBoard) {
        this.flightBoard = flightBoard;
    }

    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    public void setLearningMatch(Boolean learningMatch) {
        this.learningMatch = learningMatch;
    }

    public void setnMaxPlayer(Integer nMaxPlayer) {
        this.nMaxPlayer = nMaxPlayer;
    }

    public boolean isNicknameUsed(String nickname) {

        synchronized (usedNicknames) {
            return usedNicknames.contains(nickname);
        }
    }


    public void addPlayer(Player player) throws TooManyPlayersException, PlayerAlreadyExistsException {

        synchronized (playerMap) {

            if (playerMap.size() >= nMaxPlayer) {
                throw new TooManyPlayersException(nMaxPlayer);
            }

            if (isNicknameUsed(player.getNickName())) {
                throw new PlayerAlreadyExistsException(player.getNickName());
            }
            playerMap.put(player.getNickName(), player);
            synchronized (usedNicknames) {
                usedNicknames.add(player.getNickName());
            }


        }

//        playerShip.put(player,player.getShip());

    }


    public void removePlayer(String nickname) {
        synchronized (playerMap) {

            Player player = playerMap.get(nickname);

            if (player == null) return;

            playerMap.remove(nickname);
            synchronized (usedNicknames) {
                usedNicknames.remove(nickname);
            }
        }
    }


    public void generatelvtwoDeckes() {

    }

    public void reorderPlayer() {

    }


    public ArrayList<Player> getPlayers() {
        synchronized (playerMap) {
            return new ArrayList<>(playerMap.values());
        }
    }

//    public Ship getPlayerShip(Player player) {
//
//        return playerShip.get(player);
//    }

    public Player getPlayer(String nickname) {
        synchronized (playerMap) {
            return playerMap.get(nickname);
        }
    }

    public int getNumPlayers() {
        synchronized (playerMap) {
            return playerMap.size();
        }
    }


    public TileBunch getTileBunch() {
        return tileBunch;
    }

    public Integer getMaxPlayers() {
        return nMaxPlayer;
    }

    public Boolean getIsLearningMatch() {
        return learningMatch;
    }


    public HashMap<Player, Integer> getPlayerOrder() {
        return playerOrder;
    }


    public Player getPlayerFromName(String nickname) {
        return playerMap.get(nickname);
    }


    public ArrayList<CardDeck> createBuildingCardDecks(CardDeck lvl1cards, CardDeck lvl2cards) {
        lvl1cards.shuffle();
        lvl2cards.shuffle();

        ArrayList<CardDeck> decks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CardDeck deck = new CardDeck(i < 3); //L'ultima non è spiabile
            deck.addCard(lvl2cards.pop());
            deck.addCard(lvl2cards.pop());
            deck.addCard(lvl1cards.pop());
            decks.add(deck);
        }

        return new ArrayList<>(decks);
    }

    public CardDeck createFlightDeck(ArrayList<CardDeck> decks) {
        CardDeck flightDeck = new CardDeck(true);

        for (CardDeck deck : decks) {
            flightDeck = flightDeck.merge(new CardDeck(deck.getCards(), deck.isSpyable()));
        }

        flightDeck.shuffle();
        if (!learningMatch) {
            flightDeck.putFirstLvl2CardOnTop();
        }

        return flightDeck;
    }

    public void createDecks() {

        ArrayList<CardDeck> decks = new ArrayList<>();


        if (learningMatch) {
            Decks.add(Util.createLearningDeck());
//            decks.addAll(Decks);
        } else {

            ArrayList<CardDeck> tempDecks = createBuildingCardDecks(Util.createLvl1Deck(), Util.createLvl2Deck());
            Decks = new ArrayList<>(tempDecks);
//            decks.addAll(Decks);

        }


    }



}





