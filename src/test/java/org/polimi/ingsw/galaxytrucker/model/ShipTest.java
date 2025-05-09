//package org.polimi.ingsw.galaxytrucker.model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
//import org.polimi.ingsw.galaxytrucker.enums.Connector;
//import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
//import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
//import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
//import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
//import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ShipTest {
//
//    Ship myShip;
//
//    @BeforeEach
//    void setUp() {
//        myShip = new Ship(false);
//    }
//
//    @Test
//    void generateSlot() {
//
//        myShip.generateSlot();
//        assertNotNull(myShip.getShipBoard()[0][0]);
//
//    }
//
//
//    @Test
//    void createIP() {
//        myShip.createIP();
//        assertTrue( myShip.getInvalidPositions().contains(new Position(0, 0)));
//    }
//
//    @Test
//    void putTile() {
//
//        ArrayList<Connector> connectors = new ArrayList<>();
//        Shield myComponent = new Shield(new ArrayList<>(), false);
//        Tile myTile = new Tile(0,0,connectors, myComponent);
//        myShip.putTile(myTile, new Position(0,0));
//        assertNull(myShip.getShipBoard()[0][0].getTile());
//    }
//
//    @Test
//    void updateSets() {
//        ArrayList<Connector> connectors = new ArrayList<>();
//        BatterySlot myComponent = new BatterySlot(2);
//        Tile myTile = new Tile(0,0,connectors, myComponent);
//        myShip.putTile(myTile, new Position(4,4));
//        int num = myShip.getBatteryPos().size();
//        assertEquals(1, num);
//    }
//
//    @Test
//    void removeTile() {
//    }
//
//    @Test
//    void calcExposedConnectors() {
//        ArrayList<Connector> connectors = new ArrayList<>();
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//
//        BatterySlot myComponent = new BatterySlot(2);
//        Tile myTile1 = new Tile(0,0,connectors, myComponent);
//        Tile myTile2 = new Tile(0,0,connectors, myComponent);
//
//
//
//        myShip.putTile(myTile1, new Position(4,4));
//        myShip.putTile(myTile2, new Position(4,5));
//
//
//        myShip.calcExposedConnectors();
//        assertEquals(6, myShip.getnExposedConnector());
//
//    }
//
//    @Test
//    void checkShip() {
//        ArrayList<Connector> connectors = new ArrayList<>();
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//
//        BatterySlot myComponent = new BatterySlot(2);
//        Tile myTile1 = new Tile(0,0,connectors, myComponent);
//        Tile myTile2 = new Tile(1,0,connectors, myComponent);
//
//
//
//        myShip.putTile(myTile1, new Position(4,4));
//        myShip.putTile(myTile2, new Position(4,5));
//
//        assertTrue(myShip.checkShip());
//    }
//
//    @Test
//    void truncateShip() {
//        ArrayList<Connector> connectors = new ArrayList<>();
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//        connectors.add(Connector.UNIVERSAL);
//
//        BatterySlot myComponent = new BatterySlot(2);
//        Tile myTile1 = new Tile(0,0,connectors, myComponent);
//        Tile myTile2 = new Tile(1,0,connectors, myComponent);
//        Tile myTile3 = new Tile(2,0,connectors, myComponent);
//
//        Tile myTile4 = new Tile(3,0,connectors, myComponent);
//        Tile myTile5 = new Tile(3,0,connectors, new Shield(new ArrayList<>(), false));
//
//
//
//        myShip.putTile(myTile1, new Position(4,4));
//        myShip.putTile(myTile2, new Position(4,5));
//        myShip.getShipBoard()[4][5].getTile().setMyComponent(new LifeSupportSystem(AlienColor.BROWN));
//
//        myShip.putTile(myTile2, new Position(3,4));
//        myShip.putTile(myTile3, new Position(3,5));
//        myShip.getShipBoard()[3][5].getTile().setMyComponent(new ModularHousingUnit());
//        ((ModularHousingUnit)myShip.getShipBoard()[3][5].getTile().getMyComponent()).addPurpleAlien();
//
//
//        myShip.putTile(myTile4, new Position(2,4));
//        myShip.putTile(myTile5, new Position(1,4));
/// /
/// /        myShip.removeTile(myTile2,new Position(4,5));
/// /        myShip.brokenPositions.add(new Position(4,5));
//
//        myShip.checkShip();
//        System.out.println(myShip.toString());
//
//        myShip.removeTile(myTile2,new Position(3,4));
//
//
//        ArrayList<Slot[][]> tronconi = myShip.getTronc();
//
//        for (Slot[][] slots : tronconi) {
//            myShip.updateShipBoard(slots);
//            System.out.println(myShip.toString());
//        }
//
//        assertEquals(2, tronconi.size());
//
//    }
//
//    @Test
//    void getProtectedSides() {
//    }
//
//    @Test
//    void activateShield() {
//    }
//
//    @Test
//    void activateDoubleEngine() {
//    }
//
//    @Test
//    void activateDoubleCannon() {
//    }
//
//    @Test
//    void getTronc(){
//
//    }
//}