package org.polimi.ingsw.galaxytrucker.model;

import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeCompleted;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.essentials.FlightBoardMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightBoard {


    private FlightBoardMap flightBoardMap;
    private final HashMap<Color, Integer> playerSteps;




    private final Boolean learningMatch;

    public FlightBoard(boolean learningMatch) {
        this.learningMatch = learningMatch;
        playerSteps = new HashMap<>();
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

        }
    }

    public ArrayList<Color> getRankedPlayers() {

        ArrayList<Color> ranked = (ArrayList<Color>) playerSteps.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))  // Ordine decrescente
                .map(Map.Entry::getKey)  // Estraggo solo i Color
                .toList();


        return ranked;
    }


    @NeedsToBeCompleted

    public Player getLeader() {

        Color leaderColor = getRankedPlayers().getFirst();
        return null;
    }

    public Boolean isPlayerOverLapped(Color token) {
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
