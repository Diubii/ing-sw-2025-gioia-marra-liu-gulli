package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardVisitorsInterface;

import java.util.ArrayList;

import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.RESET;


/**
 * Methods to Print a Card to console
 */
public class CardPrintUtils {

   public static void printPlanetList(ArrayList<Planet> planets){
       StringBuilder sb;
       for(int i=0; i< planets.size(); i++){
           sb = new StringBuilder();
           if(planets.get(i).isOccupied()){
               sb.append("Occ");
           }
           else{
               sb.append("   ");
           }
           sb.append(" P").append(i+1).append(": ");

           int j=0;
           for( j=0; j < planets.get(i).getGoods().size(); j++){
               switch (planets.get(i).getGoods().get(j).getColor()){
                   case RED -> sb.append(RED).append("█").append(RESET);
                   case BLUE -> sb.append(BLUE).append("█").append(RESET);
                   case GREEN -> sb.append(GREEN).append("█").append(RESET);
                   case YELLOW -> sb.append(BRIGHT_YELLOW).append("█").append(RESET);
                   case EMPTY -> sb.append(" ");
               }

           }
           System.out.println(sb.toString());
       }
   }

    private static final AdventureCardVisitorsInterface<String[]> adventureCardPrintVisitor = new AdventureCardPrintVisitor();

    public static String[][] composeRow(ArrayList<AdventureCard> CardList, int row, int columns,int lastRow) {
        String[][] tileRow = new String[columns][5];

        int Offset= row*columns;

        //Se è l'ultima dopo aver calcolato l'offset deve fare solo le colonne dell'ultima fila
        if(lastRow!=0) columns=lastRow;

        //Ciclo da 0 a 6 per colonne
        for(int i =0; i<columns; i++){
            tileRow[i]=getCardStrings(CardList.get(i+Offset));
        }

        return tileRow;
    }

    public static void printDeck(CardDeck deck,int columns) {
        int rows = deck.getCards().size()/columns, rowsEachCard = 9;
        int r, c;

        int lastRow = deck.getCards().size()%columns;
        if(lastRow!=0) rows++;
        //Compone la riga come per la nave per stamparle affiancate
        for ( r = 0; r < rows; r++) {
            String[][] TilesStringRow;
            if(r == rows-1 && lastRow!= 0){
                //Ottengo tutta la Riga di Tiles e per ciascuna le righe di cui è fatta
                TilesStringRow = composeRow(deck.getCards(), r, columns,lastRow);
                columns=lastRow; //Per l'ultima
            }
            else{
                TilesStringRow = composeRow(deck.getCards(), r, columns,0);
            }


            for (int i = 0; i < rowsEachCard; i++) {

                for (c = 0; c < columns; c++) {
                    System.out.print(TilesStringRow[c][i]);  // Stampo tutte le "prime righe" di ciscuna tile affiancate, poi seconde...
                }
                System.out.println(); // vai a capo dopo una riga
            }
        }
    }

    public static void printCard(AdventureCard card){
        String[] tileString = getCardStrings(card);

        for(int i=0;i<tileString.length;i++){
            System.out.println(tileString[i]);
        }
    }

    public static String colorBlock(Good good) {
        return switch (good.getColor()) {
            case RED -> TuiColor.RED + " █ " + TuiColor.RESET;
            case BLUE -> TuiColor.BLUE + " █ " + TuiColor.RESET;
            case GREEN -> TuiColor.GREEN + " █ " + TuiColor.RESET;
            case YELLOW -> TuiColor.BRIGHT_YELLOW + " █ " + TuiColor.RESET;
            default -> " ";
        };
    }
    public static String[] getCardStrings(AdventureCard card){
        return card.accept(adventureCardPrintVisitor);
    }
}