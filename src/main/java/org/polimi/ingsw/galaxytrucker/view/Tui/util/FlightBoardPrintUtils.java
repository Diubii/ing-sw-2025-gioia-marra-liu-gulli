package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMapSlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;

import javax.sound.sampled.Line;
import java.util.ArrayList;

public class FlightBoardPrintUtils {

    public static String[][] composeRow(ArrayList<FlightBoardMapSlot> slots, int rows, int columns, int r) {
        String[][] tileRow = new String[columns][3];
        Boolean dimezzo=false;

        //Ciclo per colonne
        for(int i =0; i<columns; i++){
            String[] result = new String[]{
                    "╔═══╗",
                    "║   ║",
                    "╚═══╝"
            };
            if(slots.get(i).getPlayerToken()!= Color.EMPTY){
                StringBuilder builder = new StringBuilder();
                switch (slots.get(i).getPlayerToken()){
                    case RED -> builder.append("║ "+RED+"█"+RESET+" ║");
                    case BLUE -> builder.append("║ "+BLUE+"█"+RESET+" ║");
                    case GREEN -> builder.append("║ "+GREEN+"█"+RESET+" ║");
                    case YELLOW -> builder.append("║ "+BRIGHT_YELLOW+"█"+RESET+" ║");
                }
                result[1]= builder.toString();
            }
            tileRow[i]=result;
        }

        return tileRow;
    }

    public static void printFlightBoard(FlightBoard flightBoard) {
        int size = flightBoard.getFlightBoardMap().getFlightBoardMapSlots().size();
        int width =0 ,height = 0;
        int c,r,i=0;
        if(size == 18){
            width = 8;
            height = 3;
        }
        else{
            width = 10;
            height = 4;
        }

        for (r = 0; r < height; r++) {
            String[][] StringRow;
            StringRow = composeRow(flightBoard.getFlightBoardMap().getFlightBoardMapSlots(),height,width,r);

            for ( i = 0; i < 3; i++) {

                for (c = 0; c < width; c++) {
                    System.out.print(StringRow[c][i]);  // Stampo tutte le "prime righe" di ciscuna tile affiancate, poi seconde...
                }
                System.out.println(); // vai a capo dopo una riga
            }
        }

    }
}