package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

import java.util.ArrayList;

public class Engine extends Component {
    protected int enginePower;

    public Engine(String Name, int enginePower) {

        super(Name);
        this.enginePower = enginePower;
    }
    public int getEnginePower() {
        return enginePower;
    }

    public void calculatePower() {
        Tile tempTile = getMyTile();
        if (tempTile.getRotation() != 0) {
            enginePower = 0;
        }
    }

    public Boolean wellConnected(){
        Boolean wellConnected = true;
        Tile tempTile = getMyTile();
        Ship myShip = tempTile.getMySlot().getMyShip();
        ArrayList<Position> tempIP = myShip.getInvalidPositions();
        Slot mySlot = tempTile.getMySlot();

        //posizione nord
        Position nord = new Position(mySlot.getPosition().getY() -1 , mySlot.getPosition().getX());
        //posizione sud
        Position sud = new Position(mySlot.getPosition().getY() +1 , mySlot.getPosition().getX());
        //posizione est
        Position est = new Position(mySlot.getPosition().getY() , mySlot.getPosition().getX() + 1);
        //posizione ovest
        Position ovest = new Position(mySlot.getPosition().getY() , mySlot.getPosition().getX() -1);

        switch (tempTile.getRotation()){
            case 0: if (!tempIP.contains(sud) &&  myShip.getShipBoard()[sud.getY()][sud.getX()].getTile() != null ) { wellConnected = false; break;}
            case 90:if (!tempIP.contains(est) && myShip.getShipBoard()[est.getY()][est.getX()].getTile() != null) { wellConnected = false; break; }
            case 180:if (!tempIP.contains(nord) && myShip.getShipBoard()[nord.getY()][nord.getX()].getTile() != null) { wellConnected = false; break;}
            case 270:if (!tempIP.contains(ovest) && myShip.getShipBoard()[ovest.getY()][ovest.getX()].getTile() != null) { wellConnected = false; break;}
        }

        return wellConnected;

    }


}
