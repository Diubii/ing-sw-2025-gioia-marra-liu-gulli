package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.view.View;


public class Tui extends View {


    private static PrintStream out;
    private Thread inputThread;
    private Boolean isSocket;
    private ClientController clientController;



    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

    }




    public void start() throws ExecutionException, IOException {

        String banner = "\033[1;34m" + // Colore Blu Chiaro
                "   __    _   __    _   _  __ _  __  _____ ___  _ __  __  _    ___  ___    ___\n" +
                " ,'_/  .' \\ / /  .' \\ | |/,'| |/,' /_  _// o |/// /,'_/ / //7/ _/ / o | ,' _/\n" +
                "/ /_n / o // /_ / o / /  /  | ,'    / / /  ,'/ U // /_ /  ,'/ _/ /  ,' _\\ `. \n" +
                "|__,'/_n_//___//_n_/,'_n_\\ /_/     /_/ /_/`_\\\\_,' |__//_/\\/___//_/`_\\/___,' \n" +
                "\033[0m"; // Reset colore

        out.println(banner);
        if (isSocket) askSocketServerInfo();
        else askRMIServerInfo();
    }

    public void askSocketServerInfo() throws ExecutionException, IOException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";
        String defaultPort = "5000";
        boolean validInput;

        out.println("Please specify the following settings. The default value is shown between brackets.");

        out.print("Enter the server address [" + defaultAddress + "]: ");

        String address = ReadLine.run(inputThread);

        if (address.isEmpty()) {
            serverInfo.put("address", defaultAddress);
        } else  {
            serverInfo.put("address", address);
        }
//            else {
//                out.println("Invalid address!");
////                clearCli();
//                validInput = false;
//            }

        out.print("Enter the server port [" + defaultPort + "]: ");
        String port = ReadLine.run(inputThread);

        if (port.equals("")) {
            serverInfo.put("port", defaultPort);
            validInput = true;
        } else {

                serverInfo.put("port", port);
                validInput = true;
            }

//                notifyObserver(obs -> obs.onUpdateServerInfo(serverInfo));

        int numero = Integer.parseInt(serverInfo.get("port"));
        SERVER_INFO message = new SERVER_INFO(NetworkMessageType.SERVER_INFO, serverInfo.get("address"), numero );
                notifyObservers(message);
    }




    public void askRMIServerInfo() throws ExecutionException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";
        String defaultPort = "1099";
        boolean validInput;

        out.println("Please specify the following settings. The default value is shown between brackets.");

        out.print("Enter the server address [" + defaultAddress + "]: ");

        String address = ReadLine.run(inputThread);

        if (address.isEmpty()) {
            serverInfo.put("address", defaultAddress);
        } else  {
            serverInfo.put("address", address);
        }
//            else {
//                out.println("Invalid address!");
////                clearCli();
//                validInput = false;
//            }

        out.print("Enter the server port [" + defaultPort + "]: ");
        String port = ReadLine.run(inputThread);

        if (port.equals("")) {
            serverInfo.put("port", defaultPort);
            validInput = true;
        } else {

            serverInfo.put("port", port);
            validInput = true;
        }

//                notifyObserver(obs -> obs.onUpdateServerInfo(serverInfo));


    }


    public void askNickname(){
        out.print("Enter your nickname: ");
        try {
            String nickname = ReadLine.run(inputThread);
            NICKNAME_REQUEST nicknameRequest = new NICKNAME_REQUEST(NetworkMessageType.NICKNAME_REQUEST, nickname,clientController.)
            notifyObservers(n);
        } catch (ExecutionException e) {
            out.println(STR_INPUT_CANCELED);
        }
    }

}



