package org.polimi.ingsw.galaxytrucker.model.utils;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.enums.Connector;
import org.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.BatterySlot;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;

import javax.smartcardio.Card;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void createLvl1Deck() throws IOException {

        CardDeck dexk = Util.createLvl1Deck();


        System.out.println(dexk.pop().getName());
    }

    @Test
    void createLvl2Deck() {
    }

    @Test
    void createLearningDeck() {
    }

    @Test

    void checkShipS(){
        Ship ship = MockShipFactory.createMockShip();

        ArrayList<Connector> connectors = new ArrayList<>();
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);
        connectors.add(Connector.UNIVERSAL);


        Tile tile = new Tile(0,0,connectors,new BatterySlot(2));

        ship.putTile(tile, new Position(5,3));
        ship.putTile(tile, new Position(5,2));


        Pair<Boolean, ArrayList<Integer>> pair =  Util.checkShipStructure(ship, new Position(3,2));
        ShipPrintUtils.printShip(ship);
        System.out.println("B: " + pair.getKey() );

        ship.removeTile(new Position(5,2),false);

        ship.checkShip(null);
        System.out.println(ship.toString());

        ArrayList<Ship> ships = ship.getTronc();

        for (Ship ship1 : ships) {
            ShipPrintUtils.printShip(ship1);

        }

    }
}