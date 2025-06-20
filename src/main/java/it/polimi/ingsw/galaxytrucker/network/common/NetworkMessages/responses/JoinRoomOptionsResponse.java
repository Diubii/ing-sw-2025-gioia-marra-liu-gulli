package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses;

import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class JoinRoomOptionsResponse extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 75L;
    private final ArrayList<LobbyInfo> lobbyInfos;

    public JoinRoomOptionsResponse(ArrayList<LobbyInfo> lobbyInfos, int id) {

        super(id);
        this.lobbyInfos = lobbyInfos;
    }

    public ArrayList<LobbyInfo> getLobbyInfos() {
        return lobbyInfos;
    }

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
