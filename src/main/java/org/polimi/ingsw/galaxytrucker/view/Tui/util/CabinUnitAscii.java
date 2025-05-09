package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.enums.AlienColor;

public class CabinUnitAscii {


    public static void printCabinUnitWithFigures(int numberOfFigures, boolean isAlien, AlienColor purple) {

        String color = String.valueOf(TuiColor.BLACK);

        switch (purple) {
            case BROWN -> color = String.valueOf(TuiColor.YELLOW);
            case PURPLE -> color = String.valueOf(TuiColor.PURPLE);
            case EMPTY -> color = String.valueOf(TuiColor.BLACK);

        }

        // Cabin Unit ASCII
        String[] cabinUnit = {
                "  _________",
                " /        /|",
                "+--------+ |",
                "| Cabin  | |",
                "|  Unit  | +",
                "|________|/"
        };

        // Figure ASCII (uman* o alien*)
        String[] figure = isAlien
                ? new String[]{" 👽 ", "/|\\", "/ \\"}
                : new String[]{" O ", "/|\\", "/ \\"};

        // Stampa ogni riga della Cabin + Figure
        for (int i = 0; i < cabinUnit.length; i++) {
            System.out.print(color + cabinUnit[i] + TuiColor.RESET);
            if (i < figure.length) {
                // Stampa tutte le figure su questa riga
                for (int j = 0; j < numberOfFigures; j++) {
                    System.out.print("   " + figure[i]);
                }
            }
            System.out.println();
        }
    }


}


