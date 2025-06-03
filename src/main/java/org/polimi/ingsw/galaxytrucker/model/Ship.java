package org.polimi.ingsw.galaxytrucker.model;

import javafx.geometry.Pos;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Rappresenta la nave del giocatore, composta da una griglia di {@link Slot}
 * in cui vengono posizionate le {@link Tile}. La nave può contenere vari componenti
 * come motori, batterie, cannoni e stive di carico.
 */

public class Ship implements Serializable {

    @Serial
    private static final long serialVersionUID = 35856L;

    private final int shipboardMaxX=7;
    private final int shipboardMaxY=5;

    private Slot[][] shipBoard = new Slot[shipboardMaxX][shipboardMaxY];
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
    private Boolean synch;


    private Boolean learningMatch;

    private Set<Position> storagePos;
    private Set<Position> redStoragePos;
    private Set<Position> housingPos;
    private Set<Position> batteryPos;
    private Set<Position> cannonPos;
    private Set<Position> lssPos;
    private Set<Position> enginePos;

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
        //Tile of chosen color

//        putTile();

    }

    /**
     * Inizializza la griglia della nave creando gli {@link Slot} vuoti.
     */
    public void generateSlot() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
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


    // GETTERS Start ----------------------

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
        calcExposedConnectors();
        return nExposedConnector;
    }

    public int getnBatterieLeft() {
        ArrayList<Position> batteryPos = getComponentPositionsFromName("BatterySlot");
        int nBatterieLeft = 0;
        for (Position pos : batteryPos) {
            nBatterieLeft += ((BatterySlot) getComponentFromPosition(pos)).getBatteriesLeft();
        }
        return nBatterieLeft;
    }

    public int getDestroyedTiles() {
        return destroyedTiles;
    }

    public int getnGoods() {
        return nGoods;
    }

    public int getNPurpleAlien() {
        return (int) getComponentPositionsFromName("ModularHousingUnit").stream().filter(p -> ((ModularHousingUnit) shipBoard[p.getX()][p.getY()].getTile().getMyComponent()).getNPurpleAlien() > 0).count();
    }

    public int getNBrownAlien() {
        return (int) getComponentPositionsFromName("ModularHousingUnit").stream().filter(p -> ((ModularHousingUnit) shipBoard[p.getX()][p.getY()].getTile().getMyComponent()).getNBrownAlien() > 0).count();
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    public int getnCrew() {
        this.nCrew =
                getComponentPositionsFromName("ModularHousingUnit").stream()
                        .map(p -> ((ModularHousingUnit) shipBoard[p.getX()][p.getY()].getTile().getMyComponent()).getNCrewMembers())
                        .reduce(0, Integer::sum)
                        +
                        getComponentPositionsFromName("CentralHousingUnit").stream()
                                .map(p -> ((CentralHousingUnit) shipBoard[p.getX()][p.getY()].getTile().getMyComponent()).getNCrewMembers())
                                .reduce(0, Integer::sum);

        return this.nCrew;
    }

    public Boolean getSynch() {
        return synch;
    }

    public void setSynch(Boolean synch) {
        this.synch = synch;
    }

    // GETTERS End ----------------------


    public ArrayList<Position> getInvalidPositions() {
        return invalidPositions;
    }
    // GETTERS End ----------------------


    /**
     * Crea la lista delle posizioni invalide sulla nave in base alla modalità di gioco.
     *
     * @return Lista delle posizioni invalide.
     */
    public ArrayList<Position> createIP() {
        invalidPositions = new ArrayList<>();
        if (learningMatch) {
            invalidPositions.add(new Position(0, 0));
            invalidPositions.add(new Position(1, 0));
            invalidPositions.add(new Position(2, 0));
            invalidPositions.add(new Position(6, 0));
            invalidPositions.add(new Position(5, 0));
            invalidPositions.add(new Position(4, 0));
            //-------------------------------------------
            invalidPositions.add(new Position(1, 1));
            invalidPositions.add(new Position(0, 1));
            invalidPositions.add(new Position(5, 1));
            invalidPositions.add(new Position(6, 1));
            //-------------------------------------------

            invalidPositions.add(new Position(6, 2));
            invalidPositions.add(new Position(0, 2));
            //-------------------------------------------
            invalidPositions.add(new Position(0, 3));
            invalidPositions.add(new Position(6, 3));

            invalidPositions.add(new Position(0, 4));
            invalidPositions.add(new Position(6, 4));
            invalidPositions.add(new Position(3, 4));

        } else {
            invalidPositions.add(new Position(0, 0));
            invalidPositions.add(new Position(1, 0));
            invalidPositions.add(new Position(3, 0));
            invalidPositions.add(new Position(6, 0));
            invalidPositions.add(new Position(5, 0));
            //-------------------------------------------
            invalidPositions.add(new Position(0, 1));
            invalidPositions.add(new Position(6, 1));
            //------------------------------------------
            invalidPositions.add(new Position(3, 4));
        }

        return new ArrayList<Position>(invalidPositions);
    }

    /**
     * Inserisce una tile in una posizione specifica della nave.
     *
     * @param tile La tile da posizionare.
     * @param pos  La posizione in cui posizionare la tile.
     */
    public void putTile(Tile tile, Position pos) {


        if (Util.inBoundaries(pos.getX(), pos.getY()) && !invalidPositions.contains(pos)) {


            if (shipBoard[pos.getX()][pos.getY()] == null) {
                shipBoard[pos.getX()][pos.getY()] = new Slot(pos);
            }

            try {
                shipBoard[pos.getX()][pos.getY()].putTile(tile);
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
            case "Cannon":
                cannonPos.add(pos);
                break;
            case "Engine":
                enginePos.add(pos);
                break;
            case "DoubleCannon":
                cannonPos.add(pos);
                break;
            case "DoubleEngine":
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

                break;
            }

            case "LifeSupportSystem":
                lssPos.add(pos);
                break;

            case "Shield"     :

        }

    }

    /**
     * Rimuove una tile dalla nave.
     * // @param tile La tile da rimuovere.
     *
     * @param pos La posizione della tile da rimuovere.
     */
    public void removeTile(Position pos, Boolean isNormalRemove) {
        //ComponentNameVisitor visitor = new ComponentNameVisitor();
        //if (tile != null && tile.getMyComponent() != null) {
        System.out.println("STO ELIMIMANDO" + pos);
//LOGICA PER AGGIORNARE LE POSIZIONI AL PRIMO INSERIMENTO
//            switch (tile.getMyComponent().accept(visitor)) {
//                case "BatterySlot":
//                    batteryPos.remove(pos);
//                    break;
//                case "Cannon":
//                    cannonPos.remove(pos);
//                    break;
//                case "Engine":
//                    enginePos.remove(pos);
//                    break;
//                case "ModularHousingUnit":
//                    housingPos.remove(pos);
//                    break;
//                case "GenericCargoHolds": {
//                    GenericCargoHolds test = (GenericCargoHolds) tile.getMyComponent();
//                    Boolean s = test.isSpecial();
//                    if (s) {
//                        redStoragePos.remove(pos);
//                    } else storagePos.remove(pos);
//                }
//
//                case "LifeSupportSystem":
//                    lssPos.remove(pos);
//                    break;
//            }

        //se sto distruggendo e non semplicemente eliminando le aggiungo alle broken
        if (!isNormalRemove) {
            brokenPositions.add(pos);
        }

        Tile tileNeedToRemove = getTileFromPosition(pos);

        destroyedTiles++;
        shipBoard[pos.getX()][pos.getY()].removeTile();

    }

    //}

    /**
     * Calcola il numero di connettori esposti nella nave.
     */
    public void calcExposedConnectors() {
        nExposedConnector = 0;
        int tempSum;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                ArrayList<Position> validPos = new ArrayList<Position>();
                Position tempPos = new Position(i, j);

                if (shipBoard[i][j].getTile() != null) {
                    Tile myTile = shipBoard[i][j].getTile();
                    //calculate neighbours
                    Position nord = new Position(i, j - 1);
                    Position sud = new Position(i, j + 1);
                    Position est = new Position(i + 1, j);
                    Position ovest = new Position(i - 1, j);

                    //NORD
                    if (myTile.getSides().get(0) != Connector.EMPTY) {
                        if (!invalidPositions.contains(nord) && Util.inBoundaries(nord.getX(), nord.getY())) {
                            Tile tempTile = shipBoard[nord.getX()][nord.getY()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }
                    //EST
                    if (myTile.getSides().get(1) != Connector.EMPTY) {
                        if (!invalidPositions.contains(est) && Util.inBoundaries(est.getX(), est.getY())) {
                            Tile tempTile = shipBoard[est.getX()][est.getY()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }

                    //SUD
                    if (myTile.getSides().get(2) != Connector.EMPTY) {
                        if (!invalidPositions.contains(sud) && Util.inBoundaries(sud.getX(), sud.getY())) {
                            Tile tempTile = shipBoard[sud.getX()][sud.getY()].getTile();
                            if (tempTile == null) {
                                nExposedConnector++;
                            }
                        } else nExposedConnector++;

                    }
                    //OVEST
                    if (myTile.getSides().get(3) != Connector.EMPTY) {
                        if (!invalidPositions.contains(ovest) && Util.inBoundaries(ovest.getX(), ovest.getY())) {
                            Tile tempTile = shipBoard[ovest.getX()][ovest.getY()].getTile();
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

        for (int j = 0; j < 5; j++) {
             for (int i = 0; i < 7; i++) {

                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {

                    if (shipBoard[i][j].getTile().getWellConnected()) {

                        sb.append("[Y] ");// Slot con Tile

                    } else sb.append("[N] ");
//                    System.out.printf("FOUND %d %d\n", i, j);
                } else if (shipBoard[i][j] != null && shipBoard[i][j].getTile() == null) {
                    int finalJ = j;
                    int finalI = i;
                    if (invalidPositions.stream().anyMatch(pos -> pos.getX() == finalI && pos.getY() == finalJ)) {
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
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {
                    String name = shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor());

                    if (!Util.wellConnectedConnectors(this, shipBoard[i][j], shipBoard[i][j].getTile())) {

                        shipBoard[i][j].getTile().setWellConnected(false);
                        return false;
                    } else {
                        //System.out.println("CHECKING : " + shipBoard[i][j].getPosition());
                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("Engine") || shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("DoubleEngine")) {
                            Boolean temp = Util.EngineWellConnected(shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                            shipBoard[i][j].getTile().setWellConnected(temp);
                            return temp;


                        }

                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("Cannon") || shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("DoubleCannon")) {

                            Boolean temp = Util.CannonWellConnected(shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                            shipBoard[i][j].getTile().setWellConnected(temp);
                            return temp;
                        }

                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("ModularHousingUnit")) {

                            AlienColor al = null;
                            ModularHousingUnit temp = (ModularHousingUnit) shipBoard[i][j].getTile().getMyComponent();
                            if (temp.getNBrownAlien() > temp.getNPurpleAlien() && temp.getNCrewMembers() == 0)
                                al = AlienColor.BROWN;
                            if (temp.getNPurpleAlien() > temp.getNBrownAlien() && temp.getNCrewMembers() == 0)
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


    public ArrayList<Ship> getTronc() {
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
                        System.out.println("TILE DA RIM " + slot.getPosition());

                        toRemove.add(board); // Segna la board per la rimozione
                        try {
                            toAdd.addAll(truncateShip(temp, brokenPositions)); // Aggiunge nuovi tronconi
                        }
                        catch (InvalidTilePosition e){
                            System.err.println("Error while computing trunks: " + e.getMessage());
                        }

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

        Position up = new Position(pos.getX(), pos.getY() - 1);

        // Controlla se ci sono slot validi sopra, sinistra, sotto e destra della posizione attuale
        if (!invalidPositions.contains(up) && Util.inBoundaries(up.getX(), up.getY())) {
            System.out.println("VALIDA : " + up);
            if (shipBoard[up.getX()][up.getY()] != null && shipBoard[up.getX()][up.getY()].getTile() != null) {
                System.out.println("1");

                if (shipBoard[up.getX()][up.getY()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.UP, shipBoard[up.getX()][up.getY()]));

                } else {
                    brokenPos.add(up);

                }
            }

        }

        Position left = new Position(pos.getX() - 1, pos.getY());

        if (!invalidPositions.contains(left) && Util.inBoundaries(left.getX(), left.getY())) {
            System.out.println("VALIDA : " + left);

            if (shipBoard[left.getX()][left.getY()] != null && shipBoard[left.getX()][left.getY()].getTile() != null) {
                System.out.println("1");

                if (shipBoard[left.getX()][left.getY()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.LEFT, shipBoard[left.getX()][left.getY()]));

                } else {
                    brokenPos.add(left);

                }
            }

        }

        Position right = new Position(pos.getX() + 1, pos.getY());

        if (!invalidPositions.contains(right) && Util.inBoundaries(right.getX(), right.getY())) {
            System.out.println("VALIDA : " + right);

            if (shipBoard[right.getX()][right.getY()] != null && shipBoard[right.getX()][right.getY()].getTile() != null) {
                System.out.println("1");
                if (shipBoard[right.getX()][right.getX()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.DOWN, shipBoard[right.getX()][right.getY()]));

                } else {
                    brokenPos.add(right);

                }
            }

        }

        Position down = new Position(pos.getX(), pos.getY() + 1);

        if (!invalidPositions.contains(down) && Util.inBoundaries(down.getX(), down.getY())) {
            System.out.println("VALIDA : " + down);
            if (shipBoard[down.getX()][down.getY()] != null && shipBoard[down.getX()][down.getY()].getTile() != null) {
                System.out.println("1");

                if (shipBoard[down.getX()][down.getY()].getTile().getWellConnected()) {
                    System.out.println("OK");

                    villagers.add(new Pair<>(ProjectileDirection.RIGHT, shipBoard[down.getX()][down.getY()]));

                } else {
                    brokenPos.add(down);

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

                for (int j = 0; j < 7; j++) {
                    for (int k = 0; k < 5; k++) {
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

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                Tile tempTile = shipBoard[i][j].getTile();
                Slot tempSlot = shipBoard[i][j];
                if (!invalidPositions.contains(tempSlot.getPosition()) && Util.inBoundaries(i, j)) {
                    if (tempTile != null) {
                        if (tempTile.getMyComponent().accept(new ComponentNameVisitor()).equals("Shield")) {

                            Shield tempShield = (Shield) tempTile.getMyComponent();
                            if (tempShield.isCharged()) sides.addAll(tempShield.getProtectedSides());

                        }
                    }
                }
            }
        }


        return sides;

    }

    public Boolean activateShield(Position shieldPos, Position batteryPos) {

        if (!invalidPositions.contains(shieldPos) && Util.inBoundaries(shieldPos.getX(), shieldPos.getY())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getX(), batteryPos.getY())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da pbatteryPos e carico shieldPos
                 *
                 */

                BatterySlot battery = (BatterySlot) getComponentFromPosition(batteryPos);
                Shield shield = (Shield) getComponentFromPosition(shieldPos);

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

        if (!invalidPositions.contains(enginePos) && Util.inBoundaries(enginePos.getX(), enginePos.getY())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getX(), batteryPos.getY())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da batteryPos e carico enginePos
                 *
                 */

                BatterySlot battery = (BatterySlot) getComponentFromPosition(batteryPos);
                DoubleEngine engine = (DoubleEngine) getComponentFromPosition(enginePos);

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

        if (!invalidPositions.contains(cannonPos) && Util.inBoundaries(cannonPos.getX(), cannonPos.getY())) {
            if (!invalidPositions.contains(batteryPos) && Util.inBoundaries(batteryPos.getX(), batteryPos.getY())) {
                //sono sicuro che entrambe le tile esistano
                /*
                 * levo una batteria da pbatteryPos e carico enginePos
                 *
                 */

                BatterySlot battery = (BatterySlot) getComponentFromPosition(batteryPos);
                DoubleCannon cannon = (DoubleCannon) getComponentFromPosition(cannonPos);

                if (battery.getBatteriesLeft() > 0) {
                    battery.removeBattery();
                    cannon.setCharged(true);
                    return true;
                }
            }

        }

        return false;
    }

    public int remainingTiles() {
        int remainingTiles = 0;
        List<Tile> tiles = Arrays.stream(this.getShipBoard())
                .flatMap(Arrays::stream).map(Slot::getTile)
                .filter(Objects::nonNull)
                .toList();


        for (Tile tile : tiles) {
            if (tile != null) {
                remainingTiles++;
            }
            return remainingTiles;
        }


        return 0;
    }

    public Slot[] getSetAsideTiles() {
        return setAsideTiles;
    }

    public int calculateEnginePower() {

        ArrayList<Position> enginePos = getComponentPositionsFromName("Engine");
        ArrayList<Position> doubleEnginePos = getComponentPositionsFromName("doubleEngine");

        ArrayList<Position> allEnginePos = new ArrayList<>();
        allEnginePos.addAll(enginePos);
        allEnginePos.addAll(doubleEnginePos);

        int enginePower = allEnginePos
                .stream()
                .mapToInt(p ->
                        ((Engine) getComponentFromPosition(p)).getEnginePower())
                .sum();
        if (enginePower != 0) {
            enginePower += getNBrownAlien() * 2;
        }

        return enginePower;
    }

    public Component getComponentFromPosition(Position position) {
        if(position.getY() >= 0 && position.getX() >= 0 && position.getY() < shipboardMaxY && position.getX() < shipboardMaxX) {
            Tile tile = getTileFromPosition(position);
            if (tile != null) {
                return tile.getMyComponent();
            }
        }

        return null;
    }

    public ArrayList<Position> getComponentPositionsFromName(String componentName) {

        ArrayList<Position> positions = new ArrayList<>();

        List<Slot> Slots = Arrays.stream(shipBoard)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .toList();

        for (Slot slot : Slots) {
            Position p = slot.getPosition();
            Tile tile = slot.getTile();
            if (tile != null && tile.getMyComponent().accept(new ComponentNameVisitor()).equals(componentName)) {
                positions.add(p);
            }
        }

        return positions;

    }

    public Tile getLastTile() {
        return lastTile;
    }

    public void setLastTile(Tile tile) {
        lastTile = tile;
    }

    /**
     * Finds the direction of the first component given a direction to search according to it, and the row/column to search in.
     *
     * @param direction  The direction which the search has to start from.
     * @param fixedIndex The row or column to search in.
     * @return The {@link Position} of the component on the shipboard.
     * @author Alessandro Giuseppe Gioia
     */
    public Position getFirstComponentFromDirectionAndIndex(ProjectileDirection direction, int fixedIndex) {
        if(fixedIndex < 0) {
            return null;
        }

        switch (direction) {
            case UP, DOWN -> {
                if(fixedIndex >= shipboardMaxX ) {
                    return null;
                }
            }
            case LEFT, RIGHT -> {
                if(fixedIndex >= shipboardMaxY ) {
                    return null;
                }
            }
        }
        int start, end, step;
        switch (direction) {
            case UP -> {
                start = 0;
                end = shipboardMaxY;
                step = 1;
            }
            case DOWN -> {
                start = shipboardMaxY - 1;
                end = -1;
                step = -1;
            }
            case LEFT -> {
                start = 0;
                end = shipboardMaxX;
                step = 1;
            }
            case RIGHT -> {
                start = shipboardMaxX - 1;
                end = -1;
                step = -1;
            }
            default -> {
                return null;
            }
        }


            for (int i = start; i != end; i += step) {
                Position pos = switch (direction) {
                    case UP, DOWN -> new Position(fixedIndex, i);
                    case LEFT, RIGHT -> new Position(i, fixedIndex);
                };

                if (getComponentFromPosition(pos) != null) {
                    return pos;
                }
            }

            return null;
    }

    public Tile getTileFromPosition(Position position) {
        return shipBoard[position.getX()][position.getY()].getTile();
    }

    /**
     * Finds all connected {@link Tile}s and their position to the one in the given position.
     *
     * @param position
     * @return An {@link ArrayList} of {@link Pair}s containing the {@link Position} of the tile and the tile itself.
     * @author Alessandro Giuseppe Gioia
     */
    public ArrayList<Pair<Position, Tile>> getConnectedTiles(Position position) {
        ArrayList<Pair<Position, Tile>> connectedTilesWithPosition = new ArrayList<>();

        int positionX = position.getX();
        int positionY = position.getY();

        int positionUp = position.getY() - 1;
        int positionRight = position.getX() + 1;
        int positionDown = position.getY() + 1;
        int positionLeft = position.getX() - 1;

        Tile tile = getTileFromPosition(position);


        if (positionUp >= 0) {
            Tile tileUp = shipBoard[positionX][positionUp].getTile();
            if (tileUp != null) {
                connectedTilesWithPosition.add(new Pair<>(new Position(positionX, positionUp), tileUp));
            }
        }
        if (positionRight < shipboardMaxX) {
            Tile tileRight = shipBoard[positionRight][positionY].getTile();
            if (tileRight != null) {
                connectedTilesWithPosition.add(new Pair<>(new Position(positionRight, positionY), tileRight));
            }
        }
        if (positionDown < shipboardMaxY) {
            Tile tileDown = shipBoard[positionX][positionDown].getTile();
            if (tileDown != null ) {
                connectedTilesWithPosition.add(new Pair<>(new Position(positionX, positionDown), tileDown));
            }
        }
        if (positionLeft >= 0) {
            Tile tileLeft = shipBoard[positionLeft][positionY].getTile();
            if (tileLeft != null ) {
                connectedTilesWithPosition.add(new Pair<>(new Position(positionLeft, positionY), tileLeft));
            }
        }


        return connectedTilesWithPosition;
    }

    /**
     * Finds all connected housing unit components to the one in the given position.
     *
     * @param position
     * @return An {@link ArrayList} of {@link Pair}s containing the {@link Position} of the housing unit and the tile of it.
     * @author Alessandro Giuseppe Gioia
     */
    public ArrayList<Pair<Position, Tile>> getConnectedHousingUnitTiles(Position position) {
        ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
        ArrayList<Pair<Position,Tile>> connected = getConnectedTiles(position);
        return getConnectedTiles(position).stream()
                .filter(p -> (p.getValue().getMyComponent().accept(componentNameVisitor).equals("CentralHousingUnit") || p.getValue().getMyComponent().accept(componentNameVisitor).equals("ModularHousingUnit")))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Float calculateFirePower() {

        ArrayList<Position> cannonPos = getComponentPositionsFromName("Cannon");
        ArrayList<Position> doubleCannonPos = getComponentPositionsFromName("DoubleCannon");
        ArrayList<Position> allCannons = new ArrayList<>();
        allCannons.addAll(cannonPos);
        allCannons.addAll(doubleCannonPos);


        Float firePower = (float) allCannons
                .stream()
                .mapToDouble(p->
                        ((Cannon)getComponentFromPosition(p)).getFirePower() )
                .sum();
        if (firePower != 0) {
            firePower += getNPurpleAlien() * 2;
        }

        return firePower;
    }

    public int getHumanCrewNumber() {

        int number = 0;

        ArrayList<Position> positions = new ArrayList<>(getComponentPositionsFromName("ModularHousingUnit"));
        positions.addAll(getComponentPositionsFromName("CentralHousingUnit"));

        for (Position pos : positions) {
            Tile tempTile = getTileFromPosition(pos);
            if (tempTile.getMyComponent().accept(new ComponentNameVisitor()).equals("ModularHousingUnit")) {
                ModularHousingUnit modularHousingUnit = (ModularHousingUnit) tempTile.getMyComponent();
                if (!modularHousingUnit.getAlienColor().equals(AlienColor.EMPTY))
                    number += modularHousingUnit.getNCrewMembers();

            } else if (tempTile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")) {
                CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tempTile.getMyComponent();
                number += centralHousingUnit.getNCrewMembers();
            }
        }

        return number;

    }

    public ArrayList<Good> getGoodsOnShipBoard() {
        ArrayList<Good> goods = new ArrayList<>();

        ArrayList<Position> genericCargoHoldsPos = getComponentPositionsFromName("GenericCargoHolds");

        for (Position p : genericCargoHoldsPos) {
            Component component = getComponentFromPosition(p);
            if (component instanceof GenericCargoHolds) {
                GenericCargoHolds hold = (GenericCargoHolds) component;
                goods.addAll(hold.getGoods());
            }
        }

        return goods;
    }

    public void setLearningMatch(Boolean isLearningMatch) {
        this.learningMatch = isLearningMatch;
    }
}
