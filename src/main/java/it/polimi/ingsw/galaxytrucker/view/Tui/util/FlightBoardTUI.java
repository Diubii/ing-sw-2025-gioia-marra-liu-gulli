package it.polimi.ingsw.galaxytrucker.view.Tui.util;

import it.polimi.ingsw.galaxytrucker.enums.Color;

import java.util.ArrayList;

/**
 * Utility class to render a rectangular flight board using ANSI colored blocks
 * in a TUI (text-based user interface).
 */
public class FlightBoardTUI {

    // ANSI Colors
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m■" + RESET;
    public static final String GREEN = "\u001B[32m■" + RESET;
    public static final String YELLOW = "\u001B[33m■" + RESET;
    public static final String BLUE = "\u001B[34m■" + RESET;
    public static final String EMPTY = "\u001B[20m■" + RESET;


    /**
     * Stampa la pista rettangolare chiusa, riempiendola con i colori dati.
     *
     * @param colori Lista di 18 colori (tipo RED, GREEN, ecc.)
     */
    public static void printPistaRettangolare(ArrayList<Color> colori) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        if (colori.size() != 18) {
            System.out.println("La lista deve contenere esattamente 18 colori.");
            return;
        }

        String[][] griglia = new String[5][6]; // 5 righe x 6 colonne

        // Posizioni dei 18 quadrati lungo la pista (in senso orario)
        int[][] posizioni = {
                // Top row (6)
                {0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5},
                // Right column (3)
                {1, 5}, {2, 5}, {3, 5},
                // Bottom row (6)
                {4, 5}, {4, 4}, {4, 3}, {4, 2}, {4, 1}, {4, 0},
                // Left column (3)
                {3, 0}, {2, 0}, {1, 0}
        };

        // Inserisce i colori nell'ordine fornito
        for (int i = 0; i < 18; i++) {
            int r = posizioni[i][0];
            int c = posizioni[i][1];


            griglia[r][c] = (String) FlightBoardTUI.class.getField(colori.get(i).toString()).get(null);
        }

        // Stampa la griglia
        for (int r = 0; r < 5; r++) {
            // Riga superiore dei quadrati
            for (int c = 0; c < 6; c++) {
                if (griglia[r][c] != null) System.out.print("┌───┐ ");
                else System.out.print("      ");
            }
            System.out.println();

            // Riga centrale con il colore
            for (int c = 0; c < 6; c++) {
                if (griglia[r][c] != null) System.out.print("│ " + griglia[r][c] + " │ ");
                else System.out.print("      ");
            }
            System.out.println();

            // Riga inferiore dei quadrati
            for (int c = 0; c < 6; c++) {
                if (griglia[r][c] != null) System.out.print("└───┘ ");
                else System.out.print("      ");
            }
            System.out.println();
        }
    }

    // Esempio di uso
    public static void main(String[] args) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        ArrayList<Color> colori = new ArrayList<>();
        colori.add(Color.RED);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.YELLOW);
        colori.add(Color.RED);
        colori.add(Color.RED);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.EMPTY);
        colori.add(Color.BLUE);
        colori.add(Color.EMPTY);
        colori.add(Color.GREEN);
        colori.add(Color.GREEN);


        printPistaRettangolare(colori);
    }
}
