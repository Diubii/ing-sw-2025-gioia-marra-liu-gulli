package it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Player joined update.
 */
public class PlayerJoinedUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 12217367L;


    private ArrayList<PlayerInfo> playersJoinedBefore;
    private final PlayerInfo playerInfo;

    /**
     * Instantiates a new Player joined update.
     *
     * @param playerInfo the player info
     */
    public PlayerJoinedUpdate(PlayerInfo playerInfo) {
//        this.players = players;
        this.playerInfo = playerInfo;

    }

    /**
     * Gets player info.
     *
     * @return the player info
     */
//    public ArrayList<Player> getPlayers() {
//        return players;
//    }
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    /**
     * Sets players joined before.
     *
     * @param playersJoinedBefore the players joined before
     */
    public void setPlayersJoinedBefore(ArrayList<PlayerInfo> playersJoinedBefore) {
        this.playersJoinedBefore = playersJoinedBefore;
    }

    /**
     * Gets players joined before.
     *
     * @return the players joined before
     */
    public ArrayList<PlayerInfo> getPlayersJoinedBefore() {
        return playersJoinedBefore;
    }


    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) {
        return visitor.visit(this);
    }
}
