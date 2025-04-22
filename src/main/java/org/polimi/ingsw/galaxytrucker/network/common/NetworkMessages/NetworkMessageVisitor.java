package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NetworkMessageVisitor extends Remote {
    void getNicknameRequest(NicknameRequest nickname) throws RemoteException;

    void getNicknameResponse(NicknameResponse response) throws RemoteException;
}

