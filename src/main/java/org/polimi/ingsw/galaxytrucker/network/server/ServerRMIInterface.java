package org.polimi.ingsw.galaxytrucker.network.server;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;
import org.polimi.ingsw.galaxytrucker.network.client.rmi.ClientRMI;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;


    public interface ServerRMIInterface extends Remote {

        void receiveMessage(NetworkMessage message, ClientInterfaceRMI clientRMI) throws RemoteException;

        public void handleRMIRegistration(ClientInterfaceRMI clientStub) throws RemoteException;

    }


