package org.polimi.ingsw.galaxytrucker.network.common;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.TileBunch;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LobbyManager {
    private Game RealGame;

    private final HashMap<String, Color> PlayerColors;
    private Color nextAvailableColor;
    private final HashMap<String, ClientHandler> PlayerHandlers = new HashMap<>();
    private TileBunch tileBunch = null;
    private final GameController gameController;
    private final ArrayList<String> playerShipFinished = new ArrayList<>();
    private final ArrayList<String> playerCrewFinished = new ArrayList<>();



    private ArrayList<Pair<Integer, CompletableFuture<NetworkMessage>>> pendingResponses;
    public final Object positionLock = new Object();
    public final Object checkShipLock = new Object();

    //locks

    final public Object  lock5 = new Object();

    public Object getLock5() {
        return lock5;
    }


    final Object lock1 = new Object();
    final Object lock2 = new Object();
    final Object lock3 = new Object();
    final Object lock4 = new Object();

    public ArrayList<String> getPlayerShipFinished() {
        synchronized (lock1) {
            return playerShipFinished;
        }
    }

    public void addPlayerShipFinished(String playerShipFinished) {
        synchronized (lock1) {
            this.playerShipFinished.add(playerShipFinished);
        }
    }

    public int getPlayerShipFinishedSize(){
        synchronized (lock1){
            return this.playerShipFinished.size();
        }
    }


    public int getPlayerCrewFinishedSize(){
        synchronized (lock4){
            return this.playerCrewFinished.size();
        }
    }

    public void addPlayerCrewFinished(String playerNickname){
        synchronized (lock4){
            playerCrewFinished.add(playerNickname);
        }
    }

    public int getPlayerCrewSize(){
        synchronized (lock4){
            return this.playerCrewFinished.size();
        }
    }





    public void addPendingResponse(CompletableFuture<NetworkMessage> response, Integer id) {

        synchronized (lock2) {
            pendingResponses.add(new Pair<>(id, response));
        }
    }

    public void removePendingResponse(Integer id) {

        synchronized (lock2) {
            Pair<Integer, CompletableFuture<NetworkMessage>> pendingResponse = pendingResponses.stream().filter(pair -> pair.getKey().equals(id)).findFirst().orElse(null);



            pendingResponses.remove(pendingResponse);
        }

    }

    public void completePendingResponse(Integer id, NetworkMessage response) {
        synchronized (lock2) {
            Pair<Integer, CompletableFuture<NetworkMessage>> pendingResponse = pendingResponses.stream().filter(pair -> pair.getKey().equals(id)).findFirst().orElse(null);
            if(pendingResponse != null){
                pendingResponse.getValue().complete(response);
                pendingResponses.remove(pendingResponse);
            }
            else{
                System.err.println("Couldn't complete future related to " + response.toString() + ".");
            }
        }
    }

//    public  void completePendingResponse(NetworkMessage response) {
//        pendingResponses.get().complete(response);
//

    public ArrayList<Pair<Integer, CompletableFuture<NetworkMessage>>> getPendingResponses() {

        synchronized (lock2) {
            return pendingResponses;

        }
    }


    public void setRealGame(Game game) {
        RealGame = game;
    }

    public Game getRealGame() {
        return RealGame;
    }

    public LobbyManager() {
        PlayerColors = new HashMap<>();
        RealGame = new Game();
        nextAvailableColor = Color.RED;
        tileBunch = new TileBunch();
        gameController = new GameController(this );
        pendingResponses = new ArrayList<>();
    }

    public HashMap<String, Color> getPlayerColors() {
        return PlayerColors;
    }

    public Color useNextAvailableColor() {

        Color temp = nextAvailableColor;

        int nextIndex = temp.ordinal() + 1;
        nextAvailableColor = Color.values()[nextIndex];

        return temp;
    }

    public void addPlayerHandler(ClientHandler handler, String playerName) {
        synchronized (lock3) {
            PlayerHandlers.put(playerName, handler);

        }
    }

    public void removePlayerHandler(String playerName) {
        synchronized (lock3) {
            PlayerHandlers.remove(playerName);
        }
    }

    public HashMap<String, ClientHandler> getPlayerHandlers() {
        synchronized (lock3) {
            return new HashMap<>(PlayerHandlers);
        }
    }

    public TileBunch getTileBunch() {
        return tileBunch;
    }

    public GameController getGameController() {
        return gameController;
    }

    public String getNicknameFromColor(Color color){
        return (PlayerColors.entrySet().stream().filter(pair -> pair.getValue().equals(color))).map(Map.Entry::getKey).findFirst().orElse(null);
    }
}
