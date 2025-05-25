package org.polimi.ingsw.galaxytrucker.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.TileRegistry;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

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

    private static void addCrewToHousingUnit(Tile tile) {
        if (tile != null && tile.getMyComponent() instanceof ModularHousingUnit) {
            ModularHousingUnit unit = (ModularHousingUnit) tile.getMyComponent();
            unit.addHumanCrew();
            tile.setMyComponent(unit);
        }
    }


}
