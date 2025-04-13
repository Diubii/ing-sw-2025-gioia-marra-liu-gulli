package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;

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






}
