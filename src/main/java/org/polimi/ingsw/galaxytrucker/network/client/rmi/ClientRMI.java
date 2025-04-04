package org.polimi.ingsw.galaxytrucker.network.client.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    Registry registry;
    public ClientRMI(String address, int port) throws RemoteException {
        registry = LocateRegistry.getRegistry(address, port);
    }
}
