package it.polimi.ingsw.galaxytrucker.network.common;

import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageVisitorsInterface;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {
    public abstract <T> T accept(NetworkMessageVisitorsInterface<T> visitor);

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

    public static int getCounter() {
        return id;
    }


}
