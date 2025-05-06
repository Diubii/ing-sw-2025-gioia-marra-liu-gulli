package org.polimi.ingsw.galaxytrucker.visitors;

import org.polimi.ingsw.galaxytrucker.enums.CardPhase;
import org.polimi.ingsw.galaxytrucker.exceptions.PlayerNotFoundException;
import org.polimi.ingsw.galaxytrucker.model.Player;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public interface AdventureCardVisitorsInterface {
    void visitAbandonedShip(AbandonedShip abandonedShip, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException;

    void visitAbandonedStation(AbandonedStation abandonedStation, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException;

    void visitCombatZone(CombatZone combatZone, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException;

    void visitEpidemic(Epidemic epidemic, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);

    void visitMeteorSwarm(MeteorSwarm meteorSwarm, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);

    void visitOpenSpace(OpenSpace openSpace, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager, CardPhase cardPhase) throws ExecutionException, InterruptedException, PlayerNotFoundException;

    void visitPlanets(Planets planets, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager) throws ExecutionException, InterruptedException;

    void visitStardust(Stardust stardust, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);

    void visitPirates(Pirates pirates, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);

    void visitSlavers(Slavers slavers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);

    void visitSmugglers(Smugglers smugglers, ArrayList<Player> rankedPlayers, LobbyManager lobbyManager);
}
