package org.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.*;

public class MockResponsesFactory {


    public static Map<String, ArrayList<NetworkMessage>> forAbandonedShip() {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("A", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(false),
                        new DiscardCrewMembersResponse(new ArrayList<>(
                                List.of(
                                        new Position(3, 2)
                                )
                        ))
                )));
        responses.put("B", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(true),
                        new DiscardCrewMembersResponse(new ArrayList<>(
                                List.of(
                                        new Position(3, 2)
                                )
                        ))
                        )));
        responses.put("C", new ArrayList<>(
                List.of(new ActivateAdventureCardResponse(true),
                        new DiscardCrewMembersResponse(new ArrayList<>(
                                List.of(
                                        new Position(3, 2)
                                )
                        ))
                        )));
        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forAbandonedStation() {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("A", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(true),
                        new ShipUpdate(new Ship(false), "A")
                )));
        responses.put("B", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(false)
                )));
        responses.put("C", new ArrayList<>(
                List.of(new ActivateAdventureCardResponse(false)
                )));
        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forOpenSpace() {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        ArrayList<Position> batteryPositions = new ArrayList<>();
        batteryPositions.add(new Position(2, 2));
        ArrayList<Position> componentPositions = new ArrayList<>();
        componentPositions.add(new Position(3, 3));
        responses.put("A", new ArrayList<>(
                List.of(
                       new ActivateComponentResponse(
                               ActivatableComponent.DoubleEngine,
                               componentPositions,
                               batteryPositions
                                )

                )));
        responses.put("B", new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleEngine,
                                new ArrayList<>(),
                                new ArrayList<>()

                        )
                )));
        responses.put("C", new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleEngine,
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                )));
        return responses;
    }

}