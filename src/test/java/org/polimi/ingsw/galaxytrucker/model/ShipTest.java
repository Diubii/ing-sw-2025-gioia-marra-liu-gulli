package org.polimi.ingsw.galaxytrucker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    Ship myShip;

    @BeforeEach
    void setUp() {
        myShip = new Ship(false);
    }

    @Test
    void generateSlot() {

        myShip.generateSlot();
        assertNotNull(myShip.getShipBoard()[0][0]);

    }


    @Test
    void createIP() {
        myShip.createIP();
        assertTrue( myShip.getInvalidPositions().contains(new Position(0, 0)));
    }

    @Test
    void putTile() {

        ArrayList<Connector> connectors = new ArrayList<>();
        Shield myComponent = new Shield(new ArrayList<>(), false);
        Tile myTile = new Tile(0,0,connectors, myComponent);
        myShip.putTile(myTile, new Position(0,0));
        assertNull(myShip.getShipBoard()[0][0].getTile());
    }

    @Test
    void updateSets() {
        ArrayList<Connector> connectors = new ArrayList<>();
        BatterySlot myComponent = new BatterySlot(2);
        Tile myTile = new Tile(0,0,connectors, myComponent);
        myShip.putTile(myTile, new Position(4,4));
        int num = myShip.getBatteryPos().size();
        assertEquals(1, num);
    }

    @Test
    void removeTile() {
    }

    @Test
    void calcExposedConnectors() {
        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);

        BatterySlot myComponent = new BatterySlot(2);
        Tile myTile1 = new Tile(0,0,connectors, myComponent);
        Tile myTile2 = new Tile(0,0,connectors, myComponent);



        myShip.putTile(myTile1, new Position(4,4));
        myShip.putTile(myTile2, new Position(4,5));


        myShip.calcExposedConnectors();
        assertEquals(6, myShip.getnExposedConnector());

    }

    @Test
    void checkShip() {
        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);

        BatterySlot myComponent = new BatterySlot(2);
        Tile myTile1 = new Tile(0,0,connectors, myComponent);
        Tile myTile2 = new Tile(1,0,connectors, myComponent);



        myShip.putTile(myTile1, new Position(4,4));
        myShip.putTile(myTile2, new Position(4,5));

        assertTrue(myShip.checkShip());
    }

    @Test
    void truncateShip() {
        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);

        BatterySlot myComponent = new BatterySlot(2);
        Tile myTile1 = new Tile(0,0,connectors, myComponent);
        Tile myTile2 = new Tile(1,0,connectors, myComponent);
        Tile myTile3 = new Tile(2,0,connectors, myComponent);



        myShip.putTile(myTile1, new Position(4,4));
        myShip.putTile(myTile2, new Position(4,5));
        myShip.putTile(myTile2, new Position(3,4));
        myShip.putTile(myTile3, new Position(3,5));
//
//        myShip.removeTile(myTile2,new Position(4,5));
//        myShip.brokenPositions.add(new Position(4,5));
        myShip.removeTile(myTile3,new Position(3,4));
        myShip.brokenPositions.add(new Position(3,4));


        ArrayList<Slot[][]> tronconi = myShip.getTronc();
        myShip.updateShipBoard(tronconi.get(0));
        System.out.println(myShip.toString());
        assertEquals(1, tronconi.size());

    }

    @Test
    void getProtectedSides() {
    }

    @Test
    void activateShield() {
    }

    @Test
    void activateDoubleEngine() {
    }

    @Test
    void activateDoubleCannon() {
    }

    @Test
    void getTronc(){

    }
}