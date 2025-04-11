package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.exceptions.PlayerAlreadyExistsException;
import org.polimi.ingsw.galaxytrucker.exceptions.TooManyPlayersException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.network.common.GameNetworkModel;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CreateRoomRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.JoinRoomRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.JoiniRoomOptionsRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.NicknameRequest;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomOptionsResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.JoinRoomResponse;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.NICKNAME_RESPONSE;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import org.polimi.ingsw.galaxytrucker.network.server.MessageManager;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterLabels;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.PrinterUtils;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.TuiColor;

import java.util.ArrayList;

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
        NICKNAME_RESPONSE nicknameResponse = new NICKNAME_RESPONSE(null);


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

        JoinRoomOptionsResponse joinRoomOptionsResponse = new JoinRoomOptionsResponse(lobbyInfos);
        clientHandler.sendMessage(joinRoomOptionsResponse);
    }

    public void handleJoinRoomRequest(JoinRoomRequest message, ClientHandler clientHandler) throws TooManyPlayersException, PlayerAlreadyExistsException {

        String mess = "";

        JoinRoomResponse joinRoomResponse = new JoinRoomResponse(null, null);
        ArrayList<ClientHandler> playersHandlers = new ArrayList<>();
        Boolean result = false;



        GameNetworkModel myGame = GameModels.get(message.getRoomId());
            synchronized (myGame){
                if (myGame.getPlayerColors().size() == myGame.getRealGame().getMaxPlayers()){
                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.RED, "LOBBY NUMBER " + message.getRoomId() + "IS FULL");
                    joinRoomResponse.setErrMess(mess);
                    joinRoomResponse.setOperationSuccess(false);
                } else {

                    Player myPlayer = new Player(message.getNickName(),0, 0, myGame.getRealGame().getIsLearningMatch());
                    mess = PrinterUtils.getTextWithLabel(PrinterLabels.LobbyInfo, TuiColor.GREEN, "CONNECTED TO LOBBY " + message.getRoomId());
                     playersHandlers = (ArrayList<ClientHandler>) myGame.getPlayerHandlers().values();

                    myGame.getPlayerColors().putIfAbsent(message.getNickName(), myGame.useNextAvailableColor());
                    myGame.getRealGame().addPlayer(myPlayer);
                    myGame.addPlayerHandler(clientHandler, myPlayer.getNickName());


                    LobbyInfo myLobbyInfo = lobbyInfos.stream().filter(l -> l.getLobbyID() == message.getRoomId()).findFirst().orElse(null);
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
            }

            clientHandler.sendMessage(joinRoomResponse);
            //others
//        if (result) {
//            for (ClientHandler c: playersHandlers) {
//                c.sendMessage(PlayerConnected);
//            }
//        }


    }

    public MessageManager getMessageManager() {
        return messageManager;
    }




}
