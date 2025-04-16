package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.observer.Observable;

import java.io.IOException;
import java.net.Socket;

public  interface Client {

public void sendMessage(NetworkMessage message) throws IOException;
}
