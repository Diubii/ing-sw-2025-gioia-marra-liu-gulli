package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.*;

/**
 * The player
 * Must be created as soon as it enters a game.
 */
public class Player {

    private String nickname;
    private int nCredits;

    private Ship ship;
    private int placement;

    private HashMap<String, Integer> detailedScores;

    private List<Good> ListOfGoods;
    private List<Good> ListUnloadedGoods;

    public Player(String nickname, int nCredits, int placement, Boolean leaningMatch) {
        this.nickname = nickname;
        this.nCredits = 0;
        this.placement = placement;
        this.ship = new Ship(leaningMatch); //Rivedere
    }

    public String getNickName() {
        return nickname;
    }
    public Ship getShip() { return ship; }
    public void addCredits(int credits){
        nCredits += credits;
    }
}
