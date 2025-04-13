package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class JoinRoomOptionsResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 75L;
    private final ArrayList<LobbyInfo> lobbyInfos;

    public JoinRoomOptionsResponse(ArrayList<LobbyInfo> lobbyInfos) {

        this.lobbyInfos = lobbyInfos;
    }

    public ArrayList<LobbyInfo> getLobbyInfos() {
        return lobbyInfos;
    }

    public void accept(ServerController serverController, ClientHandler clientHandler) {

    }

}
