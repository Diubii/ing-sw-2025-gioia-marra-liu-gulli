package org.polimi.ingsw.galaxytrucker.model.essentials;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
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
     * @param T Il tile contenente il motore.
     * @param P La nave in cui è posizionato il motore.
     * @param S Lo slot in cui è posizionato il motore.
     * @return {@code true} se il motore è ben connesso, {@code false} altrimenti.
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
     * @return {@code true} se il sistema di supporto vitale è ben connesso, {@code false} altrimenti.
     */
    public static Boolean CheckLifeSupportSystem(AlienColor color, Tile T, Ship P, Slot S) {
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
        if (color.equals(AlienColor.BROWN)) {
            if ("BrownLifeSupportSystem".equals(s1) || "BrownLifeSupportSystem".equals(s2) ||
                    "BrownLifeSupportSystem".equals(s3) || "BrownLifeSupportSystem".equals(s4)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Verifica se i connettori di una tile sono ben connessi agli slot adiacenti della nave.
     *
     * @param s      La nave in cui si sta effettuando il controllo.
     * @param mySlot Lo slot della tile in esame.
     * @param T      La tile in esame.
     * @return Una coppia contenente un booleano che indica se la connessione è valida e un intero con il numero di connessioni valide.
     */
    public static Pair<Boolean, Integer> wellConnectedConnectors(Ship s, Slot mySlot, Tile T) {

        Slot[][] TempShipBoard = s.getShipBoard();
        Connector c1;
        Connector c2;
        Connector c3;
        Connector c4;
        int sum = 0;


        //UP TILE

        if (mySlot.getPosition().getY() - 1 >= 0 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {
            c1 = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile().getSides().get(2);
            sum += 1;
        } else {
            c1 = null;
        }

        //LEFT

        if (mySlot.getPosition().getX() - 1 >= 0 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {

            sum += 1;
            c2 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile().getSides().get(3);
        } else c2 = null;
        //BOTTOM

        if (mySlot.getPosition().getY() + 1 < 5 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {
            c3 = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile().getSides().get(0);
            sum += 1;


        } else c3 = null;

        //RIGHT TILE

        if (mySlot.getPosition().getX() + 1 < 7 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {
            c4 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile().getSides().get(1);
            sum += 1;


        } else c4 = null;


        if ((c1 == T.getSides().get(0) || c1 == null) && (c2 == T.getSides().get(1) || c2 == null) && (c3 == T.getSides().get(2) || c3 == null) && (c4 == T.getSides().get(3) || c4 == null)) {
            return new Pair<>(true, sum);
        }

        return new Pair<>(false, sum);

    }

    /**
     * Visita gli slot adiacenti a una determinata tile e verifica la loro connessione.
     *
     * @param s               La nave contenente le tile.
     * @param mySlot          Lo slot di riferimento.
     * @param T               La tile da verificare.
     * @param tilesID         Lista degli ID delle tile già visitate.
     * @param invalidPositions Lista delle posizioni non valide.
     */
    public static void visitHelper(Ship s, Slot mySlot, Tile T, ArrayList<Integer> tilesID, ArrayList<Position> invalidPositions) {

        Slot[][] TempShipBoard = s.getShipBoard();

        //UP TILE

        if (mySlot.getPosition().getY() - 1 >= 0 && !s.getInvalidPositions().contains(mySlot.getPosition())) {

            Tile temp = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile();
            visitTile(temp, tilesID, mySlot, invalidPositions, s);
        }


        //LEFT

        if (mySlot.getPosition().getX() - 1 >= 0 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {

            Tile temp = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile();
            visitTile(temp, tilesID, mySlot, invalidPositions, s);
        }
        //BOTTOM

        if (mySlot.getPosition().getY() + 1 < 5 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {
            Tile temp = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile();
            visitTile(temp, tilesID, mySlot, invalidPositions, s);


        }

        //RIGHT TILE

        if (mySlot.getPosition().getX() + 1 < 7 && !s.getInvalidPositions().contains(mySlot.getPosition()) && mySlot.getTile() != null) {
            Tile temp = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile();
            visitTile(temp, tilesID, mySlot, invalidPositions, s);

        }

    }

    /**
     * Visita una tile e verifica se è ben connessa ai sistemi di supporto vitale.
     *
     * @param tile            La tile da visitare.
     * @param tilesID         Lista degli ID delle tile già visitate.
     * @param slot            Lo slot della tile in esame.
     * @param invalidPositions Lista delle posizioni non valide.
     * @param myShip          La nave in cui si sta effettuando la verifica.
     */
    public static void visitTile(Tile tile, ArrayList<Integer> tilesID, Slot slot, ArrayList<Position> invalidPositions, Ship myShip) {
        ComponentNameVisitor cnv = new ComponentNameVisitor();

        if (tile == null || tilesID.contains(tile.getId())) {
            return;
        } else {
            tilesID.add(tile.getId());

            if ((tile.getMyComponent().accept(cnv).equals("PurpleLifeSupportSystem") && !Util.CheckLifeSupportSystem(AlienColor.PURPLE, tile, myShip, slot)) || ((tile.getMyComponent().accept(cnv).equals("BrownLifeSupportSystem") && !Util.CheckLifeSupportSystem(AlienColor.BROWN, tile, myShip, slot)))) {
                invalidPositions.add(slot.getPosition());
                return;
            } else {
                Util.visitHelper(myShip, slot, tile, tilesID, invalidPositions);
            }


        }
    }


    public static Boolean inBoundaries(int y, int x){
        return  (y >= 0 && y <5) && (x >= 0 && x <7);
    }

}
