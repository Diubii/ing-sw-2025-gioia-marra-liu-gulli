package org.polimi.ingsw.galaxytrucker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightBoard {

    private boolean learningMatch;
    private int lapLength;
    private Map<String, Integer> playerPositions = new HashMap<String, Integer>();
    private Map<String, Integer> playerLap = new HashMap<String, Integer>();
    private Player leader;
    private List<Player> players;

    public FlightBoard(List<Player> rankedplayers, boolean leaderMatch) {
        this.players = rankedplayers;
        this.learningMatch = leaderMatch;
        this.leader = rankedplayers.get(0);
        initializeFlightBoards(learningMatch);
    }

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
     *
     * @param nickname The name of the player who needs to move.
     * @param amount The number of steps to move.
     *   Functionality:
     * Move the player.
     * Call updateLeader to update the leader.
     * Call checkOverlapping to check if there is overlapping.
     * Incomplete: If there is a player on the position during the movement, skip that position first.
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


    private void updateLeader(){
        Player newLeader = null;
        return;

    }


    private void checkOverLapping(String nickname) {
            return ;
    }


    public int getBoardPosition(String nickname) {
        return playerPositions.get(nickname);
    }

    public Player getLeader() {
        return leader;
    }
}
