package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.*;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NICKNAME_REQUEST;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
//import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.view.View;


public class Tui extends Observable implements View {


    private static final String STR_INPUT_CANCELED = "CAXX";
    private static PrintStream out;
    private final Boolean isSocket;
    private final ClientController clientController;
//    ReadLine readLine = new ReadLine();
private static final Scanner scanner = new Scanner(System.in);
    private static final Object inpuLock = new Object();



    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

    }


    public String readLine(String prompt) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            synchronized (inpuLock) {
                System.out.print(prompt);
                if (scanner.hasNextLine()) {
                    return scanner.nextLine();
                } else {
                    throw new NoSuchElementException("Nessuna linea trovata.");
                }
            }
        });

        String input = future.get();
        executor.shutdown();
        return input;
    }




    public void start() throws ExecutionException, IOException, InterruptedException {

        String banner = "\033[1;34m" + // Colore Blu Chiaro
                "   __    _   __    _   _  __ _  __  _____ ___  _ __  __  _    ___  ___    ___\n" +
                " ,'_/  .' \\ / /  .' \\ | |/,'| |/,' /_  _// o |/// /,'_/ / //7/ _/ / o | ,' _/\n" +
                "/ /_n / o // /_ / o / /  /  | ,'    / / /  ,'/ U // /_ /  ,'/ _/ /  ,' _\\ `. \n" +
                "|__,'/_n_//___//_n_/,'_n_\\ /_/     /_/ /_/`_\\\\_,' |__//_/\\/___//_/`_\\/___,' \n" +
                "\033[0m"; // Reset colore

        out.println(banner);
        if (isSocket) askSocketServerInfo();
//        else askRMIServerInfo();
    }

    public void askSocketServerInfo() throws ExecutionException, IOException, InterruptedException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";
        String defaultPort = "5000";
        boolean validInput;

        out.println("Please specify the following settings. The default value is shown between brackets.");

    String prop = "Enter the server address [" + defaultAddress + "]: ";


        String address = readLine(prop);

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

//        out.print("Enter the server port [" + defaultPort + "]: ");
        String prompt = "Enter the server port [" + defaultPort + "]: ";
        String port = readLine(prompt);


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




//    public void askRMIServerInfo() throws ExecutionException {
//        Map<String, String> serverInfo = new HashMap<>();
//        String defaultAddress = "localhost";
//        String defaultPort = "1099";
//        boolean validInput;
//
//        out.println("Please specify the following settings. The default value is shown between brackets.");
//
//        out.print("Enter the server address [" + defaultAddress + "]: ");
//
//        String address = ReadLine.run();
//
//        if (address.isEmpty()) {
//            serverInfo.put("address", defaultAddress);
//        } else  {
//            serverInfo.put("address", address);
//        }
////            else {
////                out.println("Invalid address!");
//////                clearCli();
////                validInput = false;
////            }
//
//        out.print("Enter the server port [" + defaultPort + "]: ");
//        String port = ReadLine.run();
//
//        if (port.equals("")) {
//            serverInfo.put("port", defaultPort);
//            validInput = true;
//        } else {
//
//            serverInfo.put("port", port);
//            validInput = true;
//        }
//
////                notifyObserver(obs -> obs.onUpdateServerInfo(serverInfo));
//
//
//    }


    public void askNickname() throws IOException, ExecutionException, InterruptedException {

              String nickname = readLine("Enter your nickname: ");
        NICKNAME_REQUEST nicknameRequest = new NICKNAME_REQUEST(NetworkMessageType.NICKNAME_REQUEST, nickname,true);

            notifyObservers(nicknameRequest);

    }




    public void showGenericMessage(String message){
        System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);
    }

    @Override
    public void askMaxPlayers() throws ExecutionException, InterruptedException, IOException {
        String number = readLine("YOU ARE THE HOST \n INSERT MAX PLAYERS: ");
//        NICKNAME_REQUEST nicknameRequest = new NICKNAME_REQUEST(NetworkMessageType.NICKNAME_REQUEST, nickname,true);

        notifyObservers(number);
    }


}



