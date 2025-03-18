package org.polimi.ingsw.galaxytrucker.model;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;

import java.util.ArrayList;
import java.util.Set;

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

    private Set<Position> storagePos;
    private Set<Position> redStoragePos;
    private Set<Position> housingPos;
    private Set<Position> batteryPos;
    private Set<Position> cannonPos;
    private Set<Position> enginePos;
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
                shipBoard[i][j] = new Slot(new Position(i, j));
            }
        }
    }


    // GETTERS START ----------------------
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
    // GETTERS END ----------------------


    public ArrayList<Position> getInvalidPositions() {
        return invalidPositions;
    }

    //carcamento da FILE?
    public ArrayList<Position> createIP(){
        invalidPositions = new ArrayList<>();
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
            shipBoard[pos.getY()][pos.getX()] = new Slot(pos);
        }

        updateSets(pos, tile);
        shipBoard[pos.getY()][pos.getX()].putTile(tile);
        //da aggiungere la logica che controlla che Tile e' stat inserita e l'aggiornamento delle varie pos
    }

    public void updateSets(Position pos, Tile tile){
        ComponentNameVisitor visitor = new ComponentNameVisitor();

//LOGICA PER AGGIORNARE LE POSIZIONI AL PRIMO INSERIMENTO
        switch (tile.getMyComponent().accept(visitor)){
            case "BatterySlot": batteryPos.add(pos); break;
            case "CannonSlot": cannonPos.add(pos); break;
            case "EngineSlot": enginePos.add(pos); break;
            case "ModularHousingUnit": housingPos.add(pos); break;
            case "GenericCargoHolds": {
                GenericCargoHolds test = (GenericCargoHolds) tile.getMyComponent();
                Boolean s = test.isSpecial();
                if (s){
                    redStoragePos.add(pos);
                }else storagePos.add(pos);
            }
        }

    }

    public void removeTile(Tile tile, Position pos, Slot slot){
        ComponentNameVisitor visitor = new ComponentNameVisitor();

//LOGICA PER AGGIORNARE LE POSIZIONI AL PRIMO INSERIMENTO
        switch (tile.getMyComponent().accept(visitor)){
            case "BatterySlot": batteryPos.remove(pos); break;
            case "CannonSlot": cannonPos.remove(pos); break;
            case "EngineSlot": enginePos.remove(pos); break;
            case "ModularHousingUnit": housingPos.remove(pos); break;
            case "GenericCargoHolds": {
                GenericCargoHolds test = (GenericCargoHolds) tile.getMyComponent();
                Boolean s = test.isSpecial();
                if (s){
                    redStoragePos.remove(pos);
                }else storagePos.remove(pos);
            }
        }

        slot.putTile(null);
    }

    public void calcExposedConnectors(){
        int tempSum;
        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 5; j++){
                ArrayList<Position> validPos = new ArrayList<Position>();
                Position tempPos = new Position(i, j);
                Tile myTile = shipBoard[i][j].getTile();
                //calculate neihbours
                Position nord = new Position(i-1, j);
                Position sud = new Position(i+1, j);
                Position est = new Position(i, j+1);
                Position ovest = new Position(i, j-1);

                //NORD
                if (myTile.getSides().get(0) != Connector.EMPTY ){
                    if (!invalidPositions.contains(nord)) {
                        Tile tempTile = shipBoard[nord.getX()][nord.getY()].getTile();
                        if (tempTile == null){
                            nExposedConnector++;
                        }
                    }else nExposedConnector++;

                }
                //OVEST
                if (myTile.getSides().get(1) != Connector.EMPTY ){
                    if (!invalidPositions.contains(ovest)) {
                        Tile tempTile = shipBoard[ovest.getX()][ovest.getY()].getTile();
                        if (tempTile == null){
                            nExposedConnector++;
                        }
                    }else nExposedConnector++;

                }
                //SUD
                if (myTile.getSides().get(2) != Connector.EMPTY ){
                    if (!invalidPositions.contains(sud)) {
                        Tile tempTile = shipBoard[sud.getX()][sud.getY()].getTile();
                        if (tempTile == null){
                            nExposedConnector++;
                        }
                    }else nExposedConnector++;

                }
                //EST
                if (myTile.getSides().get(3) != Connector.EMPTY ){
                    if (!invalidPositions.contains(est)) {
                        Tile tempTile = shipBoard[est.getX()][est.getY()].getTile();
                        if (tempTile == null){
                            nExposedConnector++;
                        }
                    }else nExposedConnector++;

                }


            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {
                    sb.append("[1] "); // Slot con Tile
                } else {
                    sb.append("[ ] "); // Slot vuoto
                }
            }
            sb.append("\n"); // Vai a capo dopo ogni riga
        }

        return sb.toString();
    }


}
