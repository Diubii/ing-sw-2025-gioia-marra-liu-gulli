package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameStartedUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PlayerJoinedUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipViewUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerController {

    final ArrayList<GameNetworkModel> GameModels;
    private final MessageManager messageManager;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<String> usedNicknames = new ArrayList<>();
    private final ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();

    public ServerController( ArrayList<GameNetworkModel> model) {
        this.GameModels = model;
        messageManager = new MessageManager(this.GameModels, this);



//        model.setRealGame(new Game(4, false));
    }

    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public ArrayList<ClientHandler> getClients() {
        synchronized (clients) {
            return new ArrayList<>(clients);
        }
    }


    public  void handleNicknameRequest(NicknameRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {
        Boolean result = false;
        boolean flag = false;

        //get nickname & check
        String tempNick = message.getNickname();
        NicknameResponse nicknameResponse = new NicknameResponse(null);


        synchronized (usedNicknames) {

            if (!usedNicknames.contains(tempNick)) {
                usedNicknames.add(tempNick);
                nicknameResponse.setResponse("VALID");
            } else {System.out.println("[+] NOT ADDED " + message.getNickname());
                nicknameResponse.setResponse("INVALID");
            }

        }

        clientHandler.sendMessage(nicknameResponse);
        System.out.println("SENDING RESPONSE\n");

    }


    public void handleCreateRoomRequest(CreateRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {

            GameNetworkModel newGame = new GameNetworkModel();
            Player myPlayer = new Player(message.getNickName(), 0, 0, message.getIsLearningMatch());

            newGame.getPlayerColors().putIfAbsent(message.getNickName(), newGame.useNextAvailableColor());
            newGame.getRealGame().setLearningMatch(message.getIsLearningMatch());
            newGame.getRealGame().setnMaxPlayer(message.getMaxPlayers());
            newGame.getRealGame().addPlayer(myPlayer);
            newGame.addPlayerHandler(clientHandler, myPlayer.getNickName());

            GameModels.add(newGame);
            int index = GameModels.indexOf(newGame);
            lobbyInfos.add(new LobbyInfo(message.getNickName(), message.getMaxPlayers(), 1, index));

    }

    public void handleJoinRoomOptionsRequest(JoiniRoomOptionsRequest message, ClientHandler clientHandler){

        JoinRoomOptionsResponse joinRoomOptionsResponse = new JoinRoomOptionsResponse(null);
        synchronized (lobbyInfos) {
            joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos);
        }
        clientHandler.sendMessage(joinRoomOptionsResponse);
    }

    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {

        String mess = "";
        LobbyInfo myLobbyInfo;

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null, null);
        ArrayList<ClientHandler> playerHandlers = new ArrayList<>();
        boolean result = false;
        PlayerJoinedUpdate playerJoinedUpdate = null;



        GameNetworkModel myGame = GameModels.get(message.getRoomId());
            synchronized (myGame){
                if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers()){
                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NUMBER " + message.getRoomId() + "IS FULL");
                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(false);
                } else {

                    Player myPlayer = new Player(message.getNickName(),0, 0, myGame.getRealGame().getIsLearningMatch());
                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());
                    playerHandlers = (ArrayList<ClientHandler>) myGame.getPlayerHandlers().values();

                    myGame.getPlayerColors().putIfAbsent(message.getNickName(), myGame.useNextAvailableColor());

                    myGame.getRealGame().addPlayer(myPlayer);
                    myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());

                    ArrayList<Player> players = (ArrayList<Player>) myGame.getRealGame().getPlayers();
                    HashMap<String, Color> playerInfo = myGame.getPlayerColors();
                    playerJoinedUpdate = new PlayerJoinedUpdate(players, playerInfo);


                    synchronized (lobbyInfos) {
                        myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
                    }
                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(true);

                    if (myLobbyInfo != null) {
                        myLobbyInfo.addConnectedPlayer();
                        result = true;
                    } else {
                        mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NOT FOUND :) " + message.getRoomId());
                        joinRoomResponse.setOperationSuccess(false);

                    }
                }

                //fine synchronized e

                //se tutto è andato bene
                if (result) {
                    for (ClientHandler c: playerHandlers) {
                        c.sendMessage(playerJoinedUpdate);
                    }

                    //dopo aver mandato la notifica di connessione vedo se ho raggiunto il numero massimo di player per la lobby
                    //e starto il gioco automaticamente lato server

                    if (myGame.getRealGame().getMaxPlayers() == myGame.getRealGame().getPlayers().size()) {
                        for (ClientHandler c: playerHandlers) {
                            c.sendMessage(new GameStartedUpdate());
                        }

                        //dopo aver notificato tutti starto il gioco
                        myGame.getGameController().nextState();

                    }
                }
            }

            clientHandler.sendMessage(joinRoomResponse);
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GameNetworkModel getGameFromHandler(ClientHandler clientHandler){

        return GameModels.stream().filter(gameModel ->
             gameModel.getPlayerHandlers().containsValue(clientHandler)).findFirst().orElse(null);
    }

    public void handleDrawTileRequest(DrawTileRequest message, ClientHandler clientHandler){
        //il client mi chiede una Tile, e devo restituirla
        GameNetworkModel myGame = getGameFromHandler(clientHandler);
        Tile myTile = null;
        DrawTileResponse drawTileResponse;

        synchronized (myGame.getTileBunch()){

            //-1 significa cge si pesca dal mazzo, invece se e' presente un valore valiido di TileId si prende da quelle face-up
             if (message.getTileId() != -1){

                 myTile = myGame.getTileBunch().drawFaceUpTile(message.getTileId());
                 if (myTile == null){
                     drawTileResponse = new DrawTileResponse(null);
                     drawTileResponse.setErrorMessage("ALREADY TAKEN!");
                 } else {
                     drawTileResponse = new DrawTileResponse(myTile);
                     drawTileResponse.setErrorMessage("VALID");
                 }

             } else {

                 myTile = myGame.getTileBunch().drawTile();
                 if (myTile == null){
                     drawTileResponse = new DrawTileResponse(null);
                     drawTileResponse.setErrorMessage("EMPTY");
                 } else {
                     drawTileResponse = new DrawTileResponse(myTile);
                     drawTileResponse.setErrorMessage("VALID");
                 }

             }

        }
        clientHandler.sendMessage(drawTileResponse);


    }

    public void handleFetchShipRequest(FetchShipRequest message, ClientHandler clientHandler){

        GameNetworkModel myGame = getGameFromHandler(clientHandler);

        Player targetPlayer = myGame.getRealGame().getPlayer(message.getTargetNickname());
        Ship targetShip;

        synchronized (targetPlayer.getShip()){
            targetShip = targetPlayer.getShip();
            ShipViewUpdate shipViewUpdate = new ShipViewUpdate(targetShip);
            clientHandler.sendMessage(shipViewUpdate);
        }
    }
}
