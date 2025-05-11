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
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
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

    public static CardDeck createLvl1Deck() throws IOException {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
        }).stream().filter(card -> card.getLevel() == 1).toList());


        return new CardDeck(list, true);

    }

    public static CardDeck createLvl2Deck() throws IOException {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
        }).stream().filter(card -> card.getLevel() == 2).toList());

        System.out.println("LVL2: ");

        return new CardDeck(list, true);

    }

    public static CardDeck createLearningDeck() throws IOException {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
        }).stream().filter(AdventureCard::isLearningFlight).toList());

        return new CardDeck(list, true);

    }

    public static CardDeck createTestDeck() throws IOException {
        File file = new File("src/main/resources/cardsdata.json"); // metti qui il percorso corretto
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<AdventureCard> list = new ArrayList<AdventureCard>(mapper.readValue(file, new TypeReference<ArrayList<AdventureCard>>() {
        }).stream().toList());
        ArrayList<AdventureCard> cardsToTest = new ArrayList<>();
        cardsToTest.add(list.get(4)); //Open Space
        cardsToTest.add(list.get(31)); //Planets
        cardsToTest.add(list.get(3)); //Stardust
        cardsToTest.add(list.get(24)); //Epidemic

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
        Position nord = new Position(S.getPosition().getY() - 1, S.getPosition().getX());
        Position sud = new Position(S.getPosition().getY() + 1, S.getPosition().getX());
        Position est = new Position(S.getPosition().getY(), S.getPosition().getX() + 1);
        Position ovest = new Position(S.getPosition().getY(), S.getPosition().getX() - 1);

        // Controlla la connessione del motore in base alla rotazione della tile
        if (T.getRotation() == 0) {
            if (!tempIP.contains(sud) && inBoundaries(sud.getY(), sud.getY()) && P.getShipBoard()[sud.getY()][sud.getX()].getTile() != null) {
                wellConnected = false;
            }
        } else wellConnected = false;

        return wellConnected;
    }

    public static Boolean CannonWellConnected(Tile T, Ship P, Slot S) {
        boolean wellConnected = true;
        ArrayList<Position> tempIP = P.getInvalidPositions();
        Position nord = new Position(S.getPosition().getY() - 1, S.getPosition().getX());
        Position sud = new Position(S.getPosition().getY() + 1, S.getPosition().getX());
        Position est = new Position(S.getPosition().getY(), S.getPosition().getX() + 1);
        Position ovest = new Position(S.getPosition().getY(), S.getPosition().getX() - 1);

        switch (T.getRotation()) {
            case 0:
                if (!tempIP.contains(nord) && inBoundaries(nord.getY(), nord.getX()) && P.getShipBoard()[nord.getY()][nord.getX()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 90:
                if (!tempIP.contains(ovest) && inBoundaries(ovest.getY(), ovest.getX()) && P.getShipBoard()[ovest.getY()][ovest.getX()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 180:
                if (!tempIP.contains(sud) && inBoundaries(sud.getY(), sud.getX()) && P.getShipBoard()[sud.getY()][sud.getX()].getTile() != null) {
                    wellConnected = false;
                }
                break;
            case 270:
                if (!tempIP.contains(est) && inBoundaries(est.getY(), est.getX()) && P.getShipBoard()[est.getY()][est.getX()].getTile() != null) {
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
        if (S.getPosition().getY() - 1 >= 0 && TempShipBoard[S.getPosition().getY() - 1][S.getPosition().getX()].getTile() != null) {
            s1 = TempShipBoard[S.getPosition().getY() - 1][S.getPosition().getX()].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s1 = null;
        }

        // Controlla la tile a sinistra
        if (S.getPosition().getX() - 1 >= 0 && TempShipBoard[S.getPosition().getY()][S.getPosition().getX() - 1].getTile() != null) {
            s2 = TempShipBoard[S.getPosition().getY()][S.getPosition().getX() - 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s2 = null;
        }

        // Controlla la tile sotto
        if (S.getPosition().getY() + 1 <= 6 && TempShipBoard[S.getPosition().getY() + 1][S.getPosition().getX()].getTile() != null) {
            s3 = TempShipBoard[S.getPosition().getY() + 1][S.getPosition().getX()].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s3 = null;
        }

        // Controlla la tile a destra
        if (S.getPosition().getX() + 1 <= 4 && TempShipBoard[S.getPosition().getY()][S.getPosition().getX() + 1].getTile() != null) {
            s4 = TempShipBoard[S.getPosition().getY()][S.getPosition().getX() + 1].getTile().getMyComponent().accept(new ComponentNameVisitor());
        } else {
            s4 = null;
        }

        // Verifica se c'è un sistema di supporto vitale del colore richiesto
        if (color.equals(AlienColor.PURPLE)) {
            if ("PurpleLifeSupportSystem".equals(s1) || "PurpleLifeSupportSystem".equals(s2) ||
                    "PurpleLifeSupportSystem".equals(s3) || "PurpleLifeSupportSystem".equals(s4)) {
                return true;
            }
        }
        if (color.equals(AlienColor.BROWN)) {
            return "BrownLifeSupportSystem".equals(s1) || "BrownLifeSupportSystem".equals(s2) ||
                    "BrownLifeSupportSystem".equals(s3) || "BrownLifeSupportSystem".equals(s4);
        }

        return false;
    }


    /**
     * Verifica se i connettori di una tile sono ben connessi agli slot adiacenti della nave.
     *
     * @param s      La nave in cui si sta effettuando il controllo.
     * @param mySlot Lo slot della tile in esame.
     * @param T      La tile in esame.
     * @return Una coppia contenente un booleano che indica se la connessione è valida e un intero con il numero di connessioni valide.
     */
    public static Pair<Boolean, Integer> wellConnectedConnectors(Ship s, Slot mySlot, Tile T) {

        Slot[][] TempShipBoard = s.getShipBoard();
        Connector c1;
        Connector c2;
        Connector c3;
        Connector c4;
        int sum = 0;


        //UP TILE

        if (mySlot.getPosition().getY() - 1 >= 0 && TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile() != null) {
            c1 = TempShipBoard[mySlot.getPosition().getY() - 1][mySlot.getPosition().getX()].getTile().getSides().get(2);
            sum += 1;
        } else {
            c1 = null;
        }

        //LEFT

        if (mySlot.getPosition().getX() - 1 >= 0 && TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile() != null) {

            sum += 1;
            c2 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() - 1].getTile().getSides().get(3);
        } else c2 = null;
        //DOWN

        if (mySlot.getPosition().getY() + 1 < 5 && TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile() != null) {
            c3 = TempShipBoard[mySlot.getPosition().getY() + 1][mySlot.getPosition().getX()].getTile().getSides().get(0);
            sum += 1;


        } else c3 = null;

        //RIGHT TILE

        if (mySlot.getPosition().getX() + 1 < 7 && TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile() != null) {
            c4 = TempShipBoard[mySlot.getPosition().getY()][mySlot.getPosition().getX() + 1].getTile().getSides().get(1);
            sum += 1;


        } else c4 = null;


        if ((compatible(c1, T.getSides().get(0)) || c1 == null) && (compatible(c2, T.getSides().get(1)) || c2 == null) && (compatible(c2, T.getSides().get(2)) || c3 == null) && (compatible(c3, T.getSides().get(3)) || c4 == null)) {
            return new Pair<>(true, sum);
        }

        return new Pair<>(false, sum);

    }


    public static Boolean checkNearLFS(Position position, AlienColor color, Ship myShip) {

        ArrayList<Position> adjacentPos = getAdjacentPositions(position);

        for (Position pos : adjacentPos) {

            Slot tempSlot = myShip.getShipBoard()[pos.getY()][pos.getX()];

            if (tempSlot != null && tempSlot.getTile() != null) {
                if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("PurpleLifeSupportSystem") && color.equals(AlienColor.PURPLE)) {
                    return true;
                } else if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("BrownLifeSupportSystem") && color.equals(AlienColor.BROWN)) {
                    return true;
                }
            }

        }

        return false;

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

                    if (inBoundaries(positions.get(i).getY(), positions.get(i).getX()) && myShip.getShipBoard()[positions.get(i).getY()][positions.get(i).getX()].getTile() != null && compatible(myShip.getShipBoard()[positions.get(i).getY()][positions.get(i).getX()].getTile().getSides().get((i + 2) % 4), tile.getSides().get(i))) {
                        Tile tempTile = myShip.getShipBoard()[positions.get(i).getY()][positions.get(i).getX()].getTile();
                        System.out.println("STO PER VISITARE : " + positions.get(i).getY() + positions.get(i).getX());
                        visitTile(tempTile, tilesID, myShip.getShipBoard()[positions.get(i).getY()][positions.get(i).getX()], invalidPositions, newBrokenPos, myShip);


                    }
//                    System.out.println("[2]I: " + i + " " + positions.size());


                }

            }

        }
    }


    public static Boolean inBoundaries(int y, int x) {
        return (y >= 0 && y < 5) && (x >= 0 && x < 7);
    }

    public static ArrayList<Position> getAdjacentPositions(Position pos) {
        ArrayList<Position> adjacent = new ArrayList<>();
        adjacent.add(new Position(pos.getY() - 1, pos.getX())); // Nord
        adjacent.add(new Position(pos.getY(), pos.getX() - 1)); // Ovest
        adjacent.add(new Position(pos.getY() + 1, pos.getX())); // Sud
        adjacent.add(new Position(pos.getY(), pos.getX() + 1)); // Est
        return adjacent;
    }

    public static Boolean compatible(Connector connector1, Connector connector2) {
        if (connector1 == null && connector2 != null) return true;
        if (connector1 != null && connector2 == null) return true;

        if (connector1 != null && connector1.equals(Connector.EMPTY) && !connector2.equals(Connector.EMPTY))
            return false;
        if (connector1 != null && connector1.equals(connector2)) return true;
        if (connector1 != null && connector1.equals(Connector.UNIVERSAL) && !connector2.equals(Connector.EMPTY))
            return true;
        return connector1 != null && connector2.equals(Connector.UNIVERSAL);
    }

    public static ArrayList<Good> getMostValuableGoods(Ship ship) {
        //red, yellow, green, blue
        Good firstGood = null;
        Good secondGood = null;

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("GenericCargoHolds");
        Map<Color, ArrayList<Position>> goodPositions = new HashMap<>();

        goodPositions.put(Color.RED, new ArrayList<>());
        goodPositions.put(Color.BLUE, new ArrayList<>());
        goodPositions.put(Color.GREEN, new ArrayList<>());
        goodPositions.put(Color.YELLOW, new ArrayList<>());


        //trovo tutte le Tiles da rimuovere
        List<Slot> Slots = Arrays.stream(ship.getShipBoard())
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .toList();

        for (Slot s : Slots) {
            Tile tempTile = s.getTile();
            if (tempTile != null) {
                if (storagePos.contains(s.getPosition())) {
                    //se la firstGood è null
                    GenericCargoHolds genericCargoHolds = (GenericCargoHolds) tempTile.getMyComponent();
                    if (genericCargoHolds.hasGood(Color.RED)) {
                        goodPositions.get(Color.RED).add(s.getPosition());
                    }
                    if (genericCargoHolds.hasGood(Color.YELLOW)) {
                        goodPositions.get(Color.YELLOW).add(s.getPosition());
                    }
                    if (genericCargoHolds.hasGood(Color.GREEN)) {
                        goodPositions.get(Color.GREEN).add(s.getPosition());
                    }
                    if (genericCargoHolds.hasGood(Color.BLUE)) {
                        goodPositions.get(Color.BLUE).add(s.getPosition());
                    }
                }
            }
        }

        //dopo averle, parto dalla piu importante


        List<Color> priority = List.of(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE);
        int index = 0;

        while ((firstGood == null || secondGood == null) && index < priority.size()) {
            int j = 0;
            while (j < 2 && !goodPositions.get(priority.get(index)).isEmpty()) {
                Tile tile = ship.getTileFromPosition(goodPositions.get(priority.get(index)).get(j));
                GenericCargoHolds genericCargoHolds = (GenericCargoHolds) tile.getMyComponent();

                Color currentColor = priority.get(index);

                if (firstGood == null) {

                    //firstGood = priority.get(index);

                    genericCargoHolds.removeGood(currentColor);
                    firstGood = new Good(currentColor);
                    goodPositions.get(currentColor).remove(j);
                    j++;
                    continue;
                }
                if (secondGood == null) {

                    genericCargoHolds.removeGood(currentColor);
                    secondGood = new Good(currentColor);
                    goodPositions.get(currentColor).remove(j);
                    j++;
                }
            }
            index++;
        }

        return new ArrayList<>(List.of(firstGood, secondGood));

    }

    public static void removeTwoBatteries(Ship ship, Boolean excludeSecond) {
        boolean firstBattery = false;
        boolean secondBattery = false;

        ArrayList<Position> storagePos = ship.getComponentPositionsFromName("BatterySlot");

        int index = 0;
        while ((!firstBattery || (!secondBattery && !excludeSecond)) && index < storagePos.size()) {
            Tile tile = ship.getTileFromPosition(storagePos.get(index));
            BatterySlot batterySlot = (BatterySlot) tile.getMyComponent();

            while (batterySlot.getBatteriesLeft() > 0) {
                if (!firstBattery) {
                    if (batterySlot.removeBattery()) {
                        firstBattery = true;
                    }
                }

                if (!secondBattery && !excludeSecond) {
                    if (batterySlot.removeBattery()) {
                        secondBattery = true;
                    }
                }

                if (excludeSecond) {
                    break;
                }
            }

            index++;
        }
    }
}
