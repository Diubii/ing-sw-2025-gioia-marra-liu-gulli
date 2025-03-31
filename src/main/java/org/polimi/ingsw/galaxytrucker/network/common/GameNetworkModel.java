package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.model.game.Game;

import java.util.ArrayList;
import java.util.List;

public class GameNetworkModel {
    private final List<String> moves = new ArrayList<>();
    private Game RealGame = null;

    public synchronized void addMove(String move) {
        moves.add(move);
    }

    public synchronized List<String> getMoves() {
        return new ArrayList<>(moves);
    }

    public void setRealGame(Game game) {
        RealGame = game;
    }

    public Game getRealGame() {
        return RealGame;
    }
}
