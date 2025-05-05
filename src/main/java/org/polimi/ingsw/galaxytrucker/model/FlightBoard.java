package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMap;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class FlightBoard implements Serializable{

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

    public void positionPlayer(Color token, int pos ){
        for (int i = 0; i < flightBoardMap.getFlightBoardMapSlots().size(); i++){
            if (i == pos){
                if (flightBoardMap.getFlightBoardMapSlots().get(i).getPlayerToken() == Color.EMPTY){
                    flightBoardMap.getFlightBoardMapSlots().get(i).setPlayerToken(token);
                    playerSteps.put(token, i);
                }
            }
        }
    }

    public void movePlayer(Color token, int steps){
        int initialPos = playerSteps.get(token) % flightBoardMap.getFlightBoardMapSlots().size();
        boolean occupied = true;
        int additionalSteps = 0;

        while (occupied){
            int tempFinalPos = (initialPos + steps + additionalSteps) % flightBoardMap.getFlightBoardMapSlots().size();
            if (flightBoardMap.getFlightBoardMapSlots().get(tempFinalPos).getPlayerToken() != Color.EMPTY){
                //occupata

                if (steps > 0)  additionalSteps++;
                else additionalSteps--;

            } else {
                positionPlayer(token, tempFinalPos);
                playerSteps.put(token, initialPos+steps+additionalSteps);
                occupied = false;
            }

            flightBoardMap.getFlightBoardMapSlots().get(initialPos).setPlayerToken(Color.EMPTY);


        }
    }

    public ArrayList<Color> getRankedPlayers() {
        return new ArrayList<>(playerSteps.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))  // Ordine decrescente
                .map(Map.Entry::getKey)  // Estraggo solo i Color
                .toList());
    }

    public void removePlayer(Color token){
        if(!playerSteps.containsKey(token)) return;

        int initialPos = playerSteps.get(token);
        int tempFinalPos = (initialPos) % flightBoardMap.getFlightBoardMapSlots().size();


        playerSteps.remove(token);
        flightBoardMap.getFlightBoardMapSlots().get(tempFinalPos).setPlayerToken(Color.EMPTY);
    }

    @NeedsToBeCompleted
    public Color getLeader() {
        return getRankedPlayers().getFirst();
    }

    public Boolean isPlayerLapped(Color token) {
        int myPos = playerSteps.get(token) % flightBoardMap.getFlightBoardMapSlots().size();
        int mySteps = playerSteps.get(token);
        int myLoops = mySteps / flightBoardMap.getFlightBoardMapSlots().size();

        for (Map.Entry<Color, Integer> entry : playerSteps.entrySet()) {

            int otherPlayerSteps = entry.getValue();
            int otherPlayerLoops = otherPlayerSteps / flightBoardMap.getFlightBoardMapSlots().size();

            if (otherPlayerSteps > playerSteps.get(token) && otherPlayerLoops > myLoops) return true;


        }

        return false;

    }

    public ArrayList<Integer> getOccupiedPositions() {
        ArrayList<Integer> positions = new ArrayList<>();

        for (Map.Entry<Color, Integer> entry : playerSteps.entrySet()) {

            int position = entry.getValue() % flightBoardMap.getFlightBoardMapSlots().size();
            positions.add(position);

        }

        return positions;
    }

    public FlightBoardMap getFlightBoardMap(){
        return flightBoardMap;
    }
}
