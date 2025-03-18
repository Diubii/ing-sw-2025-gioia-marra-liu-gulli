package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.Color;

public class Good {
    private Color color;
    public Good(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public int getValue(){
        switch (color){
            case RED -> {
                return 4;
            }
            case YELLOW -> {
                return 3;
            }
            case GREEN -> {
                return 2;
            }
            case BLUE -> {
                return 1;
            }


        }
        return 0;
    }
}
