package org.polimi.ingsw.galaxytrucker.network.client;

import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.PlayerState;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Planet;
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


    private ArrayList<Integer> tilesToRemove = new ArrayList<>();
    private PlayerInfo myInfo;
    private ArrayList<PlayerInfo> playerInfos;
    private FlightBoard flightBoard;
    private ArrayList<CardDeck> cardDecks = new ArrayList<>();
    private ArrayList<Tile>  faceUpTiles = new ArrayList<>();
    private Tile[] reservedTiles = new Tile[2];
    private boolean isLeader;
    private Planet selectedPlanet;
    private PlayerState playerState;

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public ArrayList<Integer> getTilesToRemove() {
        return tilesToRemove;
    }

    public void setTilesToRemove(ArrayList<Integer> tilesToRemove) {
        this.tilesToRemove = tilesToRemove;
    }

    public void addTileToRemove(Integer tileID) {
        this.tilesToRemove.add(tileID);
    }


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

    public PlayerInfo getPlayerInfoByNickname(String nickname) {
        if (myInfo != null && myInfo.getNickName().equals(nickname)) {
            return myInfo;
        }

        synchronized (playerInfos) {
            return playerInfos.stream()
                    .filter(info -> info.getNickName().equals(nickname))
                    .findFirst()
                    .orElse(null);
        }
    }


    public void setLeader(boolean isLeader ) {
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public boolean hasPlayerWithNickname(String nickname) {
        return getPlayerInfoByNickname(nickname) != null;
    }

    public Planet getSelectedPlanet() {
        return selectedPlanet;
    }
    public void setSelectedPlanet(Planet selectedPlanet) {
        this.selectedPlanet = selectedPlanet;
    }
}
