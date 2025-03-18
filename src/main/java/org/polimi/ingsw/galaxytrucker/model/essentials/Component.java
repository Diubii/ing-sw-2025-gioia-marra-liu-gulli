package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitorInterface;

public  class Component {
    private  Tile myTile;
    private final String Name;
    private Boolean Structural;
    protected int rotation = 0;

    public Component(String name, Boolean structural) {
        this.Name = name;

        Structural = structural;

    }

//    public void getName(ComponentNameVisitor cnm){
//
//
//
//    }
//
    public int getRotation() {
        return rotation;
    }
    public void setRotation(int r) {
        rotation = r;
    }

    public String accept(ComponentNameVisitorInterface visitor) {
        return visitor.visit(this);
    }}
