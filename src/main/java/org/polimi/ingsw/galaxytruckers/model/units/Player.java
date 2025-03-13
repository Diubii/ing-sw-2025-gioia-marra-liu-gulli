package org.polimi.ingsw.galaxytruckers.model.units;

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

    public Player(String nickname, int nCredits, int placement) {
        this.nickname = nickname;
        this.nCredits = 0;
        this.placement = placement;
        this.ship = new Ship(); //Rivedere
    }


}
