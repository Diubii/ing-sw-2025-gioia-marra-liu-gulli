package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.polimi.ingsw.galaxytrucker.controller.GameController;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileDirection;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileSize;
import org.polimi.ingsw.galaxytrucker.enums.ProjectileType;
import org.polimi.ingsw.galaxytrucker.model.MockShipFactory;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.Shield;
import org.polimi.ingsw.galaxytrucker.model.game.Game;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;

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
    void testReactToProjectile_MeteorBig_WithoutTile() {

        Player playerA = ctx.lobby.getGameController().getRankedPlayers().get(0);
        Ship shipA = MockShipFactory.createHighFirePowerShip();
        playerA.replaceShip(shipA);

        Projectile projectile = new Projectile(ProjectileType.CannonFire, ProjectileDirection.UP, ProjectileSize.Big);

        int diceRoll = 12;

        Tile destroyed = controller.reactToProjectile(playerA, projectile, diceRoll);
        assertNull(destroyed);


    }

}
