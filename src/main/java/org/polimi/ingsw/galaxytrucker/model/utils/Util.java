package org.polimi.ingsw.galaxytrucker.model.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Classe di utilità che fornisce metodi per verificare la connettività di componenti nella nave.
 *
 * @author nerd53
 */
public class Util {

    public static CardDeck createLvl1Deck() {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        CardDeck cardDeck = null;
        try {
            ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
            }).stream().filter(card -> card.getLevel() == 1).toList());
            cardDeck = new CardDeck(list, true);
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return cardDeck;
    }

    public static CardDeck createLvl2Deck() {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        CardDeck cardDeck = null;
        try {
            ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
            }).stream().filter(card -> card.getLevel() == 2).toList());
            cardDeck = new CardDeck(list, true);
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return cardDeck;

    }

    public static CardDeck createLearningDeck() {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        CardDeck cardDeck = null;
        try {
            ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
            }).stream().filter(AdventureCard::isLearningFlight).toList());
            cardDeck = new CardDeck(list, true);
        }catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return cardDeck;

    }

    public static CardDeck createTestDeck() throws IOException {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
        }).stream().toList());
        ArrayList<AdventureCard> cardsToTest = new ArrayList<>();
        //cardsToTest.add(list.get(3));
        //cardsToTest.add(list.get(3)); //Stardust
        //cardsToTest.add(list.get(24)); //Epidemic
         cardsToTest.add(list.get(16)); //AbandonedShip
         cardsToTest.add(list.get(31)); //Planets
         cardsToTest.add(list.get(4)); //Open Space
         cardsToTest.add(list.get(19)); //AbandonedStation



//        cardsToTest.add(list.get(8));


        cardsToTest.add(list.get(3)); //Stardust
        //cardsToTest.add(list.get(24)); //Epidemic

        return new CardDeck(cardsToTest, true);

    }

    /**
     * Generates the tiles of a game and puts them in an {@link ArrayList}
     *
     * @return The {@link ArrayList} of tiles
     * @throws IOException
     */
    public static ArrayList<Tile> generateTiles() throws IOException {
        File file = new File("src/main/resources/tiledata.json");
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Tile> list = new ArrayList<>(mapper.readValue(file, new TypeReference<ArrayList<Tile>>() {
        }));

        return list;
    }

    /**
     * Controlla se un motore è ben connesso alla nave, verificando che non sia bloccato da altri componenti.
     *
     * @param T Il tile contenente il motore.
     * @param P La nave in cui è posizionato il motore.
     * @param S Lo slot in cui è posizionato il motore.
     * @return {@code true} se il motore è ben connesso, {@code false} altrimenti.
     */
    public static Boolean EngineWellConnected(Tile T, Ship P, Slot S) {
        boolean wellConnected = true;
        ArrayList<Position> tempIP = P.getInvalidPositions();

        // Posizioni adiacenti alla tile corrente
        //Position nord = new Position(S.getPosition().getY() - 1, S.getPosition().getX());
        Position sud = new Position(S.getPosition().getX(), S.getPosition().getY() + 1);
        //Position est = new Position(S.getPosition().getY(), S.getPosition().getX() + 1);
        //Position ovest = new Position(S.getPosition().getY(), S.getPosition().getX() - 1);

        // Controlla la connessione del motore in base alla rotazione della tile
        if (T.getRotation() == 0) {
            if (!tempIP.contains(sud) && inBoundaries(sud.getX(), sud.getY()) && P.getShipBoard()[sud.getX()][sud.getY()].getTile() != null) {
                wellConnected = false;
            }
        } else wellConnected = false;

        return wellConnected;
    }

    public static Boolean CannonWellConnected(Tile T, Ship P, Slot S) {
        boolean wellConnected = true;
        ArrayList<Position> tempIP = P.getInvalidPositions();
        Position nord = new Position(S.getPosition().getX(), S.getPosition().getY() - 1);
        Position sud = new Position(S.getPosition().getX(), S.getPosition().getY() + 1);
        Position est = new Position(S.getPosition().getX() + 1, S.getPosition().getY());
        Position ovest = new Position(S.getPosition().getX() - 1, S.getPosition().getY());

        switch (T.getRotation()) {
            case 0:
                if (!tempIP.contains(nord) && inBoundaries(nord.getX(), nord.getY()) && P.getShipBoard()[nord.getX()][nord.getY()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 90:
                if (!tempIP.contains(ovest) && inBoundaries(ovest.getX(), ovest.getY()) && P.getShipBoard()[ovest.getX()][ovest.getY()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 180:
                if (!tempIP.contains(sud) && inBoundaries(sud.getX(), sud.getY()) && P.getShipBoard()[sud.getX()][sud.getY()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 270:
                if (!tempIP.contains(est) && inBoundaries(est.getX(), est.getY()) && P.getShipBoard()[est.getX()][est.getY()].getTile() != null) {
                    wellConnected = false;
                }
                break;
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
        ArrayList<Position> tempIP = P.getInvalidPositions();
        Slot[][] TempShipBoard = P.getShipBoard();
        String s1, s2, s3, s4;

        // Controlla la tile sopra
//        if (S.getPosition().getY() - 1 >= 0 && TempShipBoard[S.getPosition().getX][S.getPosition().getY() - 1].getTile() != null) {
//            s1 = TempShipBoard[S.getPosition().getX()][S.getPosition().getY() - 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
//        } else {
//            s1 = null;
//        }
//
//        // Controlla la tile a sinistra
//        if (S.getPosition().getX() - 1 >= 0 && TempShipBoard[S.getPosition().getX() - 1][S.getPosition().getY()].getTile() != null) {
//            s2 = TempShipBoard[S.getPosition().getX() - 1][S.getPosition().getY()].getTile().getMyComponent().accept(new ComponentNameVisitor());
//        } else {
//            s2 = null;
//        }
//
//        // Controlla la tile sotto
//        if (S.getPosition().getY() + 1 <= 6 && TempShipBoard[S.getPosition().getY() + 1][S.getPosition().getX()].getTile() != null) {
//            s3 = TempShipBoard[S.getPosition().getX()][S.getPosition().getY() + 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
//        } else {
//            s3 = null;
//        }
//
//        // Controlla la tile a destra
//        if (S.getPosition().getX() + 1 <= 4 && TempShipBoard[S.getPosition().getY()][S.getPosition().getX() + 1].getTile() != null) {
//            s4 = TempShipBoard[S.getPosition().getX() + 1][S.getPosition().getY()].getTile().getMyComponent().accept(new ComponentNameVisitor());
//        } else {
//            s4 = null;
//        }
//
//        // Verifica se c'è un sistema di supporto vitale del colore richiesto
//        if (color.equals(AlienColor.PURPLE)) {
//            if ("PurpleLifeSupportSystem".equals(s1) || "PurpleLifeSupportSystem".equals(s2) ||
//                    "PurpleLifeSupportSystem".equals(s3) || "PurpleLifeSupportSystem".equals(s4)) {
//                return true;
//            }
//        }
//        if (color.equals(AlienColor.BROWN)) {
//            return "BrownLifeSupportSystem".equals(s1) || "BrownLifeSupportSystem".equals(s2) ||
//                    "BrownLifeSupportSystem".equals(s3) || "BrownLifeSupportSystem".equals(s4);
//        }
//
//        return false;

        ArrayList<Pair<Position, Tile>> connectedTiles = new ArrayList<>(P.getConnectedTiles(S.getPosition()));
        for (Pair<Position, Tile> pair : connectedTiles) {
            Component component = pair.getValue().getMyComponent();

            if (component != null && wellConnectedTiles(T.getSides(), pair.getValue().getSides(), S.getPosition(), pair.getKey())){
                if (component.accept(new ComponentNameVisitor()).equals("PurpleLifeSupportSystem") && color.equals(AlienColor.PURPLE)){
                    return true;
                } else if (component.accept(new ComponentNameVisitor()).equals("BrownLifeSupportSystem") && color.equals(AlienColor.BROWN)){
                    return true;
                }
            }
        }

        return  false;
    }


    /**
     * Verifica se i connettori di una tile sono ben connessi agli slot adiacenti della nave.
     *
     * @param s      La nave in cui si sta effettuando il controllo.
     * @param mySlot Lo slot della tile in esame.
     * @param T      La tile in esame.
     * @return Una coppia contenente un booleano che indica se la connessione è valida e un intero con il numero di connessioni valide.
     */
    public static Boolean wellConnectedConnectors(Ship s, Slot mySlot, Tile T) {

        Slot[][] TempShipBoard = s.getShipBoard();

        Boolean wellConnected = true;


        int myPosX = mySlot.getPosition().getX();
        int myPosY = mySlot.getPosition().getY();

        Connector cUp;
        Connector cLeft;
        Connector cDown;
        Connector cRight;
        //UP TILE

        if (myPosY - 1 >= 0 && TempShipBoard[myPosX][myPosY-1].getTile() != null) {
             cUp = TempShipBoard[myPosX][myPosY - 1].getTile().getSides().get(2);
            //Controlla se il connettore superiore del tile corrente è compatibile con il connettore inferiore del tile superiore
            wellConnected = compatible(cUp,T.getSides().get(0));
            if(!wellConnected){
                return false;
            }
        }

        //LEFT

        if (myPosX - 1 >= 0 && TempShipBoard[myPosX-1][myPosY].getTile() != null) {
            cLeft = TempShipBoard[myPosX- 1][myPosY].getTile().getSides().get(1);
            //Controlla se il connettore sinistra del tile corrente è compatibile con il connettore destra del tile a sinistra
            wellConnected = compatible(cLeft,T.getSides().get(3));
            if(!wellConnected){
                return false;
            }
        }
        //DOWN

        if (myPosY + 1 < 5 && TempShipBoard[myPosX][myPosY+1].getTile() != null) {
            cDown = TempShipBoard[myPosX][myPosY + 1].getTile().getSides().get(0);
            //Controlla se il connettore inferiore del tile corrente è compatibile con il connettore superiore del tile inferiore
            wellConnected = compatible(cDown, T.getSides().get(2));
            if (!wellConnected) {
                return false;
            }
        }
        //RIGHT TILE

        if (myPosX + 1 < 7 && TempShipBoard[myPosX+1][myPosY].getTile() != null) {
            cRight = TempShipBoard[myPosX+1][myPosY].getTile().getSides().get(3);

            wellConnected = compatible(cRight, T.getSides().get(1));
            if(!wellConnected){
                return false;
            }

        }
        return wellConnected;

    }

    public static Boolean wellConnectedTiles(ArrayList<Connector> tile1Connectors, ArrayList<Connector> tile2Connectors, Position position1, Position position2) {

        String relativePos;
        Boolean wellConnected = false;

        if (position1.getX() != position2.getX()) {
            relativePos = "left-right";
        } else relativePos = "up-down";


        if (relativePos.equals("left-right")){
            if (position1.getX() > position2.getX()) {
                wellConnected = compatible(tile1Connectors.get(1), tile2Connectors.get(3));
            } else wellConnected = compatible(tile1Connectors.get(3), tile2Connectors.get(1));
        } else {
            if (position1.getY() > position2.getY()) {
                wellConnected = compatible(tile1Connectors.getFirst(), tile2Connectors.get(2));

            } else  wellConnected = compatible(tile2Connectors.getFirst(), tile1Connectors.get(2));

            }


        return wellConnected;
    }


    public static Boolean checkNearLFS(Position position, AlienColor color, Ship myShip) {

        return CheckLifeSupportSystem(color, myShip.getTileFromPosition(position),myShip, myShip.getShipBoard()[position.getX()][position.getY()]);

    }

    /**
     * Visita una tile e verifica se è ben connessa ai sistemi di supporto vitale.
     *
     * @param tile             La tile da visitare.
     * @param tilesID          Lista degli ID delle tile già visitate.
     * @param slot             Lo slot della tile in esame.
     * @param invalidPositions Lista delle posizioni non valide.
     * @param myShip           La nave in cui si sta effettuando la verifica.
     */
    public static void visitTile(Tile tile, ArrayList<Integer> tilesID, Slot slot, ArrayList<Position> invalidPositions, Queue<Position> newBrokenPos, Ship myShip) {
        ComponentNameVisitor cnv = new ComponentNameVisitor();

        if (tile == null || tilesID.contains(tile.getId())) {
            return;
        } else {

            if (!tile.getWellConnected()) {
                newBrokenPos.add(slot.getPosition());
                return;
            } else {
                tilesID.add(tile.getId());
                //NORD

                ArrayList<Position> positions = getAdjacentPositions(slot.getPosition());
                int s = positions.size();
                for (int i = 0; i < s; i++) {

                    if (inBoundaries(positions.get(i).getX(), positions.get(i).getY()) && myShip.getShipBoard()[positions.get(i).getX()][positions.get(i).getY()].getTile() != null && compatible(myShip.getShipBoard()[positions.get(i).getX()][positions.get(i).getY()].getTile().getSides().get((i + 2) % 4), tile.getSides().get(i))) {
                        Tile tempTile = myShip.getShipBoard()[positions.get(i).getX()][positions.get(i).getY()].getTile();
                        System.out.println("STO PER VISITARE : " + positions.get(i).getX() + positions.get(i).getY());
                        visitTile(tempTile, tilesID, myShip.getShipBoard()[positions.get(i).getX()][positions.get(i).getY()], invalidPositions, newBrokenPos, myShip);


                    }
//                    System.out.println("[2]I: " + i + " " + positions.size());


                }

            }

        }
    }


    /**
     * Controlla se la Ship e' formata o no da tronconi separati

     * @param myShip           La nave in cui si sta effettuando la verifica.
     */
    public  static Pair<Boolean, ArrayList<Integer>> checkShipStructure(Ship myShip, Position startingPos) {

        ArrayList<Integer> visitedTilesId = new ArrayList<>();
        visitedTilesId.add(myShip.getTileFromPosition(startingPos).getId());
        checkShipStructureTileVisitor(startingPos, myShip, visitedTilesId);

        //ora in visitedTilesId ho tutte le tile che fanno parte del troncone principale
        //devo confrontare le Tile totali con quelle visitate, se sono le stesse allora non ci sono troconi separati

        ArrayList<Integer> tilesId = new ArrayList<>(Arrays.stream(myShip.getShipBoard())
                .flatMap(Arrays::stream).map(Slot::getTile)
                .filter(Objects::nonNull).map(Tile::getId).toList());

        Collections.sort(visitedTilesId);
        Collections.sort(tilesId);
        boolean same = visitedTilesId.equals(tilesId);

        return new Pair<>(same, visitedTilesId);

    }

    private static void checkShipStructureTileVisitor(Position startingPos, Ship myShip, ArrayList<Integer> visitedTilesId) {

        ArrayList<Pair<Position, Tile>> connectedTiles = myShip.getConnectedTiles(startingPos);


        if (!connectedTiles.isEmpty()) {
            for (Pair<Position, Tile> connectedTile : connectedTiles) {
                if (!visitedTilesId.contains(connectedTile.getValue().getId())) {
                    visitedTilesId.add(connectedTile.getValue().getId());
                    checkShipStructureTileVisitor(connectedTile.getKey(), myShip, visitedTilesId);

                }
            }

        }
    }

    public static Boolean inBoundaries(int x, int y) {
        return (y >= 0 && y < 5) && (x >= 0 && x < 7);
    }

    public static ArrayList<Position> getAdjacentPositions(Position pos) {
        ArrayList<Position> adjacent = new ArrayList<>();
        adjacent.add(new Position(pos.getX(), pos.getY() - 1)); // Nord
        adjacent.add(new Position(pos.getX() - 1, pos.getY())); // Ovest
        adjacent.add(new Position(pos.getX(), pos.getY() + 1)); // Sud
        adjacent.add(new Position(pos.getX() + 1, pos.getY())); // Est
        return adjacent;
    }

    public static Boolean compatible(Connector connector1, Connector connector2) {
        //casi null
        if (connector1 == null && connector2 != null) return true;
        if (connector1 == null || connector2 == null) return true;

        //con un connetore empty
        if (connector1.equals(Connector.EMPTY) && !connector2.equals(Connector.EMPTY)) return false;
        if (connector2.equals(Connector.EMPTY) && !connector1.equals(Connector.EMPTY)) return false;

        //sono identici
        if (connector1.equals(connector2)) return true;

        //con un connetore universal
        if (connector1.equals(Connector.UNIVERSAL) && !connector2.equals(Connector.EMPTY)) return true;
        if (connector2.equals(Connector.UNIVERSAL) && !connector1.equals(Connector.EMPTY)) return true;

        return false;
    }

    public static ArrayList<Good> getAndRemoveMostValuableGoods(Ship ship, int penalty ) {
        //red, yellow, green, blue
        ArrayList<Good> goodsToDiscard = new ArrayList<>();
        Map<Color, ArrayList<Position>> goodPositions = new HashMap<>();

        goodPositions.put(Color.RED, new ArrayList<>());
        goodPositions.put(Color.BLUE, new ArrayList<>());
        goodPositions.put(Color.GREEN, new ArrayList<>());
        goodPositions.put(Color.YELLOW, new ArrayList<>());

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("GenericCargoHolds");

        for (Position pos : storagePos) {
            GenericCargoHolds hold = (GenericCargoHolds) ship.getComponentFromPosition(pos);
            if (hold.isEmpty()) {
                continue;
            }

            for (Good good : hold.getGoods()) {
                Color color = good.getColor();
                goodPositions.get(color).add(pos);
            }
        }

        //dopo averle, parto dalla piu importante


        List<Color> priority = List.of(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);

        int index = 0;

        while (goodsToDiscard.size() < penalty && index < priority.size()) {
            Color currentColor = priority.get(index);
            List<Position> positions = goodPositions.get(currentColor);

            if (positions.isEmpty()) {
                index++;
                continue;
            }

            Position pos = positions.remove(0);
            GenericCargoHolds hold = (GenericCargoHolds) ship.getComponentFromPosition(pos);

            hold.removeGood(currentColor);

            goodsToDiscard.add(new Good(currentColor));

        }

        return goodsToDiscard;

    }

    public static void removeBatteries(Ship ship, int batteryToDiscard) {
        if (batteryToDiscard <= 0) return;

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("BatterySlot");

        int removed = 0;

        for (Position pos : storagePos) {
            BatterySlot batterySlot = (BatterySlot) ship.getComponentFromPosition(pos);

            while (batterySlot.getBatteriesLeft() > 0 && removed < batteryToDiscard) {
                boolean success = batterySlot.removeBattery();
                if (success) {
                    removed++;
                } else {
                    break;
                }
            }

            if (removed >= batteryToDiscard) {
                break; //Done
            }
        }
    }
}
