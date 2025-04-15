package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerJoinedUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 12L;

    private final ArrayList<Player> players;
    private final  HashMap<String, Color> playerInfo;

    public PlayerJoinedUpdate(ArrayList<Player> players, HashMap<String, Color> playerInfo) {
        this.players = players;
        this.playerInfo = playerInfo;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public HashMap<String, Color> getPlayerInfo() {
        return playerInfo;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) {

    }
}
