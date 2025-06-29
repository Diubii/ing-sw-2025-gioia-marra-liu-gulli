package it.polimi.ingsw.galaxytrucker.model;


import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the collection of tiles used during the building phase of the game.
 * It initializes all tiles and provides functionality for players to draw and return tiles.
 */
public class TileBunch {
    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * The list of tiles available for drawing.
     */
    private ArrayList<Tile> tiles;
    /**
     * The list of face-up tiles available for selection.
     */
    private ArrayList<Tile> faceUpTiles;

    Random rand = new Random();

    public TileBunch() {
        this.faceUpTiles = new ArrayList<>();
        try {
            tiles = Util.generateTiles();
            ArrayList<Integer> centralCabIds = new ArrayList<Integer>();
            centralCabIds.add(33);
            centralCabIds.add(34);
            centralCabIds.add(52);
            centralCabIds.add(61);
            tiles.removeIf(tile -> centralCabIds.contains(tile.getId()));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
//        getFaceUpTiles();
    }


    public synchronized ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }
    /**
     * Draws and removes a face-up tile by its ID.
     *
     * @param id the tile ID to draw
     * @return the tile if found, or null
     */
    public synchronized Tile drawFaceUpTile(int id) {
        if (!faceUpTiles.isEmpty()) {
            for (Tile tile : faceUpTiles) {
                if (tile.getId() == id) {
                    faceUpTiles.remove(tile);
                    return tile;
                }
            }
        }
        return null;
    }


    /**
     * Draws a random tile from the hidden pile,
     * flips it, removes it from the list, and returns it.
     */
    public synchronized Tile drawTile() {
        if (!tiles.isEmpty()) {
            int randomIndex = rand.nextInt(tiles.size());
            Tile tile = tiles.get(randomIndex);
            tile.flip();

            tiles.remove(randomIndex);
            return tile;
        }
        return null;

    }


    /**
     * Returns a tile to the face-up pile.
     *
     * @param tile the tile to return
     */
    public synchronized void returnTile(Tile tile) {
        faceUpTiles.add(tile);
    }


    public synchronized int getRemainingTiles() {
        return tiles.size();
    }
}
