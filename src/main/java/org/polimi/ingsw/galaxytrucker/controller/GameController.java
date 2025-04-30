package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.FlightBoard;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCardEffects;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleCannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

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

        myGame.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));

        while (myGame.getRealGame().getFlightDeck().getSize() > 0 && !myGame.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) { //Finché ci sono sia carte che giocatori in gioco
            handleTurn();
        }

    }

    private void handleTurn() throws ExecutionException, InterruptedException {
        //NOTIFICA A TUTTI CHI È IL LEADER (Alessandro: Serve?)

        //Prendo i players ordinati per placement
        ArrayList<Player> rankedPlayers = new ArrayList<>(myGame.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).toList()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti //Prendiamo i giocatori che stanno giocando
        rankedPlayers.sort(Comparator.comparingInt(Player::getPlacement));

        AdventureCard adventureCard = myGame.getRealGame().getFlightDeck().pop();
        MatchInfoUpdate miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), myGame.getRealGame().getFlightDeck().getSize());
        //NOTIFICHIAMO CHE CARTA È STATA PESCATA E LA MANDIAMO
        DrawnAdventureCardUpdate dacu = new DrawnAdventureCardUpdate(adventureCard);
        myGame.getPlayerHandlers().values().forEach(ch -> {ch.sendMessage(miu); ch.sendMessage(dacu);}); //Mando la carta pescata ad ogni player

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
            }
        }
    }

    public void removePlayerFromGame(String nickname) throws PlayerNotFoundException {
        myGame.getRealGame().getPlayer(nickname).setPlayerState(PlayerState.Spectating);
        myGame.getRealGame().getFlightBoard().removePlayer(myGame.getPlayerColors().get(nickname));

        FlightBoardUpdate fbu = new FlightBoardUpdate(myGame.getRealGame().getFlightBoard());
        PlayerRemovedUpdate pru = new PlayerRemovedUpdate(nickname);
        myGame.getPlayerHandlers().values().forEach(ch -> {ch.sendMessage(pru); ch.sendMessage(fbu);}); //Notifichiamo i client che un player ha perso e aggiorniamo la flight board
    }

    /**
     * Removes the first tile hit by the projectile if the conditions to destroy it are met.
     *
     * @param projectile The projectile.
     * @param diceRoll   Result of the dice rolled by the player.
     * @author Alessandro Giuseppe Gioia
     */
    @NeedsToBeCompleted("Controllare per tronconi. Un po' scettico sul fatto che il messaggio debba essere mandato da questo metodo.")
    public void reactToProjectile(Player targetPlayer, Projectile projectile, int diceRoll) {
        Ship ship = targetPlayer.getShip();
        Position pos = ship.getFirstComponentFromDirectionAndIndex(projectile.getDirection(), diceRoll);
        if(pos == null) return;

        boolean aTileHasBeenDestroyed = false;
        String message = "Tile distrutta in posizione [" + pos.getX() + "," + pos.getY() + "]";

        if (projectile.getType() == ProjectileType.CannonFire) {
            if (projectile.getSize() == ProjectileSize.BIG) {
                ship.removeTile(pos, false);
                aTileHasBeenDestroyed = true;
            }
            else if (projectile.getSize() == ProjectileSize.LITTLE){
                if(!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())){
                    ship.removeTile(pos, false);
                    aTileHasBeenDestroyed = true;
                }
            }
        } else if (projectile.getType() == ProjectileType.Meteor) {
            if (projectile.getSize() == ProjectileSize.BIG) {
                if(!protectWithFirstAvailableCannon(ship, projectile.getDirection())){
                    ship.removeTile(pos, false);
                    aTileHasBeenDestroyed = true;
                }
            }
            else if (projectile.getSize() == ProjectileSize.LITTLE) {
                ArrayList<Connector> tileConnectors = ship.getShipBoard()[pos.getY()][pos.getX()].getTile().getSides();
                int index = -1;

                switch (projectile.getDirection()) {
                    case UP    -> index = 0;
                    case RIGHT -> index = 1;
                    case DOWN  -> index = 2;
                    case LEFT  -> index = 3;
                }

                if(tileConnectors.get(index) != Connector.EMPTY){ //Se non è un lato liscio
                    if(!protectWithFirstAvailableCorrectlyOrientedChargedShield(ship, projectile.getDirection())){ //Se non c'è uno shield disponibile a proteggere
                        ship.removeTile(pos, false);
                        aTileHasBeenDestroyed = true;
                    }
                }
            }
        }

        if(aTileHasBeenDestroyed){
            myGame.getPlayerHandlers().get(targetPlayer.getNickName()).sendMessage(new GameMessage(message));
        }
    }


    /**
     * Finds the first available charged shield oriented according to the direction from which the projectile will come from and protects
     * the ship with it, discharging it.
     *
     * @param direction The direction which the projectile will come from.
     * @return {@code true} if the ship is protected, {@code false} if it is not.
     * @author Alessandro Giuseppe Gioia
     */
    private boolean protectWithFirstAvailableCannon(Ship ship, ProjectileDirection direction) {
        for (Position cannonPos : ship.getComponentPositionsFromName("Cannon")) {
            Cannon cannon = (Cannon) ship.getComponentFromPosition(cannonPos);
            if(cannon.getRotation() == direction.ordinal()){
                return true;
            }
        }

        for (Position cannonPos : ship.getComponentPositionsFromName("DoubleCannon")) {
            DoubleCannon doubleCannon = (DoubleCannon) ship.getComponentFromPosition(cannonPos);
            if(doubleCannon.getRotation() == direction.ordinal() && doubleCannon.isCharged()){
                doubleCannon.setCharged(false);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first available charged shield oriented according to the direction from which the projectile will come from and protects
     * the ship with it, discharging it.
     *
     * @param direction The direction which the projectile will come from.
     * @return {@code true} if the ship is protected, {@code false} if it is not.
     * @author Alessandro Giuseppe Gioia
     */
    private boolean protectWithFirstAvailableCorrectlyOrientedChargedShield(Ship ship, ProjectileDirection direction) {
        for (Position shieldPos : ship.getComponentPositionsFromName("Shield")) {
            Shield shield = (Shield) ship.getComponentFromPosition(shieldPos);
            if (shield.isCharged() && shield.getProtectedSides().contains(direction)) {
                shield.setCharged(false);
                return true;
            }
        }
        return false;
    }

}
