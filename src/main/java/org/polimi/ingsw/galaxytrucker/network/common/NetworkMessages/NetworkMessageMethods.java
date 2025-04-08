package org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NetworkMessageMethods extends UnicastRemoteObject implements NetworkMessageVisitor, Serializable {

    public NetworkMessageMethods() throws RemoteException {
        super();
    }

    public void getNicknameRequest(NicknameRequest nickReq) throws RemoteException {
        System.out.println("Got nickname request: " + nickReq.getNickname());
    }

    public void getNicknameResponse(NICKNAME_RESPONSE nickRes) throws RemoteException {
        System.out.println("Got nickname response");
    }
}
