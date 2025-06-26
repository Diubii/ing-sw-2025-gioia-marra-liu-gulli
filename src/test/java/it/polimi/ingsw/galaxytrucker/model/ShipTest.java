package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleEngine;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void testPutTileInInvalidPosition() {
        Ship ship = new Ship(true); // learningMatch = true
        ship.createIP(); // populates invalidPositions

        Position invalidPos = new Position(0, 0); // one of the known invalid positions
        Tile tile = TileRegistry.getFirstTileOfType("Engine");

        ship.putTile(tile, invalidPos);
        Tile result = ship.getTileFromPosition(invalidPos);

        assertNull(result); // nothing should be placed
    }


    @Test
    public void testPutTile() {
        Ship myShip = new Ship(false);
        Tile tile = TileRegistry.getFirstTileOfType("Engine");;
        Position pos = new Position(3, 2);

        myShip.putTile(tile, pos);

        Tile tile2 = myShip.getTileFromPosition(pos);
        assertNotNull(tile2); // make sure the tile was placed

        assertEquals(tile.getId(), tile2.getId());

    }
//tests for UpdateSets
    @Test
    public void testUpdateSetsWithDoubleEngine() {
        Ship ship = new Ship(false);

        Tile doubleEngineTile = TileRegistry.getClonedTilesOfType("DoubleEngine").get(0);

        ((DoubleEngine) doubleEngineTile.getMyComponent()).setCharged(true);

        Position pos = new Position(2, 2);

        ship.putTile(doubleEngineTile, pos);
        ArrayList<Position> enginePositions = ship.getEnginePos();

        assertTrue(enginePositions.contains(pos), "enginePos should contain DoubleEngine position");
    }


//tests for calculateEnginePower
    @Test
    public void testPowerEngine_NoEngines() {
        Ship ship = new Ship(false); // No engines added
        assertEquals(0, ship.calculateEnginePower());
    }

    /**
     * Tests that one engine tiles contribute a total of 1 power.
     */
    @Test
    public void testPowerEngine_OneEngine() {

            Ship ship = new Ship(false);
            Tile engineTile = TileRegistry.getFirstTileOfType("Engine");
            Position pos = new Position(3, 2);

            ship.putTile(engineTile, pos);

            int power = ship.calculateEnginePower();


            assertEquals(1, power);

    }
    /**
     * Tests that two engine tiles contribute a total of 2 power.
     */
    @Test
    public void testPowerEngine_TwoEngine() {

        Ship ship = new Ship(false);
        List<Tile> engineTiles = TileRegistry.getClonedTilesOfType("Engine");

        Tile engineT1 = engineTiles.get(0);
        Tile engineT2 = engineTiles.get(1);

        Position pos1 = new Position(3, 2);
        Position pos2 = new Position(3, 3);

        ship.putTile(engineT1, pos1);
        ship.putTile(engineT2, pos2);

        int power = ship.calculateEnginePower();


        assertEquals(2, power);

    }
    /**
     * Tests that a DoubleEngine tile contributes 1 or 2 power depending on its charged state.
     */
    @Test
    public void testPowerEngine_WithDoubleEngine() {

        Ship ship = new Ship(false);
        List<Tile> engineTiles = TileRegistry.getClonedTilesOfType("Engine");

        Tile engineT1 = engineTiles.get(0);
        Tile engineT2 = engineTiles.get(1);
        Tile doubleET1 = TileRegistry.getClonedTilesOfType("DoubleEngine").get(0);

        Position pos1 = new Position(3, 2);
        Position pos2 = new Position(2, 2);
        Position pos3 = new Position(3, 3);

        ship.putTile(engineT1, pos1);
        ship.putTile(engineT2, pos2);
        ship.putTile(doubleET1, pos3);

        int power = ship.calculateEnginePower();

        assertEquals(2, power);
        Tile doubleEngine = ship.getTileFromPosition(pos3);
        DoubleEngine dEngine = (DoubleEngine) doubleEngine.getMyComponent();
        dEngine.setCharged(true);
        int newPower = ship.calculateEnginePower();
        assertEquals(4, newPower);
        dEngine.setCharged(false);
        int newPower2 = ship.calculateEnginePower();
        assertEquals(2, newPower2);

    }
    // end tests for calculateEnginePower


//test for updateSets
    @Test
    void updateSets() {
        Tile batterySlot1 = TileRegistry.getFirstTileOfType("BatterySlot");
        myShip.putTile(batterySlot1, new Position(2,3));
        int num = myShip.getBatteryPos().size();
        assertEquals(1, num);

        Position pos1 = new Position(3,3);
        Position pos2 = new Position(2,1);
        Tile cannon = TileRegistry.getFirstTileOfType("Cannon");
        myShip.putTile(cannon, pos1);
        num = myShip.getCannonPos().size();
        assertEquals(1, num);

        Tile doubleCannon = TileRegistry.getFirstTileOfType("DoubleCannon");
        myShip.putTile(doubleCannon, pos2);
        List<Position>  cannonPos = myShip.getCannonPos();
        Tile testTile = myShip.getTileFromPosition(pos1);
        Tile testTile2 = myShip.getTileFromPosition(pos2);
        num = myShip.getCannonPos().size();
        assertEquals(2, num);
        assertEquals(testTile.getId(),cannon.getId());
        assertEquals(testTile2.getId(),doubleCannon.getId());

        Position pos3 = new Position(2,2);
        Position pos4 = new Position(1,1);
        Tile engine = TileRegistry.getFirstTileOfType("Engine");
        myShip.putTile(engine, pos3);
        num = myShip.getEnginePos().size();
        assertEquals(1, num);

        Tile doubleEngine = TileRegistry.getFirstTileOfType("DoubleEngine");
        myShip.putTile(doubleEngine, pos4);
        List<Position>  enginePos = myShip.getEnginePos();
        Tile testTile3 = myShip.getTileFromPosition(pos3);
        Tile testTile4 = myShip.getTileFromPosition(pos4);
        num = myShip.getEnginePos().size();
        assertEquals(2, num);
        assertEquals(testTile3.getId(),engine.getId());
        assertEquals(testTile4.getId(),doubleEngine.getId());


    }

    @Test
    void removeTile() {
    }

    @Test
    void testCalcExposedConnectors() {

        Ship mockShip1 = MockShipFactory.createMockShip();
        Ship mockShip2 = MockShipFactory.createMockShip2();
        Ship mockShip3 = MockShipFactory.createShipWithConnectedHousingUnits();

        int exposedConnectors1 = mockShip1.getnExposedConnector();
        System.out.println("1  " + exposedConnectors1);
        int exposedConnectors2 = mockShip2.getnExposedConnector();
        System.out.println("2  " + exposedConnectors2);
        int exposedConnectors3 = mockShip3.getnExposedConnector();
        System.out.println("3  " + exposedConnectors3);

        assertEquals(exposedConnectors1,5);
        assertEquals(exposedConnectors3,7);
        assertEquals(exposedConnectors2,7);



    }


    @Test
    void testCheckShip() {
        Ship ship = MockShipFactory.createMockShip();
        assertTrue(ship.checkShip());
        ShipPrintUtils.printShip(ship);
        Ship errorShip = MockShipFactory.createMockShipForCheckShip();
        ShipPrintUtils.printShip(errorShip);

        assertFalse(errorShip.checkShip());
    }

    @Test
    void testGetFirstComponentFromDirectionAndIndex() {

        //Test per trovare il primo tile in ogni direzione
        Ship ship = MockShipFactory.createMockShip();
        Position pos1 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.UP,3);
        Position pos2 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.DOWN,2);
        Position pos3 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.LEFT,1);
        Position pos4 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.RIGHT,3);

        Position realPos1 = new Position(3,1);
        Position realPos2 = new Position(2,3);
        Position realPos3 = new Position(2,1);
        Position realPos4 = new Position(3,3);

        assertEquals(realPos1,pos1);
        assertEquals(realPos2,pos2);
        assertEquals(realPos3,pos3);
        assertEquals(realPos4,pos4);

        System.out.println("1  " + pos1);
        System.out.println("2  " + pos2);
        System.out.println("3  " + pos3);
        System.out.println("4  " + pos4);

        //Test per verificare che venga restituito null per righe o colonne senza alcun tile.
        Position pos5 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.UP,10);
        Position pos6 = ship.getFirstComponentFromDirectionAndIndex(ProjectileDirection.DOWN,10);

        assertNull(pos5);
        assertNull(pos6);


    }

    @Test
    void testTruncateShip() {

        Ship ship = MockShipFactory.createMockShip();
        ShipPrintUtils.printShip(ship);

        //distruggo tile

        ship.removeTile(new Position(3,1), false);

        ArrayList<Ship> Troncons = ship.getTronc();

        for (Ship ship1: Troncons){
            ShipPrintUtils.printShip(ship1);

        }



        System.out.println("Next Test");
        System.out.println();
        ship = MockShipFactory.createEasyDestroyedShip();
        ShipPrintUtils.printShip(ship);

        ship.removeTile(new Position(4,2), false);
        Troncons = ship.getTronc();

        for (Ship ship1: Troncons){
            ShipPrintUtils.printShip(ship1);

        }


        System.out.println("Next Test");
        System.out.println();
        ship = MockShipFactory.createEasyDestroyedShip();
        ShipPrintUtils.printShip(ship);

        ship.removeTile(new Position(2,1), false);
        Troncons = ship.getTronc();

        for (Ship ship1: Troncons){
            ShipPrintUtils.printShip(ship1);

        }

        System.out.println("Next Test");
        System.out.println();
        ship = MockShipFactory.createEasyDestroyedShip();
        ShipPrintUtils.printShip(ship);

        ship.removeTile(new Position(3,2), false);
        Troncons = ship.getTronc();

        for (Ship ship1: Troncons){
            ShipPrintUtils.printShip(ship1);

        }



        System.out.println("Next Test");
        System.out.println();
        ship = MockShipFactory.createEasyDestroyedShip();
        ShipPrintUtils.printShip(ship);

        ship.removeTile(new Position(2,2), false);
        Troncons = ship.getTronc();

        for (Ship ship1: Troncons){
            ShipPrintUtils.printShip(ship1);

        }
    }


    @Test
    void testRemainingTiles(){

        Ship ship  = MockShipFactory.createMockShip();
        ShipPrintUtils.printShip(ship);
        int remainingTiles = ship.remainingTiles();
        assertEquals(7, remainingTiles);


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