package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.controller.GameController;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import it.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import it.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Projectile;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.DoubleCannon;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private GameTestHelper.GameTestContext ctx;
    private GameController controller;

    private final String playerANickname = "A";
    private final String playerBNickname = "B";
    private final String playerCNickname = "C";

    private final ArrayList<Player> players = new ArrayList<>(
            List.of(
                    new Player(playerANickname, 0, 1, false),
                    new Player(playerBNickname, 0, 2, false),
                    new Player(playerCNickname, 0, 3, false)
            )
    );
    @BeforeEach
    public void setup() {
        Map<String, ArrayList<NetworkMessage>> responses = MockResponsesFactory.emptyResponsesFor(players);
        ctx = GameTestHelper.setupGame(responses, players);
        controller = ctx.lobby.getGameController();
    }

    @Test
    void testReactToProjectile_CannonFireBig_ShouldDestroyTile() {


        Player playerA = players.get(0);
        Ship shipA = MockShipFactory.createHighFirePowerShip();
        playerA.replaceShip(shipA);

        Projectile projectile = new Projectile(ProjectileType.CannonFire, ProjectileDirection.UP, ProjectileSize.Big);

        int diceRoll = 3;
        Position targetPosition = new Position(3,1);

        Tile tileBeforeAttack = shipA.getTileFromPosition(targetPosition);
        assertNotNull(tileBeforeAttack);

        Tile destroyed = controller.reactToProjectile(playerA, projectile, diceRoll);
        shipA = playerA.getShip();
        Tile tileAfterAttack= shipA.getTileFromPosition(targetPosition);

        assertNotNull(destroyed, "Big cannon fire should destroy a tile");
        assertNull(tileAfterAttack, "Big cannon fire should destroy a tile");


    }
    @Test
    void testReactToProjectile_CannonFireBig_WithoutTile() {

        Player playerA = ctx.lobby.getGameController().getRankedPlayers().get(0);
        Ship shipA = MockShipFactory.createHighFirePowerShip();
        playerA.replaceShip(shipA);

        Projectile projectile = new Projectile(ProjectileType.CannonFire, ProjectileDirection.UP, ProjectileSize.Big);

        int diceRoll = 12;

        Tile destroyed = controller.reactToProjectile(playerA, projectile, diceRoll);
        assertNull(destroyed);


    }

    @Test
    void testReactToProjectile_CannonFireLittle_WithShield() {
        Player playerA = ctx.lobby.getGameController().getRankedPlayers().get(0);
        Ship shipA = MockShipFactory.createMockShipWithShield();


        //Testare che lo scudo non ruotato, una volta caricato, difenda correttamente dagli attacchi
        // dopo un utilizzo la carica charged diventa false e al secondo attacco lo scudo non difende più con successo.
        ArrayList<Position> shields =shipA.getComponentPositionsFromName("Shield");
        Position pos1 = shields.get(0);
        Position pos2 = shields.get(1);


        Shield shield1 = (Shield) shipA.getComponentFromPosition(pos1);



        playerA.replaceShip(shipA);


        Projectile projectile1 = new Projectile(ProjectileType.CannonFire, ProjectileDirection.UP, ProjectileSize.Little);
        Projectile projectile2 = new Projectile(ProjectileType.CannonFire, ProjectileDirection.RIGHT, ProjectileSize.Little);
        Tile unattackedTile2 = shipA.getTileFromPosition(new Position(3,3));

        int diceRoll = 3;

        shield1.setCharged(true);
        Tile destroyed1 = controller.reactToProjectile(playerA, projectile1, diceRoll);
        Tile destroyed2 = controller.reactToProjectile(playerA, projectile2, diceRoll);

        assertNull(destroyed1);
        assertEquals(unattackedTile2,destroyed2);


        //Quasi identico al test precedente, ma questa volta lo scudo è già ruotato al momento della creazione della nave.
        Shield shield2 = (Shield) shipA.getComponentFromPosition(pos2);
        shield2.setCharged(true);
        Position pos3 = new Position(2,2);
        Position pos4 = new Position(2,4);
        Tile unuttackedTile3 = shipA.getTileFromPosition(pos3);
        Tile unuttackedTile4 = shipA.getTileFromPosition(pos4);

        Projectile projectile3 = new Projectile(ProjectileType.CannonFire, ProjectileDirection.LEFT, ProjectileSize.Little);
        Projectile projectile4 = new Projectile(ProjectileType.CannonFire, ProjectileDirection.DOWN, ProjectileSize.Little);
        int diceRoll2 = 2;

        Tile destroyed3 = controller.reactToProjectile(playerA, projectile3, diceRoll2);
        Tile destroyed4 = controller.reactToProjectile(playerA, projectile4, diceRoll2);

        assertNull(destroyed3);
        assertEquals(unuttackedTile4, destroyed4);

        Position targetPosition3 = pos3;
        Position targetPosition4 = pos4;
        Tile targetTile3 = shipA.getTileFromPosition(targetPosition3);
        Tile targetTile4 = shipA.getTileFromPosition(targetPosition4);
        assertEquals(unuttackedTile3,targetTile3);
        assertNull(targetTile4);

    }

    @Test
    void testReactToProjectile_MeteorBig() {

        Player playerA = ctx.lobby.getGameController().getRankedPlayers().get(0);
        Ship shipA = MockShipFactory.createHighFirePowerShip();
        playerA.replaceShip(shipA);

        //projectTile meteor up big
        //Verifica che, se il meteorite proviene dall'alto e colpisce la stessa colonna in cui si trova un Cannon orientato verso l'alto,
        // allora l'attacco venga difeso con successo e nessun tile venga rimosso.
        Projectile projectile1 = new Projectile(ProjectileType.Meteor, ProjectileDirection.UP, ProjectileSize.Big);

        int diceRoll1 = 1;
        Tile destroyed1 = controller.reactToProjectile(playerA, projectile1, diceRoll1);
        assertNull(destroyed1);

        //Verifica che, se il meteorite proviene dall'alto e colpisce la stessa colonna in cui si trova un DoubleCannon Caricato orientato verso l'alto,
        // allora l'attacco venga difeso con successo e nessun tile venga rimosso.
        //Dopo l’utilizzo del doubleCannon, lo stato charged diventa false;
        // se viene attaccato di nuovo senza che il DoubleCannon sia carico,
        // l’attacco ha successo e il tile corrispondente viene rimosso

        int diceRoll2 = 2;

        Position doubleCannon1Pos = new Position(2,1);
        DoubleCannon doubleCannon1 =(DoubleCannon) shipA.getComponentFromPosition(doubleCannon1Pos);
        Tile doubleCannon1Tile = shipA.getTileFromPosition(doubleCannon1Pos);


        doubleCannon1.setCharged(true);
        Tile destroyed2 = controller.reactToProjectile(playerA, projectile1, diceRoll2);
        assertNull(destroyed2);

        destroyed2 = controller.reactToProjectile(playerA, projectile1, diceRoll2);
        assertEquals(doubleCannon1Tile,destroyed2);
        //end projectTile meteor up big

        //projectTile meteor left big
        //"Verifica che, se il meteorite proviene dai lati o dal basso
        // e colpisce una posizione che corrisponde a quella di un Cannon (o di un DoubleCannon carico)
        // oppure a una riga o colonna adiacente,
        // allora l'attacco venga difeso con successo e nessun tile venga rimosso.

        Projectile projectile2 = new Projectile(ProjectileType.Meteor, ProjectileDirection.LEFT, ProjectileSize.Big);

        Player playerB = ctx.lobby.getGameController().getRankedPlayers().get(1);
        Ship shipB = MockShipFactory.createHighFirePowerShipWithMultiDirection();
        playerB.replaceShip(shipB);

        int diceRoll3 = 2;
        Tile destroyed3 = controller.reactToProjectile(playerB, projectile2, diceRoll3);
        assertNull(destroyed3);

        diceRoll3 = 3;
        destroyed3 = controller.reactToProjectile(playerB, projectile2, diceRoll3);
        assertNull(destroyed3);

        diceRoll3 = 4;
        destroyed3 = controller.reactToProjectile(playerB, projectile2, diceRoll3);
        assertNull(destroyed3);

        //projectTile meteor down big

        Projectile projectile3 = new Projectile(ProjectileType.Meteor, ProjectileDirection.DOWN, ProjectileSize.Big);
        int diceRoll4 = 0;
        Tile destroyed4 = controller.reactToProjectile(playerB, projectile3, diceRoll4);
        assertNull(destroyed4);

        diceRoll4= 1;
        destroyed4 = controller.reactToProjectile(playerB, projectile3, diceRoll4);
        assertNull(destroyed4);

        diceRoll4 = 2;
        destroyed4 = controller.reactToProjectile(playerB, projectile3, diceRoll4);
        assertNull(destroyed4);


    }
    @Test
    void testReactToProjectile_MeteorLittle() {

        Player playerA = ctx.lobby.getGameController().getRankedPlayers().get(0);
        Ship shipA = MockShipFactory.createHighFirePowerShip();
        playerA.replaceShip(shipA);


        Projectile projectile1 = new Projectile(ProjectileType.Meteor, ProjectileDirection.UP, ProjectileSize.Little);

        int diceRoll1 = 1;
        Tile destroyed1 = controller.reactToProjectile(playerA, projectile1, diceRoll1);
        assertNull(destroyed1);


        Position pos1_2 = new Position(1,2);
        Tile tile1_2 = shipA.getTileFromPosition(pos1_2);

        diceRoll1 = 2;
        Projectile projectile2 = new Projectile(ProjectileType.Meteor, ProjectileDirection.LEFT, ProjectileSize.Little);
        Tile destroyed2 = controller.reactToProjectile(playerA, projectile2, diceRoll1);
        Tile tile1_2New = shipA.getTileFromPosition(pos1_2);

        assertEquals(tile1_2,destroyed2);
        assertNull(tile1_2New);



        shipA = MockShipFactory.createMockShipWithShield();
        playerA.replaceShip(shipA);
        diceRoll1 = 2;
        Projectile projectile3 = new Projectile(ProjectileType.Meteor, ProjectileDirection.RIGHT, ProjectileSize.Little);

        ArrayList<Position> shields = shipA.getComponentPositionsFromName("Shield");
        Position shieldPosition1 = shields.get(0);
        Position shieldPosition2 = shields.get(1);

        Position pos4_2 = new Position(4,2);
        Tile tile4_2 = shipA.getTileFromPosition(pos4_2);
        Tile destroyed3 = controller.reactToProjectile(playerA, projectile3, diceRoll1);
        Tile tile4_2New = shipA.getTileFromPosition(pos4_2);

        assertEquals(tile4_2,destroyed3);
        assertNull(tile4_2New);


        Shield shield1 =  (Shield) shipA.getComponentFromPosition(shieldPosition1);
        shield1.setCharged(true);
        Tile destroyed4 = controller.reactToProjectile(playerA, projectile3, diceRoll1);
        assertNull(destroyed4);


        diceRoll1 = 2;
        Projectile projectile4 = new Projectile(ProjectileType.Meteor, ProjectileDirection.DOWN, ProjectileSize.Little);
        Shield shield2 =  (Shield) shipA.getComponentFromPosition(shieldPosition2);
        shield2.setCharged(true);
        Tile destroyed5 = controller.reactToProjectile(playerA, projectile4, diceRoll1);
        assertNull(destroyed5);

        diceRoll1 = 3;
        Position pos3_3 = new Position(3,3);
        Tile tile3_3  = shipA.getTileFromPosition(pos3_3);
        Tile destroyed6 = controller.reactToProjectile(playerA, projectile4, diceRoll1);
        Tile tile3_3New  = shipA.getTileFromPosition(pos3_3);
        assertEquals(tile3_3,destroyed6);
        assertNull(tile3_3New);



    }


}
