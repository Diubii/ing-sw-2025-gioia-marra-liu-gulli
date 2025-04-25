package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.visitors.NetworkMessageVisitorsInterface;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class PlayerJoinedUpdate extends NetworkMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 12217367L;

    //    private  final ArrayList<Player> players;
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

    @Override
    public <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException {
        return visitor.visit(this);
    }
}
