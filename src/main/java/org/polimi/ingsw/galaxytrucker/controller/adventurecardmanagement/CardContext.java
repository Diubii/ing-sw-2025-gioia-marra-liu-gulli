package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement;

import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm.AdventureCardFSMVisitor;
import org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.fsm.CardFSM;
import org.polimi.ingsw.galaxytrucker.enums.NetworkMessageType;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.AdventureCard;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.server.ClientHandler;

import java.util.ArrayList;

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
    private NetworkMessageType expectedNetworkMessageType;

    public CardContext(LobbyManager currentGame, AdventureCard firstAdventureCard, ArrayList<Player> currentRankedPlayers) {
        this.currentGame = currentGame;
        adventureCard = firstAdventureCard;
        currentPlayer = currentRankedPlayers.getFirst();
        currentPlayerHandler = currentGame.getPlayerHandlers().get(currentPlayer.getNickName());
        cardFSM = adventureCard.accept(new AdventureCardFSMVisitor());
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
     * Assigns the next player to the current one and returns it. If the current one before the assignment is the last one, it goes back to the first one.
     * @return The player
     */
    public Player getNextPlayer() {
        int i = currentRankedPlayers.indexOf(currentPlayer);
        if(i == currentRankedPlayers.size() - 1) {
            this.currentPlayer = currentRankedPlayers.getFirst();
        }
        else {
            this.currentPlayer = currentRankedPlayers.get(i + 1);
        }
        return currentPlayer;
    }

    public NetworkMessageType getExpectedNetworkMessageType() {
        return expectedNetworkMessageType;
    }
    public void setExpectedNetworkMessageType(NetworkMessageType expectedNetworkMessageType) {
        this.expectedNetworkMessageType = expectedNetworkMessageType;
    }

    public int getCurrentPhaseIndex() {
        return cardFSM.currentPhaseIndex();
    }

    public void resetFSM(){
        cardFSM.reset();
    }

    public void nextPhase() {
        cardFSM.next();
    }
}