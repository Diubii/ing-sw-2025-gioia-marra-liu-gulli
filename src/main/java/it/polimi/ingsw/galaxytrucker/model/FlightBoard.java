package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMap;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Represents the race track for ships during the flight phase.
 * Tracks player positions and steps, and handles movement and ranking.
 */
public class FlightBoard implements Serializable {

    @Serial
    private static final long serialVersionUID = 781273817837L;

    private final FlightBoardMap flightBoardMap;
    private final Map<Color, Integer> playerSteps;     // Logical step counter (ranking, lap)
    private final Map<Color, Integer> playerPositions; // Actual visible position on the board
    private final Boolean learningMatch;

    public FlightBoard(boolean learningMatch) {
        this.learningMatch = learningMatch;
        this.playerSteps = new HashMap<>();
        this.playerPositions = new HashMap<>();
        this.flightBoardMap = new FlightBoardMap(learningMatch);
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    /**
     * Places a player token on the board at a valid empty slot.
     */
    public void positionPlayer(Color token, int pos, Player player) {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int startPos = pos;
        while (flightBoardMap.getFlightBoardMapSlots().get(pos).getPlayerToken() != Color.EMPTY) {
            pos = (pos + 1) % size;
            if (pos == startPos) {
                throw new IllegalStateException("No available position on board to place token " + token);
            }
        }

        flightBoardMap.getFlightBoardMapSlots().get(pos).setPlayerToken(token);
        playerSteps.put(token, pos);
        playerPositions.put(token, pos);
        player.setPlacement(pos);
    }
    /**
     * Moves a player a number of logical steps forward or backward.
     */

    public void movePlayer(Color token, int steps, Player player) {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int currentSteps = playerSteps.get(token);
        int initialPos = playerPositions.get(token);

        int realStepsMoved = 0;
        int attemptedPos = initialPos;
        int direction = steps > 0 ? 1 : -1;

        while (realStepsMoved != steps) {
            attemptedPos = ((attemptedPos + direction) % size + size) % size;

            if (flightBoardMap.getFlightBoardMapSlots().get(attemptedPos).getPlayerToken() == Color.EMPTY) {
                realStepsMoved += direction;
            }
        }


        int finalPos = attemptedPos;
        while (flightBoardMap.getFlightBoardMapSlots().get(finalPos).getPlayerToken() != Color.EMPTY) {
            finalPos = ((finalPos + direction) % size + size) % size;
        }


        flightBoardMap.getFlightBoardMapSlots().get(initialPos).setPlayerToken(Color.EMPTY);
        flightBoardMap.getFlightBoardMapSlots().get(finalPos).setPlayerToken(token);


        playerSteps.put(token, currentSteps + realStepsMoved);
        playerPositions.put(token, finalPos);
        player.setPlacement(finalPos);
    }


    /**
     * Returns players ordered from furthest to nearest based on logical steps.
     */
    public ArrayList<Color> getRankedPlayers() {
        return new ArrayList<>(playerSteps.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(Map.Entry::getKey)
                .toList());
    }

    /**
     * Removes a player token from the board and tracking maps.
     */
    public void removePlayer(Color token) {
        if (!playerPositions.containsKey(token)) return;
        int pos = playerPositions.get(token);
        flightBoardMap.getFlightBoardMapSlots().get(pos).setPlayerToken(Color.EMPTY);
        playerSteps.remove(token);
        playerPositions.remove(token);
    }
    /**
     * Returns the current leading player (first in ranking).
     */

    public Color getLeader() {
        return getRankedPlayers().getFirst();
    }

    /**
     * Checks if a player has been lapped by another player.
     */
    public Boolean isPlayerLapped(Color token) {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int mySteps = playerSteps.get(token);
        int myLoops = mySteps / size;

        for (Map.Entry<Color, Integer> entry : playerSteps.entrySet()) {
            if (entry.getKey() == token) continue;
            int otherSteps = entry.getValue();
            int otherLoops = otherSteps / size;
            if (otherSteps > mySteps && otherLoops > myLoops) return true;
        }
        return false;
    }

    /**
     * Returns the list of board positions currently occupied.
     */
    public ArrayList<Integer> getOccupiedPositions() {
        return new ArrayList<>(playerPositions.values());
    }

    public FlightBoardMap getFlightBoardMap() {
        return flightBoardMap;
    }

    public int getPlayerPosition(Color token) {
        if (!playerPositions.containsKey(token)) {
            throw new IllegalArgumentException("Player with token " + token + " not found on the board.");
        }
        return playerPositions.get(token);
    }

    public int getPlayerSteps(Color token) {
        return playerSteps.getOrDefault(token, 0);
    }
}
