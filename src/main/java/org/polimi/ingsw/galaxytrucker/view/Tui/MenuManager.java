package org.polimi.ingsw.galaxytrucker.view.Tui;

import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.enums.MenuText;

import java.util.Scanner;

import static java.lang.System.out;

public class MenuManager {
    MenuText currentMenu = MenuText.NONE;


    public void setMenuText(GameState phase) {
        switch (phase) {

            case BUILDING_START,BUILDING_END -> currentMenu = MenuText.BUILDING_MENU;
            case SHIP_CHECK -> currentMenu = MenuText.CHECK_SHIP_MENU;
            default -> currentMenu = MenuText.NONE;
        }

    }

    public void showCurrentMenu() {
        switch (currentMenu) {
            case BUILDING_MENU -> showBuildingMenu();
            case CHECK_SHIP_MENU -> showCheckShipMenu();
            case EMBARK_CREW_MENU -> showEmbarkCrewMenu();
            default -> showNoneMenu();

        }
    }
    public void showBuildingMenu() {
        out.println("\n Building Phase Menu:");
        out.println("a. Show ship for each player");
        out.println("b. Show Adventure card deck");
        out.println("c. Show face-up tiles on table");
        out.println("d. Draw a tile (random or specific)");
        out.println("e. Show tile in hand");
        out.println("f. Rotate tile in hand (left or right)");
        out.println("g. Move tile (to ship or reserve)");
        out.println("h. Place tile");
        out.println("i. Discard tile");
        out.println("j. Finish building");
        out.println("menu. show menu");
    }

    public void showCheckShipMenu() {
        out.println("\n Check Ship Phase Menu:");
        out.println("a. View my ship");
        out.println("b. Remove tile");
        out.println("c. Send checkShip request");

    }
    public void showEmbarkCrewMenu() {
        out.println("\n Embark Crew Phase Menu:");
        out.println("a. View my ship");
        out.println("b. Allocate crew in cabins");
    }

    public void showNoneMenu() {
        out.println("\n No menu available at this stage. Please wait ...");
    }


}
