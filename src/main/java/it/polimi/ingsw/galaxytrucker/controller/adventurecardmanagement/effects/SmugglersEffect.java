package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.CardContext;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Smugglers;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.CollectRewardsRequest;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.CollectRewardsResponse;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.GameMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;

import static it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects.Utils.*;

/**
 * The {@code SmugglersEffect} class handles the resolution of the "Smugglers" adventure card effect.
 *
 * <p>Players compare their ship's firepower to that of the smugglers. Based on the result:
 * <ul>
 *   <li>If the player loses, they lose their most valuable goods and potentially batteries.</li>
 *   <li>If the player wins, they are offered a reward with an optional movement penalty.</li>
 *   <li>On a tie, no effect is applied.</li>
 * </ul>
 *
 * <p>The effect also coordinates network messaging for sending/receiving {@link ShipUpdate} and
 * {@link CollectRewardsResponse} messages to handle player decisions.
 */
public class SmugglersEffect {

    /**
     * Executes the firepower comparison between the player and the smugglers.
     * Handles loss, win, or tie scenarios and proceeds accordingly.
     *
     * @param context the {@link CardContext} containing game and player data.
     */
    public static void firePowerCheck(CardContext context){

        LobbyManager game = context.getCurrentGame();
        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        Player player = context.getCurrentPlayer();

        broadcastGameMessage(context, "Il giocatore " + player.getNickName() + " sta affrontando i Contrabbandieri!");
        sleepSafe(600);

        float smugglerFirePower = smugglers.getFirePower();
        float playerFirePower =  player.getShip().calculateFirePower();
        resetDoubleCannon(player);

//        System.out.println( player.getNickName() + "  Debug: Fire Power Check");
        ShipUpdate shipUpdate = new ShipUpdate(player.getShip(),player.getNickName());
        broadcast(context, shipUpdate);

        if (smugglerFirePower> playerFirePower){
            handleSmugglersWin(context,game,player,smugglers);
            context.previousPhase();
        }
        else if (smugglerFirePower < playerFirePower){
            context.nextPhase();
            handlePlayerWin(context, game, player);
            return;
        }
        else  {
            context.previousPhase();
            handleTie( context,game, player);

        }
        if(context.currentPlayerIsLast()){
            context.goToEndPhase();
        }
        else {
            context.nextPlayer();
        }

        context.executePhase();
    }
    /**
     * Handles the logic for when the smugglers overpower the player.
     * Removes goods or batteries as penalties and notifies the player.
     *
     * @param context  the game context.
     * @param game     the current game instance.
     * @param player   the affected player.
     * @param smugglers the {@link Smugglers} adventure card.
     */

    private static void handleSmugglersWin(CardContext context, LobbyManager game, Player player, Smugglers smugglers) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("The Smugglers are going to haunt you!"));

        GameMessage broadcast = new GameMessage(player.getNickName() + " has less FirePower than the Smugglers!");
        broadcastExcept(context, broadcast, player);
        sleepSafe(600);

        ArrayList<Good> removedGoods = getAndRemoveMostValuableGoods(context, player, smugglers.getPenalty());
        int goodsCount = removedGoods.size();
        int batteryToDiscard = smugglers.getPenalty() - goodsCount;

        String message;
        if (goodsCount == smugglers.getPenalty()) {
            message = "[Smugglers] Ha ha! We'll steal your " + goodsCount + " most valuable goods!";
        } else if (goodsCount > 0) {
            removeBatteries(context, player, batteryToDiscard);
            message = "[Smugglers] We'll steal your " + goodsCount + " most valuable good(s) and " + batteryToDiscard + " battery(ies), if you have them.";
        } else {
            removeBatteries(context,player, batteryToDiscard);
            message = "[Smugglers] You don't have any goods, so we'll steal " + batteryToDiscard + " of your batteries! Well, if you have any, poor fella.";
        }

        GameMessage personalInfo = new GameMessage(message);
        game.getPlayerHandlers().get(player.getNickName()).sendMessage(personalInfo);
    }
    /**
     * Handles the case when the player defeats the smugglers and is offered a reward.
     *
     * @param context the game context.
     * @param game    the current game instance.
     * @param player  the winning player.
     */

    private static void handlePlayerWin(CardContext context, LobbyManager game, Player player) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("You won against the Smugglers!"));

        GameMessage broadcast = new GameMessage(player.getNickName() + " has defeated the Smugglers!");
        broadcastExcept(context, broadcast, player);
        sleepSafe(600);

        CollectRewardsRequest rewardRequest = new CollectRewardsRequest();
        sendMessage(context, player, rewardRequest);
    }

    /**
     * Handles the case when the player ties with the smugglers.
     * Sends informative messages to the player and other participants.
     *
     * @param context the game context.
     * @param game    the current game instance.
     * @param player  the tying player.
     */
    private static void handleTie( CardContext context,LobbyManager game, Player player) {
        game.getPlayerHandlers().get(player.getNickName())
                .sendMessage(new GameMessage("The Smugglers are not going to haunt you!"));

        GameMessage broadcast = new GameMessage(  "Il giocatore "+player.getNickName()+ " ha pareggiato con i Contrabbandieri.");
                broadcastExcept(context,broadcast,player);
                sleepSafe(600);
    }
    /**
     * Processes the player's response to the reward collection prompt.
     * Advances the game state depending on the player's decision.
     *
     * @param context the {@link CardContext} containing the incoming message and game state.
     */
    public static void receivedRewardsCollectionResponse(CardContext context){

        CollectRewardsResponse collectRewardsResponse = (CollectRewardsResponse) context.getIncomingNetworkMessage();
        Player player = context.getCurrentPlayer();
//        System.out.println(player.getNickName() + "  Debug: Received rewards collection response");

        if(collectRewardsResponse.doesWantToCollect()) {
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " scegle di accettare la ricompensa!"), player);
            context.nextPhase();
            sendMessage(context, player, new ShipUpdate(player.getShip(), player.getNickName()));
            sleepSafe(600);

        }
        else{
            broadcastExcept(context, new GameMessage("Player " + player.getNickName() + " scegle di  non accettare la ricompensa."), player);
            sleepSafe(600);

            if(context.currentPlayerIsLast()){
                context.goToEndPhase();
            }
            else {
                context.nextPlayer();
                context.previousPhase(1);
            }
            context.executePhase();
        }
    }


    /**
     * Handles the incoming {@link ShipUpdate} after a player accepts the smugglers' reward.
     * Applies the movement penalty and ends the effect.
     *
     * @param context the {@link CardContext} including the updated ship state.
     */

    public static void receivedShipUpdate(CardContext context){
//        System.out.println(currentPlayer.getNickName() + "  Debug: Received ship update response");
        Smugglers smugglers = (Smugglers) context.getAdventureCard();
        movePlayer(context, context.getCurrentPlayer(), -smugglers.getDaysLost());

        context.nextPhase();
        context.executePhase();
    }
}
