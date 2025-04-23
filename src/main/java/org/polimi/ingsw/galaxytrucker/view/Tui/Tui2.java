package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
//import org.polimi.ingsw.galaxytrucker.controller.ClientController2;
import org.polimi.ingsw.galaxytrucker.controller.ClientController2;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
//import org.polimi.ingsw.galaxytrucker.enums.MenuText;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
//import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.CabinUnitAscii;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.view.View2;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;


public class Tui2 implements View2, Observable {


    private static final String STR_INPUT_CANCELED = "CAXX";
    private static PrintStream out;
    private final Boolean isSocket;
    private ClientController2 clientController;
    //    ReadLine readLine = new ReadLine();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Object inpuLock = new Object();
    private final ArrayList<Observer> observers = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//    private final MenuManager menuManager = new MenuManager();
    ComponentNameVisitor cNameVisitor = new ComponentNameVisitor();

    public Tui2(PrintStream out, Boolean isSocket, ClientController2 clientController2) {
        Tui2.out = out;
        this.isSocket = isSocket;
        this.clientController = clientController2;
       this.addObserver(clientController);


    }


    public String readLine(String prompt) throws ExecutionException, InterruptedException {

        synchronized (inpuLock) {
            System.out.print(prompt);
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            } else {
                throw new NoSuchElementException("No linea found.");
            }
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
        askNickname();
    }

    public void askServerInfo() {
            Map<String, String> serverInfo = new HashMap<>();
            String defaultAddress = "localhost";
            String defaultPort = "5000";

            if (!isSocket) defaultPort = "1099";



            out.println("Please specify the following settings. The default value is shown between brackets.");

            String prop = "Enter the server address [" + defaultAddress + "]: ";


            String address;
            try {
                address = readLine(prop);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (address.isEmpty()) {
                serverInfo.put("address", defaultAddress);
            } else {
                serverInfo.put("address", address);
            }

            String prompt = "Enter the server port [" + defaultPort + "]: ";
            String port;
            try {
                port = readLine(prompt);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }


            if (port.equals("")) {
                serverInfo.put("port", defaultPort);

            } else {

                serverInfo.put("port", port);

            }

            int numero = Integer.parseInt(serverInfo.get("port"));
            SERVER_INFO message = new SERVER_INFO(serverInfo.get("address"), numero);
            try {
                notifyObservers(message);
            } catch (IOException | ExecutionException e) {
                throw new RuntimeException(e);
            }
    }


    public void askNickname() throws IOException, ExecutionException, InterruptedException {
            try {
                String nickname = readLine("Enter your nickname: ");
                NicknameRequest request = new NicknameRequest(nickname);

                //Future Joined per gestire la risposta
                //Una volta creato request dobbiamo fare 3 cose se ha una risposta corrispondente
                //1.Creo un CompletableFuture per aspettare la risposta dal server.
                CompletableFuture<NetworkMessage> future = new CompletableFuture<>();

                //aggiungere alla lista

                clientController.setCompletableFuture(future, request.getId());
                //2. notify al clientController che ho creato una nuova richiesta
                notifyObservers(request);

                //3. risolvere la risposta
                NicknameResponse nicknameResponse = (NicknameResponse) future.get();
                System.out.println(nicknameResponse.getResponse());
                if ("VALID".equals(nicknameResponse.getResponse())) {
                    clientController.setNickname(request.getNickname());
                    out.println("Nickname accepted.");

                    askJoinOrCreateRoom();
                } else {
                    out.println("Nickname not accepted.");

                    try {
                        askNickname();
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore durante l'inserimento del nickname.");
            }


    }

    @Override
    public void askJoinOrCreateRoom() {

            try {
                String choice;
                do {
                    choice = readLine("Create Room (a) or Join Room (b): ").trim().toLowerCase();
                    if (!choice.equals("a") && !choice.equals("b")) {
                        out.println(" Invalid input. Please enter 'a' or 'b'.");
                    }
                } while (!choice.equals("a") && !choice.equals("b"));

                if (choice.equals("a")) {
                    handleCreateRoom();
                } else {
                    handleJoinRoomOptions();
                }

            } catch (Exception e) {
                System.err.println(" Error while choosing: " + e.getMessage());
            }

    }


    public void handleCreateRoom() {
        executor.submit(() -> {
            try {
                String maxPlayersStr = readLine("Set MAX players for Game (2-4): ");
                int maxPlayers = Integer.parseInt(maxPlayersStr);
                if (maxPlayers < 2 || maxPlayers > 4) {
                    out.println(" Please enter a number between 2 and 4.");
                    handleCreateRoom(); // Retry
                    return;
                }

                String learningInput = readLine("Is this a Learning Match? (y/n): ");
                boolean isLearningMatch;

                if (learningInput.equals("y")) {
                    isLearningMatch = true;
                } else if (learningInput.equals("n")) {
                    isLearningMatch = false;
                } else {
                    out.println(" Invalid input. Please type 'y' or 'n'.");
                    handleCreateRoom();  // Retry
                    return;
                }

                CreateRoomRequest request = new CreateRoomRequest(maxPlayers, isLearningMatch, clientController.getNickname());
                notifyObservers(request);

            } catch (NumberFormatException e) {
                out.println(" Invalid number format.");
                askJoinOrCreateRoom();  // Retry
            } catch (Exception e) {
                System.err.println(" Error during room creation: " + e.getMessage());
            }
        });
    }

    public void handleJoinRoomOptions() {
        executor.submit(() -> {
            try {
                JoiniRoomOptionsRequest request = new JoiniRoomOptionsRequest();

                CompletableFuture<NetworkMessage> future =  clientController.expectResponse(request.getId());
                notifyObservers(request);


//                future.thenAccept(response -> {
//                    List<LobbyInfo> lobbies = response.getLobbyInfos();
//                    if (lobbies.isEmpty()) {
//                        out.println(" No lobbies found. Try creating a new one.");
//                        askJoinOrCreateRoom();
//                        return;
//
//                    }
//                    out.println(" Lobbies found.");
//                    showLobbies(lobbies);
//                    askRoomCode();
//                });

            } catch (Exception e) {
                System.err.println(" Error while joining room: " + e.getMessage());
            }
        });
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


    @Override
    public void askRoomCode() {

        executor.submit(() -> {
            out.println("\nPlease enter the Lobby ID you want to join:");

            try {
                // Legge l'input dell'utente
                String input = readLine("> ");
                int lobbyId = Integer.parseInt(input);

                // Crea la richiesta con nickname e ID lobby
                JoinRoomRequest request = new JoinRoomRequest(lobbyId, clientController.getNickname());

                // Registra la richiesta per attendere una risposta futura
                CompletableFuture<NetworkMessage> future = clientController.expectResponse(request.getId());


                // Notifica agli observer (controller) la richiesta da inviare
                notifyObservers(request);

                // Gestisce la risposta quando arriva
//                future.thenAccept(response-> {
//                    if (response.getOperationSuccess()) {
//                        out.println("Successfully joined the lobby! Waiting for other players...");
//                    } else {
//                        out.println("Failed to join the lobby: " + response.getErrMess());
//                        askRoomCode();
//                    }
//                });

            } catch (Exception e) {
                // Input non valido (es. non un numero)
                System.err.println("Invalid lobby ID.");
                askRoomCode();  // Ripeti la richiesta
            }
        });
    }


    @Override
    public void askFinishBuilding() {

    }

    public void showGenericMessage(String message) {
        System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void showShipStatus(Object ship) {

    }

    @Override
    public void showShip(String targetname) {

    }

    @Override
    public void showTileTaken(int tileID) {
        out.println("Tile with ID  " + tileID +  " has been taken from the common tile bunch.");
    }


    @Override
    public void showLobbies(List<LobbyInfo> lobbies) {
        if (lobbies.isEmpty()) {
            out.println(" No game rooms, Returning to main menu. ");
            return;
        }
        out.println("：List of rooms");
        for (LobbyInfo lobby : lobbies) {
            out.printf("Lobby ID: %d | Host: %s | Players: (%d/%d) %n",
                    lobby.getLobbyID(),
                    lobby.getHost(),
                    lobby.getConnectedPlayers(),
                    lobby.getMaxPlayers());
        }

    }

    @Override
    public void showPlayerJoined(Map<String, Color> playerInfo) {
        out.println(" Players in the room:");

        for (Map.Entry<String, Color> entry : playerInfo.entrySet()) {
            String nickname = entry.getKey();
            Color color = entry.getValue();

            out.printf("- %s (Color: %s)%n", nickname, color.name());
        }

        out.println();
    }

//    @Override
//    public void askMaxPlayers() throws ExecutionException, InterruptedException, IOException {
//        String number = readLine("You are the host \n Insert Max Players num: ");
//        String learningMatch = readLine("Learning Match ?");

    /// /        NICKNAME_REQUEST nicknameRequest = new NICKNAME_REQUEST(NetworkMessageType.NICKNAME_REQUEST, nickname,true);
//
//        NUM_PLAYERS_REQUEST request = new NUM_PLAYERS_REQUEST(Integer.parseInt(number), Boolean.parseBoolean(learningMatch));
//        notifyObservers(request);
//    }


    @Override
    public void showAdventureDeck() {

    }
    @Override
    @NeedsToBeCompleted

    public void handlePhaseUpdate(PhaseUpdate phaseUpdate) {
        GameState phase = phaseUpdate.getState();

//        menuManager.setMenuText(phase);
//        menuManager.showCurrentMenu();
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


        executor.submit(() -> {
            try {
                String input = readLine("Choose an option: ");

                switch (input.trim().toLowerCase()) {
                    case "a" -> askFetchShip();
                    case "b" -> askViewAdventureDecks();
//                  case "c" -> notifyObservers(new ShowFaceUpTilesRequest());
                    case "d" -> askDrawTile();
                    case "e" -> showTileInHand();
                    case "f" -> handleRotateTile();
                    case "g" -> handleMoveTile();
                    case "h" -> askTilePlacement();
                    case "i" -> notifyObservers(new DiscardTileRequest(clientController.getCurrentTileInHand()));
                    case "j" -> notifyObservers(new FinishBuildingRequest(clientController.getCurrentShip(),clientController.getCurrentTileInHand()));

                    default -> {
                        out.println(" Invalid option.");
                        showBuildingMenu();
                        out.println("\nChoose an option (a–k): ");
                    }
                }

            } catch (Exception e) {
                out.println(" Error: " + e.getMessage());
            }
        });

    }
    @NeedsToBeCompleted
//Alla ricezione della risposta FetchShipResponse, il contenuto del messaggio di risposta deve includere:
//
//Un campo boolean inputValid che indica se il nickname cercato esiste oppure no.
//Se inputValid == false, la Tui deve semplicemente stampare un messaggio, ad esempio:
//"❌ Il nickname inserito non corrisponde a nessun giocatore."
//
//Se inputValid == true, allora la Tui deve chiamare showShip(ship) per mostrare la nave corrispondente al giocatore richiesto.
    @Override
    public void askFetchShip() {
        executor.submit(() -> {

            try{
                String targetname = readLine("> ");
                FetchShipRequest request = new FetchShipRequest(targetname);
                CompletableFuture<NetworkMessage> future =  clientController.expectResponse(request.getId());
                notifyObservers(request);

//                future.thenAccept(response-> {
//                    showShipStatus(response.getTargetNickname());
//                });


            }catch(Exception e){
                System.err.println(" Invalid name.");
            }
        });
    }
    @NeedsToBeCompleted
//Quali informazioni devono essere mostrate per una AdventureCard?
//Ad esempio:
//
//ID della carta
//Level
//Day Lost
//learningFlight
//Name
    public void askViewAdventureDecks(){
        executor.submit(() -> {
            out.println("\nPlease enter the Deck (2~4) you want to View:");

            try {

                String input = readLine("> ");
                int DeckID= Integer.parseInt(input);


                if(!(DeckID >= 2 && DeckID <= 4)) {
                    out.println(" Invalid deck ID. Please enter a valid deck ID.");
                    askViewAdventureDecks();
                }
                else{
//                    switch (DeckID) {
//                        case(2){
//                            clientController.getDeck2();
//                        }
//                        case(3){
//
//                        }
//                        case(4){
//
//                        }
//                    }
                }

            } catch (Exception e) {
                // Input non valido (es. non un numero)
                System.err.println("Invalid Deck ID.");

            }
        });
    }

    @NeedsToBeCompleted
    //Stampare le informazioni base di una tessera (Tile)

    @Override
    public void askDrawTile() {
        executor.submit(() -> {
            try {
                out.println("\n How do you want to draw a tile?");
                out.println("1. Randomly draw a tile from the back area");
                out.println("2. Choose a tile by ID from the face-up area");
                String input = readLine("> ");

                if ("1".equals(input.trim())) {
                    DrawTileRequest request = new DrawTileRequest();
                    CompletableFuture<NetworkMessage> future =clientController.expectResponse(request.getId());
                    notifyObservers(request);

//                    future.thenAccept(response -> {
//                        Tile tile = response.getTile();
//                        if (tile != null) {
//                            showTile(tile);
//                        }
//                        else{
//                            out.println("no tile found. Please try again.");
//                            askDrawTile();
//                        }
//
//                    });

                } else if ("2".equals(input.trim())) {
                    out.println("Enter the tile ID you wish to draw: ");
                    String tileIdStr = readLine("> ");
                    int tileId = Integer.parseInt(tileIdStr);
                    DrawTileRequest request = new DrawTileRequest(tileId);
                    CompletableFuture<NetworkMessage> future = clientController.expectResponse(request.getId());
                    notifyObservers(request);

//                    future.thenAccept(response -> {
//                        Tile tile = response.getTile();
//                        if (tile != null) {
//                            showTile(tile);
//                        }
//                        else{
//                            out.println("Tile chosen not found. Please try again.");
//                            askDrawTile();
//                        }
//
//                    });
                } else {
                    out.println(" Invalid choice. Please enter 1 or 2.");
                    askDrawTile();
                }

            } catch (Exception e) {
                System.err.println(" Error while reading input: " + e.getMessage());
            }
        });
    }
    @NeedsToBeCompleted
    //Stampare le informazioni base di una tessera (Tile) connector

    public void showTileInHand() {
        Tile tile = clientController.getCurrentTileInHand();
        out.println("You drew a tile.");
        out.println("Tile ID: " + tile.getId());
        out.println("Tile Type: " + tile.getMyComponent().accept(new ComponentNameVisitor()));
        out.println("Tile Rotation: " + tile.getRotation());
    }

    public void showTile(Tile tile) {

        out.println("You drew a tile.");
        out.println("Tile ID: " + tile.getId());
        out.println("Tile Type: " + tile.getMyComponent().accept(new ComponentNameVisitor()));
        out.println("Tile Rotation: " + tile.getRotation());
    }

    public void handleRotateTile(){
        executor.submit(() -> {
            try {
                out.println("Enter rotation degree (90, 180, 270, or 360): ");
                String input = readLine("> ").trim();

                int rotation = Integer.parseInt(input);

                List<Integer> validRotations = Arrays.asList(90, 180, 270, 360);

                if (!validRotations.contains(rotation)) {
                    out.println("Invalid rotation. Please enter 90, 180, 270, or 360.");
                    handleRotateTile(); // Retry
                    return;
                }


                Tile tile = clientController.getCurrentTileInHand();
                if (tile != null) {
                    tile.rotate(rotation);
                    showTile(tile);
                } else {
                    out.println(" You currently have no tile in hand to rotate.");
                }

            } catch (NumberFormatException e) {
                out.println("Please enter a valid number.");
                handleRotateTile(); // Retry
            } catch (Exception e) {
                out.println(" Error during tile rotation: " + e.getMessage());
            }
        });
    }

    public void handleMoveTile(){
        executor.submit(()-> {
            try {
                String input = readLine("Enter position to move the tile to (format: (x,y)): ").trim();

                input = input.replaceAll("[()\\s]", "");

                String[] parts = input.split(",");

                if (parts.length != 2) {
                    out.println(" Invalid format. Please use (x,y).");
                    handleMoveTile(); // Retry
                    return;
                }

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                Position position = new Position(x,y);
                clientController.setCurrentTilePosition(position);

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void askTilePlacement() {
        executor.submit(() -> {
            try {
                Tile tile = clientController.getCurrentTileInHand();
                if (tile == null) {
                    out.println(" No tile in hand to place.");
                    return;
                }

                out.println(" Current tile info:");
                showTileInHand();

                out.println("Do you want to place this tile at the current position and rotation? (y/n)");
                String confirm = readLine("> ").trim().toLowerCase();

                if (confirm.equals("y")) {
                    Position position = clientController.getCurrentTilePosition();
                    PlaceTileRequest request = new PlaceTileRequest(tile,position);
                    CompletableFuture<NetworkMessage> future = clientController.expectResponse(request.getId());
                    notifyObservers(request);
//                    future.thenAccept(response -> {
//                        String message = response.getMessage();
//                        out.println( message );
//                    });

                } else {
                    out.println(" Tile not placed. You can rotate or move it again.");
                    showBuildingMenu();
                }

            } catch (Exception e) {
                out.println(" Error during tile placement: " + e.getMessage());
            }
        });
    }

    @Override
    public void showcheckShipMenu() {

    }

    @Override
    public void showembarkCrewMenu() {

    }

    @Override
    public void checkShipMenu() {

    }

    @Override
    public void embarkCrewMenu() {

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

    public void setClientController(ClientController2 clientController) {
        this.clientController = clientController;
        this.addObserver(clientController);
    }

    public void setController(ClientController2 clientController) {
        this.clientController = clientController;
        this.addObserver(clientController);
    }
}
