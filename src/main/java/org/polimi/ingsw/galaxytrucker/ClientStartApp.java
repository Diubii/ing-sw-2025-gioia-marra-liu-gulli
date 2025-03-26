package org.polimi.ingsw.galaxytrucker;

import org.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import org.polimi.ingsw.galaxytrucker.view.Tui.Tui;

import java.util.Scanner;

public class ClientStartApp {

    public static void main(String[] args) {

        //Aggiungere che se args contengono cli o gui ad esempio non chiede nemmeno e parte subito con quell'impostazione
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digita 1 per l'interfaccia grafica oppure digita 2 per l'interfaccia testuale e poi premi invio ");
        String scelta = scanner.nextLine();

        if ("1".equals(scelta)) {
            GuiJavaFx.main(args); //JavaFx
        } else {
            Tui.start(); //Cli
        }

        scanner.close();
    }

}
