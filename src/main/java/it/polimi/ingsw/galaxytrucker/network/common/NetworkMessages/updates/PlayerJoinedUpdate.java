package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerJoinedUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 12217367L;


    private ArrayList<PlayerInfo> playersJoinedBefore;
    private final PlayerInfo playerInfo;

    public PlayerJoinedUpdate(PlayerInfo playerInfo) {
//        this.players = players;
        this.playerInfo = playerInfo;

    }

    //    public ArrayList<Player> getPlayers() {
//        return players;
//    }
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayersJoinedBefore(ArrayList<PlayerInfo> playersJoinedBefore) {
        this.playersJoinedBefore = playersJoinedBefore;
    }

    public ArrayList<PlayerInfo> getPlayersJoinedBefore() {
        return playersJoinedBefore;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
