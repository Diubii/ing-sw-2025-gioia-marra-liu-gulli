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
import java.util.ArrayList;
import java.util.List;

public class MockShipFactory {

    public static List<Tile> loadAllTiles() {
        File file = new File("src/main/resources/tiledata.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Tile> result = new ArrayList<>();
        try{
            result = mapper.readValue(file, new TypeReference<>() {});
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }

        return result;
    }

    public static List<Tile> findTilesByComponentType(String componentType) {
        List<Tile> allTiles = loadAllTiles();
        if(allTiles.isEmpty()) return allTiles;
        else return allTiles.stream()
                .filter(t -> t.getMyComponent() != null &&
                        componentType.equalsIgnoreCase(t.getMyComponent().accept(new ComponentNameVisitor())))
                .toList();
    }


    public static Ship createMockShip() {
        Ship ship = new Ship(false);

        List<Tile> centralHousings = findTilesByComponentType("CentralHousingUnit");
        List<Tile> batteries = findTilesByComponentType("BatterySlot");
        List<Tile> engines = findTilesByComponentType("Engine");
        List<Tile> cannons = findTilesByComponentType("Cannon");
        List<Tile> modularHousingUnits = findTilesByComponentType("ModularHousingUnit");
        List<Tile> doubleEngines = findTilesByComponentType("DoubleEngine");

        Tile cannon = !cannons.isEmpty() ? cannons.getFirst() : null;
        Tile engine = !engines.isEmpty() ? engines.get(10) : null;
        Tile battery = !batteries.isEmpty() ? batteries.getFirst() : null;
        Tile centralHousing = !centralHousings.isEmpty() ? centralHousings.getFirst() : null;
        Tile modularHousingUnit = !modularHousingUnits.isEmpty() ? modularHousingUnits.getFirst() : null;
        Tile doubleEngine = !doubleEngines.isEmpty() ? doubleEngines.getFirst() : null;

        ModularHousingUnit mhu;
        if(modularHousingUnit != null) {
            mhu = (ModularHousingUnit) modularHousingUnit.getMyComponent();
            mhu.addHumanCrew();
            modularHousingUnit.setMyComponent(mhu);
        }

        Tile modularHousingUnit2 = modularHousingUnits.size() > 1 ? modularHousingUnits.get(1) : null;
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

        return ship;
    }



}
