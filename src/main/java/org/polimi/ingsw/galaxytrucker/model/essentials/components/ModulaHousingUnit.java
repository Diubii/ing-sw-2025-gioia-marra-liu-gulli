package org.polimi.ingsw.galaxytrucker.model.essentials.components;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

public class ModulaHousingUnit extends CentralHousingUnit{

    private Boolean isColored = Boolean.FALSE;
    private int nBrownAlien;
    private int nPurpleAlien;

    public ModulaHousingUnit(String name, Color color, int HumanCrewNumber) {
        super(name, color, HumanCrewNumber);
    }

    public void updateBrownAlien(){
        nBrownAlien += 1;
    }

    public void updatePurpleAlien(){
        nPurpleAlien += 1;
    }

    public int getNBrownAlien() {
        return nBrownAlien;
    }

    public int getNPurpleAlien() {
        return nPurpleAlien;
    }

    public Boolean CheckLifeSupportSystem(Color color)
    {

        Tile tempTile = getMyTile();
        Slot mySlot = tempTile.getMySlot();
        Ship tempShip = tempTile.getMySlot().getMyShip();
        Slot[][] TempShipBoard = mySlot.getMyShip().getShipBoard();
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

        if (s1.equals("LifeSupportSystem") ||s2.equals("LifeSupportSystem") || s3.equals("LifeSupportSystem") || s4.equals("LifeSupportSystem")  ){
            return true;
        }



        return false;

    }
}
