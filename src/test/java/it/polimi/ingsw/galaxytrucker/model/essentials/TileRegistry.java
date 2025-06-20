package it.polimi.ingsw.galaxytrucker.model.essentials;

import it.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileRegistry {

    private static final Map<String, List<Tile>> componentTypeMap = new HashMap<>();

    static {
        List<Tile> allTiles = MockShipFactory.loadAllTiles();


        for (Tile tile : allTiles) {
            if (tile.getMyComponent() == null) continue;
            String type = tile.getMyComponent().accept(new ComponentNameVisitor());

            componentTypeMap.computeIfAbsent(type, k -> new ArrayList<>()).add(tile);
        }
    }

    /**
     * Gets a mapping of componentType → List of tiles.
     */
    public static Map<String, List<Tile>> getTileMap() {
        return componentTypeMap;
    }

    /**
     * Gets one tile by type, or null if not found.
     */
    public static Tile getFirstTileOfType(String type) {
        List<Tile> list = componentTypeMap.get(type);
        return (list != null && !list.isEmpty()) ? list.getFirst() : null;
    }

    /**
     * Gets a fresh copy of the tiles for safe mutation.
     */
    public static List<Tile> getClonedTilesOfType(String type) {
        List<Tile> original = componentTypeMap.get(type);
        if (original == null || original.isEmpty()) {
            throw new IllegalStateException("No tiles found for component type: " + type);
        }
        return original.stream().map(Tile::clone).toList();
    }
}