package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms.AdventureCardFSMVisitor;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsms.CardFSM;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

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

    public CardContext(LobbyManager currentGame, AdventureCard firstAdventureCard) {
        this.currentGame = currentGame;
        adventureCard = firstAdventureCard;
        currentRankedPlayers = currentGame.getGameController().getRankedPlayers();
        currentPlayer = currentRankedPlayers.getFirst();
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());
        cardFSM = adventureCard.accept(new AdventureCardFSMVisitor());

        expectedNumberOfNetworkMessagesPerType = new HashMap<>(Map.of(
                NetworkMessageType.ShipUpdate, 0,
                NetworkMessageType.ActivateAdventureCardResponse, 0,
                NetworkMessageType.ActivateComponentResponse, 0,
                NetworkMessageType.DiscardCrewMembersResponse, 0,
                NetworkMessageType.SelectPlanetResponse, 0
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

    public ClientHandler getCurrentPlayerHandler() {
        return currentPlayerHandler;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());
    }

    /**
     * Assigns the previous player to the current one. If the current one before the assignment is the first one, it goes back to the last one.
     */
    public void previousPlayer() {
        int i = currentRankedPlayers.indexOf(currentPlayer);
        if (i == 0) {
            this.currentPlayer = currentRankedPlayers.getLast();
        } else {
            this.currentPlayer = currentRankedPlayers.get(i - 1);
        }
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());
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
        int currentValue = expectedNumberOfNetworkMessagesPerType.get(type);
        expectedNumberOfNetworkMessagesPerType.replace(type, currentValue + 1);
    }

    public void decrementExpectedNumberOfNetworkMessages(NetworkMessageType type) {
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

    public void nextPhase() {
        cardFSM.next();
    }
}