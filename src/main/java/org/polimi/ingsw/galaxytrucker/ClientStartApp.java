package org.polimi.ingsw.galaxytrucker;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import org.polimi.ingsw.galaxytrucker.view.Tui.Tui;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ClientStartApp {

    public static void main(String[] args) throws ExecutionException, IOException, NotBoundException {

        //Aggiungere che se args contengono cli o gui ad esempio non chiede nemmeno e parte subito con quell'impostazione
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digita 1 per l'interfaccia grafica oppure digita 2 per l'interfaccia testuale e poi premi invio ");
        String scelta = scanner.nextLine();
        System.out.println("Digita 1 per RMI oppure digita 2 per SOCKET e poi premi invio ");
        String scelta2 = scanner.nextLine();

        View myT = null;
        ClientController controller;

        if ("1".equals(scelta)) {
            GuiJavaFx.main(args); //JavaFx
        } else {
            Boolean flag = scelta2.equals("2");
            controller = new ClientController(myT);

            myT = new Tui(System.out,flag, controller);

            ((Tui)myT).start(); //Cli
        }

        scanner.close();
    }

}
