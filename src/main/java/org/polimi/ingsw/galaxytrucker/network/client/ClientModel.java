package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientModel {

    public ClientModel() {
        this.myInfo = new PlayerInfo();
        this.playerInfos = new ArrayList<>();
    }
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> playerInfos;
    private FlightBoard flightBoard;
    private ArrayList<CardDeck> cardDecks = new ArrayList<>();
    private ArrayList<Tile>  faceUpTiles = new ArrayList<>();
    private Tile[] reservedTiles = new Tile[2];
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

    public ArrayList<Tile> getFaceUpTiles() {
        return faceUpTiles;
    }
    public void setFaceUpTiles(ArrayList<Tile> faceUpTiles) {

        this.faceUpTiles =  faceUpTiles;
    }
    public Tile[] getReservedTiles() {
        return reservedTiles;
    }

    public void setReservedTile(int index, Tile tile) {
        if (index >= 0 && index < 2) {
            reservedTiles[index] = tile;
        } else {
            throw new IllegalArgumentException("Reserved tile index must be 0 or 1.");
        }
    }


}
