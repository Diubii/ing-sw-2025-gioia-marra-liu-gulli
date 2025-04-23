package org.polimi.ingsw.galaxytrucker;

//import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.controller.ClientController2;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
//import org.polimi.ingsw.galaxytrucker.view.Tui.Tui;
import org.polimi.ingsw.galaxytrucker.view.Tui.Tui2;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.view.View2;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ClientStartApp {

    public static void main(String[] args) throws ExecutionException, IOException, InterruptedException {

        //Aggiungere che se args contengono cli o gui ad esempio non chiede nemmeno e parte subito con quell'impostazione
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digita 1 per l'interfaccia grafica oppure digita 2 per l'interfaccia testuale e poi premi invio ");
        String scelta = scanner.nextLine();
        System.out.println("Digita 1 per RMI oppure digita 2 per SOCKET e poi premi invio ");
        String scelta2 = scanner.nextLine();

        View2 myT = null;
        ClientController2 controller;

        if ("1".equals(scelta)) {
            GuiJavaFx.main(args); //JavaFx
        } else {
            Boolean flag = scelta2.equals("2");
            controller = new ClientController2(myT, flag);

            myT = new Tui2(System.out, flag, controller);
            controller.setView(myT);

            ((Tui2) myT).start(); //Cli
        }


    }

}
