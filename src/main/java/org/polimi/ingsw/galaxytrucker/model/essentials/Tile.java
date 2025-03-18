package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.Connector;

import java.util.ArrayList;

public class Tile {
    private int id;
    private int Rotation;
    private boolean flipped;
    private ArrayList<Connector> sides;
    private int exposedConnectors;
    private Boolean used;
    private Boolean fixed;
    private Slot mySlot;
    private Component myComponent;



    public Tile(int id,int rotation, ArrayList<Connector> s, Component c) {
        this.id = id;
        Rotation = rotation;
        this.flipped = false;
        this.sides = new ArrayList<Connector>(s);
        this.exposedConnectors = 0;
        this.used = false;
        this.fixed = false;
        this.mySlot = null;
        this.myComponent = c;
    }
    public Tile(Tile other) {
        this.id = other.id;
        this.Rotation = other.Rotation;
        this.flipped = other.flipped;
        this.sides = new ArrayList<>(other.sides); // Cloniamo la lista di connettori
        this.exposedConnectors = other.exposedConnectors;
        this.used = other.used;
        this.fixed = other.fixed;
        this.mySlot = other.mySlot; // Se vuoi un riferimento al solito Slot, altrimenti escludilo
    }

    public void setMyComponent(Component myComponent1) {
        myComponent = myComponent1;
    }

    public Component getMyComponent() {
        return myComponent;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public void setMySlot(Slot mySlot2) {

        mySlot = mySlot2;
    }



    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Boolean getUsed() {
        return used;
    }

    public Boolean getFixed(){
        return fixed;
    }

    public int getExposedConnectors(){
        return exposedConnectors;
    }

    public ArrayList<Connector> getSides(){
        return new ArrayList<Connector>(sides);
    }

    public void modifyExposedConnector(int i){
        exposedConnectors += i;
    }

    public Slot getMySlot(){
        return mySlot;
    }

    public Boolean wellConnected(){

        Slot[][] TempShipBoard = mySlot.getMyShip().getShipBoard();
        Connector c1;
        Connector c2;
        Connector c3;
        Connector c4;


        //UP TILE

        if (mySlot.getPosition().getY() -1 < 0) {
             c1 = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile().getSides().get(2);
        }
        else {
             c1  = null;
        }

        //LEFT

        if (mySlot.getPosition().getX() -1 < 0) {


             c2 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile().getSides().get(3);
        } else  c2  = null;
        //BOTTOM

        if (mySlot.getPosition().getY() +1 > 6) {
            c3 = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile().getSides().get(0);
        } else  c3 = null;

        //RIGHT TILE

        if (mySlot.getPosition().getX() +1 > 4) {
            c4 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile().getSides().get(1);
        }
        else c4 = null;


        if ((c1 == this.getSides().get(0) || c1 == null )&& (c2 == this.getSides().get(1) || c2 == null) && (c3 == this.getSides().get(2) || c3 == null) && (c4 == this.getSides().get(3) || c4 == null)) {
            return true;
        }

        return false;
    }





    public int getId() {
        return id;
    }
    public int getRotation() {
        return Rotation;
    }

    public void rotate(int addRotation) {
        Rotation += addRotation;
        Rotation %= 360;
    }

    public boolean isFlipped(){
        return flipped;
    }

    public void flip(){
        this.flipped = !this.flipped;
    }
}
