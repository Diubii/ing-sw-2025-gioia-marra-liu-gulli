package org.polimi.ingsw.galaxytrucker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlightboardTest {
    private FlightBoard flightBoard;
    private List<Player> players;
    private boolean learningMatch;

    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        learningMatch = false;
        players.add(new Player("Alice", 0, 1, learningMatch));
        players.add(new Player("Bob", 0, 2, learningMatch));
        players.add(new Player("Charlie", 0, 3, learningMatch));
        players.add(new Player("David", 0, 4, learningMatch));
        flightBoard = new FlightBoard(players, learningMatch);

    }

    @Test
    public void testInitializeFlightBoard() {
        if (learningMatch == true) {

            int i = 0;
            int j = 2;

            assertEquals(18,flightBoard.getLapLength(), "err lap lenghth should be 18" );
            for (Player player : players) {

                int position = flightBoard.getPlayerPositions(player);

                if (i == 0) {
                    assertEquals(4, flightBoard.getPlayerPositions(player),"err init FirstPlayer in learningMatch ");
                    i = i + 1;
                } else {
                    assertEquals(j, flightBoard.getPlayerPositions(player),"err init Player " +  i +  "  in learningMatch ");
                    j--;
                    i++;
                }
            }
        } else {
            assertEquals(24,flightBoard.getLapLength(), "err lap lenghth should be 24" );
            int i = 0;
            int j = 1;
            for (Player player : players) {

                int position = flightBoard.getPlayerPositions(player);

                if (i == 0 || i == 1) {
                    if(i== 0 ) {
                        assertEquals(6, flightBoard.getPlayerPositions(player),"err init FirstPlayer in NormalMatch ");
                    }
                    if(i== 1 ) {
                        assertEquals(3, flightBoard.getPlayerPositions(player),"err init SecondPlayer in NormalMatch ");
                    }
                    i = i + 1;
                }


                else {
                    assertEquals(j, flightBoard.getPlayerPositions(player),"err init Player " +  i +  "  in NormalMatch ");
                    j--;
                    i++;
                }
            }
        }
    }


    @Test
    public void testMoveBoard() {
        Player player1 = players.get(0);
        Player player4 = players.get(3);


        flightBoard.moveBoard(player1, 1);
        flightBoard.moveBoard(player4, 1);
        int positionPLayer1 = flightBoard.getPlayerPositions(player1);
        int positionPLayer4 = flightBoard.getPlayerPositions(player4);

        assertEquals(7, positionPLayer1,"err moveAdvance without jump");    //Normal advance and retreat
        assertEquals(2, positionPLayer4,"err moveAdvance with jump");   //Jump
        flightBoard.moveBoard(player4, -1);
        positionPLayer4 = flightBoard.getPlayerPositions(player4);
        assertEquals(0, positionPLayer4,"err moveRetreat with jump");  //retreat and jump



        flightBoard.moveBoard(player1, 30);
        int lapPlayer1 = flightBoard.getPlayerLap(player1);
        assertEquals(1, lapPlayer1, "err update Player's lap"); //check lap update


    }

    @Test
    public void testCheckOverlap() {

        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        Player player4 = players.get(3);

        flightBoard.moveBoard(player1, 18);
        flightBoard.updateOrder(players);

        List<Player> rankedplayers = players;
        flightBoard.checkOverLapping(rankedplayers);

        assertEquals(2, rankedplayers.size(),"err remove player in overlap");
        Player playerA = rankedplayers.get(0);
        Player playerB = rankedplayers.get(1);

        assertEquals(player1, playerA, "err Player A");
        assertEquals(player2, playerB, "err Player B");

    }





    @Test
    public void testUpdateOrder() {
        Player playerA = players.get(0);
        Player playerB = players.get(1);
        Player playerC = players.get(2);
        Player playerD = players.get(3);

        flightBoard.moveBoard(playerD, 10);
        flightBoard.moveBoard(playerC, 5);
        flightBoard.moveBoard(playerA, 1);

        List<Player> exceptedOrder = Arrays.asList(playerD, playerC, playerA, playerB);

        flightBoard.updateOrder(players);
        for(int i = 0; i < players.size(); i++) {
            assert exceptedOrder.get(i).equals(players.get(i)) : "err PlayersOrder " + i +" Player" ;

        }
        Player leader = flightBoard.getLeader();
        assertEquals(leader, playerD, "err Leader");


    }

}