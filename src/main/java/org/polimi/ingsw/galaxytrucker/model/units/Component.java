package org.polimi.ingsw.galaxytrucker.model.units;

public  class Component {
    private  Tile myTile;
    private String Name;

    public Component(String name){
        Name = name;
    }

    public void setMyTile(Tile t){
        myTile = t;
    }

    public Tile getMyTile(){
        return myTile;
    }

    public String getName() {
        return Name;
    }
}
