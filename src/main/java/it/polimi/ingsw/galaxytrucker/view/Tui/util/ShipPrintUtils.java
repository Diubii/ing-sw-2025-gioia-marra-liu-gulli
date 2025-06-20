package it.polimi.ingsw.galaxytrucker.view.Tui.util;

import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;

import static it.polimi.ingsw.galaxytrucker.view.Tui.util.TilePrintUtils.getTileStrings;

/**
 * Methods to Print a Ship on Console
 */
public class ShipPrintUtils {

    /**
     * Collects all the Strings that make up a Row of Tiles
     *
     * @param ship
     * @param row
     * @return
     */
    public static String[][] composeRow(Ship ship, int row) {
        String[][] shipRow = new String[7][5];
        //Ciclo da 0 a 6 per colonne
        for (int i = 0; i < 7; i++) {
            String[] result;
            if (ship.getInvalidPositions().contains(new Position(i, row))) {
                result = new String[]{
                        "            ",
                        "            ",
                        "            ",
                        "            ",
                        "            "};
            } else {
                result = getTileStrings(ship.getShipBoard()[i][row].getTile(), false);
            }
            shipRow[i] = result;
        }

        return shipRow;
    }

    /**
     * Prints a Ship directly to console
     *
     * @param ship
     */
    public static void printShip(Ship ship) {
        int rows = 5, cols = 7, rowsEachTile = 5;
        int r = 0, c = 0;
        //7 * 5 così puoi avere il print tile che tira assieme ogni tile e poi le affianchi soltanto
        //1-Inizia dal print tile, fanne uno fisso di esempio e prova a fare la grilia
        //2-Poi prova a fare una navicella vuota e fare il print tile con il visitor solo per
        //Tile vuote non costruibili e tile non occupate

        System.out.println("  |    4    | |    5    | |    6    | |    7    | |    8    | |    9    | |    10   | ");

        for (r = 0; r < rows; r++) {

            //Ottengo tutta la Riga di Tiles e per ciascuna le righe di cui è fatta
            String[][] TilesStringRow = composeRow(ship, r);

            for (int i = 0; i < rowsEachTile; i++) {
                if (i == 0) {
                    System.out.print("_ ");
                }
                if (i == 1) {
                    System.out.print("  ");
                }
                if (i == 2) {
                    System.out.print((r + 5) + " ");
                }
                if (i == 3) {
                    System.out.print("  ");
                }
                if (i == 4) {
                    System.out.print("_ ");
                }
                for (c = 0; c < cols; c++) {
                    System.out.print(TilesStringRow[c][i]);  // Stampo tutte le "prime righe" di ciscuna tile affiancate, poi seconde...
                }
                System.out.println(); // vai a capo dopo una riga
            }
        }

    }
}