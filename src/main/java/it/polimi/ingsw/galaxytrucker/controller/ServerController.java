package it.polimi.ingsw.galaxytrucker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.*;
import it.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import it.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.network.Heartbeat;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.network.server.MessageManager;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import it.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;
import it.polimi.ingsw.galaxytrucker.visitors.Network.NetworkMessageNameVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

/**
 * The ServerController class handles the management of clients, lobbies, game tiles,
 * network messages, and various game-related actions. It acts as the core server-side
 * controller to manage game flow and client-server interactions.
 *
 * The class also provides mechanisms for synchronously or asynchronously executing tasks
 * and manages the mapping between clients and their respective nicknames or game lobbies.
 * Its primary responsibilities include handling client requests, managing game state,
 * and broadcasting messages to clients.
 *
 * ServerController extends java.rmi.server.UnicastRemoteObject and implements it.polimi.ingsw.galaxytrucker.controller.ServerControllerHandles.
 */
public class ServerController extends UnicastRemoteObject implements ServerControllerHandles {

    private final HashMap<Integer, LobbyManager> lobbyManagers;
    private final MessageManager messageManager;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final HashMap<UUID, String> clientNicknameMap = new HashMap<>();
    private final ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();
    private final ArrayList<Heartbeat> heartbeats = new ArrayList<>();
    private ArrayList<Tile> gameTiles;
    private static final NetworkMessageNameVisitor networkMessageNameVisitor = new NetworkMessageNameVisitor();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private boolean synchronousExecution = false;
    private static Integer nextLobbyIndex = 0;

    public void setSynchronousExecution(boolean sync) {
        this.synchronousExecution = sync;
    }

    public void execute(Runnable task) {
        if (synchronousExecution) {
            task.run(); //per test
        } else {
            executor.submit(task);
        }
    }

    public ServerController() throws RemoteException {
        super();

        this.lobbyManagers = new HashMap<>();
        messageManager = new MessageManager(this);
        initActionsAllowed();
        generateGameTiles();
    }



    /**
     * Populates the list of game tiles by deserializing data from a JSON file.
     * This method reads the tiles data from a JSON file located at a specific path
     * and maps it to an ArrayList of Tile objects. If there's an issue reading or
     * deserializing the file, the error message is printed to the standard error stream.
     *
     * Note: The JSON file path is hardcoded and might need adjustment based on the
     * project's structure or deployment setup.
     *
     * Exceptions:
     * - Handles IOExceptions that might occur during file reading or parsing.
     */
    public void generateGameTiles() {
        ObjectMapper mapper = new ObjectMapper();

        String path = "tiledata.json";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println(path + " not found.");
            } else {
                this.gameTiles = (ArrayList<Tile>) mapper.readValue(in, new TypeReference<List<Tile>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error while reading " + path + " : " + e.getMessage());
        }
    }

    /**
     * Adds a client to the list of managed clients in a thread-safe manner.
     *
     * @param client The {@link ClientHandler} instance representing the client to add.
     */
    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    /**
     * Retrieves the list of all connected client handlers.
     * This method ensures thread-safe access to the clients list.
     *
     * @return An ArrayList containing all the currently connected ClientHandler instances.
     */
    public ArrayList<ClientHandler> getClients() {
        synchronized (clients) {
            return clients;
        }
    }

    /**
     * Safely removes a client, freeing its nickname if it exists, and kicking it from an eventual game it's in.
     *
     * @param client The client's {@link ClientHandler}
     * @author Alessandro Giuseppe Gioia
     */
    public void removeClient(ClientHandler client) {
        String nickname = getNicknameFromClientHandler(client);
        LobbyManager game = getLobbyFromHandler(client);
        if(game != null) {
            game.getGameController().kickPlayerFromGame(nickname);
            synchronized (lobbyInfos) {
            lobbyInfos.removeIf(l -> l.getLobbyID() == game.getGameID());
            }
            synchronized (lobbyManagers) {
                lobbyManagers.remove(game.getGameID());
            }
        }

        if (nickname != null && !nickname.isBlank()) {
            synchronized (clientNicknameMap) {
                clientNicknameMap.remove(client.getClientID());
            }
        }
        synchronized (clients) {
            clients.remove(client);
        }
    }


    public MessageManager getMessageManager() {
        return messageManager;
    }


    public LobbyManager getLobbyFromHandler(ClientHandler clientHandler) {

        LobbyManager lobbyManager;
        synchronized (lobbyManagers) {
            lobbyManager = lobbyManagers.values().stream().filter(gameModel ->
                    gameModel.getPlayerHandlers().values().stream().anyMatch(h -> h.getClientID().equals(clientHandler.getClientID()))).findFirst().orElse(null);
        }

        return lobbyManager;
    }


    /**
     * Handles the nickname request sent by the client. Validates the provided nickname and
     * updates the server's record of client nicknames if the nickname is unique.
     * Sends a response back to the client indicating whether the nickname was accepted or rejected.
     *
     * @param message The {@link NicknameRequest} object containing the nickname to be validated.
     * @param clientHandler The {@link ClientHandler} instance representing the client making the request.
     * @throws RemoteException If a remote communication error occurs during the nickname handling process.
     */
    public void handleNicknameRequest(NicknameRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
                    Boolean result = false;
                    boolean flag = false;

                    //get nickname & check
                    String nickname = message.getNickname();
                    NicknameResponse nicknameResponse = new NicknameResponse(null, message.getID());


                    //System.out.println("REQ");
                    synchronized (clientNicknameMap) {

                        if (!clientNicknameMap.containsValue(nickname)) {
                            clientNicknameMap.put(clientHandler.getClientID(), nickname);
                            nicknameResponse.setResponse("VALID");
                        } else {
                            System.out.println("[+] NOT ADDED " + message.getNickname());
                            nicknameResponse.setResponse("INVALID");
                        }

                    }

                    clientHandler.sendMessage(nicknameResponse);
        });
        //System.out.println("SENDING RESPONSE\n");
    }

    /**
     * Handles a client request to create a new game room. This method initializes a new game lobby,
     * assigns colors to players, updates the game tiles, sets the game configurations, and sends
     * appropriate responses back to the client.
     *
     * @param message       The {@link CreateRoomRequest} object containing details about the room to be created,
     *                      such as the requested nickname, max players, and type of match.
     * @param clientHandler The {@link ClientHandler} responsible for managing communication with the client
     *                      that sent the request.
     * @throws RemoteException If an error occurs during the execution of a remote method call.
     */
    public void handleCreateRoomRequest(CreateRoomRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            //get nickname & check
            String tempNick = message.getNickName();

            LobbyManager newGame;
            synchronized (lobbyManagers) {
                newGame = new LobbyManager(nextLobbyIndex);
                lobbyManagers.put(newGame.getGameID(), newGame);
                nextLobbyIndex++;
            }

            Player myPlayer = new Player(message.getNickName(), 0, 0, message.getIsLearningMatch());

            Color myColor = newGame.useNextAvailableColor();
            myPlayer.setColor(myColor);

            newGame.getPlayerColors().putIfAbsent(message.getNickName(), myColor);
            newGame.getRealGame().setLearningMatch(message.getIsLearningMatch());
            newGame.getRealGame().setnMaxPlayer(message.getMaxPlayers());
            try {
                newGame.getRealGame().addPlayer(myPlayer);
            } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                System.err.println(e.getMessage());
                return;
            }

            newGame.addPlayerHandler(clientHandler, myPlayer.getNickName());
            if(message.isSubscribedToTimerUpdates()) newGame.getTimerSubscribers().add(clientHandler);
            newGame.getRealGame().initFlightBoard();

            Tile centralTile = null;

            for (Tile tile : gameTiles) {
                if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")) {
                    //System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                    CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                    if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)) {
                        centralTile = tile;
                        //gameTiles.remove(tile);
                    }

                }
            }

//            newGame.getRealGame().getTileBunch().getTiles().remove(centralTile);
//            gameTiles.remove(centralTile);



            myPlayer.getShip().putTile(centralTile, new Position(3, 2));

            synchronized (lobbyInfos) {
                lobbyInfos.add(new LobbyInfo(message.getNickName(), message.getMaxPlayers(), 1, newGame.getGameID(), message.getIsLearningMatch()));
            }

            JoinRoomResponse joinRoomResponse = new JoinRoomResponse(message.getID());
            joinRoomResponse.setOperationSuccess(true);
            joinRoomResponse.setColor(myColor);
            joinRoomResponse.setMyShip(myPlayer.getShip());
            joinRoomResponse.setIsLearningMatch(newGame.getRealGame().getIsLearningMatch());

            PlayerInfo playerInfo1 = new PlayerInfo();
            playerInfo1.setShip(myPlayer.getShip());
            playerInfo1.setNickName(myPlayer.getNickName());
            playerInfo1.setColor(myColor);
            newGame.addPlayerInfo(playerInfo1);


            clientHandler.sendMessage(joinRoomResponse);
            clientHandler.sendMessage(new FlightBoardUpdate(newGame.getRealGame().getFlightBoard()));
        });
    }

    /**
     * Handles a request to fetch the available options for joining a room. This method
     * processes a {@code JoiniRoomOptionsRequest} and sends back a {@code JoinRoomOptionsResponse}
     * to the requesting client.
     *
     * @param message The request message containing the data related to the join room options.
     * @param clientHandler The client handler responsible for sending the response to the client.
     * @throws RemoteException If a remote communication error occurs during message handling.
     */
    public void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            new JoinRoomOptionsResponse(null, message.getID());
            JoinRoomOptionsResponse joinRoomOptionsResponse;
            synchronized (lobbyInfos) {
                joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos, message.getID());
            }
            clientHandler.sendMessage(joinRoomOptionsResponse);
        });
    }

    /**
     * Handles a request from a client to join a specific game lobby.
     * This method processes the join room request, validates the lobby and player constraints,
     * and communicates the result to the requesting client. If the player successfully joins
     * the lobby, the other players in the lobby are notified. If the lobby becomes full,
     * it transitions to the game start phase.
     *
     * @param message       The `JoinRoomRequest` object containing the player's information and the target lobby ID.
     * @param clientHandler The `ClientHandler` responsible for communicating with the requesting client.
     * @throws RemoteException If there is an error during remote method invocation.
     */
    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            String mess = "";
            LobbyInfo myLobbyInfo;

            JoinRoomResponse joinRoomResponse = new JoinRoomResponse(message.getID());
            ArrayList<ClientHandler> playerHandlers;
            boolean result = false;
            PlayerJoinedUpdate playerJoinedUpdate;

            LobbyManager myGame = lobbyManagers.get(message.getRoomId());

            if (myGame == null) {
                mess = "Lobby number " + message.getRoomId() + " doesn't exist. Try again.";
                joinRoomResponse.setErrMess(mess);
                joinRoomResponse.setOperationSuccess(false);
                joinRoomResponse.setColor(null);
                clientHandler.sendMessage(joinRoomResponse);
                return;
            }

            synchronized (myGame) {
                //System.out.println("SIZE: " + myGame.getPlayerColors().size());

                if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers() || myGame.getGameController().getGameState() != GameState.LOBBY) {
                    //System.out.println("2");


                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY" + message.getRoomId() + "DOESN'T MUNGI YOU");
                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(false);
                    joinRoomResponse.setColor(null);
                    clientHandler.sendMessage(joinRoomResponse);
                    return;

                } else {

                    //System.out.println("3");

                    Player myPlayer = new Player(message.getNickName(), 0, 0, myGame.getRealGame().getIsLearningMatch());

                    //trovo la cabina con il colore

                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());


                    Color myColor = myGame.useNextAvailableColor();
                    myGame.getPlayerColors().putIfAbsent(message.getNickName(), myColor);
                    myPlayer.setColor(myColor);


                    Tile centralTile = null;

                    for (Tile tile : gameTiles) {
                        if (tile.getMyComponent().accept(new ComponentNameVisitor()).equals("CentralHousingUnit")) {
                            //System.out.println(tile.getMyComponent().accept(new ComponentNameVisitor()));
                            CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                            if (centralHousingUnit.getIsColored() && centralHousingUnit.getColor().equals(myColor)) {
                                centralTile = tile;
                                break;
                            }

                        }
                    }

//                    myGame.getRealGame().getTileBunch().getTiles().remove(centralTile);
//                    gameTiles.remove(centralTile);


                    myPlayer.getShip().putTile(centralTile, new Position(3, 2));


                    try {
                        myGame.getRealGame().addPlayer(myPlayer);
                    } catch (TooManyPlayersException | PlayerAlreadyExistsException e) {
                        throw new RuntimeException(e);
                    }

                    myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());

                    playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());


                    PlayerInfo playerInfo1 = new PlayerInfo();
                    playerInfo1.setShip(myPlayer.getShip());
                    playerInfo1.setNickName(myPlayer.getNickName());
                    playerInfo1.setColor(myColor);
                    //qua
//                ArrayList<ClientHandler> PlayerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());

                    playerJoinedUpdate = new PlayerJoinedUpdate(playerInfo1);


                    synchronized (lobbyInfos) {
                        myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
                    }


                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(true);
                    joinRoomResponse.setColor(myColor);
                    joinRoomResponse.setMyShip(myPlayer.getShip());
                    myGame.addPlayerInfo(playerInfo1);

                    if (myLobbyInfo != null) {

                        myLobbyInfo.addConnectedPlayer();
                        result = true;
                    } else {

                        mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NOT FOUND :) " + message.getRoomId());
                        joinRoomResponse.setOperationSuccess(false);
                        joinRoomResponse.setColor(null);
                        joinRoomResponse.setMyShip(null);


                    }
                }

                //fine synchronized e

                PlayerInfo hostPlayerInfo = new PlayerInfo();
                hostPlayerInfo.setNickName(myLobbyInfo.getHost());
                hostPlayerInfo.setShip(myGame.getRealGame().getPlayer(myLobbyInfo.getHost()).getShip());
                hostPlayerInfo.setColor(myGame.getPlayerColors().get(myLobbyInfo.getHost()));

                joinRoomResponse.setIsLearningMatch(myLobbyInfo.isLearningMatch());
                if (result) joinRoomResponse.setPlayerInfos(myGame.getPlayerInfos());

//            clientHandler.sendMessage(new PlayerJoinedUpdate(hostPlayerInfo));
                clientHandler.sendMessage(new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));
                clientHandler.sendMessage(joinRoomResponse);
                //System.out.println("ID RESP: " + joinRoomResponse.getID());

                //se tutto è andato bene
                if (result) {
                    if(message.isSubscribedToTimerUpdates()) myGame.getTimerSubscribers().add(clientHandler);

                    ArrayList<ClientHandler> original = new ArrayList<>(playerHandlers);
                    playerHandlers.remove(clientHandler);

                    playerJoinedUpdate.setPlayersJoinedBefore(myGame.getPlayerInfos());
                    broadCast(playerHandlers, playerJoinedUpdate);

                    //dopo aver mandato la notifica di connessione vedo se ho raggiunto il numero massimo di player per la lobby
                    //e starto il gioco automaticamente lato server

                    if (myGame.getRealGame().getMaxPlayers() == myGame.getRealGame().getNumPlayers()) {

                        //elimino tutte le cabine centrali da quelle pescabili

                        //creo flightDeck
                        myGame.getRealGame().createDecks();
                        ArrayList<CardDeck> decks = myGame.getRealGame().getDecks();


//                    CardDeck flightDeck = myGame.getRealGame().createFlightDeck(decks);


                        DecksUpdate decksUpdate = new DecksUpdate();
                        decksUpdate.setDecks(decks);
                        decksUpdate.setFlightDeck(null);


                        //broadcst tutte le player info

                        myGame.getGameController().nextState();
                        broadCast(original, decksUpdate);
                        broadCast(original, new PhaseUpdate(GameState.BUILDING_START));
                        //MANDARE I DECK -> -> ->

                        //dopo aver notificato tutti starto il gioco

                    }
                }

            }
        });

    }

    ///////

    /**
     * Handles the request to draw a tile during the gameplay. Depending on the state of the game,
     * the requested tile is drawn from the deck, face-up tiles, or reserved tiles. It ensures game
     * rules are adhered to before processing the request.
     *
     * @param message       The request message containing details about the tile to be drawn,
     *                      whether it is from face-up tiles, reserved tiles, or the main deck.
     * @param clientHandler The client handler associated with the requesting player, used to send responses
     *                      back and retrieve player-related information.
     * @throws RemoteException If a remote communication error occurs during the execution of the request.
     */
    public void handleDrawTileRequest(DrawTileRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            //il client mi chiede una Tile, e devo restituirla
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Tile myTile ;
            DrawTileResponse drawTileResponse;
            Player player = getPlayerFromClientHandler(clientHandler);
            Ship targetShip = player.getShip();
            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());


            if (!isActionAllowed(myGame, GameAction.DRAW_TILE)) {

                drawTileResponse = new DrawTileResponse(null, message.getID());
                drawTileResponse.setErrorMessage("INVALID_STATE");
                clientHandler.sendMessage(drawTileResponse);

                return;
            }

            synchronized (myGame.getTileBunch()) {

                //null significa cge si pesca dal mazzo, invece se è presente un valore valido di Tile si prende da quelle face-up
                if (message.getTile() != null) {

                    myTile = myGame.getTileBunch().drawFaceUpTile(message.getTile().getId());
                    if (myTile == null) {
                        drawTileResponse = new DrawTileResponse(null, message.getID());
                        drawTileResponse.setErrorMessage("TAKEN");
                    } else {
                        drawTileResponse = new DrawTileResponse(myTile, message.getID());
                        drawTileResponse.setErrorMessage("VALID");
                    }

                    FaceUpTileUpdate faceUpTileUpdate = new FaceUpTileUpdate();
                    faceUpTileUpdate.setFaceUpTiles(myGame.getTileBunch().getFaceUpTiles());

                    broadCast(playerHandlers, faceUpTileUpdate);

                } else {
                  if(message.isNeedLastTile()){
                       Tile lastTile =  targetShip.getLastTile();
                      if (lastTile == null) {
                          drawTileResponse = new DrawTileResponse(null, message.getID());
                          drawTileResponse.setErrorMessage("NO_TILE");
                      }
                       else {
                          if (lastTile.getFixed()) {
                              drawTileResponse = new DrawTileResponse(null, message.getID());
                              drawTileResponse.setErrorMessage("FIXED");
                          } else {
                              targetShip.removeTile(targetShip.getLastTilePosition(), true);
                              targetShip.setLastTile(null);
                              ShipUpdate shipUpdate = new ShipUpdate(targetShip, player.getNickName());

                              broadCast(playerHandlers, shipUpdate);

                              drawTileResponse = new DrawTileResponse(lastTile, message.getID());
                              drawTileResponse.setErrorMessage("VALID");
                          }
                      }
                  }
                  else {
                      if (message.isFromReserved()) {

                          Tile[] reserverd = targetShip.getAsideTiles();
                          int index = message.getReservedSlotIndex();
                          Tile removedTile = reserverd[index];
                          if (removedTile == null) {
                              drawTileResponse = new DrawTileResponse(null, message.getID());
                              drawTileResponse.setErrorMessage("NO_TILE_AT_INDEX");
                          } else {
                              reserverd[index] = null;
                              drawTileResponse = new DrawTileResponse(removedTile, message.getID());
                              drawTileResponse.setErrorMessage("VALID");

                              ShipUpdate shipUpdate = new ShipUpdate(targetShip, player.getNickName());
                              broadCast(playerHandlers, shipUpdate);
                          }

                      }

                      else{

                            myTile = myGame.getTileBunch().drawTile();
                            if (myTile == null) {
                                drawTileResponse = new DrawTileResponse(null, message.getID());
                                drawTileResponse.setErrorMessage("EMPTY");
                            } else {
                                drawTileResponse = new DrawTileResponse(myTile, message.getID());
                                drawTileResponse.setErrorMessage("VALID");
                            }
                      }
                    }
                }

            }
            clientHandler.sendMessage(drawTileResponse);
        });
    }

    /**
     * Handles the request to check the ship's status, validating its structure and notifying
     * the involved players about the results. This process includes removing specified tiles
     * from the ship, verifying if the ship structure is valid, broadcasting updates to all players,
     * and potentially transitioning the game state if all players complete their ships.
     *
     * @param message The {@link CheckShipStatusRequest} containing details about the tiles to be removed
     *                and the player's request ID.
     * @param clientHandler The {@link ClientHandler} associated with the player making the request.
     * @throws RemoteException If a remote communication error occurs during the process.
     */
    public void handleCheckShipStatusRequest(CheckShipStatusRequest message, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            //devo controllare se la nave è corretta

            //prima di tutto la salvo come nuova nave

            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Player player = getPlayerFromClientHandler(clientHandler);
            Ship ship = player.getShip();

            //trovo tutte le Tiles da rimuovere
            List<Slot> Slots = Arrays.stream(ship.getShipBoard())
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toList();

            for (Slot slot : Slots) {
                if (slot.getTile() != null && message.getRemovedTilesId().contains(slot.getTile().getId())) {
                    ship.removeTile(slot.getPosition(), true);
                }
            }


            Boolean result;
            //controllo se e' formata da tronconi separati

            Position pos = Arrays.stream(ship.getShipBoard())
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(slot -> slot.getTile() != null && slot.getTile().getMyComponent() != null)
                    .map(Slot::getPosition)
                    .toList()
                    .getFirst();


            Boolean result1 = Util.checkShipStructure(ship, pos).getKey();

            //se non lo e' allora controllo la ship
            if (result1) {
                result = ship.checkShip();
            } else result = false;

            ShipUpdate shipUpdate = new ShipUpdate(ship, player.getNickName());
            CheckShipStatusResponse response = new CheckShipStatusResponse(ship, result, message.getID());
            clientHandler.sendMessage(shipUpdate);
            clientHandler.sendMessage(response);

            //Broadcast a tutti ShipUpdate di quel giocatore.
            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
            broadCast(playerHandlers,shipUpdate);

            if (result) {
                synchronized (myGame.checkShipLock) {
                    myGame.getGameController().addCompletedShip();
                    if (myGame.getRealGame().getNumPlayers() == myGame.getGameController().getnCompletedShips() && !myGame.getGameController().getGameState().equals(GameState.CREW_INIT)) {
                        myGame.getGameController().nextState();


                        //aggiungo gia ora la crew per la cabina centrale

                        Tile centralTile = ship.getTileFromPosition(new Position(3,2));
                        CentralHousingUnit  centralHousingUnit = (CentralHousingUnit) centralTile.getMyComponent();
                        centralHousingUnit.setHumanCrewNumber(2);

                        ShipUpdate shipUpdate2 = new ShipUpdate(ship, player.getNickName());


                        PhaseUpdate phaseUpdate = new PhaseUpdate(GameState.CREW_INIT);
                        broadCast(playerHandlers, shipUpdate2);
                        broadCast(playerHandlers, phaseUpdate);
                    }
                }
            }

        });
    }

    /**
     * Handles the response to an "ask position" request by executing the corresponding task in a separate thread.
     * It invokes the {@code handleFinishBuildingRequest2} method, which processes the {@link AskPositionResponse}.
     * Any {@link RemoteException} encountered during the execution is encapsulated and rethrown as a runtime exception.
     *
     * @param askPositionResponse the {@link AskPositionResponse} message containing the position data to handle
     * @param clientHandler the {@link ClientHandler} associated with the response, representing the client who sent it
     * @throws RemoteException if a communication-related exception occurs during the handling of the response
     */
    public void handleAskPositionResponse(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            try {
                handleFinishBuildingRequest2(askPositionResponse, clientHandler);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Handles the SelectPlanetResponse message received from a client.
     * Executes the appropriate game logic within the lobby and triggers the next phase after processing the message.
     *
     * @param selectPlanetResponse the message containing the planet selection details from the client
     * @param clientHandler the client handler associated with the sender of the message
     * @throws RemoteException if a communication-related error occurs during the remote method call
     */
    public void handleSelectPlanetResponse(SelectPlanetResponse selectPlanetResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(selectPlanetResponse);
            tryExecutePhaseAfterMessage(game, NetworkMessageType.SelectPlanetResponse);
        });
    }
    /**
     * Handles the AskTrunkResponse by forwarding it to the corresponding game's controller
     * and attempting to execute the next phase based on the message type.
     *
     * @param askTrunkResponse the AskTrunkResponse message received from the client
     * @param clientHandler the handler associated with the client sending the message
     */
    public void handleAskTrunkResponse(AskTrunkResponse askTrunkResponse, ClientHandler clientHandler) {
        LobbyManager game = getLobbyFromHandler(clientHandler);
        game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(askTrunkResponse);
        tryExecutePhaseAfterMessage(game, NetworkMessageType.AskTrunkResponse);
    }

    /**
     * Handles the request to finalize the building action for a player's ship.
     * This method processes the completion of the building phase for the player and determines possible valid positions
     * for their ship on the flight board. It then sends a response to the client with the valid options for positioning
     * the ship. Upon receiving the chosen position from the client, the player's ship will be placed in the respective position.
     *
     * @param finishBuildingRequest The request containing details about completing the ship building process for*/
    public void handleFinishBuildingRequest(FinishBuildingRequest finishBuildingRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            AskPositionUpdate askPositionUpdate;
            ArrayList<Integer> validPos = new ArrayList<>();

            // [1] fisso l'ultima tile di Player




            synchronized (myGame.positionLock) {
                int maxPos = myGame.getRealGame().getNumPlayers();
                int minPos = 1;
                ArrayList<Integer> takenPos = myGame.getRealGame().getFlightBoard().getOccupiedPositions();


                for (int i = 1; i <= maxPos; i++) {

                    int realPos = 0;

                    switch (i) {
                        case 1 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFirstPos();
                        case 2 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getSecondPos();
                        case 3 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getThirdPos();
                        case 4 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFourthPos();
                    }

                    if (!takenPos.contains(realPos)) {
                        validPos.add(i);
                    }
                }

                //in occupied positions ho i valori interi che vanno tradotti nelle posizioni
//            for (int pos: takenPos){
//
//            }

                askPositionUpdate = new AskPositionUpdate(validPos);
                askPositionUpdate.nickname = nickname;
                CompletableFuture<NetworkMessage> future = new CompletableFuture<>();

                //aggiungo alle pending responses

                myGame.addPendingResponse(future, askPositionUpdate.getID());
                //System.out.println(askPositionUpdate.getID());

                clientHandler.sendMessage(askPositionUpdate);
                //ho la mia posizione scelta, e lo posiziono
            }
        });
    }

    /**
     * Handles the request to finish building in the game, validates the position chosen by the player,
     * updates game state, and communicates state updates to relevant clients.
     *
     * @param askPositionResponse The response containing the position selected by the player.
     * @param clientHandler       The client handler associated with the player making the request.
     * @throws RemoteException If there is an issue during remote method execution.
     */
    public void handleFinishBuildingRequest2(AskPositionResponse askPositionResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            Boolean flag = false;
            Boolean validChoice = true;

            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);

            Color playerColor = myGame.getPlayerColors().get(nickname);

            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());

            int realPos = 0;
            synchronized (myGame.positionLock) {

                ArrayList<Integer> takenPos = myGame.getRealGame().getFlightBoard().getOccupiedPositions();

                switch (askPositionResponse.getPosition()) {
                    case 1 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFirstPos();
                    case 2 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getSecondPos();
                    case 3 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getThirdPos();
                    case 4 -> realPos = myGame.getRealGame().getFlightBoard().getFlightBoardMap().getFourthPos();
                }


                if (takenPos.contains(realPos)) {
                    validChoice = false;
                } else {
                    myGame.getRealGame().getFlightBoard().positionPlayer(playerColor, realPos, getPlayerFromClientHandler(clientHandler));
                    myGame.getRealGame().getPlayer(nickname).setPlacement(askPositionResponse.getPosition());
                    //Qui ha davvvero finito la fase di costruzione dicendo anche la posizione di partenza
                    myGame.addPlayerShipFinished(nickname);
                }


            }

            if (!validChoice){
                try {
                    handleFinishBuildingRequest(null, clientHandler);
                    return;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }


            broadCast(playerHandlers, new FlightBoardUpdate(myGame.getRealGame().getFlightBoard()));

//            myGame.getRealGame().getFlightBoard()


            synchronized (myGame.getRealGame().getPlayer(nickname).getShip()) {

                Ship myShip = myGame.getRealGame().getPlayer(nickname).getShip();


                //se quando arriva building request il client e' aggiornato
                Tile lastTile = myGame.getRealGame().getPlayer(nickname).getShip().getLastTile();

                if (!(lastTile == null)) {
                    int lastTileId = lastTile.getId();
//                Ship myShip = myGame.getRealGame().getPlayer(nickname).getShip();

                    //prendo tutte le tile
                    List<Slot> slots = Arrays.stream(myShip.getShipBoard())
                            .flatMap(Arrays::stream)
                            .filter(Objects::nonNull)
                            .toList();

                    //trovo la tile e la fisso
                    for (Slot slot : slots) {
                        if (slot.getTile() != null && slot.getTile().getId() == lastTileId) {
                            myShip.getShipBoard()[slot.getPosition().getX()][slot.getPosition().getY()].getTile().setFixed(true);
                            break;
                        }
                    }

                }

                broadCast(playerHandlers, new ShipUpdate(myShip, nickname));
            }

            System.out.println("n gioctori che hanno finito: "+myGame.getPlayerShipFinishedSize());
            //controllo se tutti hanno finito
            if (myGame.getPlayerShipFinishedSize() == myGame.getRealGame().getNumPlayers()) {

                for (ClientHandler handler : playerHandlers){
                    Player player = getPlayerFromClientHandler(handler);
                    Ship ship = player.getShip();
                    ship.setInitialTiles(ship.remainingTiles());

                }
                //TODO MATTIA FORSE MODIFICARE QUI PER scadenza timer, ma loro nn dovrebbe risultare che hanno già tutti finito
                myGame.getGameController().nextState();
                System.out.println("Fase successiva: "+myGame.getGameController().getGameState().toString());
                if (myGame.getGameController().getGameState().equals(GameState.BUILDING_END))
                //se hanno finito tutti allora si passa alla fase di check_ship
                {
                    myGame.getGameController().nextState();
                    //System.out.println("STATE: " + myGame.getGameController());
                    broadCast(playerHandlers, new PhaseUpdate(GameState.SHIP_CHECK));
                }else if(myGame.getGameController().getGameState().equals(GameState.SHIP_CHECK)){
                    broadCast(playerHandlers, new PhaseUpdate(GameState.SHIP_CHECK));
                }

            }
        });
    }

    /**
     * Handles the request to place a tile on the game board. Validates whether the action can be performed
     * based on the game state*/
    public void handlePlaceTileRequest(PlaceTileRequest placeTileRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            PlaceTileResponse placeTileResponse = new PlaceTileResponse(null, placeTileRequest.getID());
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            Player myPlayer = myGame.getRealGame().getPlayer(nickname);
            Ship myShip = myPlayer.getShip();


            //controllo se posso eseguirla

            if (!isActionAllowed(myGame, GameAction.PLACE_TILE)) {
                placeTileResponse.setMessage("INVALID_STATE");
                clientHandler.sendMessage(placeTileResponse);
                return;
            }

            //dopo che ho tutto devo semplicemente inserire la Tile

            synchronized (myShip) {
                Tile myTile = placeTileRequest.getTile();
                if(placeTileRequest.isToReserved()){
                    int index = placeTileRequest.getReservedSlotIndex();

                    myShip.getAsideTiles()[index] = myTile;
                    placeTileResponse.setMessage("VALID");
                }
                else{
                    Position myPos = placeTileRequest.getPos();

                    if (myShip.getInvalidPositions().contains(myPos)) {
                        placeTileResponse.setMessage("INVALID_POS");
                        clientHandler.sendMessage(placeTileResponse);
                        return;
                    }

                    if (myShip.getTileFromPosition(myPos) != null) {
                        placeTileResponse.setMessage("OCCUPIED_POS");
                        clientHandler.sendMessage(placeTileResponse);
                        return;
                    }

                    myShip.putTile(myTile, myPos);
                    myShip.setLastTile(myTile);
                    myShip.setLastTilePosition(myPos);
                    placeTileResponse.setMessage("VALID");

                }

                    //broadCasto la nuova nave a tutti
                    ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
                    ShipUpdate shipUpdate = new ShipUpdate(myShip, myPlayer.getNickName());
                    broadCast(playerHandlers, shipUpdate);
                    clientHandler.sendMessage(placeTileResponse);

            }

        });
    }

    /**
     * Handles a request to discard a tile during the game. The method processes the
     * request by discarding the specified tile, updating the game state, and broadcasting
     * the relevant updates to all players in the game.
     *
     * @param discardTileRequest the request containing the tile to be discarded
     * @param clientHandler the handler for the client making the discard request
     * @throws RemoteException if a communication-related exception occurs during the operation
     */

    public void handleDiscardTileRequest(DiscardTileRequest discardTileRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {

            LobbyManager myGame = getLobbyFromHandler(clientHandler);

            //myGame.getTileBunch().getFaceUpTiles();

            String nickname = getNicknameFromClientHandler(clientHandler);
            Player myPlayer = myGame.getRealGame().getPlayer(nickname);
            myGame.getTileBunch().returnTile(discardTileRequest.getTile());

            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
            broadCast(playerHandlers, new TileDiscardedUpdate(discardTileRequest.getTile()));


            FaceUpTileUpdate faceUpTileUpdate = new FaceUpTileUpdate();
            faceUpTileUpdate.setFaceUpTiles(myGame.getTileBunch().getFaceUpTiles());
            broadCast(playerHandlers, faceUpTileUpdate);


        });
    }


    /**
     * Handles the initialization and update of the crew positions during the game setup phase. This method updates
     * the positions of crew members on the ship, applies changes to housing units based on crew color,
     * and progresses the game state when all players have completed their setup.
     *
     * @param crewInitUpdate Contains the initial setup data for the crew positions, including crew member colors
     *                       and their corresponding positions on the ship.
     * @param clientHandler The client handler representing the player making the current crew setup update request.
     *
     * @throws RemoteException If a remote communication error occurs during the operation.
     */
    public void handleCrewInitUpdate(CrewInitUpdate crewInitUpdate, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            Player myPlayer = getPlayerFromClientHandler(clientHandler, myGame);
            Ship myShip = myPlayer.getShip();

            List<Position> positions = new ArrayList<>(crewInitUpdate.getCrewPos().stream().map(Pair::getKey).toList());

            List<Slot> Slots = Arrays.stream(myShip.getShipBoard())
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toList();

            for (Slot s : Slots) {

                Tile tempTile = s.getTile();
                Position tempPos = s.getPosition();

                if (positions.contains(tempPos) && s.getTile() != null) {

                    AlienColor color = crewInitUpdate.getCrewPos().stream().filter(pair -> pair.getKey().equals(tempPos)).map(Pair::getValue).findFirst().orElse(null);

                    if (color == null) return;

                    if (color.equals(AlienColor.PURPLE)) {
                        ModularHousingUnit purpleHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        purpleHousing.addPurpleAlien();
                    }

                    if (color.equals(AlienColor.BROWN)) {
                        ModularHousingUnit brownHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        brownHousing.addBrownAlien();
                    } else {
                        ModularHousingUnit humanHousing = (ModularHousingUnit) tempTile.getMyComponent();
                        humanHousing.addHumanCrew();
                    }
                }
            }

            //dopo aver implementato la lista di quelli che hanno finito e si broadcasta la fase nuova


            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(myGame.getPlayerHandlers().values());
            ShipUpdate shipUpdate = new ShipUpdate(myShip, myPlayer.getNickName());
            broadCast(playerHandlers, shipUpdate);


            myGame.addPlayerCrewFinished(myPlayer.getNickName());
            if (myGame.getPlayerCrewSize() == myGame.getRealGame().getNumPlayers()) {

                myGame.getGameController().nextState();
                PhaseUpdate phaseUpdate = new PhaseUpdate(GameState.FLIGHT);
                broadCast(playerHandlers, phaseUpdate);

                //new Thread(() -> {
                try {


                    myGame.getGameController().startFlight();
                } catch (ExecutionException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
                //}).start();
            }
        });
    }

    /**
     * Handles the response for activating an adventure card. This method is responsible for processing
     */
    public void handleActivateAdventureCardResponse(ActivateAdventureCardResponse activateAdventureCardResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            synchronized (game.getGameController()) {
                game.getGameController().notify();
            }
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(activateAdventureCardResponse);
            tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateAdventureCardResponse);
        });
    }

    /**
     * Handles the response for activating a component within a ship. This method is responsible
     * for processing the activation of various types of components (e.g., Double Engine, Double Cannon, Shield)
     * based on the provided response data and updating the client accordingly.
     * It*/
    public void handleActivateComponentResponse(ActivateComponentResponse activateComponentResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            Player player = getPlayerFromClientHandler(clientHandler);
            Ship ship = player.getShip();
            ActivatableComponent activatableComponent = activateComponentResponse.getActivatableComponentType();

            ArrayList<Position> activatedComponentPositions = activateComponentResponse.getActivatedComponentPositions();
            ArrayList<Position> batteriesPositions = activateComponentResponse.getBatteriesPositions();


            if (activatedComponentPositions == null || batteriesPositions == null ||
                    activatedComponentPositions.isEmpty() || batteriesPositions.isEmpty()) {
                tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateComponentResponse);
                return;
            }

            for (int i = 0; i < activateComponentResponse.getActivatedComponentPositions().size(); i++) {
                //TODO FIX
                Position componentPos = activatedComponentPositions.get(i);
                Position batteryPos = batteriesPositions.get(i);

                switch (activatableComponent) {
                    case DoubleEngine -> ship.activateDoubleEngine(componentPos, batteryPos);
                    case DoubleCannon -> ship.activateDoubleCannon(componentPos, batteryPos);
                    case Shield -> ship.activateShield(componentPos, batteryPos);
                }
            }

            //Mando la shipUpdate
            ShipUpdate shipUpdate = new ShipUpdate(ship, player.getNickName());
            ArrayList<ClientHandler> playerHandlers = new ArrayList<>(game.getPlayerHandlers().values());

            broadCast(playerHandlers, shipUpdate);

            tryExecutePhaseAfterMessage(game, NetworkMessageType.ActivateComponentResponse);
        });
    }

    /**
     **/
    public void handleHeartbeatRequest(HeartbeatRequest ignoredHeartbeatRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            synchronized (heartbeats) {
                heartbeats.stream()
                        .filter(h -> h.getClientHandler().getClientID().equals(clientHandler.getClientID()))
                        .findFirst()
                        .ifPresent(h -> {
                            heartbeats.remove(h);
                            h.regenerate();
                        });
            }
        });

    }

    /**
     * Handles the ship update request sent by a client. This includes processing changes to the ship state
     * such as fixing tiles or handling the ship update during the flight phase depending on the current
     * game state.
     *
     * @param shipUpdate The {@link ShipUpdate} object containing the details of the ship update sent by the client.
     * @param clientHandler The {@link ClientHandler} object representing the client that sent the ship update request.
     * @throws RemoteException If a remote communication error occurs during the execution of the update.
     */
    public void handleShipUpdate(ShipUpdate shipUpdate, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);




            if (shipUpdate.getOnlyFix()) {
                String nickname = getNicknameFromClientHandler(clientHandler);
                Player myPlayer = game.getRealGame().getPlayer(nickname);
                Ship myShip = myPlayer.getShip();

                if(myShip.getLastTile() != null) {
                    synchronized (myShip) {
                        myShip.getLastTile().setFixed(true);
                    }
                }

                ShipUpdate update = new ShipUpdate(myShip, myPlayer.getNickName());
                ArrayList<ClientHandler> playerHandlers = new ArrayList<>(game.getPlayerHandlers().values());

                broadCast(playerHandlers, update);
            }


            if (game.getGameController().getGameState() == GameState.FLIGHT) {
                if(game.getGameController().getCurrentCardContext() != null) {

                    //Caricamento merci
                    String nickname = getNicknameFromClientHandler(clientHandler);
                    Player myPlayer = game.getRealGame().getPlayer(nickname);
                    myPlayer.replaceShip(shipUpdate.getShipView());

                    game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(shipUpdate);
                    tryExecutePhaseAfterMessage(game, shipUpdate.accept(networkMessageNameVisitor));
                }
            }
        });
    }

    /**
     * Handles the response related to discarding crew members. This method processes the incoming
     * DiscardCrewMembersResponse, updates the game state, and executes the appropriate phase based
     * on the received response.
     *
     * @param discardCrewMembersResponse the response object containing details of the discarded crew members
     **/
    public void handleDiscardCrewMembersResponse(DiscardCrewMembersResponse discardCrewMembersResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(discardCrewMembersResponse);
            tryExecutePhaseAfterMessage(game, discardCrewMembersResponse.accept(networkMessageNameVisitor));
        });
    }

    /**
     * Handles the response received for collecting rewards, updating the game state accordingly,
     * and attempting to execute the next game phase if applicable.
     *
     * @param collectRewardsResponse the response object containing*/
    public void handleCollectRewardsResponse(CollectRewardsResponse collectRewardsResponse, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager game = getLobbyFromHandler(clientHandler);
            game.getGameController().getCurrentCardContext().setIncomingNetworkMessage(collectRewardsResponse);
            tryExecutePhaseAfterMessage(game, collectRewardsResponse.accept(networkMessageNameVisitor));
        });
    }

    /**
     * Handles the request to draw an adventure card.
     *
     * @param drawAdventureCardRequest the request object containing the details of the draw adventure card action
     * @param clientHandler the client handler responsible for managing communication with the client
     * @throws RemoteException*/
    public void handleDrawAdventureCardRequest(DrawAdventureCardRequest drawAdventureCardRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            GameController gameController = myGame.getGameController();

            if (gameController.getCurrentCardContext() != null) {
                clientHandler.sendMessage(new GameMessage("È già in corso una carta avventura."));
                return;
            }

            gameController.handleTurn();
        });
    }

    /**
     * Handles the ready turn request sent by a client. This method processes the request
     * asynchronously, updates the player's readiness status in the lobby, and notifies the game
     * controller if all active players are ready.
     *
     * @param readyTurnRequest the request containing the player's ready status information
     * @param clientHandler the handler associated with the client making the request
     * @throws RemoteException if a remote communication error occurs during execution
     */
    public void handleReadyTurnRequest(ReadyTurnRequest readyTurnRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            GameController gameController = myGame.getGameController();
            try {

                new Thread(() -> {
                    myGame.addReadyPlayer(nickname);
                    if (myGame.allActivePlayerReady()) {
                        gameController.sendMatchInfoUpdate();
                    }
                }).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Handles a request for early landing from a client. This method initiates the process
     * of removing the player from the game, updating the game state, and managing the player's
     * early landing status.
     *
     * @param earlyLandingRequest the request object containing information about the early landing
     *                            request made by the player.
     * @param clientHandler       the client handler associated with the requesting player.
     * @throws RemoteException    if a remote communication issue occurs while handling the request.
     */
    public void handleEarlyLandingRequest(EarlyLandingRequest earlyLandingRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
            String nickname = getNicknameFromClientHandler(clientHandler);
            GameController gameController = myGame.getGameController();

            myGame.getGameController().removePlayerFromGame(nickname, PlayerLostReason.Quit);
            new Thread(() -> {
//                myGame.getGameController().handleTurnBeforeDrawnCard();
                myGame.addEarlyLandingPlayer(nickname);
                if (myGame.allActivePlayerReady()) {
                    gameController.sendMatchInfoUpdate();
                }
            }).start();
        });
    }

    /**
     * Handles the request for timer information and sends the details back to the client.
     *
     * @param askTimerInfoRequest the request object containing information regarding the timer info request
     * @param clientHandler the client handler responsible for managing communication with the client
     * @throws RemoteException if a communication-related exception occurs during execution
     */
    public void handleAskTimerInfoRequest(AskTimerInfoRequest askTimerInfoRequest, ClientHandler clientHandler) throws RemoteException {
        this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);
//        myGame.completePendingResponse(askTimerInfoRequest.getID());
            TimerInfoResponse timerInfoResponse = new TimerInfoResponse(askTimerInfoRequest.getID(), myGame.getRealGame().getTimerInfos());
            //predo i timerInfo

            ArrayList<TimerInfo> timerInfos = myGame.getRealGame().getTimerInfos();

            clientHandler.sendMessage(timerInfoResponse);
        });
    }

    /**
     * Handles the flip timer request by updating the respective timer and initiating the next timer sequence.
     *
     * @param flipTimerRequest the request containing the identifier of the timer to be flipped
     * @param clientHandler the handler for the client making the request
     * @throws RemoteException if a remote communication error occurs
     */
    public void handleFlipTimerRequest(FlipTimerRequest flipTimerRequest, ClientHandler clientHandler) throws RemoteException {
       this.execute(() -> {
            LobbyManager myGame = getLobbyFromHandler(clientHandler);

            synchronized (myGame.timerLock) {
                ArrayList<TimerInfo> timerInfos = myGame.getRealGame().getTimerInfos();
                TimerInfo timer = timerInfos.stream().filter(t -> !t.isFlipped()).findFirst().orElse(null);

                if (timer == null) return;

//        timer.setTimerStatus(TimerStatus.STARTED);
                timer.setFlipped(true);

                boolean lastTimer = (timer.getIndex() == myGame.getRealGame().getTimerInfos().size() - 1);

                startTimer(10, myGame.getGameController(), new ArrayList<>(myGame.getPlayerHandlers().values()), lastTimer, timer.getIndex());
            }
        });
    }

    /*
     *
     * UTILS
     * */

    /**
     * Starts a timer that runs for the specified duration in seconds. When the timer starts, a message is broadcasted to clients.
     * During the timer operation, the time elapsed is updated on a shared TimerInfo object. When the timer ends, it may trigger
     * a change in the game state depending on the game controller and provided parameters.
     *
     * @param seconds        The duration of the timer in seconds.
     * @param gameController The GameController managing the state of the game.
     * @param clients        The list of ClientHandler objects representing the connected clients that need
     *                       to be notified of timer events.
     * @param last           A boolean value indicating if this is the last timer, which may trigger a state change
     *                       when the timer ends.
     * @param index          The index of the timer, used to associate the timer with a specific TimerInfo object.
     */
    public void startTimer(int seconds, GameController gameController, ArrayList<ClientHandler> clients, boolean last, int index) {
        //mando a tutti la notifica di end_timer\

        GameMessage gameMessage = new GameMessage("Timer n. " + (index + 1) + " started");
        broadCast(clients, gameMessage);

        new Thread(()->{
            LobbyManager game = getLobbyFromHandler(clients.getFirst());
            TimerInfo timerinfo = game.getRealGame().getTimerInfos().stream().filter(t -> t.getIndex() == index).findFirst().orElse(null);
            if(timerinfo == null)
            {
                System.err.println("TimerInfo null");
                return;
            }

            timerinfo.setTimerStatus(TimerStatus.STARTED);

            int secondsIn = seconds;
            timerinfo.setValue(seconds);

            while (secondsIn > 0){
                try {
                    TimerInfoResponse timerInfoResponse = new TimerInfoResponse(game.getRealGame().getTimerInfos());
                    broadCast(game.getTimerSubscribers(), timerInfoResponse);

                    Thread.sleep(1000);
                    secondsIn -= 1;
                    timerinfo.setValue(secondsIn);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            timerinfo.setTimerStatus(TimerStatus.ENDED);
            TimerInfoResponse timerInfoResponse = new TimerInfoResponse(game.getRealGame().getTimerInfos());
            timerInfoResponse.setLast(last);
            broadCast(clients, timerInfoResponse);

//            if (last){
//
//                if ((gameController.getGameState().isBefore( GameState.BUILDING_END))) {
//
//                    gameController.nextState();
//                    PhaseUpdate update = new PhaseUpdate(GameState.BUILDING_END);
//                    broadCast(clients, update);
//                }
//
//            }
        }).start();




        // Timer scaduto → cambio stato
//        scheduler.schedule(() -> {
//            gameController.nextState();
//            PhaseUpdate update = new PhaseUpdate(GameState.BUILDING_END);
//            broadCast(clients, update);
//
//        }, seconds, TimeUnit.SECONDS);
    }

    /**
     * Handles the execution of a game phase after processing a network message of the specified type.
     * The method checks the current game's card context, processes the incoming network message,
     * and determines whether the phase execution should proceed based on the specified conditions.
     *
     * @param game the current instance of LobbyManager, which manages the game and its associated context.
     * @param type the type of network message to process, represented as a NetworkMessageType.
     */
    private void tryExecutePhaseAfterMessage(LobbyManager game, NetworkMessageType type) {
        CardContext cardContext =game.getGameController().getCurrentCardContext();

        cardContext.decrementExpectedNumberOfNetworkMessages(type);
        int expectedNetworkMessages = cardContext.getExpectedNumberOfNetworkMessagesPerType().get(type);
        NetworkMessage networkMessage = cardContext.getIncomingNetworkMessage();
        NetworkMessageNameVisitor visitor = new NetworkMessageNameVisitor();


        if(Objects.equals(cardContext.getAdventureCard().getName(), "Pianeti")){
            if(networkMessage.accept(visitor).equals(NetworkMessageType.ShipUpdate))
            {
                game.getGameController().getCurrentCardContext().executePhase();
                return;
            }
        }

        if (expectedNetworkMessages == 0) {
            game.getGameController().getCurrentCardContext().executePhase();
        } else if (expectedNetworkMessages == -1) {
            game.getGameController().getCurrentCardContext().incrementExpectedNumberOfNetworkMessages(type);
        }
    }

    /**
     * Broadcasts a network message to a list of client handlers.
     *
     * @param clients the list of client handlers to send the message to
     * @param message the network message to be broadcasted
     */
    public void broadCast(ArrayList<ClientHandler> clients, NetworkMessage message) {
        NetworkMessageType networkMessageType = message.accept(networkMessageNameVisitor);

//        System.out.println("[DEBUG] Broadcasting"+ message );
        for (ClientHandler clientHandler : clients) {
//            String nickname = getNicknameFromClientHandler(clientHandler);

            clientHandler.sendMessage(message);
        }
    }

    /**
     * A constant map that defines the allowed actions for each game state.
     * The map uses {@link GameState} as its keys and associates them with a set of
     * {@link GameAction} values, representing the permissible actions for each state.
     * This is implemented using an {@link EnumMap} for efficient storage and retrieval.
     */
    private static final Map<GameState, Set<GameAction>> allowedActionsPerState = new EnumMap<>(GameState.class);

    /**
     * Initializes the mapping of allowed actions for each game state.
     *
     * This method defines which actions are permissible for specific game states during the game lifecycle.
     * The mapping is stored in the allowedActionsPerState, associating each GameState with an EnumSet of
     * GameActions that are valid in that state.
     *
     * Modifications to this mapping may be necessary to accommodate additional game states or
     * actions as the game evolves.
     */
    private void initActionsAllowed() {
        allowedActionsPerState.put(GameState.BUILDING_START, EnumSet.of(GameAction.DRAW_TILE, GameAction.PLACE_TILE, GameAction.DISCARD_TILE));
        allowedActionsPerState.put(GameState.BUILDING_TIMER, EnumSet.of(GameAction.DRAW_TILE, GameAction.PLACE_TILE, GameAction.DISCARD_TILE, GameAction.FINISH_BUILDING));
//        allowedActionsPerState.put(GameState.SHIP_CHECK, EnumSet.of(GameAction.FETCH_SHIP));
        // altri stati se necessario
    }

    /**
     * Determines if a specific game action is allowed in the current state of the game.
     *
     * @param myGame the game manager that provides access to the current game's state and controller
     * @param action the game action to check for allowance
     * @return true if the action is allowed in the current state, false otherwise
     */
    private boolean isActionAllowed(LobbyManager myGame, GameAction action) {
        GameState currentState = myGame.getGameController().getGameState();
        Set<GameAction> allowedActions = allowedActionsPerState.getOrDefault(currentState, Collections.emptySet());
        return allowedActions.contains(action);
    }

    /**
     * Retrieves the nickname associated with a given ClientHandler.
     *
     * @param clientHandler the ClientHandler whose associated nickname is to be retrieved.
     * @return the nickname of the client associated with the provided ClientHandler,
     *         or null if no nickname is found for the client.
     */
    public String getNicknameFromClientHandler(ClientHandler clientHandler) {
        return clientNicknameMap.get(clientHandler.getClientID());
    }

    /**
     * Retrieves the Player object associated with the provided ClientHandler.
     *
     * @param clientHandler the ClientHandler instance from which the player is to be retrieved
     * @return the Player object associated with the given ClientHandler
     */
    private Player getPlayerFromClientHandler(ClientHandler clientHandler) {
        LobbyManager myGame = getLobbyFromHandler(clientHandler);
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }

    /**
     * Retrieves a Player instance associated with the given ClientHandler.
     *
     * @param clientHandler the client handler representing the connection with the client
     * @param myGame the lobby manager containing the current game and player details
     * @return the Player instance corresponding to the client handler
     */
    private Player getPlayerFromClientHandler(ClientHandler clientHandler, LobbyManager myGame) {
        return myGame.getRealGame().getPlayer(getNicknameFromClientHandler(clientHandler));
    }

    /**
     * Starts a new heartbeat for the specified client handler.
     * This method initializes a new Heartbeat instance, adds it to the list of active heartbeats,
     * and starts the heartbeat process.
     *
     * @param clientHandler the client handler associated with the new heartbeat
     */
    public void startNewHeartbeat(ClientHandler clientHandler) {
        Heartbeat heartbeat = new Heartbeat(this, clientHandler);
        heartbeats.add(heartbeat);
        heartbeat.start();
    }

    /**
     * Mainly used for testing
     * @return newly created roomId.
     */
    public int getLastCreatedGameId() {
        return nextLobbyIndex - 1;
    }


    public ArrayList<Heartbeat> getHeartbeats() {
        return heartbeats;
    }
}



