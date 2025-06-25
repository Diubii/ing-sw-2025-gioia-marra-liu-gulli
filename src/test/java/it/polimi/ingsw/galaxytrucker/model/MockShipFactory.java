package it.polimi.ingsw.galaxytrucker.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;

import it.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockShipFactory {

    /**
     * Loads all tiles from the JSON file (once) and caches them for future access.
     * @return list of Tile objects
     */
    public static List<Tile> loadAllTiles() {
        File file = new File("src/main/resources/tiledata.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            System.err.println("[MockShipFactory] Failed to load tiledata.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static List<Tile> findTilesByComponentType(String componentType) {
        List<Tile> allTiles = loadAllTiles();
        if(allTiles.isEmpty()) return allTiles;
        else return allTiles.stream()
                .filter(t -> t.getMyComponent() != null &&
                        componentType.equalsIgnoreCase(t.getMyComponent().accept(new ComponentNameVisitor())))
                .toList();
    }

    public static Map<String, List<Tile>> getAllClonedTiles() {
        Map<String, List<Tile>> map = new HashMap<>();
        for (String type : TileRegistry.getTileMap().keySet()) {
            map.put(type, TileRegistry.getClonedTilesOfType(type));
        }
        return map;
    }

    public static Tile getRequiredTile(Map<String, List<Tile>> map, String type, int index) {
        List<Tile> list = map.get(type);
        if (list == null || list.size() <= index) {
            throw new IllegalStateException("No tile found for type: " + type + " at index " + index);
        }
        return list.get(index);
    }

    public static Ship createMockShip() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;
    }
    public static Ship createMockShip2() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile modularHousingUnit3 = getRequiredTile(tiles, "ModularHousingUnit", 5);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        ModularHousingUnit mhu3;
        if(modularHousingUnit3 != null) {
            mhu3 = (ModularHousingUnit) modularHousingUnit3.getMyComponent();
            mhu3.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu3);
        }

        if(engine != null )ship.putTile(engine, new Position(5, 2));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(modularHousingUnit3 != null ) ship.putTile(modularHousingUnit3, new Position(1, 1));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;
    }

    public static Ship createShipWithConnectedHousingUnits(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile modularHousingUnit3 = getRequiredTile(tiles, "ModularHousingUnit", 5);
        Tile modularHousingUnit4 = getRequiredTile(tiles, "ModularHousingUnit", 3);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

       addCrewToHousingUnit(modularHousingUnit);
       addCrewToHousingUnit(modularHousingUnit2);
        addCrewToHousingUnit(modularHousingUnit3);
        addCrewToHousingUnit(modularHousingUnit4);


        if(engine != null )ship.putTile(engine, new Position(5, 2));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(modularHousingUnit3 != null ) ship.putTile(modularHousingUnit3, new Position(1, 1));
        if(modularHousingUnit4!= null ) ship.putTile(modularHousingUnit4, new Position(1, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;


    }

    public static Ship  createHighFirePowerShip(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);



        Tile modularHousingUnit4 = getRequiredTile(tiles, "ModularHousingUnit", 3);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);




        addCrewToHousingUnit(modularHousingUnit4);


        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(5, 2));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 1));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));
        if(cannon2 != null ) ship.putTile(cannon2, new Position(1, 1));
        if(modularHousingUnit4!= null ) ship.putTile(modularHousingUnit4, new Position(1, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;


    }

    public static Ship  createHighFirePowerShip2(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);



        Tile modularHousingUnit4 = getRequiredTile(tiles, "ModularHousingUnit", 3);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);




        addCrewToHousingUnit(modularHousingUnit4);


        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(5, 2));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 1));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));

        if(modularHousingUnit4!= null ) ship.putTile(modularHousingUnit4, new Position(1, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;


    }

    private static void addCrewToHousingUnit(Tile tile) {
        if (tile != null && tile.getMyComponent() instanceof ModularHousingUnit) {
            ModularHousingUnit unit = (ModularHousingUnit) tile.getMyComponent();
            unit.addHumanCrew();
            tile.setMyComponent(unit);
        }
    }


    public static Ship createMockShipForCheckShip() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        engine.rotate(90);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(3, 3));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(2, 2));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(engine != null) ship.putTile(engine, new Position(4, 1));
        ship.putTile(doubleEngine, new Position(2, 4));
        ship.putTile(doubleEngine, new Position(1, 1));


        return ship;
    }

    public static Ship createMockShipWithShield() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);
        Tile shield1 = getRequiredTile(tiles, "Shield", 0);
        Tile shield2 = getRequiredTile(tiles, "Shield", 1);
        shield2.rotate(180);



        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(3, 3));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(2, 2));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(shield1 != null) ship.putTile(shield1, new Position(1, 1));
        if(shield2 != null) ship.putTile(shield2, new Position(2, 4));


        return ship;
    }
    public static Ship  createHighFirePowerShipWithMultiDirection(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 20);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);




        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);





        cannon.rotate(270);
        doubleCannon2.rotate(90);
        cannon2.rotate(180);

        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(5, 2));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(1, 2));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 1));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));
        if(cannon2 != null ) ship.putTile(cannon2, new Position(1, 3));

        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;


    }

    public static Ship  createHighFirePowerShipWithMultiDirection2(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 20);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile cannon4 = getRequiredTile(tiles, "Cannon", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile doubleCannon3 = getRequiredTile(tiles, "DoubleCannon", 1);
        Tile battery2 = getRequiredTile(tiles, "BatterySlot", 1);


        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);





        cannon.rotate(270);
        doubleCannon2.rotate(90);
        cannon2.rotate(180);

        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(5, 2));
        if(doubleCannon3!=null) ship.putTile(doubleCannon3, new Position(5, 1));
        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(1, 2));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 1));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));
        if(cannon2 != null ) ship.putTile(cannon2, new Position(1, 3));

        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(battery2!= null) ship.putTile(battery2, new Position(2, 4));
        if(cannon4!=null) ship.putTile(cannon4, new Position(3, 1));

        return ship;


    }


    public static Ship  createEasyDestroyedShip(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 20);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile cannon4 = getRequiredTile(tiles, "Cannon", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile doubleCannon3 = getRequiredTile(tiles, "DoubleCannon", 1);
        Tile battery2 = getRequiredTile(tiles, "BatterySlot", 1);


        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);





        cannon.rotate(270);
        doubleCannon2.rotate(90);
        cannon2.rotate(180);

        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(5, 2));

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));

        if(cannon != null) ship.putTile(cannon, new Position(1, 2));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 1));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));


        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(battery2!= null) ship.putTile(battery2, new Position(2, 4));


        return ship;


    }

    public static Ship  createEasyDestroyedShip2(){
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 20);
        Tile doubleCannon2 = getRequiredTile(tiles, "DoubleCannon", 10);
        Tile cannon2 = getRequiredTile(tiles, "Cannon", 2);
        Tile cannon3 = getRequiredTile(tiles, "Cannon", 14);
        Tile cannon4 = getRequiredTile(tiles, "Cannon", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile doubleCannon3 = getRequiredTile(tiles, "DoubleCannon", 1);
        Tile battery2 = getRequiredTile(tiles, "BatterySlot", 1);
        Tile battery6 = getRequiredTile(tiles, "BatterySlot", 5);

        Tile component3 = getRequiredTile(tiles, "Component", 2);
        component3.rotate(90);

        Tile component4 = getRequiredTile(tiles, "Component", 3);

        Tile component2 = getRequiredTile(tiles, "Component", 1);


        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);



        cannon.rotate(270);
        doubleCannon2.rotate(90);
        cannon2.rotate(180);

        if(doubleCannon2 != null )ship.putTile(doubleCannon2, new Position(6, 2));

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(0, 2));

        if(component3 != null) ship.putTile(component3, new Position(2, 2));
        if(component4 != null) ship.putTile(component4, new Position(2, 1));
        if(component2 != null) ship.putTile(component2, new Position(5, 2));
        if(doubleCannon != null) ship.putTile(doubleCannon, new Position(2, 0));
        if(cannon3 != null) ship.putTile(cannon3, new Position(4, 2));


        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(battery2!= null) ship.putTile(battery2, new Position(2, 4));
        if(battery6!=null) ship.putTile(battery6, new Position(1, 2));


        return ship;


    }
    public static Ship createMockShip_CombatZone() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile genericCargoHold2 = getRequiredTile(tiles, "GenericCargoHolds", 1);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(3, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));
        if(genericCargoHold2 != null) ship.putTile(genericCargoHold2, new Position(2, 4));

        assert genericCargoHold != null;
        GenericCargoHolds hold1 = (GenericCargoHolds) genericCargoHold.getMyComponent();
        assert genericCargoHold2 != null;
        GenericCargoHolds hold2 = (GenericCargoHolds) genericCargoHold2.getMyComponent();

        Good good1 = new Good(Color.RED);
        Good  good2 = new Good(Color.GREEN);
        Good  good3 = new Good(Color.BLUE);
        Good  good4 = new Good(Color.YELLOW);

        hold1.playerLoadGood(good1);
        hold1.playerLoadGood(good2);
        hold2.playerLoadGood(good3);
        hold2.playerLoadGood(good4);


        return ship;
    }


    public static Ship createMockShip3() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        cannon.rotate(180);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(2, 4));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;
    }

    public static Ship createMockShip4() {
        Ship ship = new Ship(false);
        Map<String, List<Tile>> tiles = MockShipFactory.getAllClonedTiles();


        Tile cannon = getRequiredTile(tiles, "Cannon", 0);
        cannon.rotate(270);
        Tile engine = getRequiredTile(tiles, "Engine", 10);
        Tile battery = getRequiredTile(tiles, "BatterySlot", 0);
        Tile centralHousing = getRequiredTile(tiles, "CentralHousingUnit", 0);
        Tile modularHousingUnit = getRequiredTile(tiles, "ModularHousingUnit", 0);
        Tile modularHousingUnit2 = getRequiredTile(tiles, "ModularHousingUnit", 1);
        Tile genericCargoHold = getRequiredTile(tiles, "GenericCargoHolds", 0);
        Tile brownLifeSupport = getRequiredTile(tiles, "BrownLifeSupportSystem", 0);
        Tile purpleLifeSupport = getRequiredTile(tiles, "PurpleLifeSupportSystem", 0);
        Tile doubleEngine = getRequiredTile(tiles, "DoubleEngine", 8);
        Tile doubleCannon = getRequiredTile(tiles, "DoubleCannon", 0);

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        ModularHousingUnit mhu2;
        if(modularHousingUnit2 != null) {
            mhu2 = (ModularHousingUnit) modularHousingUnit2.getMyComponent();
            mhu2.addHumanCrew();
            modularHousingUnit2.setMyComponent(mhu2);
        }

        if(centralHousing != null) ship.putTile(centralHousing, new Position(3, 2));
        if(battery != null) ship.putTile(battery, new Position(2, 2));
        if(doubleEngine != null) ship.putTile(doubleEngine, new Position(3, 3));
        if(cannon != null) ship.putTile(cannon, new Position(1, 1));
        if(modularHousingUnit != null) ship.putTile(modularHousingUnit, new Position(2, 1));
        if(modularHousingUnit2 != null) ship.putTile(modularHousingUnit2, new Position(4, 2));
        if(genericCargoHold != null) ship.putTile(genericCargoHold, new Position(2, 3));

        return ship;
    }
}
