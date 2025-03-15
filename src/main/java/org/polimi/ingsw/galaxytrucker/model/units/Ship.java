package org.polimi.ingsw.galaxytrucker.model.units;

import javafx.util.Pair;

import java.util.ArrayList;

public class Ship {

    private Slot[][] shipBoard = new Slot[7][5];
    private Slot[] setAsideTiles = new Slot[2];
    private int nExposedConnector;
    private int destroyedTiles;
    private  int nBatterieLeft;
    private int nCrew;
    private Boolean purpleAlien;
    private Boolean brownAlien;
    private int nGoods;
    private ArrayList<Pair<Good, Pair<Position, Slot>>> listOfGoods;
    private ArrayList<Good> listNotLoadedGoods;

    private Boolean learningMatch;

    private ArrayList<Position> storagePos;
    private ArrayList<Position> redStoragePos;
    private ArrayList<Position> housingPos;
    private ArrayList<Position> batteryPos;
    private ArrayList<Position> cannonPos;
    private ArrayList<Position> enginePos;
    private ArrayList<Position> invalidPositions;

    public Ship(Boolean learningMatch) {
        listNotLoadedGoods = new ArrayList<>();
        listOfGoods = new ArrayList<>();
        this.learningMatch = learningMatch;
        invalidPositions = createIP();
        generateSlot();

    }

    public void generateSlot(){
        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 5; j++){
                shipBoard[i][j] = new Slot(new Position(i, j), this);
            }
        }
    }

    public Slot[][] getShipBoard(){
        return shipBoard.clone();
    }

    public ArrayList<Position> getStoragePos(){
        return new ArrayList<Position>(storagePos);
    }
    public ArrayList<Position> getRedStoragePos()
    {
        return new ArrayList<Position>(redStoragePos);
    }

    public ArrayList<Position> getHousingPos(){
        return new ArrayList<Position>(housingPos);
    }

    public ArrayList<Position> getBatteryPos(){
        return new ArrayList<Position>(batteryPos);
    }

    public ArrayList<Position> getCannonPos(){
        return new ArrayList<Position>(cannonPos);
    }

    public ArrayList<Position> getEnginePos(){
        return new ArrayList<Position>(enginePos);
    }

    public int getnExposedConnector(){
        return nExposedConnector;
    }

    public int getnBatterieLeft(){
        return nBatterieLeft;
    }

    public int getDestroyedTiles(){
        return destroyedTiles;
    }

    public int getnGoods(){
        return nGoods;
    }

    public Boolean getPurpleAlien(){
        return purpleAlien;
    }

    public Boolean getBrownAlien(){
        return brownAlien;
    }

    public Boolean getLearningMatch(){
        return learningMatch;
    }

    public int getnCrew(){
        return nCrew;
    }


    public ArrayList<Position> getInvalidPositions() {
        return invalidPositions;
    }

    public ArrayList<Position> createIP(){
        if (learningMatch){
            invalidPositions.add(new Position(0,0));
            invalidPositions.add(new Position(0,1));
            invalidPositions.add(new Position(0,2));
            invalidPositions.add(new Position(0,6));
            invalidPositions.add(new Position(0,5));
            invalidPositions.add(new Position(0,4));
            //-------------------------------------------
            invalidPositions.add(new Position(1,1));
            invalidPositions.add(new Position(1,0));
            invalidPositions.add(new Position(1,5));
            invalidPositions.add(new Position(1,6));
            //-------------------------------------------

            invalidPositions.add(new Position(2,6));
            invalidPositions.add(new Position(2,0));
            //-------------------------------------------
            invalidPositions.add(new Position(3,0));
            invalidPositions.add(new Position(3,6));

            invalidPositions.add(new Position(4,0));
            invalidPositions.add(new Position(4,6));
            invalidPositions.add(new Position(4,3));

        }else {
            invalidPositions.add(new Position(0,0));
            invalidPositions.add(new Position(0,1));
            invalidPositions.add(new Position(0,3));
            invalidPositions.add(new Position(0,6));
            invalidPositions.add(new Position(0,5));
            //-------------------------------------------
            invalidPositions.add(new Position(1,0));
            invalidPositions.add(new Position(1,6));
            //------------------------------------------
            invalidPositions.add(new Position(4,3));
        }

        return new ArrayList<Position>(invalidPositions);
    }

    public void putTile(Tile tile, Position pos){
        if (shipBoard[pos.getY()][pos.getX()] == null){
            shipBoard[pos.getY()][pos.getX()] = new Slot(pos, this);
        }
        shipBoard[pos.getY()][pos.getX()].putTile(tile);
    }


}
