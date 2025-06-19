package org.polimi.ingsw.galaxytrucker.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockShipFactoryTest {

    @Test
     void testFindTilesByComponentType() throws IOException {
        List<Tile> batteryTiles = MockShipFactory.findTilesByComponentType("BatterySlot");

        assertNotNull(batteryTiles);
        assertFalse(batteryTiles.isEmpty(), "No BatterySlot tiles found");

        for (Tile tile : batteryTiles) {
            String type = tile.getMyComponent().accept(new ComponentNameVisitor());
            assertEquals("BatterySlot", type, "Unexpected component type found: " + type);
        }
    }

    @Test
        void testCreateMockShip() throws IOException {
        Ship ship = MockShipFactory.createMockShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

        }
    @Test
    void testCreateMockShip2()  {
        Ship ship = MockShipFactory.createMockShip2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );



    }

    @Test
    void testCreateShipWithConnectedHousingUnits()  {
        Ship ship = MockShipFactory.createShipWithConnectedHousingUnits();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }

    @Test
    void testCreateShipCreateHighFirePowerShip()  {
        Ship ship = MockShipFactory.createHighFirePowerShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }
    @Test
    void testCreateMockShipForCheckShip()  {
        Ship ship = MockShipFactory.createMockShipForCheckShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }

    @Test
    void testCreateMockShipWithShield()  {
        Ship ship = MockShipFactory.createMockShipWithShield();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }
    @Test
    void testCreateHighFirePowerShipWithMultiDirection()  {
        Ship ship = MockShipFactory.createHighFirePowerShipWithMultiDirection();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }

    @Test
    void testCreateHighFirePowerShipWithMultiDirection2()  {
        Ship ship = MockShipFactory.createHighFirePowerShipWithMultiDirection2();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }

    @Test
    void  testCreateEasyDestroyedShip()  {
        Ship ship = MockShipFactory.createEasyDestroyedShip();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }
    @Test
    void testCreateMockShip_CombatZone() {
        Ship ship = MockShipFactory.createMockShip_CombatZone();
        System.out.println(ship);
        ShipPrintUtils.printShip(ship);
        System.out.println("nCrew "+ship.getnCrew());
        System.out.println("min Engine power " + ship.calculateEnginePower());
        System.out.println("min fire power "  + ship.calculateFirePower());
        System.out.println("export component " + ship.getnExposedConnector() );

    }

}
