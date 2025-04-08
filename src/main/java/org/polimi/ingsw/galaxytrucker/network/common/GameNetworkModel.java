package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameNetworkModel {
    private Game RealGame;

    private final HashMap<String, Color> PlayerColors;
    private Color nextAvailableColor;

    public void setRealGame(Game game) {
        RealGame = game;
    }

    public Game getRealGame() {
        return RealGame;
    }

    public GameNetworkModel() {
        PlayerColors = new HashMap<>();
        RealGame = new Game();
        nextAvailableColor = Color.RED;
    }

    public HashMap<String, Color> getPlayerColors() {
        return PlayerColors;
    }

    public Color useNextAvailableColor() {

        Color temp = nextAvailableColor;

        int nextIndex = temp.ordinal() + 1;
        nextAvailableColor = Color.values()[nextIndex];

        return temp;
    }


}
