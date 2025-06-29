package it.polimi.ingsw.galaxytrucker.network.server;

import it.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles;
import it.polimi.ingsw.galaxytrucker.network.client.rmi.ClientInterfaceRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Interface for the RMI-based server component in Galaxy Trucker.
 * This defines the methods that remote RMI clients can invoke to interact
 * with the server, such as registering themselves or retrieving server-side handlers.
 *
 * <p>Extends {@link Remote} to support Java RMI communication.</p>
 */
public interface ServerRMIInterface extends Remote {

    /**
     * Retrieves the controller handles associated with the server.
     *
     * @return the {@link ServerControllerHandles} object used to route and manage requests.
     * @throws RemoteException if a communication-related exception occurs during the call.
     */
    ServerControllerHandles getControllerHandles() throws RemoteException;
    /**
     * Retrieves the server-side handler associated with a given RMI client stub.
     *
     * @param clientInterfaceRMI the client stub used to identify the connection.
     * @return the {@link RMIClientHandler} associated with the client.
     * @throws RemoteException if a communication-related exception occurs.
     */
    RMIClientHandler getClientHandler(ClientInterfaceRMI clientInterfaceRMI) throws RemoteException;
    /**
     * Registers a new RMI client with the server.
     *
     * @param clientStub the stub representing the remote client.
     * @throws RemoteException if registration fails due to communication issues.
     */
    void handleRMIRegistration(ClientInterfaceRMI clientStub) throws RemoteException;

}


