package org.polimi.ingsw.galaxytrucker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents the flight board that tracks player positions during the game.
 * It manages player movement, lap progression, and leader updates.
 */
public class FlightBoard {

    /** Indicates whether the match is a learning match. */
    private boolean learningMatch;

    /** The length of one lap in the flight track. */
    private int lapLength;

    /** A map storing the player's name and their current position on the board. */
    private Map<String, Integer> playerPositions = new HashMap<String, Integer>();

    /** A map storing the player's name and the number of laps completed. */
    private Map<String, Integer> playerLap = new HashMap<String, Integer>();

    /** The current leader of the race. */
    private Player leader;

    /** The list of players participating in the race. */
    private List<Player> players;

    /**
     * Constructs a FlightBoard instance and initializes player positions.
     *
     * @param rankedplayers The list of players ordered by rank.
     * @param leaderMatch {@code true} if it is a learning match, otherwise {@code false}.
     */
    public FlightBoard(List<Player> rankedplayers, boolean leaderMatch) {
        this.players = rankedplayers;
        this.learningMatch = leaderMatch;

        if (rankedplayers.size() > 0)         this.leader = rankedplayers.getFirst();
        else this.leader = null;
        initializeFlightBoards(learningMatch);
    }

    /**
     * Initializes player positions and lap length based on the match type.
     *
     * @param learningMatch {@code true} if it is a learning match, otherwise {@code false}.
     */
    private void initializeFlightBoards(boolean learningMatch) {
        if (learningMatch) {
            lapLength = 18;
            int position = 0;
            for (int i = players.size() - 1; i >= 0; i--) {
                String name = players.get(i).getNickName();
                if (players.size() > 1 && i == 0) {
                    position++;
                }
                playerPositions.put(name, position);
                playerLap.put(name, 0);
                position++;
            }

        } else if (learningMatch) {
            lapLength = 24;
            int positionTwo = 0;
            for (int i = players.size() - 1; i >= 0; i--) {
                String name = players.get(i).getNickName();
                if (players.size() > 1) {
                    if (i == 0) {
                        positionTwo = positionTwo + 2;
                    }
                    if (i == 1 && players.size() > 2) {
                        positionTwo++;
                    }
                }
                playerPositions.put(name, positionTwo);
                playerLap.put(name, 0);
                positionTwo++;
            }
        }

    }

    /**
     * Moves a player by a specified amount on the board.
     * If the player completes a lap, their lap count is updated.
     * Also updates the leader and checks for overlapping positions.
     *
     * @param nickname The name of the player who needs to move.
     * @param amount The number of steps to move.
     */
    public void moveBoard(String nickname, int amount) {
        if (!playerPositions.containsKey(nickname)) return;

        int oldPosition = playerPositions.get(nickname);
        int newPosition = oldPosition + amount;
        if (newPosition >= lapLength) {
            int currentLap = playerLap.get(nickname);
            currentLap++;
            playerLap.put(nickname, currentLap);
            newPosition %= lapLength;
        }
        if (newPosition < 0) {
            newPosition = lapLength -1 + newPosition;
            int currentLap = playerLap.get(nickname);
            currentLap--;
            playerLap.put(nickname, currentLap);
        }

        playerPositions.put(nickname, newPosition);
        updateLeader();
        checkOverLapping(nickname);
    }


    /**
     * Updates the current leader based on player positions.
     * (Implementation needed)
     */
    private void updateLeader(){
        Player newLeader = null;
        return;

    }

    /**
     * Checks if a player is overlapping with another player on the board.
     * (Implementation needed)
     *
     * @param nickname The name of the player to check.
     */
    private void checkOverLapping(String nickname) {
            return ;
    }

    /**
     * Gets the board position of a specific player.
     *
     * @param nickname The player's name.
     * @return The player's current position on the board.
     */
    public int getBoardPosition(String nickname) {
        return playerPositions.get(nickname);
    }

    /**
     * Gets the current leader of the race.
     *
     * @return The leader player.
     */
    public Player getLeader() {
        return leader;
    }
}
