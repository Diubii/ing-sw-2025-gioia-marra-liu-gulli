package it.polimi.ingsw.galaxytrucker.network.common;

import java.io.Serial;
import java.io.Serializable;

public class LobbyInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 111L;

    private final String host;
    private final int maxPlayers;
    private int connectedPlayers;
    private final int lobbyID;
    private final boolean isLearningMatch;

    public LobbyInfo(String host, int maxPlayers, int connectedPlayers, int lobbyID,boolean isLearningMatch) {
        this.host = host;
        this.maxPlayers = maxPlayers;
        this.connectedPlayers = connectedPlayers;
        this.lobbyID = lobbyID;
        this.isLearningMatch = isLearningMatch;
    }

    public String getHost() {
        return host;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public synchronized int getConnectedPlayers() {
        return connectedPlayers;
    }

    public synchronized void addConnectedPlayer() {
        connectedPlayers++;
    }

    public synchronized void removeConnectedPlayer() {
        if(connectedPlayers > 0) connectedPlayers--;
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public boolean isLearningMatch() {
        return isLearningMatch;
    }
}
