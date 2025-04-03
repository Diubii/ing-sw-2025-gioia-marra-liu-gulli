package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ConsoleColor;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.view.View;


public class Tui extends View {


    private static PrintStream out;
    private Thread inputThread;
    private Boolean isSocket;
    private ClientController clientController;
    private Map<String, String> serverInfo = new HashMap<>();



    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);
    }

    public void start() throws ExecutionException, IOException {
        printBanner();
        askServerInfo();
        connectToServer();
        askNickname();
    }

    private void printBanner() {
        String banner = ConsoleColor.Banner.getAnsiColorCode() +
                "   __    _   __    _   _  __ _  __  _____ ___  _ __  __  _    ___  ___        \n" +
                " ,'_/  .' \\ / /  .' \\ | |/,'| |/,' /_  _// o |/// /,'_/ / //7/ _/ / o |     \n" +
                "/ /_n / o // /_ / o / /  /  | ,'    / / /  ,'/ U // /_ /  ,'/ _/ /  ,'        \n" +
                "|__,'/_n_//___//_n_/,'_n_\\ /_/     /_/ /_/`_\\\\_,' |__//_/\\/___//_/`_\\    \n" +
                ConsoleColor.Reset.getAnsiColorCode();

        out.println(banner);
    }
    private void askServerInfo() throws ExecutionException, IOException {
        final String socketDefaultAddress = "localhost";
        final String socketDefaultPort = "6969";

        final String rmiDefaultAddress = "localhost";
        final String rmiDefaultPort = "1009";

        //Assegna l'indirizzo di default in base a Socket o RMI
        final String defaultAddress = isSocket ? socketDefaultAddress : rmiDefaultAddress;
        final String defaultPort = isSocket ? socketDefaultPort : rmiDefaultPort;

        out.println("Please specify the following settings. The default value is shown between brackets.");

        out.print("Enter the server address [" + defaultAddress + "]: ");
        String address = ReadLine.run(inputThread);

        //Se l'utente non inserisce niente -> defaultAddress, altrimenti quello inserito dall'utente
        assert address != null;
        if (address.isEmpty()) serverInfo.put("address", defaultAddress);
        else serverInfo.put("address", address);

        out.print("Enter the server port [" + defaultPort + "]: ");
        String port = ReadLine.run(inputThread);

        //Se l'utente non inserisce niente -> defaultPort, altrimenti quella inserita dall'utente
        assert port != null;
        if (port.isEmpty()) serverInfo.put("port", defaultPort);
        else serverInfo.put("port", port);
    }
    private void connectToServer() throws ExecutionException, IOException {
        if(isSocket){
            new ClientSocket(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
        }
        else{
            Registry registry = LocateRegistry.getRegistry(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
        }
    }
    private void askNickname() throws ExecutionException {
        out.print("Come ti chiami? ");
        String nickname = ReadLine.run(inputThread);
    }
}



