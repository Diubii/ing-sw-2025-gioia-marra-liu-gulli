package it.polimi.ingsw.galaxytrucker;

//import it.polimi.ingsw.galaxytrucker.controller.ClientController;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.view.Tui.Tui;
import it.polimi.ingsw.galaxytrucker.view.View;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TUIstart {

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digita 1 per RMI oppure digita 2 per SOCKET e poi premi invio: ");
        String scelta2 = scanner.nextLine();

        View myT = null;
        ClientController controller;

        Boolean flag = scelta2.equals("2");
        controller = new ClientController(myT, flag);

        myT = new Tui(System.out, flag, controller);
        controller.setView(myT);

        ((Tui) myT).start(); //Cli

    }
}
