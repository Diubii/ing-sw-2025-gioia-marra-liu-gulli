package org.polimi.ingsw.galaxytrucker.model;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Rappresenta la nave del giocatore, composta da una griglia di {@link Slot}
 * in cui vengono posizionate le {@link Tile}. La nave può contenere vari componenti
 * come motori, batterie, cannoni e stive di carico.
 */

public class Ship implements Serializable {

    @Serial
    private static final long serialVersionUID = 35856L;

    private Slot[][] shipBoard = new Slot[5][7];
    private final Slot[] setAsideTiles = new Slot[2];
    private int nExposedConnector;
    private int destroyedTiles;
    private int nBatterieLeft;
    private int nCrew;
    private int purpleAlien;
    private int brownAlien;
    private int nGoods;
    private ArrayList<Pair<Good, Pair<Position, Slot>>> listOfGoods;
    private ArrayList<Good> listNotLoadedGoods;
    private Tile lastTile;
    private  Boolean synch;


    private Boolean learningMatch;

    private Set<Position> storagePos;
    private Set<Position> redStoragePos;
    private Set<Position> housingPos;
    private Set<Position> batteryPos;
    private Set<Position> cannonPos;
    private Set<Position> lssPos;
    private Set<Position> enginePos = new LinkedHashSet<>();
    private ArrayList<Position> invalidPositions;
    public Queue<Position> brokenPositions = new LinkedList<>();


    /**
     * Costruttore della nave.
     *
     * @param learningMatch Indica se la partita è in modalità apprendimento.
     */
    public Ship(Boolean learningMatch) {
        listNotLoadedGoods = new ArrayList<>();
        listOfGoods = new ArrayList<>();
        this.learningMatch = learningMatch;
        invalidPositions = createIP();
        generateSlot();
        initializePos();
        //TIle of chosen color

//        putTile();

    }

    /**
     * Inizializza la griglia della nave creando gli {@link Slot} vuoti.
     */
    public void generateSlot() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                shipBoard[i][j] = new Slot(new Position(i, j));
            }
        }
    }

    public void initializePos() {
        enginePos = new LinkedHashSet<>();
        batteryPos = new LinkedHashSet<>();
        cannonPos = new LinkedHashSet<>();
        redStoragePos = new LinkedHashSet<>();
        housingPos = new LinkedHashSet<>();
        storagePos = new LinkedHashSet<>();
        lssPos = new LinkedHashSet<>();

    }


    // GETTERS START ----------------------

    /**
     * Restituisce la board della nave.
     *
     * @return Una matrice di {@link Slot} rappresentante la nave.
     */
    public Slot[][] getShipBoard() {
        return shipBoard;
    }

    public void updateShipBoard(Slot[][] shipB) {
        synch = true;
        this.shipBoard = shipB;
    }

    public ArrayList<Position> getStoragePos() {
        return new ArrayList<Position>(storagePos);
    }

    public ArrayList<Position> getRedStoragePos() {
        return new ArrayList<Position>(redStoragePos);
    }

    public ArrayList<Position> getHousingPos() {
        return new ArrayList<Position>(housingPos);
    }

    public ArrayList<Position> getBatteryPos() {
        return new ArrayList<Position>(batteryPos);
    }

    public ArrayList<Position> getCannonPos() {
        return new ArrayList<Position>(cannonPos);
    }

    public ArrayList<Position> getEnginePos() {
        return new ArrayList<Position>(enginePos);
    }

    public ArrayList<Position> getLifeSSPos() {
        return new ArrayList<Position>(lssPos);
    }


    public int getnExposedConnector() {
        return nExposedConnector;
    }

    public int getnBatterieLeft() {
        return nBatterieLeft;
    }

    public int getDestroyedTiles() {
        return destroyedTiles;
    }

    public int getnGoods() {
        return nGoods;
    }

    public int getNPurpleAlien() {
        return purpleAlien;
    }

    public int getNBrownAlien() {
        return brownAlien;
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    public int getnCrew() {
        return nCrew;
    }

    public Boolean getSynch(){
        return synch;
    }

    public void setSynch(Boolean synch) {
        this.synch = synch;
    }

    // GETTERS END ----------------------


    public ArrayList<Position> getInvalidPositions() {
        return invalidPositions;
    }
    // GETTERS END ----------------------


    /**
     * Crea la lista delle posizioni invalide sulla nave in base alla modalità di gioco.
     *
     * @return Lista delle posizioni invalide.
     */
    public ArrayList<Position> createIP() {
        invalidPositions = new ArrayList<>();
        if (learningMatch) {
            invalidPositions.add(new Position(0, 0));
            invalidPositions.add(new Position(0, 1));
            invalidPositions.add(new Position(0, 2));
            invalidPositions.add(new Position(0, 6));
            invalidPositions.add(new Position(0, 5));
            invalidPositions.add(new Position(0, 4));
            //-------------------------------------------
            invalidPositions.add(new Position(1, 1));
            invalidPositions.add(new Position(1, 0));
            invalidPositions.add(new Position(1, 5));
            invalidPositions.add(new Position(1, 6));
            //-------------------------------------------

            invalidPositions.add(new Position(2, 6));
            invalidPositions.add(new Position(2, 0));
            //-------------------------------------------
            invalidPositions.add(new Position(3, 0));
            invalidPositions.add(new Position(3, 6));

            invalidPositions.add(new Position(4, 0));
            invalidPositions.add(new Position(4, 6));
            invalidPositions.add(new Position(4, 3));

        } else {
            invalidPositions.add(new Position(0, 0));
            invalidPositions.add(new Position(0, 1));
            invalidPositions.add(new Position(0, 3));
            invalidPositions.add(new Position(0, 6));
            invalidPositions.add(new Position(0, 5));
            //-------------------------------------------
            invalidPositions.add(new Position(1, 0));
            invalidPositions.add(new Position(1, 6));
            //------------------------------------------
            invalidPositions.add(new Position(4, 3));
        }

        return new ArrayList<Position>(invalidPositions);
    }

    /**
     * Inserisce una tile in una posizione specifica della nave.
     *
     * @param tile La tile da posizionare.
     * @param pos  La posizione in cui posizionare la tile.
     */
    public void putTile(Tile tile, Position pos) throws InvalidTilePosition {


        if (Util.inBoundaries(pos.getY(), pos.getX()) && !invalidPositions.contains(pos)) {


            if (shipBoard[pos.getY()][pos.getX()] == null) {
                shipBoard[pos.getY()][pos.getX()] = new Slot(pos);
            }

            try {
                shipBoard[pos.getY()][pos.getX()].putTile(tile);
                updateSets(pos, tile);
            } catch (InvalidTilePosition e) {
                System.out.println(e.getMessage());
            }


        }
    }

    /**
     * Aggiorna le posizioni dei componenti all'interno della nave.
     *
     * @param pos  La posizione della tile inserita.
     * @param tile La tile inserita.
     */
    public void updateSets(Position pos, Tile tile) {
        ComponentNameVisitor visitor = new ComponentNameVisitor();

//LOGICA PER AGGIORNARE LE POSIZIONI AL PRIMO INSERIMENTO
        switch (tile.getMyComponent().accept(visitor)) {
            case "BatterySlot":
                batteryPos.add(pos);
                break;
            case "CannonSlot":
                cannonPos.add(pos);
                break;
            case "EngineSlot":
                enginePos.add(pos);
                break;
            case "ModularHousingUnit":
                housingPos.add(pos);
                break;
            case "GenericCargoHolds": {
                GenericCargoHolds test = (GenericCargoHolds) tile.getMyComponent();
                Boolean s = test.isSpecial();
                if (s) {
                    redStoragePos.add(pos);
                } else storagePos.add(pos);
            }

            case "LifeSupportSystem":

                lssPos.add(pos);
                break;

        }

    }

    /**
     * Rimuove una tile dalla nave.
     *
     * @param tile La tile da rimuovere.
     * @param pos  La posizione della tile da rimuovere.
     */
    public void removeTile(Tile tile, Position pos, Boolean isNormalRemove) {
        ComponentNameVisitor visitor = new ComponentNameVisitor();

        if (tile != null && tile.getMyComponent() != null) {
            System.out.println("STO ELIMIMANDO" + pos.getY() + pos.getX());
//LOGICA PER AGGIORNARE LE POSIZIONI AL PRIMO INSERIMENTO
            switch (tile.getMyComponent().accept(visitor)) {
                case "BatterySlot":
                    batteryPos.remove(pos);
                    break;
                case "Cannon":
                    cannonPos.remove(pos);
                    break;
                case "Engine":
                    enginePos.remove(pos);
                    break;
                case "ModularHousingUnit":
                    housingPos.remove(pos);
                    break;
                case "GenericCargoHolds": {
                    GenericCargoHolds test = (GenericCargoHolds) tile.getMyComponent();
                    Boolean s = test.isSpecial();
                    if (s) {
                        redStoragePos.remove(pos);
                    } else storagePos.remove(pos);
                }

                case "LifeSupportSystem":
                    lssPos.remove(pos);
                    break;
            }

            //se sto distruggendo e non semplicemente eliminando le aggiungo alle broken
            if (!isNormalRemove) {
                brokenPositions.add(pos);
            }

            destroyedTiles++;
            getShipBoard()[pos.getY()][pos.getX()].removeTile();

        }

    }

    /**
     * Calcola il numero di connettori esposti nella nave.
     */
    public void calcExposedConnectors() {
        int tempSum;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                ArrayList<Position> validPos = new ArrayList<Position>();
                Position tempPos = new Position(i, j);

                if (shipBoard[i][j].getTile() != null) {
                    Tile myTile = shipBoard[i][j].getTile();
                    //calculate neihbours
                    Position nord = new Position(i - 1, j);
                    Position sud = new Position(i + 1, j);
                    Position est = new Position(i, j + 1);
                    Position ovest = new Position(i, j - 1);

                    //NORD
                    if (myTile.getSides().get(0) != Connector.EMPTY) {
                        if (!invalidPositions.contains(nord) && Util.inBoundaries(nord.getY(), nord.getX())) {
                            Tile tempTile = shipBoard[nord.getY()][nord.getX()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }
                    //OVEST
                    if (myTile.getSides().get(1) != Connector.EMPTY) {
                        if (!invalidPositions.contains(ovest) && Util.inBoundaries(ovest.getY(), ovest.getX())) {
                            Tile tempTile = shipBoard[ovest.getY()][ovest.getX()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }
                    //SUD
                    if (myTile.getSides().get(2) != Connector.EMPTY) {
                        if (!invalidPositions.contains(sud) && Util.inBoundaries(sud.getY(), sud.getX())) {
                            Tile tempTile = shipBoard[sud.getY()][sud.getX()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }
                    //EST
                    if (myTile.getSides().get(3) != Connector.EMPTY) {
                        if (!invalidPositions.contains(est) && Util.inBoundaries(est.getY(), est.getX())) {
                            Tile tempTile = shipBoard[est.getY()][est.getX()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }

                }
            }
        }

    }


    /**
     * Rappresentazione testuale della nave.
     *
     * @return Stringa che rappresenta la nave.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {

                    if (shipBoard[i][j].getTile().getWellConnected()) {

                        sb.append("[Y] ");// Slot con Tile

                    } else sb.append("[N] ");
//                    System.out.printf("FOUND %d %d\n", i, j);
                } else if (shipBoard[i][j] != null && shipBoard[i][j].getTile() == null) {
                    int finalJ = j;
                    int finalI = i;
                    if (invalidPositions.stream().anyMatch(pos -> pos.getX() == finalJ && pos.getY() == finalI)) {
                        sb.append("[X] ");
                    } else {
                        sb.append("[.] "); // Slot vuoto
//                        System.out.printf("%d %d [null]\n", i, j);

                    }
                } else if (shipBoard[i][j] == null) sb.append("[.] "); // Slot vuoto

            }
            sb.append("\n"); // Vai a capo dopo ogni riga
        }

        return sb.toString();
    }

    /**
     * Verifica se la nave è ben collegata e rispetta le regole di connessione.
     *
     * @return {@code true} se la nave è valida, {@code false} altrimenti.
     */
    public Boolean checkShip() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {
                    if (!Util.wellConnectedConnectors(this, shipBoard[i][j], shipBoard[i][j].getTile()).getKey()) {

                        shipBoard[i][j].getTile().setWellConnected(false);
                        return false;
                    } else {
                        System.out.println("CHECKING : " + shipBoard[i][j].getPosition().getY() + " " + shipBoard[i][j].getPosition().getX());
                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("Engine") || shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("DoubleEngine")) {
                            Boolean temp = Util.EngineWellConnected(shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                            shipBoard[i][j].getTile().setWellConnected(temp);
                            return temp;


                        }

                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("Cannon") || shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("DoubleCannon") ) {

                                Boolean temp = Util.CannonWellConnected(shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                                shipBoard[i][j].getTile().setWellConnected(temp);
                                return temp;
                            }

                            if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("ModularHousingUnit")) {

                            AlienColor al = null;
                            ModularHousingUnit temp = (ModularHousingUnit) shipBoard[i][j].getTile().getMyComponent();
                            if (temp.getNBrownAlien() > temp.getNPurpleAlien() && temp.getHumanCrewNumber() == 0)
                                al = AlienColor.BROWN;
                            if (temp.getNPurpleAlien() > temp.getNBrownAlien() && temp.getHumanCrewNumber() == 0)
                                al = AlienColor.PURPLE;

                            if (al != null) {
                                Boolean result = Util.CheckLifeSupportSystem(al, shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                                if (!result) {
                                    temp.removeAlienCrew();
                                }

                                shipBoard[i][j].getTile().setWellConnected(result);
                                return result;

                            }


                        }
                    }
                }
            }
        }

        return true;

    }

    public void setCrew(Boolean isAlien, AlienColor color, Tile tile) {
        ModularHousingUnit modularHousingUnit = (ModularHousingUnit) tile.getMyComponent();
        if (isAlien) {
            if (color == AlienColor.BROWN) {
                modularHousingUnit.addBrownAlien();
            } else modularHousingUnit.addPurpleAlien();
        } else {

            modularHousingUnit.addHumanCrew();
        }

    }


    public ArrayList<Ship> getTronc() throws InvalidTilePosition {


        ArrayList<Ship> tronconi = new ArrayList<>();
        tronconi.add(this);

        while (!brokenPositions.isEmpty()) {

            Position temp = brokenPositions.poll();


            ListIterator<Ship> iterator = tronconi.listIterator();
//        ArrayList<Slot[][]> targetSlot = new ArrayList<>();


            System.out.println("TRONCONI SIZE: " + tronconi.size());
            int size = tronconi.size();
            boolean bigger = true;

            for (int i = 0; i < tronconi.size() && bigger; i++) {
                ArrayList<Ship> toRemove = new ArrayList<>();
                ArrayList<Ship> toAdd = new ArrayList<>();


                Ship board = tronconi.get(i);

                List<Slot> Slots = Arrays.stream(board.getShipBoard())
                        .flatMap(Arrays::stream)
                        .filter(Objects::nonNull)
                        .toList();



                    for (Slot slot : Slots) {

                        if (slot.getPosition().equals(temp) && slot.getLastAction()) {
                            System.out.println("TILE DA RIM " + slot.getPosition().getY() + " " + slot.getPosition().getX());

                            toRemove.add(board); // Segna la board per la rimozione
                            toAdd.addAll(truncateShip(temp, brokenPositions)); // Aggiunge nuovi tronconi

//                            Ship t2 = toAdd.getLast();
//                            Ship tSh = new Ship(getLearningMatch());
//                            tSh.updateShipBoard(t2);
//                            System.out.println("GETTRONC + ADD : SIZE" + toAdd.size());
//                            System.out.println(tSh.toString());

                            break;
                        } else {
                            if (slot.getPosition().equals(temp)) System.out.println("EXTRA");

                        }
                    }


                // Rimuove gli elementi segnati
                tronconi.removeAll(toRemove);
                System.out.println("[1]NEW : SIZE" + tronconi.size());

                // Aggiunge i nuovi elementi
                tronconi.addAll(toAdd);
                System.out.println("[2]NEW : SIZE" + tronconi.size());

                if (size == tronconi.size()) {
                    bigger = false;
                }
                size = tronconi.size();
            }


        }
        //ho finito di processare le posizioni
        return tronconi;

    }


    public ArrayList<Ship> truncateShip(Position pos, Queue<Position> brokenPos) throws InvalidTilePosition {

        System.out.println("IF");

        checkShip();
        // Lista dei vicini validi della posizione corrente
        ArrayList<Pair<ProjectileDirection, Slot>> villagers = new ArrayList<>();

        // Controlla se ci sono slot validi sopra, sinistra, sotto e destra della posizione attuale
        if (!invalidPositions.contains(new Position(pos.getY() - 1, pos.getX())) && Util.inBoundaries(pos.getY() - 1, pos.getX())) {
            System.out.println("VALIDA : " + new Position(pos.getY() - 1, pos.getX()).getY() + new Position(pos.getY() - 1, pos.getX()).getX());
            if (shipBoard[pos.getY() - 1][pos.getX()] != null && shipBoard[pos.getY() - 1][pos.getX()].getTile() != null) {
                System.out.println("1");

                if (shipBoard[pos.getY() - 1][pos.getX()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.FRONT, shipBoard[pos.getY() - 1][pos.getX()]));

                } else {
                    brokenPos.add(new Position(pos.getY() - 1, pos.getX()));

                }
            }

        }


        if (!invalidPositions.contains(new Position(pos.getY(), pos.getX() - 1)) && Util.inBoundaries(pos.getY(), pos.getX() - 1)) {
            System.out.println("VALIDA : " + new Position(pos.getY(), pos.getX() - 1).getY() + new Position(pos.getY(), pos.getX() - 1).getX());

            if (shipBoard[pos.getY()][pos.getX() - 1] != null && shipBoard[pos.getY()][pos.getX() - 1].getTile() != null) {
                System.out.println("1");

                if (shipBoard[pos.getY()][pos.getX() - 1].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.LEFT, shipBoard[pos.getY()][pos.getX() - 1]));

                } else {
                    brokenPos.add(new Position(pos.getY(), pos.getX() - 1));

                }
            }

        }

        if (!invalidPositions.contains(new Position(pos.getY() + 1, pos.getX())) && Util.inBoundaries(pos.getY() + 1, pos.getX())) {
            System.out.println("VALIDA : " + new Position(pos.getY() + 1, pos.getX()).getY() + new Position(pos.getY() + 1, pos.getX()).getX());

            if (shipBoard[pos.getY() + 1][pos.getX()] != null && shipBoard[pos.getY() + 1][pos.getX()].getTile() != null) {
                System.out.println("1");
                if (shipBoard[pos.getY() + 1][pos.getX()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.BOTTOM, shipBoard[pos.getY() + 1][pos.getX()]));

                } else {
                    brokenPos.add(new Position(pos.getY() + 1, pos.getX()));

                }
            }

        }

        if (!invalidPositions.contains(new Position(pos.getY(), pos.getX() + 1)) && Util.inBoundaries(pos.getY(), pos.getX() + 1)) {
            System.out.println("VALIDA : " + new Position(pos.getY(), pos.getX() + 1).getY() + new Position(pos.getY(), pos.getX() + 1).getX());
            if (shipBoard[pos.getY()][pos.getX() + 1] != null && shipBoard[pos.getY()][pos.getX() + 1].getTile() != null) {
                System.out.println("1");

                if (shipBoard[pos.getY()][pos.getX() + 1].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.RIGHT, shipBoard[pos.getY()][pos.getX() + 1]));

                } else {
                    brokenPos.add(new Position(pos.getY(), pos.getX() + 1));

                }
            }

        }


        // Se ci sono vicini validi, verifica le connessioni tra di loro
        if (!villagers.isEmpty()) {
            System.out.println("VILLAGERS SIZE: " + villagers.size());

            // Lista di nodi (tile ID e i loro collegamenti)
            ArrayList<Pair<Integer, ArrayList<Integer>>> Nodes = new ArrayList<>();
            ArrayList<Pair<Integer, ArrayList<Integer>>> nodeLinkedTiles = new ArrayList<>();

//                ArrayList<Integer> tilesVisitedId = new ArrayList<>();

            // Per ogni vicino, controlla la sua connessione con gli altri
            for (int i = 0; i < villagers.size(); i++) {

                // Crea un nodo per il villager attuale
                Nodes.add(new Pair<>(villagers.get(i).getValue().getTile().getId(), new ArrayList<>()));
                Tile myTile = villagers.get(i).getValue().getTile();

                // Lista delle tile raggiungibili da questo vicino
                ArrayList<Integer> tilesID = new ArrayList<>();

                // Esegue una visita per raccogliere tutte le tile raggiungibili
                Util.visitTile(myTile, tilesID, villagers.get(i).getValue(), invalidPositions, brokenPos, this);

                System.out.println("TILES CONNESSE A " + myTile.getId());
                for (Integer integer : tilesID) {
                    System.out.println("LIST [+]" + integer);
                }


                nodeLinkedTiles.add(new Pair<>(i, new ArrayList<>(tilesID)));

                // Controlla quali altri vicini sono raggiungibili da questo
                for (int j = i + 1; j < villagers.size(); j++) {
                    if (tilesID.contains(villagers.get(j).getValue().getTile().getId())) {
                        Nodes.get(i).getValue().add(villagers.get(j).getValue().getTile().getId());
//                            tilesVisitedId.add(villagers.get(j).getValue().getTile().getId());
                    }
                }
            }

            // Creazione delle classi di equivalenza (tronconi della nave)
            ArrayList<ArrayList<Integer>> equivalenceClasses = new ArrayList<>();
            for (int i = 0; i < Nodes.size(); i++) {
                final int finalI = i;

                if (equivalenceClasses.stream().noneMatch(list -> list.contains(Nodes.get(finalI).getKey()))) {

                    equivalenceClasses.add(nodeLinkedTiles.get(finalI).getValue());
                }
            }

            // Numero di tronconi distinti
            int numTronconi = equivalenceClasses.size();

            // Creazione delle nuove navi separate
            ArrayList<Ship> ships = new ArrayList<>(numTronconi);
            for (ArrayList<Integer> equivalenceClass : equivalenceClasses) {

                // Crea una nuova nave vuota
                Ship myShip = new Ship(this.learningMatch);
                Slot[][] myshipBoard = myShip.getShipBoard();

                for (int j = 0; j < 5; j++) {
                    for (int k = 0; k < 7; k++) {
                        // Se la tile appartiene alla classe di equivalenza, viene inclusa nella nuova nave
                        if (shipBoard[j][k].getTile() != null && equivalenceClass.contains(shipBoard[j][k].getTile().getId()) || !invalidPositions.contains(new Position(j, k))) {
//                            myshipBoard[j][k] = shipBoard[j][k];
                            myShip.putTile(shipBoard[j][k].getTile(), shipBoard[j][k].getPosition());
                        }
                    }
                }

                // Aggiunge la nuova nave alla lista delle navi separate
                ships.add(myShip);
            }

            return ships; // Restituisce l'elenco delle navi separate
        }


        ArrayList<Ship> finalShips = new ArrayList<>();
        finalShips.add(null);
        return finalShips; // Se non ci sono sezioni da separare, restituisce null
    }

    public Set<ProjectileDirection> getProtectedSides() {

        Set<ProjectileDirection> sides = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                Tile tempTile = shipBoard[i][j].getTile();
                Slot tempSlot = shipBoard[i][j];
                if (!invalidPositions.contains(tempSlot.getPosition()) && Util.inBoundaries(i, j)) {
                    if (tempTile != null) {
                        if (tempTile.getMyComponent().accept(new ComponentNameVisitor()).equals("Shield")) {

                            Shield tempShield = (Shield) tempTile.getMyComponent();
                            if (tempShield.getCharged()) sides.addAll(tempShield.getProtectedSides());

                        }
                    }
                }
            }
        }


        return sides;

    }

    public Boolean activateShield(Position shieldPos, Position batteryPos) {

        if (!invalidPositions.contains(shieldPos) && Util.inBoundaries(shieldPos.getY(), shieldPos.getX())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getY(), batteryPos.getX())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da pbatteryPos e carico shieldPos
                 *
                 */

                BatterySlot battery = (BatterySlot) shipBoard[batteryPos.getY()][batteryPos.getX()].getTile().getMyComponent();
                Shield shield = (Shield) shipBoard[shieldPos.getY()][shieldPos.getX()].getTile().getMyComponent();

                if (battery.getBatteriesLeft() > 0) {
                    battery.removeBattery();
                    shield.setCharged(true);
                    return true;
                }
            }

        }

        return false;
    }

    public Boolean activateDoubleEngine(Position enginePos, Position batteryPos) {

        if (!invalidPositions.contains(enginePos) && Util.inBoundaries(enginePos.getY(), enginePos.getX())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getY(), batteryPos.getX())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da pbatteryPos e carico enginePos
                 *
                 */

                BatterySlot battery = (BatterySlot) shipBoard[batteryPos.getY()][batteryPos.getX()].getTile().getMyComponent();
                DoubleEngine engine = (DoubleEngine) shipBoard[enginePos.getY()][enginePos.getX()].getTile().getMyComponent();

                if (battery.getBatteriesLeft() > 0) {
                    battery.removeBattery();
                    engine.setCharged(true);
                    return true;
                }
            }

        }

        return false;
    }

    public Boolean activateDoubleCannon(Position cannonPos, Position batteryPos) {

        if (!invalidPositions.contains(cannonPos) && Util.inBoundaries(cannonPos.getY(), cannonPos.getX())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getY(), batteryPos.getX())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da pbatteryPos e carico enginePos
                 *
                 */

                BatterySlot battery = (BatterySlot) shipBoard[batteryPos.getY()][batteryPos.getX()].getTile().getMyComponent();
                DoubleCannon cannon = (DoubleCannon) shipBoard[cannonPos.getY()][cannonPos.getX()].getTile().getMyComponent();

                if (battery.getBatteriesLeft() > 0) {
                    battery.removeBattery();
                    cannon.setCharged(true);
                    return true;
                }
            }

        }

        return false;
    }


    public Slot[] getSetAsideTiles() {
        return setAsideTiles;
    }


    public Tile getLastTile() {
        return lastTile;
    }

    public void setLastTile(Tile tile) {
        lastTile = tile;
    }
}
