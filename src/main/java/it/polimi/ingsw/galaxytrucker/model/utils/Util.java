package it.polimi.ingsw.galaxytrucker.model.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.Connector;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Utility class that provides methods for verifying connectivity of components in the ship.
 *
 * @author nerd53
 */
public class Util {

    /**
     * Creates a deck of level 1 adventure cards by loading data from a JSON file.
     *
     * @return A CardDeck containing level 1 adventure cards
     */
    public static CardDeck createLvl1Deck() {
        ObjectMapper mapper = new ObjectMapper();

        String path = "cardsdata.json";

        try (InputStream in = Util.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                ArrayList<AdventureCard> cards = new ArrayList<>(mapper.readValue(in, new TypeReference<ArrayList<AdventureCard>>() {
                }).stream().filter(card -> card.getLevel() == 1).toList());
                return new CardDeck(cards, true);
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }

        return null;
    }

    /**
     * Creates a deck of level 2 adventure cards by loading data from a JSON file.
     *
     * @return A CardDeck containing level 2 adventure cards
     */
    public static CardDeck createLvl2Deck() {
        ObjectMapper mapper = new ObjectMapper();

        String path = "cardsdata.json";

        try (InputStream in = Util.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                ArrayList<AdventureCard> cards = new ArrayList<>(mapper.readValue(in, new TypeReference<ArrayList<AdventureCard>>() {
                }).stream().filter(card -> card.getLevel() == 2).toList());
                return new CardDeck(cards, true);
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }

        return null;
    }

    /**
     * Creates a deck of learning flight adventure cards by loading data from a JSON file.
     *
     * @return A CardDeck containing learning flight adventure cards
     */
    public static CardDeck createLearningDeck() {
        ObjectMapper mapper = new ObjectMapper();

        String path = "cardsdata.json";

        try (InputStream in = Util.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                ArrayList<AdventureCard> cards = new ArrayList<>(mapper.readValue(in, new TypeReference<ArrayList<AdventureCard>>() {}));
                return new CardDeck(cards, true);
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }

        return null;
    }

    /**
     * Creates a test deck of specific adventure cards for testing purposes.
     *
     * @return A CardDeck containing specific cards for testing
     * @throws IOException if there's an error reading the JSON file
     */
    public static CardDeck createTestDeck() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String path = "cardsdata.json";

        ArrayList<AdventureCard> list = null;

        try (InputStream in = Util.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                list = new ArrayList<>(mapper.readValue(in, new TypeReference<ArrayList<AdventureCard>>() {}));
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }


        ArrayList<AdventureCard> cardsToTest = new ArrayList<>();
        if(list == null) return new CardDeck(cardsToTest, true);
        //cardsToTest.add(list.get(3));
        //TUTTE LE 11 TIPOLOGIE DI CARTE

        //sembra ok
//        cardsToTest.add(list.get(1)); //Contrabbandieri
//        cardsToTest.add(list.get(10)); //Meteoriti
//        cardsToTest.add(list.get(3)); //Stardust
//
//
//        cardsToTest.add(list.get(0)); //Schiavisti
//
//        cardsToTest.add(list.get(2)); //Pirati
//
//        cardsToTest.add(list.get(24)); //Epidemic
//        cardsToTest.add(list.get(16)); //AbandonedShip
//
//
//
         cardsToTest.add(list.get(4)); //Open Space
        cardsToTest .add(list.get(5));
        cardsToTest .add(list.get(6));
//         cardsToTest.add(list.get(19)); //AbandonedStation


//        cardsToTest.add(list.get(31)); //Planets
        //Si blocca sempre sembra
//        cardsToTest.add(list.get(15)); //Zona di guerra





//        cardsToTest.add(list.get(8));


//        cardsToTest.add(list.get(3)); //Stardust
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
        ObjectMapper mapper = new ObjectMapper();

        String path = "tiledata.json";

        try (InputStream in = Util.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                return new ArrayList<>(mapper.readValue(in, new TypeReference<ArrayList<Tile>>() {}));
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Checks if an engine is well connected to the ship, verifying that it is not blocked by other components.
     *
     * @param T The tile containing the engine
     * @param P The ship where the engine is positioned
     * @param S The slot where the engine is positioned
     * @return {@code true} if the engine is well connected, {@code false} otherwise
     */
    public static Boolean EngineWellConnected(Tile T, Ship P, Slot S) {
        boolean wellConnected = true;
        ArrayList<Position> tempIP = P.getInvalidPositions();

        Position sud = new Position(S.getPosition().getX(), S.getPosition().getY() + 1);

        if (T.getRotation() == 0) {
            if (!tempIP.contains(sud) && inBoundaries(sud.getX(), sud.getY()) && P.getShipBoard()[sud.getX()][sud.getY()].getTile() != null) {
                wellConnected = false;
            }
        } else wellConnected = false;

        return wellConnected;
    }

    /**
     * Checks if a cannon is well connected to the ship, verifying that it is not blocked by other components.
     *
     * @param T The tile containing the cannon
     * @param P The ship where the cannon is positioned
     * @param S The slot where the cannon is positioned
     * @return {@code true} if the cannon is well connected, {@code false} otherwise
     */
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
     * Verifies if a certain life support system is present near the specified position.
     *
     * @param color The color of the life support system to check
     * @param T     The tile on which the check is performed
     * @param P     The ship in which the search is being conducted
     * @param S     The reference slot within the ship
     * @return {@code true} if the life support system is well connected, {@code false} otherwise
     */
    public static Boolean CheckLifeSupportSystem(AlienColor color, Tile T, Ship P, Slot S) {
        Boolean wellConnected = true;
        ArrayList<Pair<Position, Tile>> connectedTiles = new ArrayList<>(P.getConnectedTiles(S.getPosition()));
        for (Pair<Position, Tile> pair : connectedTiles) {
            Component component = pair.getValue().getMyComponent();

            if (component != null && wellConnectedTiles(T.getSides(), pair.getValue().getSides(), S.getPosition(), pair.getKey())){
                if (component.accept(new ComponentNameVisitor()).equals("PurpleLifeSupportSystem") && color.equals(AlienColor.PURPLE)){
                    return true;
                }  if (component.accept(new ComponentNameVisitor()).equals("BrownLifeSupportSystem") && color.equals(AlienColor.BROWN)){
                    return true;
                }
            }
        }

        return  false;
    }


    /**
     * Verifies if the connectors of a tile are properly connected to adjacent ship slots.
     *
     * @param s      The ship being checked
     * @param mySlot The slot containing the tile being checked
     * @param T      The tile being checked
     * @return true if all connections are valid, false otherwise
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

    /**
     * Checks if two adjacent tiles are properly connected through their connectors.
     *
     * @param tile1Connectors   Connectors of the first tile
     * @param tile2Connectors   Connectors of the second tile
     * @param position1         Position of the first tile
     * @param position2         Position of the second tile
     * @return true if the tiles are properly connected, false otherwise
     */
    public static Boolean wellConnectedTiles(ArrayList<Connector> tile1Connectors, ArrayList<Connector> tile2Connectors, Position position1, Position position2) {

        String relativePos;
        Boolean wellConnected = false;

        if (position1.getX() != position2.getX()) {
            relativePos = "left-right";
        } else relativePos = "up-down";


        if (relativePos.equals("left-right")){
            if (position1.getX() > position2.getX()) {
                wellConnected = compatible(tile1Connectors.get(1), tile2Connectors.get(3)) && !tile1Connectors.get(1).equals(Connector.EMPTY);
            } else wellConnected = compatible(tile1Connectors.get(3), tile2Connectors.get(1)) && !tile1Connectors.get(3).equals(Connector.EMPTY);
        } else {
            if (position1.getY() > position2.getY()) {
                wellConnected = compatible(tile1Connectors.getFirst(), tile2Connectors.get(2)) && !tile1Connectors.getFirst().equals(Connector.EMPTY);

            } else  wellConnected = compatible(tile2Connectors.getFirst(), tile1Connectors.get(2)) && !tile2Connectors.getFirst().equals(Connector.EMPTY);

        }


        return wellConnected;
    }


    /**
     * Checks if there's a life support system of the specified color near the given position.
     *
     * @param position The position to check around
     * @param color    The color of the life support system to look for
     * @param myShip   The ship to search within
     * @return true if a matching life support system is found nearby, false otherwise
     */
    public static Boolean checkNearLFS(Position position, AlienColor color, Ship myShip) {

        return CheckLifeSupportSystem(color, myShip.getTileFromPosition(position),myShip, myShip.getShipBoard()[position.getX()][position.getY()]);

    }

    /**
     * Visits a tile and verifies its connectivity to life support systems.
     *
     * @param tile             The tile to visit
     * @param tilesID          List of already visited tile IDs
     * @param slot             The slot containing the tile
     * @param invalidPositions List of invalid positions
     * @param newBrokenPos     Queue to store newly broken positions
     * @param myShip           The ship containing the tile
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
                }

            }

        }
    }


    /**
     * Checks if the ship is composed of a single connected structure or has separate sections.
     *
     * @param myShip     The ship to check
     * @param startingPos The starting position for the check
     * @return A Pair containing:
     *         - boolean indicating if the ship has no separate sections
     *         - list of visited tile IDs
     */
    public  static Pair<Boolean, ArrayList<Integer>> checkShipStructure(Ship myShip, Position startingPos) {

        ArrayList<Integer> visitedTilesId = new ArrayList<>();
        visitedTilesId.add(myShip.getTileFromPosition(startingPos).getId());
        checkShipStructureTileVisitor(startingPos, myShip, visitedTilesId);

        //now in visitedTilesId I have all the tiles that make up the main section
        //I need to compare total tiles with visited tiles, if they are the same then there are no separate sections

        ArrayList<Integer> tilesId = new ArrayList<>(Arrays.stream(myShip.getShipBoard())
                .flatMap(Arrays::stream).map(Slot::getTile)
                .filter(Objects::nonNull).map(Tile::getId).toList());

        Collections.sort(visitedTilesId);
        Collections.sort(tilesId);
        boolean same = visitedTilesId.equals(tilesId);

        return new Pair<>(same, visitedTilesId);

    }

    /**
     * Recursive helper method for checking ship structure connectivity.
     *
     * @param startingPos   The current position to check from
     * @param myShip        The ship being checked
     * @param visitedTilesId List of IDs of visited tiles
     */
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

    /**
     * Checks if coordinates are within the ship board boundaries.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if within boundaries, false otherwise
     */
    public static Boolean inBoundaries(int x, int y) {
        return (y >= 0 && y < 5) && (x >= 0 && x < 7);
    }

    /**
     * Gets the positions of adjacent tiles to a given position.
     *
     * @param pos The position to find adjacent positions for
     * @return An ArrayList containing the positions of adjacent tiles
     */
    public static ArrayList<Position> getAdjacentPositions(Position pos) {
        ArrayList<Position> adjacent = new ArrayList<>();
        adjacent.add(new Position(pos.getX(), pos.getY() - 1)); // North
        adjacent.add(new Position(pos.getX() - 1, pos.getY())); // West
        adjacent.add(new Position(pos.getX(), pos.getY() + 1)); // South
        adjacent.add(new Position(pos.getX() + 1, pos.getY())); // East
        return adjacent;
    }

    /**
     * Checks if two connectors are compatible with each other.
     *
     * @param connector1 The first connector to check
     * @param connector2 The second connector to check
     * @return true if the connectors are compatible, false otherwise
     */
    public static Boolean compatible(Connector connector1, Connector connector2) {
        //cases with null
        if (connector1 == null && connector2 != null) return true;
        if (connector1 == null || connector2 == null) return true;

        //with one empty connector
        if (connector1.equals(Connector.EMPTY) && !connector2.equals(Connector.EMPTY)) return false;
        if (connector2.equals(Connector.EMPTY) && !connector1.equals(Connector.EMPTY)) return false;

        //they are identical
        if (connector1.equals(connector2)) return true;

        //with one universal connector
        if (connector1.equals(Connector.UNIVERSAL) && !connector2.equals(Connector.EMPTY)) return true;
        if (connector2.equals(Connector.UNIVERSAL) && !connector1.equals(Connector.EMPTY)) return true;

        return false;
    }
}
