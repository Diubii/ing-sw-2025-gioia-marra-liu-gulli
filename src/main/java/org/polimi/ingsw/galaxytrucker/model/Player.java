package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.enums.PlayerState;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;

import java.util.*;

/**
 * The player
 * Must be created as soon as it enters a game.
 */
public class Player {

    private final String nickname;
    private int nCredits;

    private Ship ship;
    private int placement;

    private HashMap<String, Integer> detailedScores;

    private List<Good> ListOfGoods;
    private List<Good> ListUnloadedGoods;
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
}
