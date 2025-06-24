package it.polimi.ingsw.galaxytrucker.view.Tui;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.GameState;
import it.polimi.ingsw.galaxytrucker.enums.TimerStatus;
import it.polimi.ingsw.galaxytrucker.model.*;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import it.polimi.ingsw.galaxytrucker.observer.Observable;
import it.polimi.ingsw.galaxytrucker.observer.Observer;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.*;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.galaxytrucker.view.Tui.util.InputUtils.parseCoordinate;
import static it.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils.printShip;
import static it.polimi.ingsw.galaxytrucker.view.Tui.util.TilePrintUtils.printTile;
import static it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor.*;

/**
 * Implements the Textual user interface.
 */
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
        startInputListener();

    }

    @Override
    public Boolean autoShowUpdates() {
        return false;
    }

    private volatile CompletableFuture<String> currentInputFuture = null;
    private static final AtomicBoolean stopInput = new AtomicBoolean(false);

    private volatile boolean flag = false;

    // Metodo per bloccare l'input manualmente
    public static void blockInput() {
        stopInput.set(true);
    }

    // Metodo per sbloccare l'input manualmente
    public static void unblockInput() {
        stopInput.set(false);
    }

    private volatile boolean inputEnabled = false;


    public void enableInput() {
        inputEnabled = true;
    }

    public void disableInput() {
        inputEnabled = false;
//        System.out.println("[DEBUG] disableInput() called.");
//        Thread.dumpStack();
    }

    public void startInputListener() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if ("RESET".equalsIgnoreCase(line)) {
                        flag = true;
                        continue;
                    }

//                    if ("RESET".equalsIgnoreCase(line)) {
//                        flag = true;
//                    }
                    CompletableFuture<String> future = currentInputFuture;
                    if (!inputEnabled) {
                        System.out.println("Non è il tuo turno.");
                        continue;
                    }

                    if (future == null) {
                        System.out.println("Input future non inizializzato.");
                        continue;
                    }
                    if (future.isDone()) {
                        System.out.println("Input già completato.");
                        continue;
                    }

                    future.complete(line);


//                        if (!line.equalsIgnoreCase("RESET")) {
//                            System.out.println("Non è il tuo turno.");
//                        }

                }
            }
        }, "ConsoleInputListener").start();
    }
//    public String readLine(String prompt) throws InterruptedException, ExecutionException {
//        System.out.print(prompt);
//        flag = false;
//        currentInputFuture = new CompletableFuture<>();
//        String input = currentInputFuture.get();
//
//

    /// /        if (input.contains("RESET")) {
    /// /            flag = true;
    /// /            return "RESET";
    /// /        }
//
//        return input.trim();
//    }
    public String readLine(String prompt) {
        inputEnabled = true;
        currentInputFuture = new CompletableFuture<>();

        System.out.print(prompt + " ");

        try {
            String input = currentInputFuture.get().trim();
            inputEnabled = false;
            return input;
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    @Override
    public void forceReset() {
        System.out.println();
        if (currentInputFuture != null && !currentInputFuture.isDone()) {
            currentInputFuture.complete("RESET");
        }
    }

    public void start() {

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

    public void askServerInfo() {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";

        int defaultPort = isSocket ? 5000 : 1099;
        int portNumber = -1;

        synchronized (outputLock) {
            out.println("Please specify the following settings. The default value is shown between brackets.");

            enableInput();
            String address = readLine("Enter the server address [" + defaultAddress + "]: ").trim();
            serverInfo.put("address", address.isBlank() ? defaultAddress : address);
            String port;
            do {
                port = readLine("Enter the server port [" + defaultPort + "]: ").trim();
                if (port.equals("RESET")) {
                    disableInput();
                    return;
                }
                try {
                    portNumber = port.isEmpty() ? defaultPort : Integer.parseInt(port);
                } catch (NumberFormatException e) {
                    out.println("Errore: la porta deve essere un numero intero.");
                }
            } while (portNumber == -1);
        }

        SERVER_INFO message = new SERVER_INFO(serverInfo.get("address"), portNumber);
        notifyObservers(message);
    }


    public void askNickname() {

        try {
            String nickname = readLine("Enter your nickname: ");
            if (checkReset(nickname)) return;

            clientController.handleNicknameInput(nickname);
        } catch (InterruptedException | ExecutionException | IOException e) {
            System.err.println("Error reading nickname: " + e.getMessage());
        }
    }

    public void askJoinOrCreateRoom() {
        enableInput();
        try {
            String choice;
            do {
                choice = readLine("Create Room (a) or Join Room (b): ").trim().toLowerCase();
                if (checkReset(choice)) return;

                if (!choice.equals("a") && !choice.equals("b")) {
                    synchronized (outputLock) {
                        out.println("Invalid input. Please enter 'a' or 'b'.");
                    }
                }
            } while (!choice.equals("a") && !choice.equals("b"));


            clientController.handleCreateOrJoinChoice(choice);

        } catch (Exception e) {
            System.err.println("Error while choosing: " + e.getMessage());
        } finally {
            disableInput();
        }
    }

    public void askCreateRoom() {
        enableInput();
        try {
            String maxPlayersStr;
            int maxPlayers;
            boolean isLearningMatch;
            synchronized (outputLock) {
                maxPlayersStr = readLine("Set MAX players for Game (2-4): ").toLowerCase();
                if (checkReset(maxPlayersStr)) return;

                maxPlayers = Integer.parseInt(maxPlayersStr);

                if (maxPlayers < 2 || maxPlayers > 4) {
                    out.println("Please enter a number between 2 and 4.");
                    askCreateRoom(); // Retry
                    return;
                }

                String learningInput = readLine("Is this a Learning Match? (y/n): ").toLowerCase();
                if (checkReset(learningInput)) return;

                switch (learningInput) {
                    case "y" -> isLearningMatch = true;
                    case "n" -> isLearningMatch = false;
                    default -> {
                        out.println("Invalid input. Please type 'y' or 'n'.");
                        askCreateRoom();  // Retry
                        return;
                    }
                }
            }

            clientController.handleCreateChoice(maxPlayers, isLearningMatch);

        } catch (NumberFormatException e) {
            synchronized (outputLock) {
                out.println("Invalid number format.");
            }
            askCreateRoom(); // Retry
        } finally {
            disableInput();
        }
    }


    public void askRoomCode() {
        synchronized (outputLock) {
            out.println("\nPlease enter the Lobby ID you want to join:");
            enableInput();
            try {
                String input = readLine("> ").trim().toLowerCase();
                if(input.equals("reset")) return;

                int lobbyId = Integer.parseInt(input);
                clientController.handleJoinChoice(lobbyId);
            } catch (NumberFormatException e) {
                out.println("Invalid Lobby ID. Please enter a number.");
                askRoomCode();
            } finally {
                disableInput();
            }
        }
    }

    public void showLobbies(List<LobbyInfo> lobbies) {
        if (lobbies.isEmpty()) {
            synchronized (outputLock) {
                out.println(" No game rooms, Returning to main menu. ");
            }
            askJoinOrCreateRoom();
            return;
        }

        synchronized (outputLock) {
            out.println("：List of rooms");
            for (LobbyInfo lobby : lobbies) {
                boolean isLearningMatch = lobby.isLearningMatch();
                String matchType = isLearningMatch ? "Learning" : "Normal";

                out.printf("Lobby ID: %d | Host: %s | GameType: " + matchType + " |Players: (%d/%d)  \n",
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
    public void showPlayersLobby(PlayerInfo myInfo, ArrayList<PlayerInfo> infoPlayer) {
        System.out.println("Giocatori nella lobby: ");
        System.out.print("IO: ");
        switch (myInfo.getColor()) {
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

                showGenericMessage("TIMER STARTED !!",false);
            }).start();
            return;
        } else {
            menuManager.setMenuText(phase);
            if (phase.equals(GameState.BUILDING_START) || phase.equals(GameState.SHIP_CHECK) || phase.equals(GameState.CREW_INIT) || phase.equals(GameState.FLIGHT)) {
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
            case SHIP_CHECK -> showCheckShipMenu();
            case CREW_INIT -> showEmbarkCrewMenu();
            case FLIGHT -> showFlightMenu();
            default -> {
//                out.println("Please wait. No input is required at this stage.");
            }
        }
    }

    @Override
    public void showBuildingMenu() {
        enableInput();

        final String choiceEnd;
        if(MenuManager.learningMatch) choiceEnd = "i";
        else choiceEnd = "j";

        final String string = "\nChoose an option (a–" + choiceEnd + ") or reprint this menu (m): ";
        String input = readLine(string).trim().toLowerCase();
        if (checkReset(input)) return;

        while (input.equals("m") || input.equals("menu") || input.equals("?")) {
            menuManager.showCurrentMenu();
            input = readLine(string).trim().toLowerCase();
        }

        clientController.handleBuildingMenuChoice(input);
    }

    @Override
    public void FetchMyShip() {
        new Thread(() -> {
            try {
                clientController.handleFetchShip(clientController.getNickname());
                menuManager.showCurrentMenu();


            } catch (Exception e) {
                showGenericMessage("Error fetching ship: " + e.getMessage() + ", please try again",false);
                FetchMyShip();
                //throw new RuntimeException(e);
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
            enableInput();
            try {
                String DeckIDStr = readLine("Enter which Deck you want to view (1~4)> ").trim();
                int DeckID = Integer.parseInt(DeckIDStr);

                if (checkReset(DeckIDStr)) return;

                if (DeckID < 1 || DeckID > 4) {
                    out.println("Deck ID not valid. Please enter a number between 1 and 4.");
                } else {
                    clientController.viewAdventureCardDeck(DeckID - 1);
                    valid = true;
                }
            } catch (Exception e) {
                out.println("Error during ask view adventure Decks: " + e.getMessage());
            } finally {
                disableInput();
            }
        } while (!valid);
    }

    @Override
    public void askFetchShip() {
        enableInput();
        boolean success = false;
        try {
            do {
                try {
                    String targetName = readLine("Enter the nickname of the player to fetch their ship: ").trim();

                    if (checkReset(targetName)) return;


                    clientController.handleFetchShip(targetName);
                    success = true;

                } catch (Exception e) {
                    showGenericMessage("Unexpected error: " + e.getMessage(),false);
                }
            } while (!success);
        } finally {
            disableInput();
        }
    }

    @Override
    public void askRotation() {

        boolean valid = false;
        enableInput();
        try {
            do {
                try {
                    out.println("Enter rotation degree (0,90, 180, 270): ");
                    String input = readLine("> ").trim();
                    if (checkReset(input)) return;


                    int rotation = InputUtils.parseRotation(input);
                    clientController.rotateCurrentTile(rotation);
                    valid = true;
                } catch (IllegalArgumentException e) {
                    out.println("Invalid input: " + e.getMessage());
                } catch (Exception e) {
                    out.println("Unexpected error: " + e.getMessage());
                }
            } while (!valid);
        } finally {
            disableInput();
        }
    }

    @Override
    public void askPosition() {
        boolean valid = false;
        Position pos = null;

        do {
            try {
                String input = readLine("Enter position to move the tile to (format: (x,y)): ").trim();
                if (checkReset(input)) return;

                pos = parseCoordinate(input);
                clientController.setTmpCurrentPosition(clientController.getCurrentPosition());
                clientController.setCurrentPos(pos.getX() - 4, pos.getY() - 5);

                valid = true;
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            } catch (Exception e) {
                out.println("Unexpected error: " + e.getMessage());
            }
        } while (!valid);

    }

    @NeedsToBeCompleted
// TileView
    @Override
    public void showTile(Tile tile) {
        if (tile != null) {
            printTile(tile);
        }
    }

    @Override
    public void handleFaceUpTilesUpdate() {

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
        enableInput();
        try {
            boolean validInput = false;
            do {
                out.println("How do you want to draw a tile?");
                out.println("1) draw a random tile(face down)");
                out.println("2) choose a face up tile");
                out.println("3) choose from your reserved tiles");
                out.println("4) Reclaim the tile that is still movable.");
                out.println();


                String choice = readLine("Choose(1/2/3/4) > ");

                if (checkReset(choice)) return;


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
                    case "4": {
//                        System.out.println("[DEBUG] You selected option 4: reclaimTile() will be called");
                        clientController.reclaimTile();
                        validInput = true;
                        break;
                    }

                    default: {
                        out.println("Invalid input. Please enter 1, 2, 3, or 4.");
                        break;
                    }
                }

            } while (!validInput);

        } finally {
            disableInput();
        }

    }

    public void askChooseTile() {
        enableInput();

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

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            disableInput();
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
                    enableInput();
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
                    } finally {
                        disableInput();
                    }
                } else {
                    showGenericMessage("There is no tile to pick",false);
                    showBuildingMenu();
                }
            } else {
                showGenericMessage("You already have a tile in hand to place ",false);
                showBuildingMenu();
            }
        }
        if (!isPicking) {
            if (clientController.hasTileInHand()) {
                if ((reservedTiles[0] == null || reservedTiles[1] == null)) {
                    showTileSlots();
                    enableInput();
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
                    } finally {
                        disableInput();
                    }
                } else {
                    showGenericMessage("There is no place to place the tile",false);
                    showBuildingMenu();
                }
            } else {
                showGenericMessage("You have no tile in hand to place",false);
                showBuildingMenu();
            }

        }


    }


    @Override
    public void askTilePlacement() {
        enableInput();
        try {
            if (!clientController.hasTileInHand()) {
                out.println("No tile in hand to place.");
                showBuildingMenu();
                return;
            }

            out.println("Current tile info:");
            printTile(clientController.getCurrentTileInHand());

            String input;
            do {
                out.println("Where do you want to place this tile?");
                out.println("1) Place on ship");
                out.println("2) Place in reserved slot");

                input = readLine("Choose (1/2): ").trim().toLowerCase();
                if (checkReset(input)) return;

                switch (input) {
                    case "1":
                        choosePlaceTileInShip();
                        return;
                    case "2":
                        askPickOrPlaceReservedTile(false);
                        return;
                    default:
                        out.println("Invalid input. Please choose 1 or 2.");
                }
            } while (true);

        } catch (Exception e) {
            out.println("Error during tile placement: " + e.getMessage());
        } finally {
            disableInput();
        }


    }

    private void choosePlaceTileInShip() {
        enableInput();
        try {

            out.println(" Current tile info:");
            printTile(clientController.getCurrentTileInHand());

            if (clientController.getCurrentPosition() != null) {
                out.println("Current position:  X: " + (clientController.getCurrentPosition().getX() + 4) + ",  Y:" + (clientController.getCurrentPosition().getY() + 5));
            } else {
                out.println("Current position: null");
            }

            out.println("Current rotation: " + clientController.getCurrentTileInHand().getRotation());
            askPosition();
            out.println("Do you want to place this tile at the current position and rotation? (y/n)");
            enableInput();
            String input = readLine("> ").trim().toLowerCase();
            if (checkReset(input)) return;

            boolean confirm = input.equals("y");
            if (confirm) {
                clientController.handleTilePlacement();
            } else {
                clientController.resetCurrentPos();
                out.println("Tile not placed. You can rotate or move it again.");
                showBuildingMenu();
            }

        } catch (Exception e) {
            out.println("Error during tile placement " + e.getMessage());
        } finally {
            disableInput();
        }

    }



    @Override
    public void showCheckShipMenu() {
        enableInput();
        try {
            String input = readLine("\nChoose an option (a–c) or menu: ").trim().toLowerCase();
            if(checkReset(input)) return;
            clientController.handleCheckShipChoice(input);
        } finally {
            disableInput();
        }
    }

    @Override
    public void showEmbarkCrewMenu() {
        enableInput();

        try {
            String input = readLine("\nChoose an option (a–b) or menu: ").trim().toLowerCase();
            if(checkReset(input)) return;
            clientController.handleEmbarkCrewMenu(Character.toString(input.charAt(input.length() - 1)));
        } finally {
            disableInput();
        }
    }

    @Override
    //Todo verificare ordine x e y
    public void askRemoveTile(Ship ship) {


        boolean valid = false;
        Position pos1 = null;
        enableInput();
        try {
            do {
                try {
                    String input = readLine("Enter position to remove the Tile from(format: (x,y)): ").trim();
                    if(checkReset(input)) return;
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
                        showCheckShipMenu();
                        return;
                    }

                    String input2 = readLine("Do you want to finish? (y/n)").trim().toLowerCase();
                    if(checkReset(input2)) return;


                    while (!input2.equals("n") && !input2.equals("y")) {


                        System.out.println("Invalid input. Please enter (y/n))");
                        input2 = readLine("Do you want to finish? (y/n)").trim().toLowerCase();


                    }
                    if (input2.equals("y")) {
                        menuManager.showCurrentMenu();
                        showCheckShipMenu();
                        valid = true;
                    }


                } catch (IllegalArgumentException e) {
                    out.println(e.getMessage());
                } catch (Exception e) {
                    out.println("Unexpected error: " + e.getMessage());
                }
            } while (!valid);
        } finally {
            disableInput();
        }

        showCheckShipMenu();
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
        enableInput();
        try {
            do {
                inputStr = readLine("Choose one > ").trim();
                if(checkReset(inputStr)) return;
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
        } finally {
            disableInput();
        }
    }

    public void showGenericMessage(String message, Boolean important) {

        synchronized (outputLock) {
            System.out.print("\n"); // Torna all'inizio della riga
//            System.out.print(" ".repeat(100)); // Cancella eventuale scrittura
//            System.out.print("\r"); // Torna di nuovo all'inizio
            if(important){
                System.out.println(TuiColor.RED + message + TuiColor.RESET);
            }
            else{
                System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);
            }


            // Ripristina il prompt e quello che l'utente aveva scritto
            System.out.print("> ");
        }

    }

    @Override
    public void showWaitOtherPlayers() {
        showGenericMessage("Attendi gli altri giocatori per avanzare alla prossima fase",false);
    }

    @Override
    public void chooseComponent(Ship myShip, ActivatableComponent component) {
        printShip(myShip);
        System.out.println("You can activate your " + component.name());
        int totalBatteries = myShip.getnBatterieLeft();
        boolean stop = false;
        Position compPos, battPos;
        ArrayList<Position> activatedComponentPositions = new ArrayList<>();
        ArrayList<Position> usedBatteryPositions = new ArrayList<>();

        //Ciclo while
        //Chiede finchè non dico no o finchè non termino le batterie
        while (!stop && totalBatteries > 0) {
            System.out.print("Activate " + component.name() + " specify the coordinates or type no to stop");
            enableInput();
            String input = readLine(" input (x,y) or no: ").trim().toLowerCase();
            disableInput();
            if(checkReset(input)) return;
            if (input.equals("n") || input.equals("no")) {
                stop = true;
            } else {
                compPos = parseCoordinate(input);
                compPos.setPos(compPos.getX() - 4, compPos.getY() - 5);
                if (!Util.inBoundaries(compPos.getX(), compPos.getY()) || myShip.getInvalidPositions().contains(compPos)) {
                    System.out.println("Wrong Coordinates, out of bounds ");
                }
                //Verifica anche tipo
                else if (!myShip.getComponentPositionsFromName(component.name()).contains(compPos)) {
                    System.out.println("Wrong Coordinates, another kind of component at that coordinates ");
                } else if (activatedComponentPositions.contains(compPos)) {
                    System.out.println("Wrong Coordinates, already activated ");
                } else {
                    System.out.print("Specify the coordinates of the battery to use");
                    enableInput();
                    input = readLine(" input (x,y): ").trim().toLowerCase();
                    disableInput();
                    battPos = parseCoordinate(input);
                    battPos.setPos(battPos.getX() - 4, battPos.getY() - 5);
                    if (!Util.inBoundaries(battPos.getX(), battPos.getY()) || myShip.getInvalidPositions().contains(battPos)) {
                        System.out.println("Wrong Coordinates, out of bounds ");
                    } else if (!myShip.getComponentPositionsFromName("batterySlot").contains(battPos)) {
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
    public void chooseDiscardCrew(Ship myShip, int nCrewToDiscard) {
        ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
        printShip(myShip);
        ArrayList<Position> housingPositions = new ArrayList<>();
        Position housingPos;
        System.out.println("You have to discard " + nCrewToDiscard + " crew:");

        while (nCrewToDiscard > 0) {
            System.out.print("specify the coordinates of a Cabin");

            try {
                enableInput();
                String input = readLine(" input (x,y): ").trim().toLowerCase();
                disableInput();

                if(checkReset(input)) return;

                housingPos = parseCoordinate(input);
                housingPos.setPos(housingPos.getX() - 4, housingPos.getY() - 5);
                System.out.println(housingPos.getX() + " " + housingPos.getY());

                if (!Util.inBoundaries(housingPos.getX(), housingPos.getY()) || myShip.getInvalidPositions().contains(housingPos)) {
                    System.out.println("Wrong Coordinates, out of bounds ");
                } else if (!myShip.getComponentPositionsFromName("ModularHousingUnit").contains(housingPos) &&
                        !myShip.getComponentPositionsFromName("CentralHousingUnit").contains(housingPos)) {
                    System.out.println("Wrong Coordinates, another kind of component at that coordinates ");
                } else {
                    int nCrewAtPos = switch (myShip.getComponentFromPosition(housingPos).accept(componentNameVisitor)) {
                        case "ModularHousingUnit" ->
                                ((ModularHousingUnit) myShip.getComponentFromPosition(housingPos)).getNCrewMembers();
                        case "CentralHousingUnit" ->
                                ((CentralHousingUnit) myShip.getComponentFromPosition(housingPos)).getNCrewMembers();
                        default -> 0;
                    };
                    Position finalHousingPos = housingPos;
                    int timesSlotUsed = (int) housingPositions.stream().filter(p -> p.equals(finalHousingPos)).count();
                    if (nCrewAtPos <= timesSlotUsed) {
                        System.out.println("Crew already selected at those coordinates ");
                    } else {
                        housingPositions.add(housingPos);
                        nCrewToDiscard--;
                        System.out.println("Accepted, you have " + nCrewToDiscard + " crew members to discard left");
                    }
                }
            } catch (IllegalArgumentException e) {
                disableInput();
                System.out.println("invalid input, out of bounds ");
            }
        }

        clientController.handleDiscardCrewMembersResponse(housingPositions);
    }


    public void chooseTroncone(ArrayList<Ship> tronconi) {
        //Mostro tutti i tronconi numerandoli
        System.out.println("The ship divided into pieces choose one to continue ");
        for (int i = 0; i < tronconi.size(); i++) {
            out.println();
            System.out.println("Ship Fragment " + (i + 1) + ": ");
            printShip(tronconi.get(i));
        }
        int choice = -1;
        enableInput();
        try {
            while (choice < 0 || choice >= tronconi.size()) {
                System.out.println("Choose a Fragment as your ship (1-" + tronconi.size() + "): ");
                try {
                    String input = readLine(" > ").trim().toLowerCase();
                    if(checkReset(input)) return;
                    int inputNum = Integer.parseInt(input);
                    choice = inputNum - 1; //0 based
                } catch (Exception e) {
                    System.out.println("Invalid choice entered, please try again");
                    choice = -1;
                }

                if (choice < 0 || choice >= tronconi.size()) {
                    System.out.println("Invalid choice entered, choose a number equivalent to a ship fragment.");
                }
            }
        } finally {
            disableInput();
        }


        //handle response indicando numero
        clientController.handleTrunkResponse(choice-1);

    }

    @Override
    public void chooseCrew(Ship myShip)  {

        //salvo tutte le posizioni delle housing

        ArrayList<Position> housinPos = myShip.getComponentPositionsFromName("ModularHousingUnit");

        int nBrownAliens = 0;
        int nPurpleAliens = 0;

        CrewInitUpdate crewInitUpdate = new CrewInitUpdate();

        System.out.println("You have " + housinPos.size() + " Cabins to fill");


        if (!housinPos.isEmpty()) {
            enableInput();
            try {


                for (Position pos : housinPos) {

                    Slot tempSlot = myShip.getShipBoard()[pos.getX()][pos.getY()];

                    if (tempSlot != null) {
                        if (tempSlot.getTile() != null) {

                            ArrayList<String> choices = new ArrayList<>();

                            ModularHousingUnit housing = (ModularHousingUnit) tempSlot.getTile().getMyComponent();

                            if (Util.checkNearLFS(pos, AlienColor.PURPLE, myShip) || Util.checkNearLFS(pos, AlienColor.BROWN, myShip)) {

                                if (Util.checkNearLFS(pos, AlienColor.PURPLE, myShip) && nPurpleAliens == 0) {
                                    CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.PURPLE);
                                    choices.add("purple");
                                }

                                if (Util.checkNearLFS(pos, AlienColor.BROWN, myShip) && nBrownAliens == 0) {
                                    CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.BROWN);
                                    choices.add("brown");

                                }
                            }
                            CabinUnitAscii.printCabinUnitWithFigures(2, false, AlienColor.EMPTY);
                            choices.add("human");



                            boolean correct = false;

                            while (!correct) {
                                StringBuilder prompt = new StringBuilder("Type ");
                                for (String choice : choices) {
                                    prompt.append("(").append(choice).append(") ");
                                }
                                String choice = readLine(prompt.toString()).toLowerCase();

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
                                        break;
                                    }

                                    case "human": { //Si inseriscono due umani
                                        correct = true;
                                        crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.EMPTY));
                                        crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.EMPTY));
                                        break;
                                    }
                                    case "reset": {}
                                    default: {
                                        System.out.println("Invalid choice: " + choice);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                disableInput();
            }
        }

        //fine for

        notifyObservers(crewInitUpdate);

    }


    @Override
    public void askActivateAdventureCard() {
        enableInput();
        askYesNoConfirmation(
                "Congratulazioni, soddisfi i requisiti. Vuoi attivare l'effetto della carta?",
                () -> clientController.sendActivateAdventureCardResponse(true),
                () -> {
                    clientController.sendActivateAdventureCardResponse(false);
                    showGenericMessage("Hai scelto di non attivare l'effetto.",false);
                }
        );
        disableInput();
    }

    @Override
    public void askDrawCard() {
        enableInput();
        askYesConfirmation(
                "Sei il leader. Inserire un 'y' per pescare la carta avventura: ",
                clientController::sendDrawAdventureCardRequest
        );
        disableInput();
    }


    // chiedere di selezionare uno dei pianeti disponibili
    @Override
    public void askSelectPlanetChoice(HashMap<Integer, Planet> landablePlanets) {
        int size = landablePlanets.size();

        boolean validInput = false;
        enableInput();
        try {
            do {
                out.println();
                out.println("List Planet choices: ");
                CardPrintUtils.printPlanetList(landablePlanets);
                String input = readLine("Scegli un pianeta, oppure '0' per non scegliere: ").trim();
                if(checkReset(input)) return;

                int choice;

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    choice = -1;
                }

                if (!landablePlanets.containsKey(choice) && choice != 0) {
                    System.out.println("Input non valido.");
                    continue;
                }


                if (choice == 0) {
                    clientController.sendSelectPlanetResponse(null, -1);
                    showGenericMessage("Hai scelto di non scegliere un pianeta. Devi aspettare la scelta degli altri giocatori.",false);
                } else {
                    Planet selected = landablePlanets.get(choice);
                    clientController.sendSelectPlanetResponse(selected, choice);
                }

                validInput = true;

            } while (!validInput);
        } finally {
            disableInput();
        }
    }


    @Override
    public void askLoadGoodChoice() {
        String input = null;
        boolean valid = false;
        enableInput();
        try {


            do {
                try {
                    input = readLine("Cosa vuoi fare? [L]Load, [D]Discard, [F]Finish: ").trim().toLowerCase();
                    if(checkReset(input)) return;
                    if (input.toLowerCase().matches("[ldf]")) {
                        valid = true;
                    } else {
                        out.println("Input non valido. Inserisci L, D o F.");
                    }
                } catch (Exception e) {
                    out.println("Errore nella lettura dell'input: " + e.getMessage());
                }
            } while (!valid);

        } finally {
            disableInput();
        }
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
        enableInput();
        try {
            while (goodIndex < 0 || goodIndex >= goods.size()) {
                try {
                    String input = readLine("Seleziona una merce da caricare (1-" + goods.size() + ", oppure 0 per saltare il caricamento merci): ");
                    if(checkReset(input)) return;
                    if (input.equals("0")) {
                        showGenericMessage("Hai scelto di non caricare nessu+na merce. ",false);
                        disableInput();
                        askLoadGoodChoice();

                        return;
                    }
                    goodIndex = Integer.parseInt(input) - 1;
                } catch (Exception e) {
                    out.println("Input non valido.");
                }
            }
        } finally {
            disableInput();
        }

        Good selectedGood = goods.get(goodIndex);

        ArrayList<Position> availableCargoHolds = clientController.getAvailableCargoHolds(myShip, selectedGood);
        if (availableCargoHolds.isEmpty()) {
            out.println(" Nessun cargo hold disponibile sulla nave.");
            askLoadGoodChoice();
            return;
        }

        Position selectedPos = null;
        enableInput();
        try {
            while (selectedPos == null) {
                try {
                    String input = readLine("Inserisci le coordinate della posizione cargo (es. 6,7 oppure 0 per saltare): ");
                    if(checkReset(input)) return;
                    if (input.equals("0")) {
                        out.println("Hai deciso di non caricare la merce.");
                        askLoadGoodChoice();
                        return;
                    }
                    Position pos = parseCoordinate(input);
                    if (availableCargoHolds.contains(pos)) {
                        selectedPos = new Position(pos.getX() - 4, pos.getY() - 5);
                    } else {
                        out.println("Questa posizione non è disponibile.");
                    }
                } catch (Exception e) {
                    out.println("Formato non valido. Usa es. 6,7");
                }
            }
        } finally {
            disableInput();
        }

        clientController.placeMerci(goodIndex, selectedGood, selectedPos);
        out.println("Merce caricata.");

        askLoadGoodChoice();
    }


    @Override
    public void askSelectGoodToDiscard(Ship myShip) {
        showShip(myShip, clientController.getMyModel().getMyInfo().getNickName());
        ArrayList<Position> occupiedPositions = clientController.getOccupiedCargoHolds(myShip);
        Position selectedPos = null;
        Position pos = null;

        if (occupiedPositions.isEmpty()) {
            out.println("Nessun cargo hold occupato.");
            askLoadGoodChoice();
            return;
        }

        enableInput();
        try {
            while (selectedPos == null) {
                try {
                    String input = readLine("Inserisci le coordinate della posizione cargo (es. 6,7 oppure 0 per saltare): ");
                    if(checkReset(input)) return;
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
        } finally {
            disableInput();
        }

        ArrayList<Good> goods = clientController.getDiscardPositionGoods(selectedPos);
        out.println(" Merci disponibili:");
        displayGoods(goods);

        int goodIndex = -1;
        enableInput();
        try {
            while (goodIndex < 0 || goodIndex >= goods.size()) {
                try {
                    String input = readLine("Seleziona una merce da caricare (1-" + goods.size() + "): ");
                    if(checkReset(input)) return;
                    goodIndex = Integer.parseInt(input) - 1;
                } catch (Exception e) {
                    out.println("Input non valido.");
                }
            }
        } finally {
            disableInput();
        }

        clientController.discardGood(goodIndex, pos);
        out.println("Merce scartata.");
        askLoadGoodChoice();
    }


    @Override
    public void showFlightMenu() {
        String input;
        boolean valid = false;
        enableInput();
        try {


            do {
                input = readLine("Inserisci la tua scelta (a/b/c/d o menu) : ").trim().toLowerCase();

                while (input.equals("m") || input.equals("menu") || input.equals("?")) {
                    menuManager.showCurrentMenu();
                    input = readLine("\nInserisci la tua scelta (a/b/c/d o menu) : ").trim().toLowerCase();
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
        } finally {
            disableInput();
        }
    }


    public void showFlightBoard(FlightBoard flightBoard, ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo) {

        FlightBoardPrintUtils.printFlightBoard(flightBoard, infoPlayers, myinfo);

    }

    @Override
    public void askCollectRewards() {
        enableInput();
        askYesNoConfirmation(
                "Hai sconfitto con successo il nemico. Scegli se accettare la ricompensa.",
                () -> clientController.sendCollectRewardsResponse(true),
                () -> {
                    clientController.sendCollectRewardsResponse(false);
                    showGenericMessage("Hai scelto di non accettare la ricompensa.",false);
                }
        );
        disableInput();
    }

    @Override
    public void
    showTimerInfos() {


        new Thread(() -> {
            ArrayList<TimerInfo> timerInfos = clientController.getSynchTimerInfos();

            printTimerInfo(timerInfos);
            showTimerMenu(timerInfos);

        }).start();

    }


    private void showTimerMenu(ArrayList<TimerInfo> timerInfos) {
        String input;
        boolean valid = false;
        boolean oneActive = false;
        enableInput();
        try {


            do {


                boolean now = false;

                for (TimerInfo timerInfo : timerInfos) {
                    if (timerInfo.getTimerStatus().equals(TimerStatus.STARTED)) {
                        oneActive = true;
                        break;
                    }
                }

                if (oneActive) {
                    input = readLine("Inserisci la tua scelta (menu) : ").trim().toLowerCase();

                } else
                    input = readLine("Inserisci la tua scelta  \n a) Flip Timer \n m) (menu/m/?)\n > ").trim().toLowerCase();

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
        } finally {
            disableInput();
        }
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
    public void notifyObservers(NetworkMessage message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    @Override
    public void showYouAreNowSpectating() {

    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }


    private Boolean checkReset(String input) {
        return input.equalsIgnoreCase("reset");
    }


    private void askYesConfirmation(String prompt, Runnable onYesAction) {
        boolean validInput = false;
        String input;
        do {
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
        } while (!validInput);
    }

    private void askYesNoConfirmation(String prompt, Runnable onYes, Runnable onNo) {
        boolean validInput = false;
        String input;
        do {
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
                case "reset" -> {
                    return;
                }
                default -> System.out.println("Input non valido. Inserisci 'y' per sì o 'n' per no.");
            }
        } while (!validInput);

    }

    public static boolean isValidNumberInRange(String input, int min, int max) {
        if (input == null || input.isBlank() || input.equalsIgnoreCase("reset")) return false;

        try {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}



