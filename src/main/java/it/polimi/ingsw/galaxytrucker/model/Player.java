package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.enums.PlayerState;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.HashMap;
import java.util.List;

/**
 * The player
 * Must be created as soon as it enters a game.
 */
public class Player {

    private final String nickname;
    private int nCredits;
    private Color color;
    private Ship ship;
    private int placement;
    private final Object shipLock = new Object();

    private PlayerState playerState;

    public Player(String nickname, int nCredits, int placement, Boolean learningMatch) {
        this.nickname = nickname;
        this.nCredits = 0;
        this.placement = placement;
        this.ship = new Ship(learningMatch); //Rivedere
    }

    public String getNickName() {
        return nickname;
    }

    public Ship getShip() {
        synchronized (shipLock) {
            return ship;
        }
    }

    public void addCredits(int credits) {
        nCredits += credits;
    }

    public int getNCredits() {
        return nCredits;
    }

    public void setNCredits(int nCredits) {
        this.nCredits = nCredits;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public void replaceShip(Ship ship) {
        this.ship = ship;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
