package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.util.Scanner;


public class Tui {

        public static void start() {
            System.out.println("Interfaccia Testuale");
            System.out.println("Scrivi 'uscita' per uscire.");

            Scanner scanner = new Scanner(System.in);
            String input;

            do {
                System.out.print("> ");
                input = scanner.nextLine();
                System.out.println("Hai scritto: " + input);
            } while (!input.equalsIgnoreCase("uscita"));

            scanner.close();
            System.out.println("Uscita dall'interfaccia testuale.");
        }

        public static void main(String[] args) {
            start();
        }
}
