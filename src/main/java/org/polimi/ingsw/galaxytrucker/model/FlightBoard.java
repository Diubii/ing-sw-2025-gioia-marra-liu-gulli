package org.polimi.ingsw.galaxytrucker.model;

import java.util.*;

public class FlightBoard {

    private final boolean learningMatch;
    private int lapLength;
    private Map<Player, Integer> playerPositions;
    private Map<Player, Integer> playerLap;
    private Player leader;
    private List<Player> players;
    private Set<String> gameoverPlayers;


    public FlightBoard(List<Player> rankedplayers, boolean learningMatch) {
        this.players = rankedplayers;
        this.learningMatch = learningMatch;
        playerPositions = new HashMap<Player, Integer>();
        playerLap = new HashMap<Player, Integer>();
        initializeFlightBoards(learningMatch);

        this.leader = rankedplayers.get(0);
        gameoverPlayers = new HashSet<>();
    }


    private void initializeFlightBoards(boolean learningMatch) {
        if (learningMatch) {
            lapLength = 18;
            int position = 0;
            for (int i = players.size() - 1; i >= 0; i--) {
                Player player = players.get(i);
                if (players.size() > 1 && i == 0) {
                    position++;
                }
                playerPositions.put(player, position);
                playerLap.put(player, 0);
                position++;
            }

        } else
        {
            lapLength = 24;
            int position = 0;
            for (int i = players.size() - 1; i >= 0; i--) {
               Player player = players.get(i);
                if (players.size() > 1) {
                    if (i == 0) {
                        position = position + 2;
                    }
                    if (i == 1 && players.size() > 2) {
                        position++;
                    }
                }
                playerPositions.put(player, position);
                playerLap.put(player, 0);
                position++;
            }
        }

    }

    /**
     *
     * @param player The player who needs to move.
     * @param amount The number of steps to move.
     *   Functionality:
     * Move the player.
     * Call updateLeader to update the leader.
     * Call checkOverlapping to check if there is overlapping.
     * Incomplete: If there is a player on the position during the movement, skip that position first.
     */
    public void moveBoard(Player player, int amount) {
        if (!playerPositions.containsKey(player) )return;  // throw exception

        int currentPos = playerPositions.get(player);
        int currentlap = playerLap.get(player);
        int direction = amount >= 0 ? 1 : -1;
        int stepsToMove = Math.abs(amount);
        int movedSteps = 0;

        while (movedSteps < stepsToMove) {
            int nextPos = (currentPos + direction + lapLength) % lapLength;

            // Check for lap change
            if (direction > 0 && nextPos == 0) {
                currentlap++;  // passed start point going forward
            } else if (direction < 0 && currentPos == 0) {
                currentlap--;  // passed start point going backward
            }

            // Check if next position is occupied
            boolean isOccupied = false;
            for (Map.Entry<Player, Integer> entry : playerPositions.entrySet()) {
                if (!entry.getKey().equals(player.getNickName()) && entry.getValue() == nextPos) {
                    isOccupied = true;
                    break;
                }
            }

            if (!isOccupied) {
                currentPos = nextPos;
                movedSteps++;
            } else {
                currentPos = nextPos; // still advance, but don't count the step
            }
        }

        playerPositions.put(player, currentPos);
        playerLap.put(player, currentlap);
    }



    public void updateOrder(List<Player> players) {

            players.sort(Comparator
                            .comparingInt((Player p) -> playerLap.get(p))
                            .thenComparing(p -> playerPositions.get(p)).reversed());

    }


    public void checkOverLapping(List<Player> rankedplayers) {

        int numPlayers =rankedplayers.size();

        int positionFirst = playerPositions.get(rankedplayers.get(0));
        int positionLast = playerPositions.get(rankedplayers.get(numPlayers-1));

        int LapFirst = playerLap.get(rankedplayers.get(0));
        int LapLast = playerLap.get(rankedplayers.get(numPlayers - 1));

        while(((LapFirst == LapLast+1)&&(positionFirst > positionLast))|| LapFirst > (LapLast+1) ) {

            playerLap.remove(rankedplayers.getLast());
            playerPositions.remove(rankedplayers.getLast());
            rankedplayers.removeLast();

            Player lastPlayer = players.get(rankedplayers.size()-1);
            LapLast = playerLap.get(lastPlayer);
            positionLast = playerPositions.get(lastPlayer);
        }

        return ;
    }




    public Player getLeader() {
        return players.get(0);
    }

    public int getPlayerPositions(Player player) {
        return playerPositions.get(player);
    }

    public int getPlayerLap(Player player) {
        return playerLap.get(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getLapLength() {
        return lapLength;
    }
}
