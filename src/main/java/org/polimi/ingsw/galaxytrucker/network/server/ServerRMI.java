package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.NetworkMessageMethods;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI {
    Registry registry;
    public ServerRMI(int port) throws RemoteException {
        registry = LocateRegistry.createRegistry(port);
        registry.rebind("server", new NetworkMessageMethods());
    }
}
