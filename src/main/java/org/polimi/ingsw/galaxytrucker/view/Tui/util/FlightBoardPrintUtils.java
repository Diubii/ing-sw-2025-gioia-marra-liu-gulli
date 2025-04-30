package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMapSlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;

import javax.sound.sampled.Line;
import java.util.ArrayList;

public class FlightBoardPrintUtils {

    public static String[] getCell(ArrayList<FlightBoardMapSlot> slots,int i){
        String[] result;
        if(i==-1){
            result = new String[]{
                    "       ",
                    "       ",
                    "       "
            };
        }
        else{
            result = new String[]{
                    "╔═════╗",
                    "║     ║",
                    "╚═════╝"
            };
            if(slots.get(i).getPlayerToken()!= Color.EMPTY){
                StringBuilder builder = new StringBuilder();
                switch (slots.get(i).getPlayerToken()){
                    case RED -> builder.append("║  "+RED+"█"+RESET+"  ║");
                    case BLUE -> builder.append("║  "+BLUE+"█"+RESET+"  ║");
                    case GREEN -> builder.append("║  "+GREEN+"█"+RESET+"  ║");
                    case YELLOW -> builder.append("║  "+BRIGHT_YELLOW+"█"+RESET+"  ║");
                }
                result[1]= builder.toString();
            }
            i=i+1; //Per stampe posizioni
            if(i==1){
                result[0]="╔═VIA═╗";
            }
            else if(i>9){
                result[0]="╔═"+i+"══╗";
            }
            else{
                result[0]="╔══"+i+"══╗";
            }
        }

        return result;
    }

    public static String[][] composeRow(ArrayList<FlightBoardMapSlot> slots, int rows, int columns, int r) {
        String[][] tileRow = new String[columns][3];
        int Offset =0; //Differenza tra le celle per righe in mezzo

        //Ciclo per colonne
        for(int i =0; i<columns; i++){

            if(r== 0){
                //Se prima riga ordine normale
                tileRow[i]=getCell(slots,i);
            }else if(r== rows-1){
                //Ultima riga ordine inverso
                //Offset per la partenza
                Offset= columns-1+rows-2;
                tileRow[i]=getCell(slots,Offset+columns-i);
            }
            else{
                //Righe in mezzo solo 2 con buco
                //Offset tra le posizioni
                Offset= columns-1+2*(rows-2)-2*(r-1);
                if(i==0){
                    tileRow[0]=getCell(slots,columns+r-1+Offset);
                }
                else if(i==columns-1){
                    tileRow[i]=getCell(slots,columns+r-1);
                }
                else{
                    tileRow[i]=getCell(slots,-1);
                }
            }
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