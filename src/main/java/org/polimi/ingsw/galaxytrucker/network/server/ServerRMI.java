package org.polimi.ingsw.galaxytrucker.network.server;

import org.polimi.ingsw.galaxytrucker.controller.ServerController;
import org.polimi.ingsw.galaxytrucker.network.common.GameInterface;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMI extends UnicastRemoteObject implements GameInterface {
    private final GameNetworkModel model;
    ServerController controller;

    public ServerRMI(GameNetworkModel model, ServerController serverController) throws RemoteException {
        super();
        this.model = model;
        this.controller = serverController;

    }

    @Override
    public void sendMove(String move) throws RemoteException {
        model.addMove(move);
        System.out.println("[RMI Server] Mossa ricevuta: " + move);
    }

    @Override
    public String getMoves() throws RemoteException {
        return String.join(", ", model.getMoves());
    }
}
