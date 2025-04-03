package org.polimi.ingsw.galaxytrucker;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class ProvaRMI implements Remote, Serializable {
    //RMI -> esegui direttamente
    //Socket -> esegue il metodo, ma poi deve inviare
    public String sayHelloRMI() throws RemoteException {
        return "Hello from ProvaRMI";
    }
}
