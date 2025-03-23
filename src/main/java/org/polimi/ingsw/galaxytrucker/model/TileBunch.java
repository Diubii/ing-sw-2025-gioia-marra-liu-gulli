package org.polimi.ingsw.galaxytrucker.model;


import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the collection of tiles used during the building phase of the game.
 * It initializes all tiles and provides functionality for players to draw and return tiles.
 */
public class TileBunch {
    /** The list of tiles available for drawing. */
    private ArrayList<Tile> tiles;
    /** The list of face-up tiles available for selection. */
    private ArrayList<Tile>  faceUpTiles;

    Random rand = new Random();

    public TileBunch() {
        this.faceUpTiles = new ArrayList<>();
        this.tiles = new ArrayList<>();
        generateTiles();
        getFaceUpTiles();
    }

    /**
     * Generates and initializes all tiles in the game.
     * The tiles are stored in the {@code tiles} list.
     */
    private void generateTiles() {
        int tiles_size = 152;
        for (int i = 0; i < tiles_size; i++) {
            int randomRotation = rand.nextInt(4)*90;
            //tiles.add(new Tile(i, randomRotation));
        }
    }

    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }

    public Tile drawFaceUpTile(int id) {
        if (!faceUpTiles.isEmpty()) {
            for (Tile tile : faceUpTiles) {
                if (tile.getId()== id) {
                    return tile;
                }
            }
        }
        return null;
    }


    public Tile drawTile() {
        if (!tiles.isEmpty()) {
            int randomIndex = rand.nextInt(tiles.size());
            Tile tile = tiles.get(randomIndex);
            tile.flip();
            faceUpTiles.add(tile);
            return tile;
        }
            return null;

    }


    public void removeTile(Tile tile){
        faceUpTiles.remove(tile);
        tiles.remove(tile);
    }

    public void returnTile(Tile tile) {
        faceUpTiles.add(tile);
    }


    public int  getRemainingTiles(){
        return tiles.size();
    }
}
