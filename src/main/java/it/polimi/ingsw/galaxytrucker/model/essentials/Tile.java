package it.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.galaxytrucker.enums.Connector;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Tile implements Serializable,Cloneable {

    @Serial
    private static final long serialVersionUID = 535L;

    private int id;
    private int Rotation;
    private boolean flipped;
    private ArrayList<Connector> sides;
    private int exposedConnectors;
    private Boolean used;
    private Boolean fixed;
    //    private Slot mySlot;
    private Component myComponent;
    private Boolean wellConnected;

    public Tile(int id, int rotation, ArrayList<Connector> s, Component c) {
        this.id = id;
        Rotation = rotation;
        this.flipped = false;
        this.sides = new ArrayList<Connector>(s);
        this.exposedConnectors = 0;
        this.used = false;
        this.fixed = false;
        this.wellConnected = true;
//        this.mySlot = null;
        this.myComponent = c;
    }

    @JsonCreator
    public Tile(
            @JsonProperty("id") int id,
            @JsonProperty("rotation") int rotation,
            @JsonProperty("flipped") boolean flipped,
            @JsonProperty("sides") ArrayList<Connector> s,
            @JsonProperty("myComponent") Component c,
            @JsonProperty("fixed") boolean fixed,
            @JsonProperty("wellConnected") boolean wellConnected
    ) {
        this.id = id;
        Rotation = rotation;
        this.flipped = flipped;
        this.sides = new ArrayList<>(s);
        this.exposedConnectors = 0;
        this.used = false;
        this.fixed = fixed;
        this.wellConnected = wellConnected;
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
        this.wellConnected = other.wellConnected;
        this.myComponent = other.myComponent;
//        this.mySlot = other.mySlot; // Se vuoi un riferimento al solito Slot, altrimenti escludilo
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMyComponent(Component myComponent1) {
        myComponent = myComponent1;
    }

    public Component getMyComponent() {
        return myComponent;
    }

//    public void setMySlot(Slot mySlot2) {
//
//        mySlot = mySlot2;
//    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Boolean getUsed() {
        return used;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public int getExposedConnectors() {
        return exposedConnectors;
    }

    public ArrayList<Connector> getSides() {
        return new ArrayList<Connector>(sides);
    }

    public void modifyExposedConnector(int i) {
        exposedConnectors += i;
    }


    public int getId() {
        return id;
    }

    public int getRotation() {
        return Rotation;
    }

    public void setRotation(int rotation){
        Rotation = rotation;
    }

    public void rotate(int addRotation) {

        Rotation = (Rotation + addRotation) % 360;
        myComponent.setRotation(Rotation);
        int numRotation = ((addRotation % 360) + 360) % 360 / 90;
        for (int i = 0; i < numRotation; i++) {
            rotateSides90();
        }
    }

    private void rotateSides90() {
        if (sides == null || sides.size() != 4) return;

        Connector temp = sides.get(3);
        sides.set(3, sides.get(2));
        sides.set(2, sides.get(1));
        sides.set(1, sides.get(0));
        sides.set(0, temp);
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void flip() {
        this.flipped = !this.flipped;
    }

    public Boolean getWellConnected() {
        return wellConnected;
    }

    public void setWellConnected(Boolean wellConnected) {
        this.wellConnected = wellConnected;
    }

    public void testPrint() {
        System.out.print(this.id);
        //System.out.print(this.myComponent.getName());
        System.out.print(this.Rotation);
        System.out.print(this.flipped);
        System.out.print(this.sides);
        System.out.print(this.exposedConnectors);
        System.out.print(this.used);
        System.out.println(this.fixed);
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }
@Override
    public Tile clone() {
        try {
            Tile copy = new Tile(this);
            if (this.myComponent != null) {
                copy.myComponent = this.myComponent.clone();
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone Tile", e);
        }
    }
}
