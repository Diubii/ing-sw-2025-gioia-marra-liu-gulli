package org.polimi.ingsw.galaxytrucker.model.adventurecards;

import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

class AdventureCardEffectsTest {

    @Test
    void visitPirates() {
    }

    @Test
    void visit() throws ExecutionException, InterruptedException {
        //Goods
        Good good1 = new Good(Color.RED);
        Good good2 = new Good(Color.BLUE);
        Good good3 = new Good(Color.GREEN);
        Good good4 = new Good(Color.YELLOW);
        ArrayList<Good> goods = new ArrayList<>();
        goods.add(good1);
        goods.add(good2);
        goods.add(good3);
        goods.add(good4);

        Planet planet = new Planet(false, goods);
        ArrayList<Planet> planetsArrayList = new ArrayList<>();
        planetsArrayList.add(planet);

        Planets planets = new Planets(1, 2, 2, "Pianeti", false, planetsArrayList, false);

        Player diubi = new Player("Diubi", 0, 1, false);
        Player smattimat = new Player("Smattimat", 0, 2, false);

        ArrayList<Player> rankedPlayers = new ArrayList<>();
        rankedPlayers.add(diubi);
        rankedPlayers.add(smattimat);

        FakeLobbyManager lobbyManager = new FakeLobbyManager();

        FakeClientHandler diubiClientHandler = new FakeClientHandler();
        FakeClientHandler smattimatClientHandler = new FakeClientHandler();

        lobbyManager.addPlayerHandler(diubi.getNickName(), diubiClientHandler);
        lobbyManager.addPlayerHandler(smattimat.getNickName(), smattimatClientHandler);

        FakeFlightBoard flightBoard = new FakeFlightBoard(false);
        lobbyManager.getRealGame().setFlightBoard(flightBoard);

//      AdventureCardEffects ace = new AdventureCardEffects();
//      ace.visit(planets);
    }
}