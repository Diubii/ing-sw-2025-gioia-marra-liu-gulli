package org.polimi.ingsw.galaxytrucker;

import org.polimi.ingsw.galaxytrucker.model.visitors.AdventureCardActivator;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.ServerSocket;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ProvaRMI{

    protected Server() throws RemoteException {
        super();
    }

    @Override
    public String sayHelloRMI() throws RemoteException {
        return "Hello, RMI";
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        System.setProperty("java.rmi.server.hostname", "localhost");

        System.out.println("Starting Server...");

        System.out.println("Starting Socket...");
        try {
            serverSocket = new ServerSocket(6969);
            System.out.println("Socket started!");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not start Socket.");
        }

        System.out.println("Starting RMI...");
        try {
            LocateRegistry.createRegistry(42069);
            Naming.rebind("//localhost:42069/prova_rmi", new Server());
            System.out.println("RMI started!");
        }
        catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Could not start RMI.");
        }

        System.out.println("Waiting for socket client...");
        try{
            Socket socket = serverSocket.accept();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("Could not accept client connection.");
        }
        System.out.println("Client connected!");
    }
}
