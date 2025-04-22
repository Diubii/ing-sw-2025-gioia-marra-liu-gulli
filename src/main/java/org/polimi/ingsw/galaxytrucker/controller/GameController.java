package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

public class GameController {

    private GameState gameState;
    private final LobbyManager myGame;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();

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

        while (myGame.getRealGame().getFlightDeck().getSize() > 0) {
            handleTurn();
        }

    }

    private void handleTurn() {

        Player activePlayer = myGame.getRealGame().getFlightBoard().getLeader();
        //NOTIFICA A TUTTI CHI E' IL LEADER
        AdventureCard adventureCard = myGame.getRealGame().getFlightDeck().pop();
        //NOTIFICHIAMO CHE CARTA E' STATA PESCATA E LA MANDIAMO,
        AdventureCardActivator adventureCardActivator = new AdventureCardEffects();

        adventureCard.activateEffect(adventureCardActivator, myGame.getRealGame().getPlayers(), myGame.getRealGame().getFlightBoard());


    }


}
