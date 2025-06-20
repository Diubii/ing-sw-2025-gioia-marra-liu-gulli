package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.enums.Color;

import java.io.Serial;
import java.io.Serializable;

public class PlayerInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3462319L;

    private Color color;
    private String NickName;
    private Ship ship;


    private final Object shipLock = "";

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getNickName() {
        return NickName;

    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public Ship getShip() {
        synchronized (shipLock) {
            return ship;
        }
    }

    public void setShip(Ship ship) {
        synchronized (shipLock) {
            this.ship = ship;
        }

    }


}
