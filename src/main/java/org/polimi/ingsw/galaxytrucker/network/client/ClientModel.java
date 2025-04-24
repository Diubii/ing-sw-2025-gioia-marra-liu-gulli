package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;

import java.util.ArrayList;

public class ClientModel {

    public ClientModel() {
        this.myInfo = new PlayerInfo();
        this.playerInfos = new ArrayList<>();
    }
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> playerInfos;
    private FlightBoard flightBoard;
    private ArrayList<CardDeck> cardDecks = new ArrayList<>();

    public ArrayList<CardDeck> getCardDecks() {
        return cardDecks;
    }

    public void setCardDecks(ArrayList<CardDeck> cardDecks) {
        this.cardDecks = cardDecks;
    }

    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    public void setFlightBoard(FlightBoard flightBoard) {
        this.flightBoard = flightBoard;
    }

    public void setMyInfo(PlayerInfo myInfo) {
        this.myInfo = myInfo;
    }
    public PlayerInfo getMyInfo() {
        return myInfo;
    }

    public void setPlayerInfos(ArrayList<PlayerInfo> playerInfos) {
        this.playerInfos = playerInfos;
    }
    public ArrayList<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

}
