package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.MatchInfoUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameController {

    private GameState gameState;
    private final LobbyManager myGame;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
//    private  final ServerController serverController;

    public LobbyManager getMyGame() {
        return myGame;
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

    public GameController(LobbyManager myGame) {

        this.myGame = myGame;
//        this.serverController = serverController;
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
                case BUILDING_TIMER -> gameState = GameState.SHIP_CHECK;
                case SHIP_CHECK -> gameState = GameState.CREW_INIT;
                case CREW_INIT -> gameState = GameState.FLIGHT;
            }
        }
    }

    public void startFlight() {

//        ArrayList<Decks>
//
//        while (myGame.getRealGame().ge) {
//            handleTurn();
//        }

    }

    private void handleTurn() throws IOException {



        Player activePlayer = myGame.getRealGame().getFlightBoard().getLeader();
        //NOTIFICA A TUTTI CHI E' IL LEADER (MATCH_INFO)
        for (ClientHandler clientHandler: myGame.getPlayerHandlers().values()){
            clientHandler.sendMessage(new MatchInfoUpdate());
        }
        
        AdventureCard adventureCard = myGame.getRealGame().getFlightDeck().pop();
        //NOTIFICHIAMO CHE CARTA E' STATA PESCATA E LA MANDIAMO,

        AdventureCardActivator adventureCardActivator = new AdventureCardEffects();

        adventureCard.activateEffect(adventureCardActivator, myGame.getRealGame().getPlayers(), myGame.getRealGame().getFlightBoard(), this);



    }


}
