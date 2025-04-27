package org.polimi.ingsw.galaxytrucker.view.Tui.util;

import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.visitors.AdventureCardPrintVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentPrintVisitor;

import java.util.ArrayList;


/**
 * Methods to Print a Card to console
 */
public class CardPrintUtils {

    private static AdventureCardPrintVisitor adventureCardPrintVisitor= new AdventureCardPrintVisitor();

    public static String[][] composeRow(ArrayList<AdventureCard> CardList, int row, int columns) {
        String[][] tileRow = new String[columns][5];

        int Offset= row*columns;


        //Ciclo da 0 a 6 per colonne
        for(int i =0; i<columns; i++){
            tileRow[i]=getCardStrings(CardList.get(i+Offset));
        }

        return tileRow;
    }

    public static void printDeck(CardDeck deck,int columns) {
        int rows = deck.getCards().size()/columns, rowsEachCard = 7;
        int r, c;


        //Compone la riga come per la nave per stamparle affiancate
        for ( r = 0; r < rows; r++) {

            //Ottengo tutta la Riga di Tiles e per ciascuna le righe di cui è fatta
            String[][] TilesStringRow = composeRow(deck.getCards(), r, columns);

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

    public static String[] getCardStrings(AdventureCard card){
        return card.accept(adventureCardPrintVisitor);
    }
}
