package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.DrawnAdventureCardUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PlayerRemovedUpdate;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class GameController {

    private GameState gameState;
    private final LobbyManager myGame;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();

    private final AdventureCardEffects adventureCardEffects = new AdventureCardEffects();

    public int getnCompletedShips() {
        synchronized (ncsLock) {
            return nCompletedShips;
        }
    }

    public void addCompletedShip() {
        synchronized (ncsLock) {
            nCompletedShips++;
        }
    }

    public GameController(LobbyManager myGame) {
        this.myGame = myGame;
        gameState = GameState.LOBBY;
    }

    private final Object gameStateLock = new Object();

    public GameState getGameState() {
        synchronized (gameStateLock) {
            return gameState;
        }
    }

    public void nextState() {
        synchronized (gameStateLock) {
            switch (gameState) {
                case LOBBY -> gameState = GameState.BUILDING_START;
                case BUILDING_START -> gameState = GameState.BUILDING_TIMER;
                case BUILDING_TIMER -> gameState = GameState.BUILDING_END;
                case BUILDING_END -> gameState = GameState.SHIP_CHECK;
                case SHIP_CHECK -> gameState = GameState.CREW_INIT;
                case CREW_INIT -> gameState = GameState.FLIGHT;
            }
        }
    }

    public void startFlight() throws ExecutionException, InterruptedException {

        while (myGame.getRealGame().getFlightDeck().getSize() > 0) {
            handleTurn();
        }

    }

    private void handleTurn() throws ExecutionException, InterruptedException {
        //NOTIFICA A TUTTI CHI È IL LEADER (Alessandro: Serve?)

        AdventureCard adventureCard = myGame.getRealGame().getFlightDeck().pop();
        //NOTIFICHIAMO CHE CARTA È STATA PESCATA E LA MANDIAMO
        DrawnAdventureCardUpdate dacu = new DrawnAdventureCardUpdate(adventureCard);
        myGame.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(dacu)); //Mando la carta pescata ad ogni player

        //Prendo i players ordinati per placement
        ArrayList<Player> rankedPlayers = new ArrayList<>(myGame.getRealGame().getPlayers()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti
        rankedPlayers.sort(Comparator.comparingInt(Player::getPlacement));

        adventureCard.activateEffect(adventureCardEffects, rankedPlayers, myGame); //Attivo l'effetto della carta

        //Controllo se ci sono giocatori doppiati e nel caso li rimuovo
        FlightBoard flightBoard = myGame.getRealGame().getFlightBoard();
        for(Color color : flightBoard.getRankedPlayers()) {
            if(flightBoard.isPlayerLapped(color)){ //Se il giocatore è doppiato
                String lappedPlayerNickname = myGame.getNicknameFromColor(color);
                try {
                    removePlayerFromGame(lappedPlayerNickname); //Lo rimuovo
                }catch (PlayerNotFoundException e) {
                    throw new RuntimeException(e);
                }

                myGame.getPlayerHandlers().values().forEach(ch -> ch.sendMessage(new PlayerRemovedUpdate(lappedPlayerNickname))); //Mando l'aggiornamento ai client rimanenti
            }
        }
    }

    @NeedsToBeChecked("Basta questo?")
    private void removePlayerFromGame(String nickname) throws PlayerNotFoundException {
        myGame.getRealGame().removePlayer(nickname);
        myGame.removePlayerHandler(nickname);
        myGame.getRealGame().getFlightBoard().getRankedPlayers().remove(myGame.getPlayerColors().get(nickname));
    }
}
