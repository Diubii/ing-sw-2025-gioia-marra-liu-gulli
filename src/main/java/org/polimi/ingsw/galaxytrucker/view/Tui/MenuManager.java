package org.polimi.ingsw.galaxytrucker.view.Tui;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.enums.MenuText;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.util.Scanner;
import java.util.function.Consumer;

import static java.lang.System.out;

public class MenuManager {
    MenuText currentMenu = MenuText.NONE;


    public void setMenuText(GameState phase) {
        switch (phase) {

            case BUILDING_START -> currentMenu = MenuText.BUILDING_MENU;
            case SHIP_CHECK -> currentMenu = MenuText.CHECK_SHIP_MENU;
            case CREW_INIT -> currentMenu = MenuText.EMBARK_CREW_MENU;
            case FLIGHT ->  currentMenu = MenuText.FLIGHT_MENU;
            default -> currentMenu = MenuText.NONE;
        }

    }

    public void showCurrentMenu() {
        switch (currentMenu) {
            case BUILDING_MENU -> showBuildingMenu();
            case CHECK_SHIP_MENU -> showCheckShipMenu();
            case EMBARK_CREW_MENU -> showEmbarkCrewMenu();
            case FLIGHT_MENU -> showFlightMenu();

            default -> {
                return;
            }

        }
    }

    public void showBuildingMenu() {

        clearConsole();
        out.println("\n Building Phase Menu:");
        out.println("a. select the ship you want to view");
        out.println("b. Show Adventure card deck");
        out.println("c. Show face-up tiles on table");
        out.println("d. Draw a tile (randomly or choose one)");
        out.println("e. Show tile in hand");
        out.println("f. Rotate tile in hand ");
        out.println("g. Place tile");
        out.println("h. Discard tile");
        out.println("i. Reserve a tile in one of the slot(1 o 2)");
        out.println("j. Finish building");
        out.println("menu. show menu");
        out.println("reset. reset ");
    }

    public void showCheckShipMenu() {
        clearConsole();
        out.println("\n Check Ship Phase Menu:");
        out.println("a. View my ship");
        out.println("b. Remove tile");
        out.println("c. Send checkShip request");
        out.println("menu. show menu");


    }

    public void showEmbarkCrewMenu() {
        clearConsole();
        out.println("\n Embark Crew Phase Menu:");
        out.println("a. View my ship");
        out.println("b. Allocate crew in cabins");
    }

    public void showNoneMenu() {
        out.println("\n No menu available at this stage. Please wait ...");
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void showPhaseStart(GameState phase) {
        switch (phase) {
            case BUILDING_START -> {
                showBuildStart();
            }
            case FLIGHT -> {
                showFlightStart();
            }
            case SHIP_CHECK -> {
            }
            case CREW_INIT -> {
            }
            case END -> {
            }
            default -> {
                return;
            }
        }
    }

    public void showFlightMenu() {
        clearConsole();
        out.println("\n Flight Menu:");
        out.println("a. view a ship");
        out.println("b. view flight board");
        out.println("c. land early");
        out.println("d. ready for the next turn");

        out.println("menu. show menu");
    }




    private void showBuildStart() {
        String banner = "\033[1;34m" +

                "######                                                 #####                            \n" +
                "#     #  #    #  #  #      #####   #  #    #  ####     #     # #####   ##   #####  ##### \n" +
                "#     #  #    #  #  #      #    #  #  ##   # #    #    #         #    #  #  #    #   #   \n" +
                "######   #    #  #  #      #    #  #  # #  # #          #####    #   #    # #    #   #   \n" +
                "#     #  #    #  #  #      #    #  #  #  # # #  ###          #   #   ###### #####    #   \n" +
                "#     #  #    #  #  #      #    #  #  #   ## #    #    #     #   #   #    # #   #    #   \n" +
                "######    ####   #  ###### #####   #  #    #  ####      #####    #   #    # #    #   #   \n" +

                "\033[0m";
        out.println(banner);


    }


    private void showFlightStart() {
        String banner = "\033[1;34m" +
                " _______  __       __    _______  __    __  .___________.        _______..___________.    ___      .______     .___________.\n" +
                "|   ____||  |     |  |  /  _____||  |  |  | |           |       /       ||           |   /   \\     |   _  \\    |           |\n" +
                "|  |__   |  |     |  | |  |  __  |  |__|  | `---|  |----`      |   (----``---|  |----`  /  ^  \\    |  |_)  |   `---|  |----`\n" +
                "|   __|  |  |     |  | |  | |_ | |   __   |     |  |            \\   \\        |  |      /  /_\\  \\   |      /        |  |     \n" +
                "|  |     |  `----.|  | |  |__| | |  |  |  |     |  |        .----)   |       |  |     /  _____  \\  |  |\\  \\----.   |  |     \n" +
                "|__|     |_______||__|  \\______| |__|  |__|     |__|        |_______/        |__|    /__/     \\__\\ | _| `._____|   |__|     \n" +

                "\033[0m";
        out.println(banner);


    }


}



