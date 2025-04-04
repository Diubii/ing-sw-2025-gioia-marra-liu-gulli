package org.polimi.ingsw.galaxytrucker.network.client.socket;

import org.polimi.ingsw.galaxytrucker.enums.ConsoleColor;
import org.polimi.ingsw.galaxytrucker.enums.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.network.client.Client;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.Printer;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Observable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String address;
    private int port;

    public ClientSocket(String address, Integer port) throws IOException {
        this.port = port;
        this.address = address;
        Printer.printlnWithLabel(PrinterLabels.ClientSocket, ConsoleColor.ClientSocket, "Connessione a " + address + ":" + port);
        try{
            socket = new Socket(address, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Printer.printlnWithLabel(PrinterLabels.ClientSocket, ConsoleColor.ClientSocket, "Connesso al server " + address + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendMessage(NetworkMessage message) throws IOException {
        try {
            outputStream.writeObject(message);
        }
        catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
}