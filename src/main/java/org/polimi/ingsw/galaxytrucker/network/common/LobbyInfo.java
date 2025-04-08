package org.polimi.ingsw.galaxytrucker.network.common;

import java.io.Serial;
import java.io.Serializable;

public class LobbyInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 111L;

    private final String host;
    private final int maxPlayers;
    private  int connectedPlayers;
    private final int lobbyID;

    public LobbyInfo(String host, int maxPlayers, int connectedPlayers, int lobbyID) {
        this.host = host;
        this.maxPlayers = maxPlayers;
        this.connectedPlayers = connectedPlayers;
        this.lobbyID = lobbyID;
    }

    public String getHost() {
        return host;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    public void addConnectedPlayer() {
        connectedPlayers++;
    }

    public void removeConnectedPlayer(){

        connectedPlayers--;
    }

    public int getLobbyID() {
        return lobbyID;
    }

}
