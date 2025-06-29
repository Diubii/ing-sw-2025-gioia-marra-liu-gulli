package it.polimi.ingsw.galaxytrucker.controller;

import it.polimi.ingsw.galaxytrucker.enums.GameState;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.FakeClientHandler;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.network.client.Client;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
/**
 * Test suite for {@link ClientController}.
 * Covers position setting, tile handling, ship fetch, deck viewing, and phase updates.
 */
public class ClientControllerTest {

    Player player1, player2;
    FakeClientHandler handler1, handler2;
    ServerController serverController;
    GameTestHelper.GameTestContext context;
    ClientController clientController1, clientController2;
    Client client1,client2;

    @BeforeEach
    void setUp() {
        player1 = new Player("TestPlayer", 0, 1, true);
        player2 = new Player("TestPlayer2", 0, 2, true);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("TestPlayer", new ArrayList<>());
        responses.put("TestPlayer2", new ArrayList<>());

        context = GameTestHelper.setupGameForBuildingPhase(responses, players);
        handler1 = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get("TestPlayer");
        handler2 = (FakeClientHandler) GameTestHelper.GameTestContext.nicknameToHandlerMap.get("TestPlayer2");
        clientController1 =  GameTestHelper.GameTestContext.nicknameToClientControllerMap.get("TestPlayer");
        clientController2 =  GameTestHelper.GameTestContext.nicknameToClientControllerMap.get("TestPlayer2");
        client1 = clientController1.getClient();
        client2 = clientController2.getClient();

        serverController = context.serverController;
    }

    /**
     * Tests setting and resetting the current tile position in hand,
     * drawing a tile, rotating it, and displaying it.
     */
    @Test
    void   testCurrentTileInHand(){

        clientController1.setCurrentPos(2,3);
        assertEquals( new Position(2,3), clientController1.getCurrentPosition());
        clientController1.resetCurrentPos();
        assertNull(clientController1.getCurrentPosition());
        clientController1.setCurrentPos(2,3);


        clientController1.handleDrawFaceDownTile();
        clientController1.showTileInHand();
        clientController1.rotateCurrentTile(90);
        clientController1.handleDrawFaceDownTile();

    }

    /**
     * Tests fetching ship data from both known and unknown players.
     */
    @Test
    void testHandleFetchShip(){
        clientController1.handleFetchShip("TestPlayer");
        clientController1.handleFetchShip("TestPlayer2");
        clientController2.handleFetchShip("TestPlayersadasn");
    }


    /**
     * Tests viewing adventure card decks by index.
     * Validates handling of valid and potentially invalid indexes.
     */
    @Test
    void testViewAdventureCardDeck(){

        clientController1.viewAdventureCardDeck(0);
        clientController1.viewAdventureCardDeck(3);
        clientController1.viewAdventureCardDeck(4);

    }



    /**
     * Tests handling a phase transition to BUILDING_END and
     * simulates various building menu choices.
     */
    @Test
    void testHandlePhaseUpdate(){
        PhaseUpdate phaseUpdate = new PhaseUpdate(GameState.BUILDING_END);

        clientController1.handlePhaseUpdate(phaseUpdate);
        clientController1.handleBuildingMenuChoice("reset");
        clientController1.handleBuildingMenuChoice("a");
        clientController1.handleBuildingMenuChoice("c");
        clientController1.handleBuildingMenuChoice("d");
        clientController1.handleBuildingMenuChoice("e");
        clientController1.handleBuildingMenuChoice("f");
    }

}

