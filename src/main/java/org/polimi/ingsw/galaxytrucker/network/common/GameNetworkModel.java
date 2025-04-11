package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameNetworkModel {
    private Game RealGame;

    private final HashMap<String, Color> PlayerColors;
    private Color nextAvailableColor;
    private final HashMap<String, ClientHandler> PlayerHandlers = new HashMap<>();


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

    public void addPlayerHandler(ClientHandler handler, String playerName) {
        PlayerHandlers.put(playerName, handler);
    }

    public void removePlayerHandler(String playerName) {
        PlayerHandlers.remove(playerName);
    }

    public HashMap<String, ClientHandler> getPlayerHandlers() {
        return PlayerHandlers;
    }


}
