package org.polimi.ingsw.galaxytrucker.model.units;


import java.util.ArrayList;
import java.util.Random;

/**
 * is mainly used during the building phase.
 * Initialize all tiles in the tile bunch
 * and provide support for players to draw tiles.
 */
public class TileBunch {
    private ArrayList<Tile> tiles;
    private ArrayList<Tile>  faceUpTiles;
    Random rand = new Random();

    public TileBunch() {
        this.faceUpTiles = new ArrayList<>();
        this.tiles = new ArrayList<>();
        generateTiles();
        getFaceUpTiles();
    }


    /**
     * Initialize all tiles and
     * store them in tiles at the end.
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
