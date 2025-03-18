package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Ship;

import java.util.ArrayList;

public class Util {
    public static Boolean EngineWellConnected(Tile T, Ship P, Slot S){
        Boolean wellConnected = true;
        Tile tempTile = T;
        Ship myShip = P;
        ArrayList<Position> tempIP = myShip.getInvalidPositions();
        Slot mySlot = S;

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

    public static Boolean CheckLifeSupportSystem(Color color, Tile T, Ship P, Slot S)
    {

        Boolean wellConnected = true;
        Tile tempTile = T;
        Ship myShip = P;
        ArrayList<Position> tempIP = myShip.getInvalidPositions();
        Slot mySlot = S;
        Slot[][] TempShipBoard = P.getShipBoard();
        String s1;
        String s2;
        String s3;
        String s4;


        //UP TILE

        if (mySlot.getPosition().getY() -1 < 0) {
            s1 = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile().getMyComponent().getName();
        }
        else {
            s1  = null;
        }

        //LEFT

        if (mySlot.getPosition().getX() -1 < 0) {


            s2 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile().getMyComponent().getName();
        } else  s2  = null;
        //BOTTOM

        if (mySlot.getPosition().getY() +1 > 6) {
            s3 = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile().getMyComponent().getName();
        } else  s3 = null;

        //RIGHT TILE

        if (mySlot.getPosition().getX() +1 > 4) {
            s4 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile().getMyComponent().getName();
        }
        else s4 = null;

//        if (s1.equals("LifeSupportSystem") ||s2.equals("LifeSupportSystem") || s3.equals("LifeSupportSystem") || s4.equals("LifeSupportSystem")  ){
//            return true;
//        }
//


        return false;

    }
}
