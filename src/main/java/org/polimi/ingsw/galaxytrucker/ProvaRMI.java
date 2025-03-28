package org.polimi.ingsw.galaxytrucker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProvaRMI extends Remote {
    String sayHelloRMI() throws RemoteException;
}
