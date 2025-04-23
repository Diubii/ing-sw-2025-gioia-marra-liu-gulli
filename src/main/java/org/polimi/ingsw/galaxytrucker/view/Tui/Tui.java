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
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
//import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.CabinUnitAscii;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.FlightBoardTUI;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;


public class Tui implements View, Observable {


    private static final String STR_INPUT_CANCELED = "CAXX";
    private static PrintStream out;
    private final Boolean isSocket;
    private final ClientController clientController;
    //    ReadLine readLine = new ReadLine();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Object inpuLock = new Object();
    private final ArrayList<Observer> observers = new ArrayList<>();

    private  MenuManager menuManager = new MenuManager();

    public Tui(PrintStream out, Boolean isSocket, ClientController controller) {
        Tui.out = out;
        this.isSocket = isSocket;
        this.clientController = controller;
        this.addObserver(clientController);

    }

    public String readLine(String prompt) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            synchronized (inpuLock) {
                System.out.print(prompt);
                if (scanner.hasNextLine()) {
                    return scanner.nextLine();
                } else {
                    throw new NoSuchElementException("Nessuna linea trovata.");
                }
            }
        });

        String input = future.get();
        executor.shutdown();
        return input;
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
                    out.println("Invalid input. Please enter 'a' or 'b'.");
                }
            } while (!choice.equals("a") && !choice.equals("b"));


            clientController.handleCreateOrJoinChoice(choice);

        } catch (Exception e) {
            System.err.println("Error while choosing: " + e.getMessage());
        }
    }

    public void askCreateRoom() {
        try {
            String maxPlayersStr = readLine("Set MAX players for Game (2-4): ");
            int maxPlayers = Integer.parseInt(maxPlayersStr);

            if (maxPlayers < 2 || maxPlayers > 4) {
                out.println("Please enter a number between 2 and 4.");
                askCreateRoom(); // Retry
                return;
            }

            String learningInput = readLine("Is this a Learning Match? (y/n): ");
            boolean isLearningMatch;

            if (learningInput.equals("y")) {
                isLearningMatch = true;
            } else if (learningInput.equals("n")) {
                isLearningMatch = false;
            } else {
                out.println("Invalid input. Please type 'y' or 'n'.");
                askCreateRoom();  // Retry
                return;
            }

            clientController.handleCreateChoice(maxPlayers, isLearningMatch);

        } catch (NumberFormatException | ExecutionException | InterruptedException e) {
            out.println("Invalid number format.");
            askCreateRoom(); // Retry
        }
    }


    public void askRoomCode() {
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

    public void showLobbies(List<LobbyInfo> lobbies) {
        if (lobbies.isEmpty()) {
            out.println(" No game rooms, Returning to main menu. ");
            return;
        }
        out.println("：List of rooms");
        for (LobbyInfo lobby : lobbies) {
            out.printf("Lobby ID: %d | Host: %s | Players: (%d/%d)  \n",
                    lobby.getLobbyID(),
                    lobby.getHost(),
                    lobby.getConnectedPlayers(),
                    lobby.getMaxPlayers());
        }


    }

    @Override
    public void showPlayerJoined(HashMap<String, Color> playerInfo) {
        out.println(" Players in the room:");

        for (Map.Entry<String, Color> entry : playerInfo.entrySet()) {
            String nickname = entry.getKey();
            Color color = entry.getValue();

            out.printf("- %s (Color: %s)%n", nickname, color.name());
        }

        out.println();
    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {

            GameState phase = phaseUpdate.getState();
            menuManager.setMenuText(phase);
            menuManager.showCurrentMenu();
            handleChoiceForPhase(phase);

    }

    public void handleChoiceForPhase(GameState phase) {
        switch (phase) {
            case BUILDING_START, BUILDING_END -> showBuildingMenu();
            case SHIP_CHECK -> showcheckShipMenu();
            default -> {
                out.println("Please wait. No input is required at this stage.");
            }
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
    public void askFetchShip() {
        new Thread(()->{
            try{
                String targetName = readLine("Enter the nickname of the player to fetch their ship ");
                clientController.handleFetchShip(targetName);

            } catch (ExecutionException | InterruptedException e) {
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

            int rotation = Integer.parseInt(input);

            List<Integer> validRotations = Arrays.asList(90, 180, 270, 360);

            if (!validRotations.contains(rotation)) {
                out.println("Invalid rotation. Please enter 90, 180, 270, or 360.");
                askRotation();

            }
            clientController.rotateCurrentTile(rotation);
        } catch (NumberFormatException e) {
            out.println("Please enter a valid number.");
            askRotation();
        } catch (Exception e) {
            out.println(" Error during tile rotation: " + e.getMessage());
        }

    }

    @Override
    public void askPosition() {
        try {
            String input = readLine("Enter position to move the tile to (format: (x,y)): ").trim();

            input = input.replaceAll("[()\\s]", "");

            String[] parts = input.split(",");

            if (parts.length != 2) {
                out.println(" Invalid format. Please use (x,y).");
                askPosition(); // Retry
                return;
            }

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            clientController.moveCurrentTile(x,y);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
@NeedsToBeCompleted
// TileView
    @Override
    public void showTile(Tile tile) {


            out.println("You drew a tile.");
            out.println("Tile ID: " + tile.getId());
            out.println("Tile Type: " + tile.getMyComponent().accept(new ComponentNameVisitor()));
            out.println("Tile Rotation: " + tile.getRotation());

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

    }

    @Override
    public void showembarkCrewMenu() {

    }



    public void showGenericMessage(String message) {
        System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);
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



