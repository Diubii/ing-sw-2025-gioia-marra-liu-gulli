package org.polimi.ingsw.galaxytrucker.model;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ComponentNameVisitor;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.model.essentials.Util;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Rappresenta la nave del giocatore, composta da una griglia di {@link Slot}
 * in cui vengono posizionate le {@link Tile}. La nave può contenere vari componenti
 * come motori, batterie, cannoni e stive di carico.
 */

public class Ship {

    private Slot[][] shipBoard = new Slot[5][7];
    private Slot[] setAsideTiles = new Slot[2];
    private int nExposedConnector;
    private int destroyedTiles;
    private int nBatterieLeft;
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
    private Set<Position> enginePos = new LinkedHashSet<>();
    private ArrayList<Position> invalidPositions;
    private ArrayList<Position> destroyedPositions;


    /**
     * Costruttore della nave.
     * @param learningMatch Indica se la partita è in modalità apprendimento.
     */
    public Ship(Boolean learningMatch) {
        listNotLoadedGoods = new ArrayList<>();
        listOfGoods = new ArrayList<>();
        this.learningMatch = learningMatch;
        invalidPositions = createIP();
        destroyedPositions = new ArrayList<>();
        generateSlot();

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


    // GETTERS START ----------------------

    /**
     * Restituisce la board della nave.
     * @return Una matrice di {@link Slot} rappresentante la nave.
     */
    public Slot[][] getShipBoard() {
        return shipBoard.clone();
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

    public Boolean getPurpleAlien() {
        return purpleAlien;
    }

    public Boolean getBrownAlien() {
        return brownAlien;
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    public int getnCrew() {
        return nCrew;
    }
    // GETTERS END ----------------------


    public ArrayList<Position> getInvalidPositions() {
        return invalidPositions;
    }
    // GETTERS END ----------------------


    /**
     * Crea la lista delle posizioni invalide sulla nave in base alla modalità di gioco.
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
     * @param tile La tile da posizionare.
     * @param pos La posizione in cui posizionare la tile.
     */
    public void putTile(Tile tile, Position pos) {


        if (shipBoard[pos.getY()][pos.getX()] == null) {
            shipBoard[pos.getY()][pos.getX()] = new Slot(pos);
        }

        updateSets(pos, tile);
        shipBoard[pos.getY()][pos.getX()].putTile(tile);
        //da aggiungere la logica che controlla che Tile e' stat inserita e l'aggiornamento delle varie pos
    }

    /**
     * Aggiorna le posizioni dei componenti all'interno della nave.
     * @param pos La posizione della tile inserita.
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
        }

    }

    /**
     * Rimuove una tile dalla nave.
     * @param tile La tile da rimuovere.
     * @param pos La posizione della tile da rimuovere.
     */
    public void removeTile(Tile tile, Position pos) {
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
            }

        }
        getShipBoard()[pos.getY()][pos.getX()].removeTile();

    }
    /**
     * Calcola il numero di connettori esposti nella nave.
     */
    public void calcExposedConnectors() {
        int tempSum;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                ArrayList<Position> validPos = new ArrayList<Position>();
                Position tempPos = new Position(i, j);
                Tile myTile = shipBoard[i][j].getTile();
                //calculate neihbours
                Position nord = new Position(i - 1, j);
                Position sud = new Position(i + 1, j);
                Position est = new Position(i, j + 1);
                Position ovest = new Position(i, j - 1);

                //NORD
                if (myTile.getSides().get(0) != Connector.EMPTY) {
                    if (!invalidPositions.contains(nord)) {
                        Tile tempTile = shipBoard[nord.getX()][nord.getY()].getTile();
                        if (tempTile == null) {
                            nExposedConnector++;
                        }
                    } else nExposedConnector++;

                }
                //OVEST
                if (myTile.getSides().get(1) != Connector.EMPTY) {
                    if (!invalidPositions.contains(ovest)) {
                        Tile tempTile = shipBoard[ovest.getX()][ovest.getY()].getTile();
                        if (tempTile == null) {
                            nExposedConnector++;
                        }
                    } else nExposedConnector++;

                }
                //SUD
                if (myTile.getSides().get(2) != Connector.EMPTY) {
                    if (!invalidPositions.contains(sud)) {
                        Tile tempTile = shipBoard[sud.getX()][sud.getY()].getTile();
                        if (tempTile == null) {
                            nExposedConnector++;
                        }
                    } else nExposedConnector++;

                }
                //EST
                if (myTile.getSides().get(3) != Connector.EMPTY) {
                    if (!invalidPositions.contains(est)) {
                        Tile tempTile = shipBoard[est.getX()][est.getY()].getTile();
                        if (tempTile == null) {
                            nExposedConnector++;
                        }
                    } else nExposedConnector++;

                }


            }
        }

    }


    /**
     * Rappresentazione testuale della nave.
     * @return Stringa che rappresenta la nave.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (shipBoard[i][j] != null && shipBoard[i][j].getTile() != null) {
                    sb.append("[1] ");// Slot con Tile
                    System.out.printf("FOUND %d %d\n", i, j);
                } else if (shipBoard[i][j] != null && shipBoard[i][j].getTile() == null) {
                    int finalJ = j;
                    int finalI = i;
                    if (invalidPositions.stream().anyMatch(pos -> pos.getX() == finalJ && pos.getY() == finalI)) {
                        sb.append("[X] ");
                    } else {
                        sb.append("[.] "); // Slot vuoto
                        System.out.printf("%d %d [null]\n", i, j);

                    }
                } else if (shipBoard[i][j] == null) sb.append("[.] "); // Slot vuoto

            }
            sb.append("\n"); // Vai a capo dopo ogni riga
        }

        return sb.toString();
    }

    /**
     * Verifica se la nave è ben collegata e rispetta le regole di connessione.
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
                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("Engine")) {
                            Boolean temp =  Util.EngineWellConnected(shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                            shipBoard[i][j].getTile().setWellConnected(temp);
                            return  temp;


                        }
                        if (shipBoard[i][j].getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("ModularHousingUnit")) {

                            AlienColor al = null;
                            ModularHousingUnit temp = (ModularHousingUnit) shipBoard[i][j].getTile().getMyComponent();
                            if (temp.getNBrownAlien() > temp.getNPurpleAlien() && temp.getHumanCrewNumber() == 0) al = AlienColor.BROWN;
                            if (temp.getNPurpleAlien() > temp.getNBrownAlien() && temp.getHumanCrewNumber() == 0) al = AlienColor.PURPLE;

                            Boolean result =  Util.CheckLifeSupportSystem(al, shipBoard[i][j].getTile(), this, shipBoard[i][j]);
                            shipBoard[i][j].getTile().setWellConnected(result);
                            return result;


                        }
                    }
                }
            }
        }

        return true;

    }

    /**
     *
     * @param positions Lista posizioni in cui sono stati distrutti dei Component
     */

    public ArrayList<Slot[][]> truncateShip(ArrayList<Position> positions) {

    // Itera su tutte le posizioni fornite
    for (Position pos : positions) {

        // Lista dei vicini validi della posizione corrente
        ArrayList<Pair<ProjectileDirection, Slot>> villagers = new ArrayList<>();

        // Controlla se ci sono slot validi sopra, sinistra, sotto e destra della posizione attuale
        if (!invalidPositions.contains(new Position(pos.getY() - 1, pos.getX())) && Util.inBoundaries(pos.getY() - 1, pos.getX())) {
            villagers.add(new Pair<>(ProjectileDirection.FRONT, shipBoard[pos.getY() - 1][pos.getX()]));
        }
        if (!invalidPositions.contains(new Position(pos.getY(), pos.getX() - 1)) && Util.inBoundaries(pos.getY(), pos.getX() - 1)) {
            villagers.add(new Pair<>(ProjectileDirection.LEFT, shipBoard[pos.getY()][pos.getX() - 1]));
        }
        if (!invalidPositions.contains(new Position(pos.getY() + 1, pos.getX())) && Util.inBoundaries(pos.getY() + 1, pos.getX())) {
            villagers.add(new Pair<>(ProjectileDirection.BOTTOM, shipBoard[pos.getY() + 1][pos.getX()]));
        }
        if (!invalidPositions.contains(new Position(pos.getY(), pos.getX() + 1)) && Util.inBoundaries(pos.getY(), pos.getX() + 1)) {
            villagers.add(new Pair<>(ProjectileDirection.RIGHT, shipBoard[pos.getY()][pos.getX() + 1]));
        }

        // Se ci sono vicini validi, verifica le connessioni tra di loro
        if (!villagers.isEmpty()) {

            // Lista di nodi (tile ID e i loro collegamenti)
            ArrayList<Pair<Integer, ArrayList<Integer>>> Nodes = new ArrayList<>();
            ArrayList<Integer> tilesVisitedId = new ArrayList<>();

            // Per ogni vicino, controlla la sua connessione con gli altri
            for (int i = 0; i < villagers.size(); i++) {

                // Crea un nodo per il villager attuale
                Nodes.add(new Pair<>(villagers.get(i).getValue().getTile().getId(), new ArrayList<>()));
                Tile myTile = villagers.get(i).getValue().getTile();

                // Lista delle tile raggiungibili da questo vicino
                ArrayList<Integer> tilesID = new ArrayList<>();

                // Esegue una visita per raccogliere tutte le tile raggiungibili
                Util.visitTile(myTile, tilesID, villagers.get(i).getValue(), invalidPositions, this);

                // Controlla quali altri vicini sono raggiungibili da questo
                for (int j = i + 1; j < villagers.size(); j++) {
                    if (tilesID.contains(villagers.get(j).getValue().getTile().getId())) {
                        Nodes.get(i).getValue().add(villagers.get(j).getValue().getTile().getId());
                        tilesVisitedId.add(villagers.get(j).getValue().getTile().getId());
                    }
                }
            }

            // Creazione delle classi di equivalenza (tronconi della nave)
            ArrayList<ArrayList<Integer>> equivalenceClasses = new ArrayList<>();
            for (int i = 0; i < Nodes.size(); i++) {
                int finalI = i;
                if (equivalenceClasses.stream().noneMatch(list -> list.contains(Nodes.get(finalI).getKey()))) {
                    equivalenceClasses.add(Nodes.get(i).getValue());
                }
            }

            // Numero di tronconi distinti
            int numTronconi = equivalenceClasses.size();

            // Creazione delle nuove navi separate
            ArrayList<Slot[][]> ships = new ArrayList<>(numTronconi);
            for (int i = 0; i < numTronconi; i++) {

                // Crea una nuova nave vuota
                Slot[][] myshipBoard = new Slot[5][7];
                for (int j = 0; j < 5; j++) {
                    for (int k = 0; k < 7; k++) {
                        // Se la tile appartiene alla classe di equivalenza, viene inclusa nella nuova nave
                        if (equivalenceClasses.get(i).contains(shipBoard[j][k].getTile().getId()) && shipBoard[j][k].getTile() != null) {
                            myshipBoard[j][k] = shipBoard[j][k];
                        }
                    }
                }

                // Aggiunge la nuova nave alla lista delle navi separate
                ships.add(myshipBoard);
            }

            return ships; // Restituisce l'elenco delle navi separate
        }
    }

    return null; // Se non ci sono sezioni da separare, restituisce null
}




}
