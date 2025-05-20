package org.polimi.ingsw.galaxytrucker.model.essentials;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitorInterface;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MockShipFactory {

    public static List<Tile> loadAllTiles() throws IOException, IOException {
        File file = new File("src/main/resources/tiledata.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, new TypeReference<>() {});
    }

    public static List<Tile> findTilesByComponentType(String componentType) throws IOException {
        List<Tile> allTiles = loadAllTiles();
        return allTiles.stream()
                .filter(t -> t.getMyComponent() != null &&
                        componentType.equalsIgnoreCase(t.getMyComponent().accept(new ComponentNameVisitor())))
                .toList();
    }


    public static Ship createMockShip() throws IOException {

        Ship ship = new Ship(false);


        List<Tile> centralHousings = findTilesByComponentType("CentralHousingUnit");
        List<Tile> batteries = findTilesByComponentType("BatterySlot");
        List<Tile> engines = findTilesByComponentType("Engine");
        List<Tile> cannons = findTilesByComponentType("Cannon");
        List<Tile> modularHousingUnits = findTilesByComponentType("ModularHousingUnit");
        List<Tile> doubleEngines = findTilesByComponentType("DoubleEngine");

        Tile cannon = cannons.get(0);
        Tile engine = engines.get(10);
        Tile battery = batteries.get(0);
        Tile centralHousing = centralHousings.get(0);
        Tile modularHousingUnit = modularHousingUnits.get(0);
        Tile doubleEngine = doubleEngines.get(0);
        ModularHousingUnit mhu =(ModularHousingUnit) modularHousingUnit.getMyComponent();
        mhu.addHumanCrew();
        modularHousingUnit.setMyComponent(mhu);
        Tile modularHousingUnit2 = modularHousingUnits.get(1);
        ModularHousingUnit mhu2 =(ModularHousingUnit) modularHousingUnit2.getMyComponent();
        mhu2.addHumanCrew();
        modularHousingUnit2.setMyComponent(mhu2);


        try {
            ship.putTile(centralHousing, new Position(3, 2));
            ship.putTile(battery, new Position(2, 2));
            ship.putTile(doubleEngine, new Position(3, 3));
            ship.putTile(cannon, new Position(3, 1));
            ship.putTile(modularHousingUnit, new Position(2, 1));
            ship.putTile(modularHousingUnit2, new Position(4, 2));

        } catch (InvalidTilePosition e) {
            System.err.println("Placement failed: " + e.getMessage());
        }

        return ship;


    }



}
