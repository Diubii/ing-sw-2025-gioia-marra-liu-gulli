package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;


import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Pirates;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.ActivateComponentRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.AskTrunkRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskTrunkResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;


/**
 * The {@code PiratesEffect} class manages the full logic of the "Pirates" adventure card.
 * <p>Key phases include:
 * <ul>
 *   <li> Firepower Check: Players compare their ship's firepower with the pirates'. Based on the result, they either win, lose, or tie.</li>
 *   <li> Reward Collection: The first winning player can choose to collect a reward in exchange for moving backward.</li>
 *   <li> Cannon Fire: Players who lose the firepower check are targeted by pirate projectiles. This may lead to ship damage and fragmentation.</li>
 *   <li> Trunk Selection: If a ship is split, the affected player chooses which part to retain.</li>
 * </ul>
 */
public class PiratesEffect {

    private final static HashMap<LobbyManager, ArrayList<Player>> losersPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> projectileIndexes = new HashMap<>();
    private final static HashMap<LobbyManager, ArrayList<Ship>> trunksPerGame = new HashMap<>();
    private final static HashMap<LobbyManager, Boolean> rewardTaken  = new HashMap<>();
    private final static HashMap <LobbyManager, Integer> projectileVictimIndexes = new HashMap<>();
    private final static HashMap<LobbyManager, Integer> currentDiceRoll = new HashMap<>();

    private final static Random rand = new Random();
    /**
     * Initiates the firepower comparison between the current player and the pirates.
     * Based on the result, either a win, loss, or tie is handled, with optional reward collection for winners.
     *
     * @param context the {@link CardContext} containing the current player and game state.
     */
    public static void firePowerCheck(CardContext context) {

        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

//        System.out.println(player.getNickName()+ " DEBUG: firePowerCheck");
        float piratesFirePower = pirates.getFirePower();
        float playerFirePower = player.getShip().calculateFirePower();

        resetDoubleCannon(player);
        ShipUpdate shipUpdate = new ShipUpdate(player.getShip(),player.getNickName());
        broadcast(context, shipUpdate);

        if (piratesFirePower > playerFirePower) {
            handlePiratesWin(context);
            sleepSafe(600);

        } else if (piratesFirePower < playerFirePower) {
            handlePlayerWin(context);
            sleepSafe(600);

            rewardTaken.computeIfAbsent(game, _ -> false);
            //Controllo prima se la ricompensa è già stata presa da un altro giocatore.

            if(!rewardTaken.get(game)) {
                //Andiamo a PiratesEffect::receivedRewardsCollectionResponse una volta ricevuta la risposta
                context.nextPhase();
                CollectRewardsRequest collectRewardsRequest = new CollectRewardsRequest();
                sendMessage(context, context.getCurrentPlayer(), collectRewardsRequest);
                return;
            }
            //Se la ricompensa è già stata riscossa, salto il blocco if-else
            // e passo direttamente al controllo se il giocatore è l’ultimo

        } else if (piratesFirePower == playerFirePower) {
            handleTie(context);
            sleepSafe(600);
        }

        if(context.currentPlayerIsLast()) {
            context.nextPhase(2); //cannonaitsStart
        }
        else{
            context.nextPlayer();
            context.previousPhase();
        }
        context.executePhase();
    }

    /**
     * Handles the case when the player wins the firepower comparison.
     *
     * @param context the {@link CardContext} for the current adventure phase.
     */
    private static void handlePlayerWin(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

//        System.out.println("DEBUG : firePower pirates.getFirePower() < player.getShip().calculateFirePower()");
        GameMessage personalMessage = new GameMessage("The Pirates are not going to haunt you!"); //personalMessage.setIsTurn(true);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
        GameMessage info = new GameMessage();
        info.setMessage(player.getNickName() + " has defeated the Pirates!");
        broadcastExcept(context, info, player);

    }
    /**
     * Handles the case when the pirates defeat the player.
     *
     * @param context the {@link CardContext} for the current adventure phase.
     */
    private static void handlePiratesWin(CardContext context) {
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

//        System.out.println("DEBUG: firePower pirates.getFirePower() > player.getShip().calculateFirePower()");
        losersPerGame.computeIfAbsent(game, _ -> new ArrayList<>());
        losersPerGame.get(game).add(player);


        GameMessage personalMessage = new GameMessage("The Pirates are going to haunt you!"); //personalMessage.setIsTurn(true);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalMessage);
        GameMessage info = new GameMessage();
        info.setMessage(player.getNickName() + " has less FirePower than the Pirates!");
        broadcastExcept(context, info, player);

    }
    /**
     * Handles a tie between the player's and pirates' firepower.
     *
     * @param context the {@link CardContext} for the current adventure phase.
     */
    private static void handleTie(CardContext context){
        Player player = context.getCurrentPlayer();
//        System.out.println("DEBUG: firePower pirates.getFirePower() == player.getShip().calculateFirePower()");

        sendGameMessage(context, player, "The Pirates are not going to haunt you!");
        GameMessage info = new GameMessage();
        info.setMessage(player.getNickName() + " ha pareggiato con i Pirati.");
        broadcastExcept(context, info, player);

    }

    /**
     * Handles the player's response regarding whether they want to collect the pirates' reward.
     * Rewards are granted only to the first player who chooses to collect them.
     *
     * @param context the {@link CardContext} including the reward response.
     */
    public static void receivedRewardsCollectionResponse(CardContext context) {
//        System.out.println("DEBUG: receivedRewardsCollectionResponse");
        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
        Pirates pirates = (Pirates) context.getAdventureCard();
        LobbyManager game = context.getCurrentGame();

        if (collectRewardsResponse.doesWantToCollect() && !rewardTaken.get(game)) {
            //aggiorno i crediti
            player.addCredits(pirates.getCredits());
            //lo muovo indietro
            movePlayer(context, player, -pirates.getDaysLost());
            rewardTaken.put(game, true);
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose to collect the rewards!"), player);
        }
        else {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " chose NOT to collect the rewards!"), player);
        }
        if(context.currentPlayerIsLast()) {
                context.nextPhase(1); //cannonaitsStart
        }
        else{
            context.nextPlayer();
            context.previousPhase(2);
        }
        context.executePhase();
    }

    /**
     * Starts the cannon fire phase in which pirates fire projectiles at losing players.
     * The phase proceeds one player and one projectile at a time.
     *
     * @param context the {@link CardContext} for the cannon fire phase.
     */
    public static void cannonaitsStart(CardContext context) {

        //dopo aver finito chiedo di tirare i dadi al primo, ma per ora per la Tui facciamo sia automatico
        LobbyManager game = context.getCurrentGame();
        Pirates pirates = (Pirates) context.getAdventureCard();


        ArrayList<Player> losers = losersPerGame.getOrDefault(game,null);

        if (losers == null || losers.isEmpty()) { //Se abbiamo finito vado alla fase finale
            broadcastGameMessage(context, "Nessuno è stato sconfitto, nessun attacco dai Pirati.");
            sleepSafe(600);

            losersPerGame.remove(game);
            projectileIndexes.remove(game);
            trunksPerGame.remove(game);
            rewardTaken.remove(game);
            projectileVictimIndexes.remove(game);

            resetState(game);
            context.goToEndPhase();
            context.executePhase();
            return;
        }

        projectileIndexes.putIfAbsent(game, 0);

        int projectileIndex = projectileIndexes.get(game);

        if (projectileIndex == pirates.getCannonFires().size()) {
            losersPerGame.remove(game);
            projectileIndexes.remove(game);
            trunksPerGame.remove(game);
            rewardTaken.remove(game);
            projectileVictimIndexes.remove(game);

            resetState(game);
            context.goToEndPhase();
            context.executePhase();
            return;
        }
        projectileVictimIndexes.putIfAbsent(game, 0);

        int projectileVictimIndex = projectileVictimIndexes.get(game);
        Player player = losers.get(projectileVictimIndex);
        context.setCurrentPlayer(player);
//        System.out.println(player.getNickName() + " DEBUG: cannonaitsStart");
        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        if (projectileIndex < pirates.getCannonFires().size()) {
            ActivateComponentRequest activateShieldRequest = new ActivateComponentRequest(ActivatableComponent.Shield);
            context.nextPhase();

            if (projectile.getSize().equals(ProjectileSize.Little) && playerCanDefendThemselvesWithAShield(player, projectile)) {
                sendMessage(context, player, activateShieldRequest);
            } else {
                context.executePhase();
            }
        }

    }


    /**
     * Executes the cannon fire for a specific player.
     * The player may receive damage, and if their ship splits, they must later choose a trunk to retain.
     *
     * @param context the {@link CardContext} for the active cannon fire.
     */
    public static void cannonaitsFire(CardContext context) {

        LobbyManager game = context.getCurrentGame();

        Pirates pirates = (Pirates) context.getAdventureCard();
        Player player = context.getCurrentPlayer();
        Ship playerShip = player.getShip();
//        ShipPrintUtils.printShip(playerShip);
//        System.out.println(player.getNickName() + " DEBUG: cannonaitsFire");
        int projectileIndex = projectileIndexes.getOrDefault(game,0);
        int victimIdx = projectileVictimIndexes.getOrDefault(game, 0);

        Projectile projectile = pirates.getCannonFires().get(projectileIndex);

        if(player.equals(losersPerGame.get(game).get(0))) {
            int viewDiceRoll = rand.nextInt(2, 13);
            currentDiceRoll.putIfAbsent(game, 0);
            currentDiceRoll.put(game, viewDiceRoll);
        }


        int diceRoll = getCorrectedDiceRoll(currentDiceRoll.get(game), projectile.getDirection());
        broadcastGameMessage(context,player.getNickName() + "  sta per essere colpito da un " + projectile.getType().name() +" "+ projectile.getSize() +" da " + projectile.getDirection().name() + ", indice " + currentDiceRoll.get(game) + "!");
        sleepSafe(600);
//        System.out.println("Stai per essere colpito da un " + projectile.getType().name()  +" "+ projectile.getSize() +" da " + projectile.getDirection().name() + ", indice " + currentDiceRoll.get(game) + "!");
        if (losersPerGame.get(game).size() == victimIdx +1 ) {
            projectileIndexes.put(game, projectileIndex + 1);
            projectileVictimIndexes.put(game, 0);
        }
        else {
            projectileVictimIndexes.put(game, victimIdx + 1);
        }

        broadcastShipUpdate(context,player);
        sleepSafe(600);

        Tile destroyedTile = game.getGameController().reactToProjectile(player, projectile, diceRoll);
//        ShipPrintUtils.printShip(playerShip);
        if (destroyedTile != null) {
            ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();
            broadcastGameMessage(context, "Purtroppo, " + player.getNickName() + " è stato colpito e ha perso un " + destroyedTile.getMyComponent().accept(componentNameVisitor));
        } else {
            broadcastGameMessage(context, "Congratulazioni, " + player.getNickName() + " è riuscito a schivare l'attacco!");
        }

        sleepSafe(600);
        resetShield(player);
        broadcastShipUpdate(context,player);

        if (destroyedTile != null) {
            ArrayList<Ship> tronconi;

//            se ho eliminato una tile vedo se ho creato dei tronconi
            tronconi = player.getShip().getTronc();
            trunksPerGame.put(game, tronconi);
            if (tronconi.size() > 1) {
//                System.out.println("in tronconi size()>1");
//                System.out.println(player.getNickName() + " size tronconi " + tronconi.size());
                //se ho creato nuovi tronconi chiedo quale tenere
                AskTrunkRequest askTrunkRequest = new AskTrunkRequest(tronconi);
                context.nextPhase();
                sendMessage(context, player, askTrunkRequest);
            } else {
                context.previousPhase();
                context.executePhase();
            }
        }
        else{
            context.previousPhase();
            context.executePhase();
        }
    }

    /**
     * Handles the player's choice of which ship segment (trunk) to retain, if their ship was split.
     * Updates the player's ship and notifies all players of the choice.
     *
     * @param context the {@link CardContext} with the trunk selection response.
     */
    public static void cannonaitsTrunks(CardContext context) {
//        System.out.println("DEBUG: cannonaitsTrunks");
        LobbyManager game = context.getCurrentGame();
        AskTrunkResponse askTrunkResponse = (AskTrunkResponse) context.getIncomingNetworkMessage();
        int indexTrunk = askTrunkResponse.getTrunkIndex();
        Player player = context.getCurrentPlayer();

        ArrayList<Ship> trunks = trunksPerGame.get(game);
        if (trunks != null && indexTrunk >= 0 && indexTrunk < trunks.size()) {
            Ship newShip = trunks.remove(indexTrunk);
            player.replaceShip(newShip);
            addDestroyedTilesInTrunc(player,trunks);
        }

        //invio a tutti la nuova nave
        broadcast(context, new ShipUpdate(player.getShip(), player.getNickName()));

        broadcastGameMessage(context, player.getNickName() + " ha scelto quale parte della nave tenere dopo l'attacco.");
        sleepSafe(600);

        context.previousPhase(2); //cannonaitsStart
        context.executePhase();
    }

    /**
     * Clears all static tracking state related to the pirates' effect for the given game instance.
     *
     * @param game the {@link LobbyManager} representing the current game.
     */
    private static void resetState(LobbyManager game) {
        losersPerGame.remove(game);
        projectileIndexes.remove(game);
        trunksPerGame.remove(game);
        rewardTaken.remove(game);
        projectileVictimIndexes.remove(game);
        currentDiceRoll.remove(game);
    }
}
