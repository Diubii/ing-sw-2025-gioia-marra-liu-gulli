//package it.polimi.ingsw.galaxytrucker.model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class FlightboardTest {
//    private FlightBoard flightBoard;
//    private List<Player> players;
//    private boolean learningMatch;
//
//    @BeforeEach
//    public void setUp() {
//        players = new ArrayList<Player>();
//        learningMatch = false;
//        players.add(new Player("Alice", 0, 1, learningMatch));
//        players.add(new Player("Bob", 0, 2, learningMatch));
//        players.add(new Player("Charlie", 0, 3, learningMatch));
//        players.add(new Player("David", 0, 4, learningMatch));
//        flightBoard = new FlightBoard(learningMatch);
//
//    }
//
//
//
//    @Test
//    public void testMoveBoard() {
//        Player player1 = players.get(0);
//        Player player4 = players.get(3);
//
//
//        flightBoard.moveBoard(player1, 1);
//        flightBoard.moveBoard(player4, 1);
//        int positionPLayer1 = flightBoard.getPlayerPositions(player1);
//        int positionPLayer4 = flightBoard.getPlayerPositions(player4);
//
//        assertEquals(7, positionPLayer1,"err moveAdvance without jump");    //Normal advance and retreat
//        assertEquals(2, positionPLayer4,"err moveAdvance with jump");   //Jump
//        flightBoard.moveBoard(player4, -1);
//        positionPLayer4 = flightBoard.getPlayerPositions(player4);
//        assertEquals(0, positionPLayer4,"err moveRetreat with jump");  //retreat and jump
//
//
//
//        flightBoard.moveBoard(player1, 30);
//        int lapPlayer1 = flightBoard.getPlayerLap(player1);
//        assertEquals(1, lapPlayer1, "err update Player's lap"); //check lap update
//
//
//    }
//
//    @Test
//    public void testCheckOverlap() {
//
//        Player player1 = players.get(0);
//        Player player2 = players.get(1);
//        Player player3 = players.get(2);
//        Player player4 = players.get(3);
//
//        flightBoard.moveBoard(player1, 18);
//        flightBoard.updateOrder(players);
//
//        List<Player> rankedplayers = players;
//        flightBoard.checkOverLapping(rankedplayers);
//
//        assertEquals(2, rankedplayers.size(),"err remove player in overlap");
//        Player playerA = rankedplayers.get(0);
//        Player playerB = rankedplayers.get(1);
//
//        assertEquals(player1, playerA, "err Player A");
//        assertEquals(player2, playerB, "err Player B");
//
//    }
//
//
//
//
//
//
//
//}