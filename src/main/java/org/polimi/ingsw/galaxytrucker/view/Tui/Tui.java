package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import javafx.util.Pair;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;

import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMapSlot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
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
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;


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


    private  MenuManager menuManager = new MenuManager();
    private GameState phase = GameState.LOBBY;

    private final Object panelLock  = new Object();


    private volatile boolean shouldExit = false;
    private ExecutorService inputExecutor = Executors.newSingleThreadExecutor();


    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

    }


    private volatile CompletableFuture<String> currentInputFuture = null;

    public String readLine(String prompt) throws InterruptedException, ExecutionException {
        currentInputFuture = new CompletableFuture<>();

        new Thread(() -> {

                System.out.print(prompt);
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    currentInputFuture.complete(line);
                }

        }).start();

        String input = currentInputFuture.get();
        return input;
    }

    @Override
    public void forceReset() {
        if (currentInputFuture != null && !currentInputFuture.isDone()) {
            currentInputFuture.complete("RESET");
        }
    }





    public void start() throws ExecutionException, IOException, InterruptedException {

        String banner = "\033[1;34m" + // Colore Blu Chiaro
                "   __    _   __    _   _  __ _  __  _____ ___  _ __  __  _    ___  ___    ___\n" +
                " ,'_/  .' \\ / /  .' \\ | |/,'| |/,' /_  _// o |/// /,'_/ / //7/ _/ / o | ,' _/\n" +
                "/ /_n / o // /_ / o / /  /  | ,'    / / /  ,'/ U // /_ /  ,'/ _/ /  ,' _\\ `. \n" +
                "|__,'/_n_//___//_n_/,'_n_\\ /_/     /_/ /_/`_\\\\_,' |__//_/\\/___//_/`_\\/___,' \n" +
                "\033[0m"; // Reset colore

        out.println(banner);
        askServerInfo();
//        else askRMIServerInfo();
//        askNickname();
    }

    public void askServerInfo() throws ExecutionException, IOException, InterruptedException {
        Map<String, String> serverInfo = new HashMap<>();
        String defaultAddress = "localhost";
        String defaultPort = "5000";
        if (!isSocket) defaultPort = "1099";
        synchronized (outputLock) {
            out.println("Please specify the following settings. The default value is shown between brackets.");
            String prop = "Enter the server address [" + defaultAddress + "]: ";
            String address = readLine(prop);
            if (address.isEmpty()) {
                serverInfo.put("address", defaultAddress);
            } else {
                serverInfo.put("address", address);
            }

            String prompt = "Enter the server port [" + defaultPort + "]: ";
            String port = readLine(prompt);

            if (port.equals("")) {
                serverInfo.put("port", defaultPort);

            } else {
                serverInfo.put("port", port);
            }
        }

        int numero = Integer.parseInt(serverInfo.get("port"));
        SERVER_INFO message = new SERVER_INFO(serverInfo.get("address"), numero);
        notifyObservers(message);
    }


    public void askNickname() {
        try {
            String nickname = readLine("Enter your nickname: ");
            clientController.handleNicknameInput(nickname);
        } catch (Exception e) {
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
            } catch (Exception e) {
                out.println("Error while joining room: " + e.getMessage());
            }
        }
    }

    public void showLobbies(List<LobbyInfo> lobbies) {
        if (lobbies.isEmpty()) {
            synchronized (outputLock) {
                out.println(" No game rooms, Returning to main menu. ");
            }
            return;
        }

        synchronized (outputLock) {
            out.println("：List of rooms");
            for (LobbyInfo lobby : lobbies) {
                out.printf("Lobby ID: %d | Host: %s | Players: (%d/%d)  \n",
                        lobby.getLobbyID(),
                        lobby.getHost(),
                        lobby.getConnectedPlayers(),
                        lobby.getMaxPlayers());
            }

        }

    }

    @Override
    public void showPlayerJoined(PlayerInfo playerInfo) {

        //da capire ccome usarla
        out.println(" Players in the room:");
//
//        for (Map.Entry<String, Color> entry : playerInfo.en) {
//            String nickname = entry.getKey();
//            Color color = entry.getValue();
//
//            out.printf("- %s (Color: %s)%n", nickname, color.name());
//        }

        out.println();
    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {


        GameState phase = phaseUpdate.getState();

        if (phase.equals(GameState.BUILDING_TIMER)){
            new Thread(()->{

                showGenericMessage("TIMER STARTED !!");
            }).start();
            return;
        } else {
            menuManager.setMenuText(phase);
            if (phaseUpdate.getState().equals(GameState.BUILDING_START) || phaseUpdate.getState().equals(GameState.SHIP_CHECK)) {
                synchronized (outputLock) {

                    menuManager.showCurrentMenu();
                }
                handleChoiceForPhase(phase);
            }
        }

    }

    public void handleChoiceForPhase(GameState phase) {
        switch (phase) {
            case BUILDING_START, BUILDING_END -> showBuildingMenu();
            case SHIP_CHECK -> showcheckShipMenu();
        }
    }
    @Override
    public void showBuildingMenu() {
        try {
            String input = readLine("\nChoose an option (a–k) or menu: ").trim().toLowerCase();


            clientController.handleBuildingMenuChoice(input);

        } catch (Exception e) {
            out.println(" Error: " + e.getMessage());
        }
    }

    @Override
    public void FetchMyShip(){
        new Thread(()->{
            try{
                clientController.handleFetchShip(clientController.getNickname());
                menuManager.showCurrentMenu();


            } catch (Exception e) {
                showGenericMessage("Error fetching ship: " + e.getMessage() +", please try again");
                FetchMyShip();
                throw new RuntimeException(e);
            }

        }).start();
    }

    @Override
    public void askViewAdventureDecks(){
        try {

            String DeckIDStr = readLine("Enter which Deck you want to view (1~3)> ").trim();
            int DeckID = Integer.parseInt(DeckIDStr);
            if(DeckID < 1 || DeckID >3){
                out.println("Deck ID not valid. Please enter a number between 1 and 3.");
                askViewAdventureDecks();
                return;
            }
            clientController.viewAdventureCardDeck(DeckID);
        } catch (Exception e) {
            out.println(" Error during ask view adventure Decks: " + e.getMessage());
        }

    }

    @Override
    public void askFetchShip() {
        new Thread(()->{
            try{
                String targetName = readLine("Enter the nickname of the player to fetch their ship ");
                clientController.handleFetchShip(targetName);
                menuManager.showCurrentMenu();


            } catch (InterruptedException | ExecutionException e) {
                showGenericMessage("Error fetching ship: " + e.getMessage() +", please try again");
                askFetchShip();
                throw new RuntimeException(e);
            }

        }).start();
    }

    @Override
    public void askRotation() {
        try {
            out.println("Enter rotation degree (90, 180, 270, or 360): ");
            String input = readLine("> ").trim();
            int rotation = InputUtils.parseRotation(input);
            clientController.rotateCurrentTile(rotation);

        } catch (Exception e) {
            out.println(" Error during tile rotation: " + e.getMessage());
        }

    }

    @Override
    public void askPosition() throws ExecutionException {
        try {
            String input = readLine("Enter position to move the tile to (format: (x,y)): ").trim();
            Position pos = InputUtils.parseCoordinate(input);
            clientController.moveCurrentTile(pos.getX(),pos.getY());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
@NeedsToBeCompleted
// TileView
    @Override
    public void showTile(Tile tile) {
    TilePrintUtils.printTile(tile);
    }

    @Override
    public void askDrawTile() {

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
            clientController.showTileInHand();

            out.println("Do you want to place this tile at the current position and rotation? (y/n)");
            String input = readLine("> ").trim().toLowerCase();
            Boolean confirm = input.equals("y");
            if (confirm) {
                clientController.handleTilePlacement(confirm);
            } else {
                out.println("Tile not placed. You can rotate or move it again.");
                showBuildingMenu();
            }

        } catch (Exception e) {
            out.println(" Error during tile placement: " + e.getMessage());
        }

    }

    @Override
    public void askFinishBuilding() {

    }

    @Override
    public void showcheckShipMenu(){
        try {
            String input = readLine("\nChoose an option (a–c) or menu: ").trim().toLowerCase();


            clientController.handleCheckShipChoice(input);

        } catch (Exception e) {
            out.println(" Error: " + e.getMessage());
        }
    }
    @Override
    public void showembarkCrewMenu() {

    }

    @Override
    public void showShip(Ship targetShipView) {
        ShipPrintUtils.printShip(targetShipView);
    }

    @Override
    public void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException {

        String input;

            System.out.println("Free positions: ");
            for (Integer i : validPositions) {
                System.out.println(" --> " + i);
            }

             input = readLine(" Choose one > ").trim().toLowerCase();
            while (Integer.parseInt(input) < validPositions.getFirst() || Integer.parseInt(input) > validPositions.getLast()) {
                askFlightBoardPosition(validPositions, id);
            }


        AskPositionResponse askPositionResponse = new AskPositionResponse(id, Integer.parseInt(input));

        clientController.getClient().sendMessage(askPositionResponse);


    }


    public void showGenericMessage(String message) {

            synchronized (outputLock) {
                System.out.print("\r"); // Torna all'inizio della riga
                System.out.print(" ".repeat(100)); // Cancella eventuale scrittura
                System.out.print("\r"); // Torna di nuovo all'inizio
                System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);

                // Ripristina il prompt e quello che l'utente aveva scritto
//            System.out.print("> " + liveInputBuffer.toString());
            }

    }






    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {

        //salvo tutte le posizioni delle housing

        ArrayList<Position> housinPos = myShip.getHousingPos();

        int nBrownAliens = 0;
        int nPurpleAliens = 0;

        CrewInitUpdate crewInitUpdate = new CrewInitUpdate();


        for (Position pos: housinPos){

            Slot tempSlot = myShip.getShipBoard()[pos.getY()][pos.getX()];

            if (tempSlot != null){
                if (tempSlot.getTile() != null){

                    ModularHousingUnit housing = (ModularHousingUnit) tempSlot.getTile().getMyComponent();

                  if (Util.checkNearLFS(pos, housing.getAlienColor(),myShip )){

                      if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("PurpleLifeSupportSystem") && housing.getAlienColor().equals(AlienColor.PURPLE) && nPurpleAliens == 0) {
                          CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.PURPLE);
                      }

                      else if (tempSlot.getTile().getMyComponent().accept(new ComponentNameVisitor()).equals("BrownLifeSupportSystem") && housing.getAlienColor().equals(AlienColor.BROWN) && nBrownAliens == 0) {
                          CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.BROWN);
                      }
                  }

                  CabinUnitAscii.printCabinUnitWithFigures(2, false, AlienColor.EMPTY);

                  String choice = readLine("Choose (1/2/3)>");
                  boolean correct = false;

                  while (!correct)
                  switch (choice){
                      case "1": {
                          if (nPurpleAliens == 0) {
                              nPurpleAliens++;
                              correct = true;
                              crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.PURPLE));

                          } else {

                              if (nBrownAliens == 0) {
                                  CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.BROWN);
                              }
                              CabinUnitAscii.printCabinUnitWithFigures(2, false, AlienColor.EMPTY);

                               choice = readLine("Choose (1/2/3)>");
                               break;


                          }

                      }

                      case "2": {
                          if (nBrownAliens == 0) {
                              nBrownAliens++;
                              correct = true;
                              crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.BROWN));

                          } else {

                              if (nPurpleAliens == 0) {
                                  CabinUnitAscii.printCabinUnitWithFigures(1, true, AlienColor.PURPLE);
                              }
                              CabinUnitAscii.printCabinUnitWithFigures(2, false, AlienColor.EMPTY);

                              choice = readLine("Choose (1/2/3)>");
                              break;


                          }
                      }

                      case "3": {
                          crewInitUpdate.addCrewPos(new Pair<>(pos, AlienColor.PURPLE));

                      }
                  }



                }
            }

        }

        //fine for

        notifyObservers(crewInitUpdate);

    }



    public void showFlightBoard() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        FlightBoard myFB = clientController.getMyModel().getFlightBoard();
        ArrayList<Color> colorsToPrint = new ArrayList<>();

//        for (int i = 0; i < myFB.getFlightBoardMap().getFlightBoardMapSlots().size(); i++) {
//
//            Color color  = myFB.getFlightBoardMap().getFlightBoardMapSlots().get(i).getPlayerToken();
//            colorsToPrint.add(color);
//        }

        colorsToPrint = (ArrayList<Color>) myFB.getFlightBoardMap().getFlightBoardMapSlots().stream().map(FlightBoardMapSlot::getPlayerToken).toList();

        FlightBoardTUI.printPistaRettangolare(colorsToPrint);



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
            }
            catch (TooManyPlayersException | PlayerAlreadyExistsException | InvalidTilePosition | InterruptedException e) {
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
}



