package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitor;

import java.util.ArrayList;


/**
 * Methods to Print a Tile in console,
 * Used for printing the entire ship.
 *
 */
public class TilePrintUtils {

    private static ComponentPrintVisitor componentPrintVisitor= new ComponentPrintVisitor();
    //Visitor per i vari component che restituisce la corretta stringa
    //Butta fuori tipo gli interni
    //Tile1Line
    //Tile2Line = | ComponentTopLine | ecc
    //ComponentTopLine da visitor
    //ComponentMidLine da visitor
    //ComponentBottomLine da visitor

    public static String[][] composeRow(ArrayList<Tile> tileList, int row,int columns,int lastRow) {
        String[][] tileRow = new String[columns][5];

        int Offset= row*columns;
        if(lastRow!=0) columns=lastRow;

        //Ciclo da 0 a 6 per colonne
        for(int i =0; i<columns; i++){
            tileRow[i]=getTileStrings(tileList.get(i+Offset),true);
        }

        return tileRow;
    }

    public static void printTileList(ArrayList<Tile> tileList,int columns) {
        int rows, rowsEachTile = 6;
        int r, c;

        rows = tileList.size() / columns;
        int lastRow = tileList.size()%columns;
        if(lastRow!=0) rows++;
        for (r = 0; r < rows; r++) {
            String[][] TilesStringRow;
            if(r == rows-1 && lastRow!= 0){
                //Ottengo tutta la Riga di Tiles e per ciascuna le righe di cui è fatta
                TilesStringRow = composeRow(tileList, r, columns,lastRow);
                columns=lastRow;
            }
            else{
                TilesStringRow = composeRow(tileList, r, columns,0);
            }


            for (int i = 0; i < rowsEachTile; i++) {

                for (c = 0; c < columns; c++) {
                    System.out.print(TilesStringRow[c][i]);  // Stampo tutte le "prime righe" di ciascuna tile affiancate, poi seconde...
                }
                System.out.println(); // vai a capo dopo una riga
            }
        }
    }

    /**
     * Prints a Tile directly to console
     * @param tile
     */
    public static void printTile(Tile tile){
        String[] tileString = getTileStrings(tile,true);

        for(int i=0;i<tileString.length;i++){
            System.out.println(tileString[i]);
        }
    }

    /**
     * Creates a String array of ROWS to represent the tile
     * @param tile
     * @return
     */
    public static String[] getTileStrings(Tile tile,boolean printId) {
        //Si assembla la stringa per la tile e si usa il visitor per il component
        //Tile 11*5 +1 per spazio
        if(tile == null){
            return new String[]{
                    ".         . ",
                    "            ",
                    "            ",
                    "            ",
                    ".         . "
            };
        }

        String[] componentRows = tile.getMyComponent().accept(componentPrintVisitor);
        if(printId){
            StringBuilder sb = new StringBuilder();
            String[] result = new String[]{
                    "",
                    "┌----"+tile.getSides().get(0).ordinal()+"----┐ ",
                    "|"     +        componentRows[0]    +       "| ",
                    tile.getSides().get(3).ordinal()+   componentRows[1]+   tile.getSides().get(1).ordinal()+" ",
                    "|"     +        componentRows[2]    +       "| ",
                    "└----"+tile.getSides().get(2).ordinal()+"----┘ "
            };
            sb.append("ID: "+tile.getId());
            if(tile.getId()>99){
                sb.append("     ");
            }else if( tile.getId()>9){
                sb.append("      ");
            }else{
                sb.append("       ");
            }
            result[0] = sb.toString();
            return result;
        }
        else{
            return new String[]{
                    "┌----"+tile.getSides().get(0).ordinal()+"----┐ ",
                    "|"     +        componentRows[0]    +       "| ",
                    tile.getSides().get(3).ordinal()+   componentRows[1]+   tile.getSides().get(1).ordinal()+" ",
                    "|"     +        componentRows[2]    +       "| ",
                    "└----"+tile.getSides().get(2).ordinal()+"----┘ "
            };
        }

    }

}