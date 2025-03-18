package org.polimi.ingsw.galaxytrucker.model.essentials;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitor;

import java.util.ArrayList;

/**
 * Classe di utilità che fornisce metodi per verificare la connettività di componenti nella nave.
 *
 * @author nerd53
 */
public class Util {

    /**
     * Controlla se un motore è ben connesso alla nave, verificando che non sia bloccato da altri componenti.
     *
     * @param T  Il tile contenente il motore.
     * @param P  La nave in cui è posizionato il motore.
     * @param S  Lo slot in cui è posizionato il motore.
     * @return   {@code true} se il motore è ben connesso, {@code false} altrimenti.
     */
    public static Boolean EngineWellConnected(Tile T, Ship P, Slot S) {
        Boolean wellConnected = true;
        Tile tempTile = T;
        Ship myShip = P;
        ArrayList<Position> tempIP = myShip.getInvalidPositions();
        Slot mySlot = S;

        // Posizioni adiacenti alla tile corrente
        Position nord = new Position(mySlot.getPosition().getY() - 1, mySlot.getPosition().getX());
        Position sud = new Position(mySlot.getPosition().getY() + 1, mySlot.getPosition().getX());
        Position est = new Position(mySlot.getPosition().getY(), mySlot.getPosition().getX() + 1);
        Position ovest = new Position(mySlot.getPosition().getY(), mySlot.getPosition().getX() - 1);

        // Controlla la connessione del motore in base alla rotazione della tile
        switch (tempTile.getRotation()) {
            case 0:
                if (!tempIP.contains(sud) && myShip.getShipBoard()[sud.getY()][sud.getX()].getTile() != null) {
                    wellConnected = false;
                    break;
                }
            case 90:
                if (!tempIP.contains(est) && myShip.getShipBoard()[est.getY()][est.getX()].getTile() != null) {
                    wellConnected = false;
                    break;
                }
            case 180:
                if (!tempIP.contains(nord) && myShip.getShipBoard()[nord.getY()][nord.getX()].getTile() != null) {
                    wellConnected = false;
                    break;
                }
            case 270:
                if (!tempIP.contains(ovest) && myShip.getShipBoard()[ovest.getY()][ovest.getX()].getTile() != null) {
                    wellConnected = false;
                    break;
                }
        }

        return wellConnected;
    }

    /**
     * Verifica se un determinato sistema di supporto vitale (Life Support System) è presente nelle vicinanze della posizione indicata.
     *
     * @param color Il colore del supporto vitale da verificare.
     * @param T     Il tile su cui viene effettuato il controllo.
     * @param P     La nave in cui si sta effettuando la ricerca.
     * @param S     Lo slot di riferimento all'interno della nave.
     * @return      {@code true} se il sistema di supporto vitale è ben connesso, {@code false} altrimenti.
     */
    public static Boolean CheckLifeSupportSystem(Color color, Tile T, Ship P, Slot S) {
        Boolean wellConnected = true;
        Tile tempTile = T;
        Ship myShip = P;
        ArrayList<Position> tempIP = myShip.getInvalidPositions();
        Slot mySlot = S;
        Slot[][] TempShipBoard = P.getShipBoard();
        String s1, s2, s3, s4;

        // Controlla la tile sopra
        if (mySlot.getPosition().getY() - 1 >= 0) {
            s1 = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s1 = null;
        }

        // Controlla la tile a sinistra
        if (mySlot.getPosition().getX() - 1 >= 0) {
            s2 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s2 = null;
        }

        // Controlla la tile sotto
        if (mySlot.getPosition().getY() + 1 <= 6) {
            s3 = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s3 = null;
        }

        // Controlla la tile a destra
        if (mySlot.getPosition().getX() + 1 <= 4) {
            s4 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s4 = null;
        }

        // Verifica se c'è un sistema di supporto vitale del colore richiesto
        if (color.equals(AlienColor.PURPLE)) {
            if ("PurpleLifeSupportSystem".equals(s1) || "PurpleLifeSupportSystem".equals(s2) ||
                    "PurpleLifeSupportSystem".equals(s3) || "PurpleLifeSupportSystem".equals(s4)) {
                return true;
            }
        }

        return false;
    }

    public static Boolean wellConnectedConnectors(Ship s, Slot mySlot, Tile T){

        Slot[][] TempShipBoard = s.getShipBoard();
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


        if ((c1 == T.getSides().get(0) || c1 == null )&& (c2 == T.getSides().get(1) || c2 == null) && (c3 == T.getSides().get(2) || c3 == null) && (c4 == T.getSides().get(3) || c4 == null)) {
            return true;
        }

        return false;
    }

}
