package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitor;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;

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


    /**
     * Prints a Tile directly to console
     * @param tile
     */
    public static void printTile(Tile tile){
        String[] tileString = getTileStrings(tile);

        for(int i=0;i<tileString.length;i++){
            System.out.println(tileString[i]);
        }
    }

    /**
     * Creates a String array of ROWS to represent the tile
     * @param tile
     * @return
     */
    public static String[] getTileStrings(Tile tile) {
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
        return new String[]{
                "┌----"+tile.getSides().get(0).ordinal()+"----┐ ",
                "|"     +        componentRows[0]    +       "| ",
                tile.getSides().get(3).ordinal()+   componentRows[1]+   tile.getSides().get(1).ordinal()+" ",
                "|"     +        componentRows[2]    +       "| ",
                "└----"+tile.getSides().get(2).ordinal()+"----┘ "
        };
    }

}
