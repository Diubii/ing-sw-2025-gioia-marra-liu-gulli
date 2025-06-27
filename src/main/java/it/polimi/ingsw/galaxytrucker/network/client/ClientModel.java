package it.polimi.ingsw.galaxytrucker.network.client;

import it.polimi.ingsw.galaxytrucker.enums.PlayerState;
import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;

import java.util.ArrayList;

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
    private ArrayList<Tile> faceUpTiles = new ArrayList<>();
    private boolean isLeader;
    private Planet selectedPlanet;
    private PlayerState playerState;
    private boolean isLearningMatch;
    private ArrayList<Good> unplacedGoods = new ArrayList<>();
    private AdventureCard currentAdventureCard;
    private final ArrayList<TimerInfo> timerInfos = new ArrayList<>();

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

        this.faceUpTiles = faceUpTiles;
    }

    //Todo: eliminare si usano quelle in myship che sono aggiornate lato server
    public Tile[] getReservedTiles() {
        if (myInfo != null && myInfo.getShip() != null) {
            return myInfo.getShip().getAsideTiles();
        } else {
            return new Tile[2];
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


    public void setLeader(boolean isLeader) {
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

    public boolean isLearningMatch() {
        return isLearningMatch;
    }

    public void setLearningMatch(boolean learningMatch) {
        isLearningMatch = learningMatch;
    }

    public AdventureCard getCurrentAdventureCard() {
        return currentAdventureCard;
    }

    public void setCurrentAdventureCard(AdventureCard currentAdventureCard) {
        this.currentAdventureCard = currentAdventureCard;
    }

    public ArrayList<Good> getUnplacedGoods() {
        return unplacedGoods;
    }

    public void setUnplacedGoods(ArrayList<Good> unplacedGoods) {
        this.unplacedGoods = unplacedGoods;
    }

    public ArrayList<TimerInfo> getTimerInfos() {
        return timerInfos;
    }
}
