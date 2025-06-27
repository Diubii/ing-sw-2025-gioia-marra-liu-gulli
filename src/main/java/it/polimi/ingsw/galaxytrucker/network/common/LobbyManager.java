package it.polimi.ingsw.galaxytrucker.network.common;

import it.polimi.ingsw.galaxytrucker.controller.GameController;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.TileBunch;
import it.polimi.ingsw.galaxytrucker.model.game.Game;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LobbyManager {
    private Game RealGame;

    private final int gameID;
    private final HashMap<String, Color> PlayerColors;
    private Color nextAvailableColor;
    private final HashMap<String, ClientHandler> PlayerHandlers = new HashMap<>();
    private final TileBunch tileBunch;
    private final GameController gameController;
    private final ArrayList<String> playerShipFinished = new ArrayList<>();
    private final ArrayList<String> playerCrewFinished = new ArrayList<>();
    private final ArrayList<PlayerInfo> playerInfos = new ArrayList<>();
    private final Set<String> readyPlayers = new HashSet<>();
    private final Set<String> earlyLandingPlayers = new HashSet<>();
    private final ArrayList<ClientHandler> timerSubscribers = new ArrayList<>();


    private ArrayList<Pair<Integer, CompletableFuture<NetworkMessage>>> pendingResponses;
    public final Object positionLock = new Object();
    public final Object checkShipLock = new Object();
    public final Object timerLock = new Object();
    //locks

    final public Object lock5 = new Object();

    public Object getLock5() {
        return lock5;
    }


    final Object lock1 = new Object();
    final Object lock2 = new Object();
    final Object lock3 = new Object();
    final Object lock4 = new Object();
    final Object lock6 = new Object();

    public int getGameID() {
        return gameID;
    }

    public ArrayList<PlayerInfo> getPlayerInfos() {
        synchronized (lock6) {
            return playerInfos;
        }
    }

    public void addPlayerInfo(PlayerInfo playerInfo) {
        synchronized (lock6) {
            this.playerInfos.add(playerInfo);
        }
    }


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

    public int getPlayerShipFinishedSize() {
        synchronized (lock1) {
            return this.playerShipFinished.size();
        }
    }


    public int getPlayerCrewFinishedSize() {
        synchronized (lock4) {
            return this.playerCrewFinished.size();
        }
    }

    public void addPlayerCrewFinished(String playerNickname) {
        synchronized (lock4) {
            playerCrewFinished.add(playerNickname);
        }
    }

    public int getPlayerCrewSize() {
        synchronized (lock4) {
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
            if (pendingResponse != null) {
                pendingResponse.getValue().complete(response);
                pendingResponses.remove(pendingResponse);
            } else {
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

    public LobbyManager(int gameID) {
        this.gameID = gameID;
        PlayerColors = new HashMap<>();
        RealGame = new Game();
        nextAvailableColor = Color.RED;
        tileBunch = new TileBunch();
        gameController = new GameController(this);
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

    public synchronized void addReadyPlayer(String playerNickname) {
        readyPlayers.add(playerNickname);
    }
    public synchronized void addEarlyLandingPlayer(String playerNickname) {
        earlyLandingPlayers.add(playerNickname);
    }
    public synchronized boolean allActivePlayerReady() {
        int playingPlayers = gameController.getPlayingPlayers().size();
        int readyPlayers = this.readyPlayers.size();
        int earlyLandingPlayers = this.earlyLandingPlayers.size();
        return readyPlayers == playingPlayers;
    }

    public synchronized  void resetReadyPlayers() {
        readyPlayers.clear();
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

    public String getNicknameFromColor(Color color) {
        return (PlayerColors.entrySet().stream().filter(pair -> pair.getValue().equals(color))).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public ArrayList<ClientHandler> getTimerSubscribers() {
        return timerSubscribers;
    }
}
