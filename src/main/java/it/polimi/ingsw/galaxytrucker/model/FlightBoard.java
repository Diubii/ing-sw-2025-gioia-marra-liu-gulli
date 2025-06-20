package it.polimi.ingsw.galaxytrucker.model;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMap;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlightBoard implements Serializable {

    @Serial
    private static final long serialVersionUID = 781273817837L;

    private FlightBoardMap flightBoardMap;
    private final HashMap<Color, Integer> playerSteps;
    private final Boolean learningMatch;

    public FlightBoard(boolean learningMatch) {
        this.learningMatch = learningMatch;
        playerSteps = new HashMap<>();
        flightBoardMap = new FlightBoardMap(learningMatch);
    }

    public Boolean getLearningMatch() {
        return learningMatch;
    }

    public void positionPlayer(Color token, int pos, Player playerFromClientHandler) {
        for (int i = 0; i < flightBoardMap.getFlightBoardMapSlots().size(); i++) {
            if (i == pos) {
                if (flightBoardMap.getFlightBoardMapSlots().get(i).getPlayerToken() == Color.EMPTY) {
                    flightBoardMap.getFlightBoardMapSlots().get(i).setPlayerToken(token);
                    playerSteps.put(token, i);
                    playerFromClientHandler.setPlacement(i);

                }
            }
        }
    }

    public void movePlayer(Color token, int steps, Player player) {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int initialPos = ((playerSteps.get(token) % size) + size) % size;
        boolean occupied = true;
        int additionalSteps = 0;
        int realSteps = 0;
        int tempFinalPos = ((initialPos + additionalSteps) % size + size) % size;

//        System.out.println("tempFinalPos: start" + tempFinalPos);
//        System.out.println("playerSteps: start" + playerSteps);

        while (realSteps != steps) {
            tempFinalPos = ((initialPos + additionalSteps + realSteps) % size + size) % size;

            if (flightBoardMap.getFlightBoardMapSlots().get(tempFinalPos).getPlayerToken() != Color.EMPTY) {
                // Occupata
                if (steps > 0) additionalSteps++;
                else additionalSteps--;
            } else {
                if (steps > 0) realSteps++;
                else realSteps--;
            }

//            System.out.println("tempFinalPos: " + tempFinalPos);
//            System.out.println("realSteps: " + realSteps);
//            System.out.println("additionalSteps: " + additionalSteps);

        }

        positionPlayer(token, tempFinalPos,player);
        flightBoardMap.getFlightBoardMapSlots().get(initialPos).setPlayerToken(Color.EMPTY);

    }

    public ArrayList<Color> getRankedPlayers() {
        return new ArrayList<>(playerSteps.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))  // Ordine decrescente
                .map(Map.Entry::getKey)
                .toList());
    }

    public void removePlayer(Color token) {
        if (!playerSteps.containsKey(token)) return;

        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int initialPos = ((playerSteps.get(token) % size) + size) % size;

        playerSteps.remove(token);
        flightBoardMap.getFlightBoardMapSlots().get(initialPos).setPlayerToken(Color.EMPTY);
    }

    @NeedsToBeCompleted
    public Color getLeader() {
        return getRankedPlayers().getFirst();
    }

    public Boolean isPlayerLapped(Color token) {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        int mySteps = playerSteps.get(token);
        int myLoops = mySteps / size;

        for (Map.Entry<Color, Integer> entry : playerSteps.entrySet()) {
            int otherPlayerSteps = entry.getValue();
            int otherPlayerLoops = otherPlayerSteps / size;

            if (otherPlayerSteps > mySteps && otherPlayerLoops > myLoops) return true;
        }

        return false;
    }

    public ArrayList<Integer> getOccupiedPositions() {
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        ArrayList<Integer> positions = new ArrayList<>();

        for (Map.Entry<Color, Integer> entry : playerSteps.entrySet()) {
            int position = ((entry.getValue() % size) + size) % size;
            positions.add(position);
        }

        return positions;
    }
    public void updateAllPlayerPlacements(Map<Color, Player> colorToPlayerMap) {
        ArrayList<Color> rankedColors = getRankedPlayers();
        int placement = 1;
        for (Color color : rankedColors) {
            Player p = colorToPlayerMap.get(color);
            if (p != null) {
                p.setPlacement(placement++);
            }
        }
    }

    public FlightBoardMap getFlightBoardMap() {
        return flightBoardMap;
    }

    public int getPlayerPosition(Color token) {

        if (!playerSteps.containsKey(token)) {
            throw new IllegalArgumentException("Player with token " + token + " not found on the flight board.");
        }
        int size = flightBoardMap.getFlightBoardMapSlots().size();
        return ((playerSteps.get(token) % size) + size) % size;
    }
}
