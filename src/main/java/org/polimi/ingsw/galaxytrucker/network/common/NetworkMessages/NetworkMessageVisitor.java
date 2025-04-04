package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NetworkMessageVisitor extends Remote {
    void getNicknameRequest(NICKNAME_REQUEST nickname) throws RemoteException;
    void getNicknameResponse(NICKNAME_RESPONSE response) throws RemoteException;
}

