package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.util.Pair;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.*;

import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
//import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.*;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;


import static org.polimi.ingsw.galaxytrucker.view.Tui.util.InputUtils.parseCoordinate;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils.printShip;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TilePrintUtils.printTile;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.RESET;


public class Tui implements View, Observable {




    private static final String STR_INPUT_CANCELED = "CAXX";
    private static PrintStream out;
    private final Boolean isSocket;
    private final ClientController clientController;
    //    ReadLine readLine = new ReadLine();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Object inputLock = new Object();
    private static final Object outputLock = new Object();

    private final ArrayList<Observer> observers = new ArrayList<>();


    public MenuManager getMenuManager() {
        return menuManager;
    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    private MenuManager menuManager = new MenuManager();




    private final Object panelLock = new Object();


    private volatile boolean shouldExit = false;
    private ExecutorService inputExecutor = Executors.newSingleThreadExecutor();


    private volatile boolean viewingFaceUpTiles = false;

    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

    }

    @Override
    public Boolean autoShowUpdates() {
        return false;
    }

    private volatile CompletableFuture<String> currentInputFuture = null;
    private static final AtomicBoolean stopInput = new AtomicBoolean(false);

    private Boolean flag = false;

    // Metodo per bloccare l'input manualmente
    public static void blockInput() {
        stopInput.set(true);
    }

    // Metodo per sbloccare l'input manualmente
    public static void unblockInput() {
        stopInput.set(false);
    }

    public String readLine(String prompt) throws InterruptedException, ExecutionException {
        currentInputFuture = new CompletableFuture<>();
        flag = false;


        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            System.out.print(prompt);
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (flag) {
                    Thread.currentThread().interrupt();

                }
                currentInputFuture.complete(line);
            }

        }).start();

        String input = currentInputFuture.get();

        if (input.contains("RESET")) {
            flag = true;
            input = "RESET";
        }

        return input;
    }

    @Override
    public void forceReset() {
        System.out.println();
        if (currentInputFuture != null && !currentInputFuture.isDone()) {
            currentInputFuture.complete("RESET");

        }
    }


    public void start() throws ExecutionException, IOException, InterruptedException {

        MenuManager.clearConsole();
        System.out.println("\r".repeat(100));

        String banner = "\033[1;34m" + // Colore Blu Chiaro
                "   __    _   __    _   _  __ _  __  _____ ___  _ __  __  _    ___  ___    ___\n" +
                " ,'_/  .' \\ / /  .' \\ | |/,'| |/,' /_  _// o |/// /,'_/ / //7/ _/ / o | ,' _/\n" +
                "/ /_n / o // /_ / o / /  /  | ,'    / / /  ,'/ U // /_ /  ,'/ _/ /  ,' _\\ `. \n" +
                "|__,'/_n_//___//_n_/,'_n_\\ /_/     /_/ /_/`_\\\\_,' |__//_/\\/___//_/`_\\/___,' \n" +
                "\033[0m"; // Reset colore

        out.println(banner);
        askServerInfo();
    }

    public void askServerInfo() throws ExecutionException, IOException, InterruptedException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";

        int defaultPort = isSocket ? 5000 : 1099;
        int portNumber = -1;
        synchronized (outputLock) {
            out.println("Please specify the following settings. The default value is shown between brackets.");


            String address = readLine("Enter the server address [" + defaultAddress + "]: ").trim();
            serverInfo.put("address", address.isEmpty() ? defaultAddress : address);

            String port;

            do {
                port = readLine("Enter the server port [" + defaultPort + "]: ").trim();
                if (port.isEmpty()) {
                   portNumber = defaultPort;
                   break;
                }
                try {
                    portNumber = Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    out.println("Errore: la porta deve essere un numero intero.");
                }
            } while (portNumber == -1);
        }
        SERVER_INFO message = new SERVER_INFO(serverInfo.get("address"),portNumber );
        notifyObservers(message);
    }

        public void askNickname() {
        try {
            String nickname = readLine("Enter your nickname: ");
            clientController.handleNicknameInput(nickname);
        } catch (InterruptedException | ExecutionException | IOException e) {
            System.err.println("Error reading nickname: " + e.getMessage());
        }
    }

    public void askJoinOrCreateRoom() {
        try {
            String choice;
            do {
                choice = readLine("Create Room (a) or Join Room (b): ").trim().toLowerCase();
                if (!choice.equals("a") && !choice.equals("b")) {
                    synchronized (outputLock) {
                        out.println("Invalid input. Please enter 'a' or 'b'.");
                    }
                }
            } while (!choice.equals("a") && !choice.equals("b"));


            clientController.handleCreateOrJoinChoice(choice);

        } catch (Exception e) {
            System.err.println("Error while choosing: " + e.getMessage());
        }
    }

    public void askCreateRoom() throws ExecutionException {
        try {
            String maxPlayersStr;
            int maxPlayers;
            boolean isLearningMatch;
            synchronized (outputLock) {
                maxPlayersStr = readLine("Set MAX players for Game (2-4): ");
                maxPlayers = Integer.parseInt(maxPlayersStr);

                if (maxPlayers < 2 || maxPlayers > 4) {

                    out.println("Please enter a number between 2 and 4.");
                    askCreateRoom(); // Retry
                    return;
                }

                String learningInput = readLine("Is this a Learning Match? (y/n): ");

                if (learningInput.equals("y")) {
                    isLearningMatch = true;
                } else if (learningInput.equals("n")) {
                    isLearningMatch = false;
                } else {
                    out.println("Invalid input. Please type 'y' or 'n'.");
                    askCreateRoom();  // Retry
                    return;
                }
            }

            clientController.handleCreateChoice(maxPlayers, isLearningMatch);

        } catch (NumberFormatException | InterruptedException e) {
            out.println("Invalid number format.");
            askCreateRoom(); // Retry
        }
    }


    public void askRoomCode() {
        synchronized (outputLock) {
            out.println("\nPlease enter the Lobby ID you want to join:");

            try {
                String input = readLine("> ").trim();
                int lobbyId = Integer.parseInt(input);


                clientController.handleJoinChoice(lobbyId);

            } catch (NumberFormatException e) {
                out.println("Invalid Lobby ID. Please enter a number.");
                askRoomCode();
            } catch (InterruptedException | ExecutionException e) {
                out.println("Error while joining room: " + e.getMessage());
            }
        }
    }

    public void showLobbies(List<LobbyInfo> lobbies) {
        if (lobbies.isEmpty()) {
            synchronized (outputLock) {
                out.println(" No game rooms, Returning to main menu. ");
                askJoinOrCreateRoom();
            }
            return;
        }

        synchronized (outputLock) {
            out.println("：List of rooms");
            for (LobbyInfo lobby : lobbies) {
                Boolean isLearningMatch = lobby.isLearningMatch();
                String matchType = isLearningMatch ? "Learning" : "Normal";

                out.printf("Lobby ID: %d | Host: %s | GameType: "+  matchType +" |Players: (%d/%d)  \n",
                        lobby.getLobbyID(),
                        lobby.getHost(),

                        lobby.getConnectedPlayers(),
                        lobby.getMaxPlayers());
            }

        }

    }


    @Override
    public void showPlayerJoined(PlayerInfo infoPlayer) {


        out.println("Giocatore entrato nella lobby:");
        switch (infoPlayer.getColor()) {
            case RED -> System.out.println(RED + "█" + RESET + " " + infoPlayer.getNickName());
            case GREEN -> System.out.println(GREEN + "█" + RESET + " " + infoPlayer.getNickName());
            case BLUE -> System.out.println(BLUE + "█" + RESET + " " + infoPlayer.getNickName());
            case YELLOW -> System.out.println(BRIGHT_YELLOW + "█" + RESET + " " + infoPlayer.getNickName());
        }
        System.out.println();

        out.println();
    }

    @Override
    public void showPlayersLobby(PlayerInfo myinfo, ArrayList<PlayerInfo> infoPlayer) {
        System.out.println("Giocatori nella lobby: ");
        System.out.print("IO: ");
        switch (myinfo.getColor()) {
            case RED -> System.out.println(RED + "█" + RESET + " ");
            case GREEN -> System.out.println(GREEN + "█" + RESET + " ");
            case BLUE -> System.out.println(BLUE + "█" + RESET + " ");
            case YELLOW -> System.out.println(BRIGHT_YELLOW + "█" + RESET + " ");
        }

        for (int p = 0; p < infoPlayer.size(); p++) {
            switch (infoPlayer.get(p).getColor()) {
                case RED -> System.out.println(RED + "█" + RESET + " " + infoPlayer.get(p).getNickName());
                case GREEN -> System.out.println(GREEN + "█" + RESET + " " + infoPlayer.get(p).getNickName());
                case BLUE -> System.out.println(BLUE + "█" + RESET + " " + infoPlayer.get(p).getNickName());
                case YELLOW -> System.out.println(BRIGHT_YELLOW + "█" + RESET + " " + infoPlayer.get(p).getNickName());
            }
        }
        System.out.println();
    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {

        GameState phase = phaseUpdate.getState();
        menuManager.showPhaseStart(phase);
        if (phase.equals(GameState.BUILDING_TIMER)) {
            new Thread(() -> {

                showGenericMessage("TIMER STARTED !!");
            }).start();
            return;
        } else {
            menuManager.setMenuText(phase);
            if (phase.equals(GameState.BUILDING_START) || phase.equals(GameState.SHIP_CHECK) || phase.equals(GameState.CREW_INIT)|| phase.equals(GameState.FLIGHT)) {
                toShowCurrentMenu();
                handleChoiceForPhase(phase);
            }
        }

    }

    public void toShowCurrentMenu() {
        synchronized (outputLock) {
            menuManager.showCurrentMenu();
        }
    }

    public void handleChoiceForPhase(GameState phase) {
        switch (phase) {
            case BUILDING_START -> showBuildingMenu();
            case SHIP_CHECK -> showcheckShipMenu();
            case CREW_INIT -> showembarkCrewMenu();
            case FLIGHT -> showFlightMenu();
            default -> {
//                out.println("Please wait. No input is required at this stage.");
            }
        }
    }

    @Override
    public void showBuildingMenu() {
        try {
            String input = readLine("\nChoose an option (a–k) or menu: ").trim().toLowerCase();
            if (checkReset(input)) return;

            while (input.equals("m") || input.equals("menu") || input.equals("?")) {
                menuManager.showCurrentMenu();
                input = readLine("\nChoose an option (a–k) or menu: ").trim().toLowerCase();

            }

            clientController.handleBuildingMenuChoice(input);

        } catch (InterruptedException | ExecutionException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void FetchMyShip() {
        new Thread(() -> {
            try {
                clientController.handleFetchShip(clientController.getNickname());
                menuManager.showCurrentMenu();


            } catch (Exception e) {
                showGenericMessage("Error fetching ship: " + e.getMessage() + ", please try again");
                FetchMyShip();
                throw new RuntimeException(e);
            }

        }).start();
    }

    @Override
    public void askShowFaceUpTiles() {
        showFaceUpTiles();


    }

    @Override
    public void askViewAdventureDecks() {
        boolean valid = false;
        do {
            try {
                String DeckIDStr = readLine("Enter which Deck you want to view (1~4)> ").trim();
                int DeckID = Integer.parseInt(DeckIDStr);

                if (checkReset(DeckIDStr)) return;
                ;


                if (DeckID < 1 || DeckID > 4) {
                    out.println("Deck ID not valid. Please enter a number between 1 and 4.");
                } else {
                    clientController.viewAdventureCardDeck(DeckID - 1);
                    valid = true;
                }
            } catch (Exception e) {
                out.println("Error during ask view adventure Decks: " + e.getMessage());
            }
        } while (!valid);
    }

    @Override
    public void askFetchShip() {
        boolean success = false;
        do {
            try {
                String targetName = readLine("Enter the nickname of the player to fetch their ship: ").trim();
                clientController.handleFetchShip(targetName);
                success = true;

            } catch (InterruptedException | ExecutionException e) {
                showGenericMessage("Error fetching ship: " + e.getMessage() + ", please try again");
            } catch (Exception e) {
                showGenericMessage("Unexpected error: " + e.getMessage());
            }
        } while (!success);

    }

    @Override
    public void askRotation() {
        boolean valid = false;
        do {
            try {
                out.println("Enter rotation degree (90, 180, 270, or 360): ");
                String input = readLine("> ").trim();
                if (checkReset(input)) return;
                ;

                int rotation = InputUtils.parseRotation(input);
                clientController.rotateCurrentTile(rotation);
                valid = true;
            } catch (IllegalArgumentException e) {
                out.println("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                out.println("Unexpected error: " + e.getMessage());
            }
        } while (!valid);
    }

    @Override
    public void askPosition() throws ExecutionException {
        boolean valid = false;
        Position pos = null;
        do {
            try {
                String input = readLine("Enter position to move the tile to (format: (x,y)): ").trim();
                if (checkReset(input)) return;
                pos = parseCoordinate(input);
                clientController.setCurrentPos(pos.getX() - 4, pos.getY() - 5);
                valid = true;
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                out.println("Unexpected error: " + e.getMessage());
            }
        } while (!valid);
    }

    @NeedsToBeCompleted
// TileView
    @Override
    public void showTile(Tile tile) {
        if(tile != null){
            printTile(tile);
        }
    }

    @Override
    public void handleFaceUpTilesUpdate(){

    }

    @Override
    public void showFaceUpTiles() {
        List<Tile> faceUpTiles = clientController.getMyModel().getFaceUpTiles();

        int size = faceUpTiles.size();

        if (size == 0) {
            out.println("Face up Tiles is empty");
            showBuildingMenu();
            return;
        }

        out.println("Face up tiles size: " + faceUpTiles.size());
        TilePrintUtils.printTileList(new ArrayList<>(faceUpTiles), 3);

    }


    @Override
    public void askDrawTile() {
        try {
            boolean validInput = false;
            do {
                out.println("How do you want to draw a tile?");
                out.println("1) draw a random tile(face down)");
                out.println("2) choose a face up tile");
                out.println("3) choose from your reserved tiles");
                String choice = readLine("Choose(1/2/3) > ");

                if (checkReset(choice)) return;
                ;

                switch (choice) {
                    case "1": {
                        clientController.handleDrawFaceDownTile();
                        validInput = true;
                        break;
                    }
                    case "2": {
                        clientController.startChooseTile();
                        validInput = true;
                        break;

                    }
                    case "3": {
                        askPickOrPlaceReservedTile(true);
                        validInput = true;
                        break;
                    }

                }

            } while (!validInput);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void askChooseTile() {

        try {
            boolean validInput = false;
            do {
                List<Tile> faceUpTiles = clientController.getMyModel().getFaceUpTiles();
                if (faceUpTiles.isEmpty()) {
                    out.println("No tiles available to choose.");
                    showBuildingMenu();
                    break;
                }

                String input = readLine("Enter the Tile ID to draw, or use R to refresh: ").trim().toLowerCase();
                if (checkReset(input)) return;
                ;

                if (input.equalsIgnoreCase("R")) {
                    out.println("Refreshing tile list");
                    Thread.sleep(200);
                    showFaceUpTiles();

                } else {

                    try {
                        int tileID = Integer.parseInt(input);
                        Tile selectedTile = clientController.getMyModel().getFaceUpTiles().stream()
                                .filter(t -> t.getId() == tileID)
                                .findFirst()
                                .orElse(null);
                        if (selectedTile != null) {
                            clientController.handleChooseFaceUpTile(selectedTile);
                            validInput = true;
                        } else {
                            out.println("Tile ID not found. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid tile ID. Please enter a number.");
                    }
                }

            } while (!validInput);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private void showTileSlots() {
        Tile[] reservedTiles = clientController.getReservedTiles();


        out.println("Reserved Slot 1:");
        if (reservedTiles[0] != null) {
            showTile(reservedTiles[0]);
        } else {
            out.println("Empty");
        }

        out.println("Reserved Slot 2:");
        if (reservedTiles[1] != null) {
            showTile(reservedTiles[1]);
        } else {
            out.println("Empty");
        }
    }

    public void askPickOrPlaceReservedTile(boolean isPicking) {

        Tile[] reservedTiles = clientController.getReservedTiles();
        if (isPicking) {
            if (!clientController.hasTileInHand()) {
                if ((reservedTiles[0] != null || reservedTiles[1] != null)) {
                    showTileSlots();
                    try {
                        boolean validInput = false;
                        int slotIndex = -1;
                        do {
                            String input = readLine("Enter the Slot Index to pick (1 or 2)> ");
                            if (checkReset(input)) return;

                            if (input.equals("1") || input.equals("2")) {
                                slotIndex = Integer.parseInt(input);
                                validInput = true;
                            } else {
                                out.println("Invalid input. Please enter 1 or 2.");
                            }
                        } while (!validInput);

                        clientController.handlePickReservedTile(slotIndex - 1, true);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    showGenericMessage("There is no tile to pick");
                    showBuildingMenu();
                }
            } else {
                showGenericMessage("You already have a tile in hand to place ");
                showBuildingMenu();
            }
        }
        if (!isPicking) {
            if (clientController.hasTileInHand() && !isPicking) {
                if ((reservedTiles[0] == null || reservedTiles[1] == null)) {
                    showTileSlots();
                    try {
                        boolean validInput = false;
                        int slotIndex = -1;
                        do {
                            String input = readLine("Enter the Slot Index to place (1 or 2)> ");
                            if (checkReset(input)) return;

                            if (input.equals("1") || input.equals("2")) {
                                slotIndex = Integer.parseInt(input);
                                validInput = true;
                            } else {
                                out.println("Invalid input. Please enter 1 or 2.");
                            }
                        } while (!validInput);

                        clientController.handlePickReservedTile(slotIndex - 1, false);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    showGenericMessage("There is no place to place the tile");
                    showBuildingMenu();
                }
            } else {
                showGenericMessage("You have no tile in hand to place");
                showBuildingMenu();
            }

        }


    }


    @Override
    public void askTilePlacement() {
        try {

            if (!clientController.hasTileInHand()) {
                out.println(" No tile in hand to place.");
                showBuildingMenu();
                return;
            }

            out.println(" Current tile info:");
            printTile(clientController.getCurrentTileInHand());

            if (clientController.getCurrentPosition() != null) {
                out.println("Current position:  X: " + clientController.getCurrentPosition().getX() + ",  Y:" + clientController.getCurrentPosition().getY());
            } else {
                out.println("Current position: null");
                askPosition();
            }
            out.println("Do you want to place this tile at the current position and rotation? (y/n)");
            String input = readLine("> ").trim().toLowerCase();
            if (checkReset(input)) return;

            boolean confirm = input.equals("y");
            if (confirm) {
                clientController.handleTilePlacement(true);
            } else {
                clientController.resetCurrentPos();
                out.println("Tile not placed. You can rotate or move it again.");
                showBuildingMenu();
            }

        } catch (Exception e) {
            out.println(" Error during tile placement ");
            e.printStackTrace();
            ;
        }

    }

    @Override
    public void askFinishBuilding() {

    }

    @Override
    public void showcheckShipMenu() {
        try {
            String input = readLine("\nChoose an option (a–c) or menu: ").trim().toLowerCase();
            clientController.handleCheckShipChoice(input);
        } catch (InterruptedException | ExecutionException e) {
            out.println(" Error: " + e.getMessage());
        }
    }

    @Override
    public void showembarkCrewMenu() {

        try {
            String input = readLine("\nChoose an option (a–b) or menu: ").trim().toLowerCase();
            clientController.handleEmbarkCrewMenu(Character.toString(input.charAt(input.length() - 1)));
        } catch (InterruptedException | ExecutionException e) {
            out.println(" Error: " + e.getMessage());
        }
    }

    @Override
    public void askRemoveTile(Ship ship) {

        boolean valid = false;
        Position pos1 = null;
        do {
            try {
                String input = readLine("Enter position to remove the Tile from(format: (x,y)): ").trim();
                pos1 = parseCoordinate(input);

                Position pos = new Position(pos1.getX() - 4, pos1.getY() - 5);

                if (!Util.inBoundaries(pos.getX(), pos.getY()) || ship.getInvalidPositions().contains(pos) || ship.getShipBoard()[pos.getY()][pos.getX()].getTile() == null) {

                    throw new IllegalArgumentException("Invalid Position " + pos);

                }


                Tile tile = ship.getShipBoard()[pos.getX()][pos.getY()].getTile();


                clientController.getMyModel().addTileToRemove(tile.getId());
                ship.removeTile(pos, true);

                if (ship.remainingTiles() == 0) {
                    System.out.println("Your ship is a ghost, go back to the menuuuuuu");
                    showcheckShipMenu();
                    return;
                }

                String input2 = readLine("Do you want to finish? (y/n)").trim().toLowerCase();


                while (!input2.equals("n") && !input2.equals("y")) {


                    System.out.println("Invalid input. Please enter (y/n))");
                    input2 = readLine("Do you want to finish? (y/n)").trim().toLowerCase();


                }
                if (input2.equals("y")) {
                    menuManager.showCurrentMenu();
                    showcheckShipMenu();
                    valid = true;
                }


            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                out.println("Unexpected error: " + e.getMessage());
            }
        } while (!valid);

        showcheckShipMenu();
    }


    @Override
    public void showShip(Ship targetShipView, String nickname) {
        printShip(targetShipView);
    }

    @Override
    public void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException {
        String inputStr;
        int chosenPos = -1;

        MenuManager.clearConsole();
        System.out.println("Free FlightBoard starting positions: ");
        for (Integer i : validPositions) {
            System.out.println(" --> " + i);
        }

        boolean valid = false;

        do {
            inputStr = readLine("Choose one > ").trim();
            if (inputStr.isEmpty()) {
                System.out.println("Input vuoto, riprova.");
                continue;
            }

            try {
                chosenPos = Integer.parseInt(inputStr);
                if (validPositions.contains(chosenPos)) {
                    valid = true;
                } else {
                    System.out.println("Posizione non valida. Riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }

        } while (!valid);

        AskPositionResponse askPositionResponse = new AskPositionResponse(id, chosenPos);
        clientController.getClient().sendMessage(askPositionResponse);
    }

    public void showGenericMessage(String message) {

        synchronized (outputLock) {
            System.out.print("\n"); // Torna all'inizio della riga
//            System.out.print(" ".repeat(100)); // Cancella eventuale scrittura
//            System.out.print("\r"); // Torna di nuovo all'inizio
            System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);

            // Ripristina il prompt e quello che l'utente aveva scritto
            System.out.print("> ");
        }

    }

    @Override
    public void chooseComponent(Ship myShip, ActivatableComponent component) throws ExecutionException, InterruptedException {
        printShip(myShip);
        System.out.println("You can activate your " + component.name());
        int totalBatteries = myShip.getnBatterieLeft();
        Boolean stop = false;
        Position compPos, battPos;
        ArrayList<Position> activatedComponentPositions = new ArrayList<>();
        ArrayList<Position> usedBatteryPositions = new ArrayList<>();

        //Ciclo while
        //Chiede finchè non dico no o finchè non termino le batterie
        while (stop == false && totalBatteries > 0) {
            System.out.print("Activate " + component.name() + " specify the coordinates or type no to stop");
            String input = readLine(" input (x,y) or no: ").trim().toLowerCase();
            if (input.equals("n") || input.equals("no")) {
                stop = true;
            } else {
                compPos = parseCoordinate(input);
                compPos.setPos(compPos.getX() - 4, compPos.getY() - 5);
                if (!Util.inBoundaries(compPos.getY(), compPos.getX()) || myShip.getInvalidPositions().contains(compPos)) {
                    System.out.println("Wrong Coordinates, out of bounds ");
                }
                //Verifica anche tipo
                else if (!myShip.getComponentPositionsFromName(component.name()).contains(compPos)) {
                    System.out.println("Wrong Coordinates, another kind of component at that coordinates ");
                } else if (activatedComponentPositions.contains(compPos)) {
                    System.out.println("Wrong Coordinates, already activated ");
                } else {
                    System.out.print("Specify the coordinates of the battery to use");
                    input = readLine(" input (x,y): ").trim().toLowerCase();
                    battPos = parseCoordinate(input);
                    battPos.setPos(battPos.getX() - 4, battPos.getY() - 5);
                    if (!Util.inBoundaries(battPos.getY(), battPos.getX()) || myShip.getInvalidPositions().contains(battPos)) {
                        System.out.println("Wrong Coordinates, out of bounds ");
                    } else if (!myShip.getComponentPositionsFromName("batterySlot").contains(compPos)) {
                        System.out.println("Wrong Coordinates, another kind of component at that coordinates ");
                    } else {
                        int batteriesInSlot = ((BatterySlot) myShip.getComponentFromPosition(battPos)).getBatteriesLeft();
                        Position finalBattPos = battPos;
                        int timesSlotUsed = (int) usedBatteryPositions.stream().filter(p -> p.equals(finalBattPos)).count();
                        if (batteriesInSlot <= timesSlotUsed) {
                            System.out.println("Batteries already depleted ad those coordinates ");
                        } else {
                            //Aggiungo a liste
                            activatedComponentPositions.add(compPos);
                            usedBatteryPositions.add(battPos);
                            totalBatteries--;
                        }
                    }
                }
            }
        }
        if (totalBatteries == 0) {
            System.out.println("Batterie terminate");
        }
        clientController.handleActivateComponentResponse(component, activatedComponentPositions, usedBatteryPositions);
    }

    //Non considerato che giocatore non ne abbia abbastanza, controllo lato server
    //per fare che il giocatore perde direttamente
    public void chooseDiscardCrew(Ship myShip, int nCrewToDiscard) throws ExecutionException, InterruptedException {
        ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
        printShip(myShip);
        ArrayList<Position> housingPositions = new ArrayList<>();
        Position housingPos;
        System.out.println("You have to discard " + nCrewToDiscard + " crew:");
        while (nCrewToDiscard > 0) {
            System.out.print("specify the coordinates of a Cabin");

            try {
                String input = readLine(" input (x,y): ").trim().toLowerCase();
                housingPos = parseCoordinate(input);
                housingPos.setPos(housingPos.getX() - 4, housingPos.getY() - 5);
                System.out.println(housingPos.getY() + " " + housingPos.getX());

                if (!Util.inBoundaries(housingPos.getY(), housingPos.getX()) || myShip.getInvalidPositions().contains(housingPos)) {
                    System.out.println("Wrong Coordinates, out of bounds ");
                } else if (!myShip.getComponentPositionsFromName("ModularHousingUnit").contains(housingPos) && !myShip.getComponentPositionsFromName("CentralHousingUnit").contains(housingPos)) {
                    System.out.println("Wrong Coordinates, another kind of component at that coordinates ");
                } else {
                    //Controllare che non comparsa già troppe volte in lista

                    int nCrewAtPos = 0;
                    //N di crew sia umani che alieni
                    switch (myShip.getComponentFromPosition(housingPos).accept(componentNameVisitor)) {
                        case "ModularHousingUnit":
                            nCrewAtPos = ((ModularHousingUnit) myShip.getComponentFromPosition(housingPos)).getNCrewMembers();
                            break;
                        case "CentralHousingUnit":
                            nCrewAtPos = ((CentralHousingUnit) myShip.getComponentFromPosition(housingPos)).getNCrewMembers();

                    }
                    Position finalHousingPos = housingPos;
                    int timesSlotUsed = (int) housingPositions.stream().filter(p -> p.equals(finalHousingPos)).count();
                    if (nCrewAtPos <= timesSlotUsed) {
                        System.out.println("Crew already selected ad those coordinates ");
                    } else {
                        //Se tutto ok aggiungere

                    housingPositions.add(housingPos);
                        nCrewToDiscard--;
                        System.out.println("Accepted, you have " + nCrewToDiscard + " crew members to discard left");
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("invalid input, out of bounds ");

            }
        }

        clientController.handleDiscardCrewMembersResponse(housingPositions);
    }

    public void chooseTroncone(ArrayList<Ship> tronconi) throws ExecutionException, InterruptedException {
        //Mostro tutti i tronconi numerandoli
        System.out.println("The ship divided into pieces choose one to continue ");
        for (int i = 0; i < tronconi.size(); i++) {
            out.println();
            System.out.println("Ship Fragment " + (i + 1) + ": ");
            printShip(tronconi.get(i));
        }
        int choice = -1;
        while (choice < 0 || choice >= tronconi.size()) {
            System.out.println("Choose a Fragment as your ship: ");
            try {
                choice = Integer.parseInt(readLine(" > ").trim().toLowerCase());
            } catch (Exception e) {
                System.out.println("Invalid choice entered, please try again");
            }
            if (choice < 0 || choice >= tronconi.size()) {
                System.out.println("Invalid choice entered, choose a number equivalent to a ship ");
            }

        }

        choice--;
        //handle response indicando numero
        clientController.handleTrunkResponse(choice);

    }

    @Override
    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {

        //salvo tutte le posizioni delle housing

        ArrayList<Position> housinPos = myShip.getComponentPositionsFromName("ModularHousingUnit");

        int nBrownAliens = 0;
        int nPurpleAliens = 0;

        CrewInitUpdate crewInitUpdate = new CrewInitUpdate();

        System.out.println("You have " + housinPos.size() + " Cabins to fill");


        if (!housinPos.isEmpty()) {
            for (Position pos : housinPos) {

                Slot tempSlot = myShip.getShipBoard()[pos.getY()][pos.getX()];

                if (tempSlot != null) {
                    if (tempSlot.getTile() != null) {

                        ArrayList<String> choiches = new ArrayList<>();

                        ModularHousingUnit housing = (ModularHousingUnit) tempSlot.getTile().getMyComponent();

                        if (Util.checkNearLFS(pos, housing.getAlienColor(), myShip)) {

                            if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("PurpleLifeSupportSystem") && housing.getAlienColor().equals(AlienColor.PURPLE) && nPurpleAliens == 0) {
                                CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.PURPLE);
                                choiches.add("purple");
                            }

                            if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("BrownLifeSupportSystem") && housing.getAlienColor().equals(AlienColor.BROWN) && nBrownAliens == 0) {
                                CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.BROWN);
                                choiches.add("brown");

                            }
                        }

                        CabinUnitAscii.printCabinUnitWithFigures(2, false, AlienColor.EMPTY);
                        choiches.add("human");


                        boolean correct = false;

                        while (!correct) {
                            StringBuilder prompt = new StringBuilder("Type ");
                            for (String choice : choiches) {
                                prompt.append("(").append(choice).append(") ");
                            }
                            String choice = readLine(prompt.toString());

                            switch (choice) {
                                case "purple": {
                                    nPurpleAliens++;
                                    correct = true;
                                    crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.PURPLE));
                                    break;
                                }

                                case "brown": {
                                    nBrownAliens++;
                                    correct = true;
                                    crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.BROWN));
                                    System.out.println("You cannot add a brown alien to this cabin.");
                                    break;
                                }

                                case "human": { //Si inseriscono due umani
                                    correct = true;
                                    crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.EMPTY));
                                    crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.EMPTY));
                                    break;
                                }

                                default: {
                                    System.out.println("Invalid choice: " + choice);
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }

        //fine for

        notifyObservers(crewInitUpdate);

    }

    @Override
    public void askActivateAdventureCard() {
        askYesNoConfirmation(
                "Congratulazioni, soddisfi i requisiti. Vuoi attivare l'effetto della carta?",
                () -> clientController.sendActivateAdventureCardResponse(true),
                () -> {
                    clientController.sendActivateAdventureCardResponse(false);
                    showGenericMessage("Hai scelto di non attivare l'effetto.");
                }
        );
    }

    @Override
    public void askDrawCard() {
        askYesConfirmation(
                "Sei il leader. Inserire un 'y' per pescare la carta avventura: ",
                clientController::sendDrawAdventureCardRequest
        );
    }



    // chiedere di selezionare uno dei pianeti disponibili
    @Override
    public void askSelectPlanetChoice(ArrayList<Planet> planetChoices) {
        int size = planetChoices.size();

        boolean validInput = false;

        do {
            try {
                out.println();
                out.println("List Planet choices: ");
                CardPrintUtils.printPlanetList(planetChoices);
                String input = readLine("Scegli un pianeta (1-" + size + "), oppure '0' per non scegliere: ").trim();


                if (!isValidNumberInRange(input, 0, size)) {
                    System.out.println("Input non valido. Inserisci un numero tra 0 e " + size + ".");
                    continue;
                }

                int choice = Integer.parseInt(input);

                if (choice == 0) {
                    clientController.sendSelectPlanetResponse(null, -1);
                    showGenericMessage("Hai scelto di non scegliere un pianeta. Devi aspettare la scelta degli altri giocatori.");
                } else {
                    Planet selected = planetChoices.get(choice - 1);
                    clientController.sendSelectPlanetResponse(selected, choice - 1);
                }

                validInput = true;

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (!validInput);
    }



    @Override
    public void askLoadGoodChoice() {
        String input = null;
        boolean valid = false;

        do {
            try {
                input = readLine("Cosa vuoi fare? [L]Load, [D]Discard, [F]Finish: ").trim().toLowerCase();
                if (input.toLowerCase().matches("[ldf]")) {
                    valid = true;
                } else {
                    out.println("Input non valido. Inserisci L, D o F.");
                }
            } catch (Exception e) {
                out.println("Errore nella lettura dell'input: " + e.getMessage());
            }
        } while (!valid);

        clientController.handleLoadGoodChoice(input);


    }

    @Override
    public void askSelectGoodToLoad(ArrayList<Good> goods, Ship myShip) {


        if (goods.isEmpty()) {
            out.println(" Nessuna merce disponibile sul pianeta.");
            askLoadGoodChoice();
            return;
        }


        displayGoods(goods);

        int goodIndex = -1;

        while (goodIndex < 0 || goodIndex >= goods.size()) {
            try {
                String input = readLine("Seleziona una merce da caricare (1-" + goods.size() + ", oppure 0 per saltare): ");
                if (input.equals("0")) {
                    showGenericMessage("Hai scelto di non caricare nessuna merce. Devi aspettare gli altri giocatori.");
                    return;
                }
                goodIndex = Integer.parseInt(input) - 1;
            } catch (Exception e) {
                out.println("Input non valido.");
            }
        }
//selezionare il good in base di index del merci cioe indece+1 della merce nella list
        Good selectedGood = goods.get(goodIndex);

        ArrayList<Position> availableCargoHolds = clientController.getAvailableCargoHolds(myShip, selectedGood);
        //in base a quale goods e e cargo holds type ritorno availableCargoHolds
        //tipo se good e rosso anche cargo holds deve essere rosso
        if (availableCargoHolds.isEmpty()) {
            out.println(" Nessun cargo hold disponibile sulla nave.");
            askLoadGoodChoice();
            return;
        }

        Position selectedPos = null;
        Position pos = null;
        while (selectedPos == null) {

            try {
                String input = readLine("Inserisci le coordinate della posizione cargo (es. 6,7 oppure 0 per saltare): ");
                if (input.equals("0")) {
                    out.println("Hai deciso di non caricare la merce.");
                    return;
                }

                pos = parseCoordinate(input);

                if (availableCargoHolds.contains(pos)) {
                    selectedPos = new Position(pos.getX() - 4, pos.getY() - 5);
                } else {
                    out.println("Questa posizione non è disponibile.");
                }
            } catch (Exception e) {
                out.println("Formato non valido. Usa es. 6,7");
            }
        }
        clientController.placeMerci(goodIndex, selectedGood, selectedPos);
        out.println("Merce caricata.");

        askLoadGoodChoice();
    }


    @Override
    public void askSelectGoodToDiscard(Ship myShip) {
        showShip(myShip,clientController.getMyModel().getMyInfo().getNickName());
        ArrayList<Position> occupiedPositions = clientController.getOccupiedCargoHolds(myShip);
        Position selectedPos = null;
        Position pos = null;
        if(occupiedPositions.isEmpty()){
            out.println("Nessun cargo hold occupato.");
            askLoadGoodChoice();
            return;
        }
        while (selectedPos == null) {

            try {
                String input = readLine("Inserisci le coordinate della posizione cargo (es. 6,7 oppure 0 per saltare): ");
                if (input.equals("0")) {
                    out.println("Hai deciso di non discard la merce.");
                    askLoadGoodChoice();
                    return;
                }
                pos = parseCoordinate(input);

                if (occupiedPositions.contains(pos)) {
                    selectedPos = new Position(pos.getX() - 4, pos.getY() - 5);
                } else {
                    out.println("Questa posizione non è disponibile.");
                }
            } catch (Exception e) {
                out.println("Formato non valido. Usa es. 6,7");
            }
        }

        ArrayList<Good> goods = clientController.getDiscardPositionGoods(selectedPos);
        out.println(" Merci disponibili:");
        displayGoods(goods);
        int goodIndex = -1;
        while (goodIndex < 0 || goodIndex >= goods.size()) {
            try {
                String input = readLine("Seleziona una merce da caricare (1-" + goods.size() + "): ");
                goodIndex = Integer.parseInt(input) - 1;
            } catch (Exception e) {
                out.println("Input non valido.");
            }
        }
        clientController.discardGood(goodIndex, pos);
        out.println("Merce scartata.");
        askLoadGoodChoice();


    }


    @Override
    public void showFlightMenu() {
        String input;
        boolean valid = false;

        do {
            try {
                input = readLine("Inserisci la tua scelta (a/b/c/d o menu) : ").trim().toLowerCase();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            while (input.equals("m") || input.equals("menu") || input.equals("?")) {
                menuManager.showCurrentMenu();
                try {
                    input = readLine("\nInserisci la tua scelta (a/b/c/d o menu) : ").trim().toLowerCase();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            if (input.matches("[abcd]")) {
                try {
                    clientController.handleFlightMenuChoice(input);
                    valid = true;
                } catch (Exception e) {
                    System.err.println("Errore durante la gestione della scelta: " + e.getMessage());
                }
            } else if (input.equals("reset")) {
                System.out.println(); // No-op for now
            } else {
                System.out.println("Input non valido");
            }

        } while (!valid);
    }



    public void showFlightBoard(FlightBoard flightBoard, ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo) {

        FlightBoardPrintUtils.printFlightBoard(flightBoard, infoPlayers, myinfo);

    }
    @Override
    public void askCollectRewards() {
        askYesNoConfirmation(
                "Hai sconfitto con successo il nemico. Scegli se accettare la ricompensa.",
                () -> clientController.sendCollectRewardsResponse(true),
                () -> {
                    clientController.sendCollectRewardsResponse(false);
                    showGenericMessage("Hai scelto di non accettare la ricompensa.");
                }
        );
    }

    @Override
    public void
    showTimerInfos() {

        new Thread(()->{
            ArrayList<TimerInfo> timerInfos = clientController.getSynchTimerInfos();

            printTimerInfo(timerInfos);
            try {
                showTimerMenu(timerInfos);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();

    }

    private void showTimerMenu(ArrayList<TimerInfo> timerInfos) throws IOException, ExecutionException, InterruptedException {
        String input;
        boolean valid = false;
        boolean oneActive = false;

        do {
            try {


                boolean now = false;

                for (TimerInfo timerInfo: timerInfos){
                    if (timerInfo.getTimerStatus().equals(TimerStatus.STARTED)) {
                        oneActive = true;
                        break;
                    }
                }

                if (oneActive){
                    input = readLine("Inserisci la tua scelta (menu) : ").trim().toLowerCase();

                } else input = readLine("Inserisci la tua scelta  \n a) Flip Timer \n m) (menu/m/?)\n > ").trim().toLowerCase();

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            if (input.equals("m") || input.equals("menu") || input.equals("?")) {

                menuManager.showCurrentMenu();
                handleChoiceForPhase(clientController.getPhase());
            }

            if (input.equals("a")) {
                valid = true;

                //vedo se e' possibile flipparne una

                clientController.sendFlipRequest(timerInfos);
                out.println("Timer flipped!");
                menuManager.showCurrentMenu();
                handleChoiceForPhase(clientController.getPhase());


            } else if (input.equals("reset")) {
                System.out.println(); // No-op for now
            } else {
                System.out.println("Input non valido");
            }

        } while (!valid);
    }

    private void printTimerInfo(ArrayList<TimerInfo> timerInfos) {
//        for (TimerInfo timerInfo: timerInfos){
//            System.out.println(timerInfo.toString());
//        }
        TimerPrinter.printTimers(timerInfos);
    }

    public void displayGoods(List<Good> goods) {
        out.println("Merci disponibili:");
        for (int i = 0; i < goods.size(); i++) {
            out.println("[" + (i + 1) + "] " + CardPrintUtils.colorBlock(goods.get(i)));
        }
        out.println("[0] Salta");
    }

    @Override
    public void showCurrentAdventureCard() {
        out.println("Carta avventura attiva: ");
        CardPrintUtils.printCard(clientController.getCurrentAdventureCard());
    }
    @Override
    public void showEndGame(ArrayList<PlayerScore> scores) {
        ScorePrintUtils.printScoreTable(scores);


    }
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NetworkMessage message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            try {
                observer.update(message);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException | InvalidTilePosition |
                     InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void notifyObservers(String message) throws IOException, ExecutionException {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }


    private Boolean checkReset(String input) {
        return input.equals("reset") || input.equals("RESET");
    }


    private void askYesConfirmation(String prompt, Runnable onYesAction) {
        boolean validInput = false;
        String input;
        do {
            try {
                input = readLine(prompt).trim().toLowerCase();
                if ("y".equals(input)) {
                    try {
                        onYesAction.run();
                        validInput = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    } else {
                    System.out.println("Input non valido. Inserisci solo 'y' per confermare.");
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (!validInput);
    }

    private void askYesNoConfirmation(String prompt, Runnable onYes, Runnable onNo) {
        boolean validInput = false;
        String input;
        do {
            try {
                input = readLine(prompt).trim().toLowerCase();
                switch (input) {
                    case "y", "yes" -> {
                        onYes.run();
                        validInput = true;
                    }
                    case "n", "no" -> {
                        onNo.run();
                        validInput = true;
                    }
                    default -> System.out.println("Input non valido. Inserisci 'y' per sì o 'n' per no.");
                }
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
                        }
        } while (!validInput);

    }

    public static boolean isValidNumberInRange(String input, int min, int max) {
        if (input == null || input.isBlank()) return false;

        try {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}



