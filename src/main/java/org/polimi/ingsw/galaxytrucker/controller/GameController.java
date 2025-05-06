package org.polimi.ingsw.galaxytrucker.controller;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Cannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleCannon;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameController {

    private GameState gameState;
    private final LobbyManager myGame;
    private int nCompletedShips = 0;
    final Object ncsLock = new Object();
    CompletableFuture cardDrawn;
    private Boolean gameEndedEarly = false;
    private CardDeck cardDeckTest = new CardDeck(true);

    public CardDeck getCardDeckTest() {
        return cardDeckTest;
    }
    public void setCardDeckTest(CardDeck cardDeckTest) {
        this.cardDeckTest = cardDeckTest;
    }

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

        Good good1 = new Good(Color.RED);
        Good good2 = new Good(Color.BLUE);
        Good good3 = new Good(Color.GREEN);
        Good good4 = new Good(Color.YELLOW);


        ArrayList<Good> goods1 = new ArrayList<>();
        goods1.add(good1);
        goods1.add(good2);
        ArrayList<Good> goods2 = new ArrayList<>();
        goods2.add(good3);
        goods2.add(good4);
        ArrayList<Good> goods3 = new ArrayList<>();
        goods3.add(good4);
        Planet p1 = new Planet(false, goods1);
        Planet p2 = new Planet(false, goods2);
        Planet p3 = new Planet(false, goods3);
        ArrayList<Planet> nPlanet = new ArrayList<>();
        nPlanet.add(p1);
        nPlanet.add(p2);
        nPlanet.add(p3);

        AbandonedShip abandonedShip = new AbandonedShip(
                17,
                1,
                 1,
                "Nave abbandonata",
                false,
                2,
                 3,
                false);
        OpenSpace openSpace = new OpenSpace(
                26,
                 2,
                 0,
                 "Spazio aperto",
                 false,
                 true
        );
        Planets planets = new Planets(
                1,
                2,
                2,
                "Planets",
                true,
                 nPlanet,
                false


                );
        cardDeckTest.addCard(abandonedShip);
        cardDeckTest.addCard(openSpace);
        cardDeckTest.addCard(planets);

        myGame.getRealGame().getPlayers().forEach(player -> player.setPlayerState(PlayerState.Playing));

        while (cardDeckTest.getSize() > 0 && !myGame.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) { //Finché ci sono sia carte che giocatori in gioco
            handleTurn();
            if (gameEndedEarly){
                handleEndGame();
                break;
            };


        }


    }

    public void handleTurnBeforeDrawnCard(){
        ArrayList<Player> rankedPlayers = new ArrayList<>(myGame.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).toList()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti //Prendiamo i giocatori che stanno giocando
        MatchInfoUpdate miu;
        rankedPlayers.sort(Comparator.comparingInt(Player::getPlacement));

        if (!rankedPlayers.isEmpty()) {
            miu = new MatchInfoUpdate(rankedPlayers.getFirst().getNickName(), cardDeckTest.getSize());

        } else {
            miu = new MatchInfoUpdate("", myGame.getRealGame().getFlightDeck().getSize());

        }
        myGame.getPlayerHandlers().values().forEach(ch -> {ch.sendMessage(miu);}); //Mando match info update



    }

    private void handleTurn() throws ExecutionException, InterruptedException {

        cardDrawn = new CompletableFuture();
        //Prendo i players ordinati per placement

        handleTurnBeforeDrawnCard();

        cardDrawn.get();
        DrawnAdventureCardUpdate drawnAdventureCardUpdate = new DrawnAdventureCardUpdate(cardDeckTest.peek());
        myGame.getPlayerHandlers().values().forEach(ch -> {
            ch.sendMessage(drawnAdventureCardUpdate);
        }); //Mando match info update


        //Test
        AdventureCard adventureCard = cardDeckTest.peek();
        ArrayList<Player> rankedPlayers = new ArrayList<>(myGame.getRealGame().getPlayers().stream().filter(p -> p.getPlayerState() == PlayerState.Playing).toList()); //Shallow copy, i players non sono clonati quindi vengono mantenuti i riferimenti //Prendiamo i giocatori che stanno giocando

        if (!rankedPlayers.isEmpty()) {

            adventureCard.activateEffect(adventureCardEffects, rankedPlayers, myGame); //Attivo l'effetto della carta


            //Controllo se ci sono giocatori doppiati e nel caso li rimuovo
            FlightBoard flightBoard = myGame.getRealGame().getFlightBoard();
            for (Color color : flightBoard.getRankedPlayers()) {
                if (flightBoard.isPlayerLapped(color)) { //Se il giocatore è doppiato
                    String lappedPlayerNickname = myGame.getNicknameFromColor(color);
                    try {
                        removePlayerFromGame(lappedPlayerNickname, false); //Lo rimuovo
                    } catch (PlayerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            myGame.getPlayerHandlers().values().forEach(ch -> {
                ch.sendMessage(new EndTurnUpdate());
            }); //Mando match info update

        } else {

            //caso in cui non ci sono piu giocatori, (quittano tutti prima dell'effetto della carta)
            gameEndedEarly = true;
            return;

        }
    }

    public void removePlayerFromGame(String nickname) throws PlayerNotFoundException {



    }

    private void handleEndGame() {
    }

    @NeedsToBeCompleted
    public void completeCardDrawn(){
        //cardDrawn.complete(null);
    }

    public void removePlayerFromGame(String nickname, boolean isLandingEarly) throws PlayerNotFoundException {
        myGame.getRealGame().getPlayer(nickname).setPlayerState(PlayerState.Spectating);
        myGame.getRealGame().getFlightBoard().removePlayer(myGame.getPlayerColors().get(nickname));

        FlightBoardUpdate fbu = new FlightBoardUpdate(myGame.getRealGame().getFlightBoard());
        PlayerLostUpdate plu = new PlayerLostUpdate(nickname, isLandingEarly);
        myGame.getPlayerHandlers().values().forEach(ch -> {ch.sendMessage(plu); ch.sendMessage(fbu);}); //Notifichiamo i client che un player ha perso e aggiorniamo la flight board

        if (myGame.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
            completeCardDrawn();
        }
    }

    public void kickPlayerFromGame(String nickname) throws PlayerNotFoundException {
        myGame.getRealGame().getFlightBoard().removePlayer(myGame.getPlayerColors().get(nickname));
        myGame.getRealGame().removePlayer(nickname);

        FlightBoardUpdate fbu = new FlightBoardUpdate(myGame.getRealGame().getFlightBoard());
        PlayerKickedUpdate pku = new PlayerKickedUpdate(nickname);
        myGame.getPlayerHandlers().values().forEach(ch -> {ch.sendMessage(pku); ch.sendMessage(fbu);}); //Notifichiamo i client che un player è stato kickato e aggiorniamo la flight board
        myGame.removePlayerHandler(nickname);

        if (myGame.getRealGame().getFlightBoard().getRankedPlayers().isEmpty()) {
            //se non ho piu giocatori completo la cardDrawn ed entro nel ramo else in handleTurn
            completeCardDrawn();
        }
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
