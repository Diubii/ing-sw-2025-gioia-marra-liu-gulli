package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import it.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerRMIInterface extends Remote {

    ServerControllerHandles getControllerHandles() throws RemoteException;
    RMIClientHandler getClientHandler(ClientInterfaceRMI clientInterfaceRMI) throws RemoteException;
    void handleRMIRegistration(ClientInterfaceRMI clientStub) throws RemoteException;

}


