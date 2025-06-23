package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement;

import it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms.CardFSM;
import it.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.server.ClientHandler;
import it.polimi.ingsw.galaxytrucker.visitors.adventurecards.AdventureCardFSMVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Gets context for a single card. Needs to be re-instantiated to change card. Has an internal {@link CardFSM}.
 */
public class CardContext {
    private final AdventureCard adventureCard;
    private Player currentPlayer;
    private ClientHandler currentPlayerHandler;
    private ArrayList<Player> currentRankedPlayers;
    private final CardFSM cardFSM;
    private final LobbyManager currentGame;
    private NetworkMessage incomingNetworkMessage;


    private final HashMap<NetworkMessageType, Integer> expectedNumberOfNetworkMessagesPerType;

    public CardContext(LobbyManager currentGame, AdventureCard adventureCard) {
        this.currentGame = currentGame;
        this.adventureCard = adventureCard;
        currentRankedPlayers = currentGame.getGameController().getRankedPlayers();
        currentPlayer = currentRankedPlayers.getFirst();
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());

        cardFSM = this.adventureCard.accept(new AdventureCardFSMVisitor());

        //Si tiene traccia dei messaggi che dovrebbero arrivarci
        expectedNumberOfNetworkMessagesPerType = new HashMap<>(Map.of(
                NetworkMessageType.ShipUpdate, 0,
                NetworkMessageType.ActivateAdventureCardResponse, 0,
                NetworkMessageType.ActivateComponentResponse, 0,
                NetworkMessageType.DiscardCrewMembersResponse, 0,
                NetworkMessageType.SelectPlanetResponse, 0,
                NetworkMessageType.AskTrunkResponse, 0,
                NetworkMessageType.CollectRewardsResponse, 0
        ));
    }

    public LobbyManager getCurrentGame() {
        return currentGame;
    }

    public AdventureCard getAdventureCard() {
        return adventureCard;
    }

    public ArrayList<Player> getCurrentRankedPlayers() {
        return currentRankedPlayers;
    }

    public void setCurrentRankedPlayers(ArrayList<Player> currentRankedPlayers) {
        this.currentRankedPlayers = currentRankedPlayers;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }



    public boolean currentPlayerIsLast(){
        return getCurrentRankedPlayers().getLast().getNickName().equals(currentPlayer.getNickName());
    }


    /**
     * Assigns the next player to the current one. If the current one before the assignment is the last one, it goes back to the first one.
     */
    public void nextPlayer() {
        int i = currentRankedPlayers.indexOf(currentPlayer);
        if (i == currentRankedPlayers.size() - 1) {
            this.currentPlayer = currentRankedPlayers.getFirst();
        } else {
            this.currentPlayer = currentRankedPlayers.get(i + 1);
        }
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());
    }

    public HashMap<NetworkMessageType, Integer> getExpectedNumberOfNetworkMessagesPerType() {
        return expectedNumberOfNetworkMessagesPerType;
    }

    public void incrementExpectedNumberOfNetworkMessages(NetworkMessageType type) {
        if(type == null) return;
        //if(type == incomingNetworkMessage.accept(new NetworkMessageNameVisitor())) executePhase();

        int currentValue = expectedNumberOfNetworkMessagesPerType.get(type);
        expectedNumberOfNetworkMessagesPerType.replace(type, currentValue + 1);
    }

    public void decrementExpectedNumberOfNetworkMessages(NetworkMessageType type) {
        if(type==null) return;

        int currentValue = expectedNumberOfNetworkMessagesPerType.get(type);
        expectedNumberOfNetworkMessagesPerType.replace(type, currentValue - 1);
    }

    /**
     * Gets the NetworkMessage passed by the ServerController.
     */
    public NetworkMessage getIncomingNetworkMessage() {
        return incomingNetworkMessage;
    }

    public void setIncomingNetworkMessage(NetworkMessage incomingNetworkMessage) {
        this.incomingNetworkMessage = incomingNetworkMessage;
    }

    public void resetFSM() {
        cardFSM.reset();
    }

    public void executePhase() {
        cardFSM.execute(this);
    }

    public void previousPhase() {
        cardFSM.previous();
    }
    public void previousPhase(int iterations){for(int i=1; i<=iterations; i++){
            cardFSM.previous();
        }
    }

    public void nextPhase() {
        cardFSM.next();
    }
    public void nextPhase(int iterations){
        for(int i = 1; i <= iterations; i++){
            cardFSM.next();
        }
    }

    public void goToEndPhase(){
        cardFSM.skipToEndState();
    }
}