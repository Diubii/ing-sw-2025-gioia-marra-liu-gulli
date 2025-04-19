package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardActivator;

public class GameController {

    private GameState gameState;
    private final GameNetworkModel myGame;

    public GameController(GameNetworkModel myGame){

        this.myGame = myGame;
        gameState = GameState.LOBBY;

    }


    public GameState getGameState(){
        return gameState;
    }

    public void nextState(){

        switch (gameState){
            case LOBBY:
                gameState = GameState.BUILDING_START;
                break;

            case BUILDING_START:
                gameState = GameState.BUILDING_TIMER;
                break;

            case BUILDING_TIMER:
                gameState = GameState.BUILDING_END;
                break;

            case BUILDING_END:
                gameState = GameState.SHIP_CHECK;
                break;

            case SHIP_CHECK:
                gameState = GameState.FLIGHT;
                break;
        }

    }

    public void startFlight(){

        while (myGame.getRealGame().getFlightDeck().getSize() > 0){
            handleTurn();
        }

    }

    private void handleTurn() {

        Player activePlayer = myGame.getRealGame().getActivePlayer();
        AdventureCard adventureCard = myGame.getRealGame().getFlightDeck().pop();
        AdventureCardActivator adventureCardActivator = new AdventureCardEffects();

        adventureCard.activateEffect(adventureCardActivator,myGame.getRealGame().getPlayers(), myGame.getRealGame().getFlightBoard());


    }


}
