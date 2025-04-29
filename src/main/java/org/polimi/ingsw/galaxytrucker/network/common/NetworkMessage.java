package org.polimi.ingsw.galaxytrucker.network.common;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public abstract class NetworkMessage implements Serializable {
    public abstract <T> T accept(NetworkMessageVisitorsInterface<T> visitor) throws TooManyPlayersException, PlayerAlreadyExistsException, InvalidTilePosition, ExecutionException, InterruptedException, IOException;

    private static int id = 0;
    private int myId;

    public NetworkMessage() {
        myId = id;
        id++;
    }

    public NetworkMessage(int id) {
        myId = id;
//        id++;
    }

    public int getID() {
        return myId;
    }

    public static int getCounter(){
        return id;
    }


}
