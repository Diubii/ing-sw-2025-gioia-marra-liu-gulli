package org.polimi.ingsw.galaxytrucker.view.Tui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.controlsfx.tools.Utils;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NicknameResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.observer.Observable;
//import org.polimi.ingsw.galaxytrucker.view.Tui.util.ReadLine;
import org.polimi.ingsw.galaxytrucker.observer.Observer;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.CabinUnitAscii;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.FlightBoardTUI;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.ComponentNameVisitor;

import static org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils.*;
import static org.polimi.ingsw.galaxytrucker.view.Tui.util.TilePrintUtils.printTile;

public class Tui implements View, Observable {


    private static final String STR_INPUT_CANCELED = "CAXX";
    private static PrintStream out;
    private final Boolean isSocket;
    private final ClientController clientController;
    //    ReadLine readLine = new ReadLine();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Object inpuLock = new Object();
    private final ArrayList<Observer> observers = new ArrayList<>();


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

        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        //TEST STAMPA DA TOGLIERE
        /*Ship testShip = new Ship(false);

        //Prendo lista tiles e metto in ship per testare
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Tile> tiles = new ArrayList<>();
        try{
            FileInputStream fis = new FileInputStream("src/main/resources/tiledata.json");
            tiles = mapper.readValue(fis, new TypeReference<ArrayList<Tile>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

          try{
              for(int i =0; i<7; i++){
                  for(int j =0; j<5; j++){
                      if(j!= 3) {
                          testShip.putTile(tiles.get(i * 5 * j), new Position(j, i));
                      }
                  }
              }
              tiles.get(152).rotate(180);
              testShip.putTile(tiles.get(18),new Position(3,0));
              testShip.putTile(tiles.get(54),new Position(3,1));
              testShip.putTile(tiles.get(64),new Position(3,2));
              testShip.putTile(tiles.get(93),new Position(3,3));
              testShip.putTile(tiles.get(152),new Position(3,4));
              testShip.putTile(tiles.get(136),new Position(3,5));
              testShip.putTile(tiles.get(137),new Position(3,6));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        printShip(testShip);
        printTile(tiles.get(152));
        //Vedi colori ok non occupano spazio, ma "emoji si più di uno esatto"...
        */
        //Per le carte

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

        boolean validInput;

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
            validInput = true;
        } else {

            serverInfo.put("port", port);
            validInput = true;
        }

        int numero = Integer.parseInt(serverInfo.get("port"));
        SERVER_INFO message = new SERVER_INFO(serverInfo.get("address"), numero);
        notifyObservers(message);
    }


    public void askNickname() throws IOException, ExecutionException, InterruptedException {

//        System.out.println("BICKNAME: ");
        String nickname = readLine("Enter yournickname: ");
        NicknameRequest nicknameRequest = new NicknameRequest(nickname);

        CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
        clientController.setCompletableFuture(future, nicknameRequest.getId());
        notifyObservers(nicknameRequest);


        NicknameResponse nicknameResponse = (NicknameResponse) future.get();
        if (nicknameResponse.getResponse().equals("VALID")){
            clientController.setNickname(nickname);
        }
        System.out.println("RESPONSE is : " + nicknameResponse.getResponse());
        askLobbyChoice();


    }


    public void showGenericMessage(String message) {
        System.out.println(TuiColor.YELLOW + message + TuiColor.RESET);
    }

    @Override
    public void askMaxPlayers() throws ExecutionException, InterruptedException, IOException {
        String number = readLine("You are the host \n Insert Max Players num: ");
        String learningMatch = readLine("Learning Match ?");
//        NICKNAME_REQUEST nicknameRequest = new NICKNAME_REQUEST(NetworkMessageType.NICKNAME_REQUEST, nickname,true);

        NUM_PLAYERS_REQUEST request = new NUM_PLAYERS_REQUEST(Integer.parseInt(number), Boolean.parseBoolean(learningMatch));
        notifyObservers(request);
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
    public void askLobbyChoice() throws ExecutionException, InterruptedException, IOException {
        String number = readLine("1) Crea ROom \n 2) Mostra ROoms ");
        if (number.equals("1")) {
            CreateRoomRequest createRoomRequest = new CreateRoomRequest(2, true, clientController.getNickname());

            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
            clientController.setCompletableFuture(future, createRoomRequest.getId());
            notifyObservers(createRoomRequest);

        } else {

            JoiniRoomOptionsRequest joiniRoomOptionsRequest = new JoiniRoomOptionsRequest();
            CompletableFuture<NetworkMessage> future = new CompletableFuture<>();
            clientController.setCompletableFuture(future, joiniRoomOptionsRequest.getId());
            notifyObservers(joiniRoomOptionsRequest);
            JoinRoomOptionsResponse response = (JoinRoomOptionsResponse) future.get();

            if (response.getLobbyInfos() != null) {
                for (LobbyInfo l : response.getLobbyInfos()) {
                    System.out.println("LOBBY INFO: ");
                    System.out.println("PLAYERS: " + l.getConnectedPlayers());
                    System.out.println("ID" + l.getLobbyID());
                    System.out.println("HOST: " + l.getHost());

                }


                int nMax = response.getLobbyInfos().size();


                String lobbyN = readLine("Insert LobbyNum >");

                JoinRoomRequest joinRoomRequest = new JoinRoomRequest(Integer.parseInt(lobbyN),clientController.getNickname());
                CompletableFuture<NetworkMessage> future2 = new CompletableFuture<>();
                clientController.setCompletableFuture(future2, joinRoomRequest.getId());

                notifyObservers(joinRoomRequest);

            }



        }

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



