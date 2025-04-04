package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ConsoleColor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.NetworkMessageVisitor;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkingConstants;
import org.polimi.ingsw.galaxytrucker.network.client.socket.ClientSocket;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.network.server.ServerRMI;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.view.View;

public class Tui extends View {


    private static PrintStream out;
    private Thread inputThread;
    private final Boolean isSocket;
    private ClientController clientController;
    private final Map<String, String> serverInfo = new HashMap<>();
    private ClientSocket clientSocket;
    private ServerRMI serverRMI;
    private NetworkMessageVisitor stub;

    private final String defaultAddress;
    private final String defaultPort;


    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

        defaultAddress = isSocket ? NetworkingConstants.SOCKET_DEFAULT_ADDRESS : NetworkingConstants.RMI_DEFAULT_ADDRESS;
        defaultPort = isSocket ? NetworkingConstants.SOCKET_DEFAULT_PORT : NetworkingConstants.RMI_DEFAULT_PORT;
    }

    public void start() throws ExecutionException, IOException, NotBoundException {
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
    private void askServerInfo() throws ExecutionException {
        //Assegna l'indirizzo di default in base a Socket o RMI


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
    private void connectToServer() throws IOException, NotBoundException {
        if(isSocket){
            clientSocket = new ClientSocket(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
        }
        else{
            Registry registry = LocateRegistry.getRegistry(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
            stub = (NetworkMessageVisitor) registry.lookup("server");
        }
    }
    private void askNickname() throws ExecutionException, IOException {
        out.print("Come ti chiami? ");
        String nickname = ReadLine.run(inputThread);
        if(isSocket) clientSocket.sendMessage(new NICKNAME_REQUEST(nickname));
        else stub.getNicknameRequest(new NICKNAME_REQUEST(nickname));
    }
}