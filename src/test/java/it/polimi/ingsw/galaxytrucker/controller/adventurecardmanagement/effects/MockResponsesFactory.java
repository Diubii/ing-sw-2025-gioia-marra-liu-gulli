package it.polimi.ingsw.galaxytrucker.controller.adventurecardmanagement.effects;

import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.Planet;
import it.polimi.ingsw.galaxytrucker.model.Player;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.AbandonedStation;
import it.polimi.ingsw.galaxytrucker.model.adventurecards.Planets;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessage;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.*;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.ShipUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockResponsesFactory {
    private final static String playerANickname = "A";
    private final static String playerBNickname = "B";
    private final static String playerCNickname = "C";

    public static Map<String, ArrayList<NetworkMessage>> emptyResponsesFor(List<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        for (Player player : players) {
            responses.put(player.getNickName(), new ArrayList<>());
        }
        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forAbandonedShip_A() {
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
                                        new Position(3, 2),
                                        new Position(3,2)
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
    public static Map<String, ArrayList<NetworkMessage>> forAbandonedShip_B() {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        responses.put("A", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(true),
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

    public static Map<String, ArrayList<NetworkMessage>> forAbandonedStation_A(ArrayList<Player> players,  AbandonedStation card) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        Player playerA = players.get(0);
        Ship newShipA = playerA.getShip();
        ArrayList<Good> goodsToLoad  = card.getGoods();
        ArrayList<Position> normalPositions = new ArrayList<>();
        ArrayList<Position> specialPositions = new ArrayList<>();
        normalPositions.add(new Position(2, 3));
        normalPositions.add(new Position(2, 3));
        loadGoodsIntoShip(newShipA, goodsToLoad, normalPositions, specialPositions);

        responses.put("A", new ArrayList<>(
                List.of(
                        new ActivateAdventureCardResponse(true),
                        new ShipUpdate(newShipA, "A")
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

    public static Map<String,ArrayList<NetworkMessage>> forPlanet_NormalConditions(ArrayList<Player> players, Planets planets) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();
        Player playerA = players.get(0);
        Player playerB = players.get(1);
        Player playerC = players.get(2);
        ArrayList<Planet> planetsToLoad = planets.getPlanets();
        responses.put(playerA.getNickName(), new ArrayList<>(
                List.of(
                        new SelectPlanetResponse(planetsToLoad.get(0),1 ),
                        new ShipUpdate(playerA.getShip(), playerA.getNickName())

                )));
        responses.put(playerB.getNickName(), new ArrayList<>(
                List.of(
                        new SelectPlanetResponse(planetsToLoad.get(1),2 ),
                        new ShipUpdate(playerB.getShip(), playerB.getNickName())

                )
                ));
        responses.put(playerC.getNickName(), new ArrayList<>(
                List.of(
                        new SelectPlanetResponse(planetsToLoad.get(2),3 ),
                        new ShipUpdate(playerC.getShip(), playerC.getNickName())

                )));
     return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forPirates(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
               ArrayList<Position>  doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
               ArrayList<Position>  batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

               ArrayList<Position> doubleCannonPosChosen = new ArrayList<>();
               ArrayList<Position> batteryPosChosen = new ArrayList<>();
               doubleCannonPosChosen.add(doubleCannonPosList.get(0));
               doubleCannonPosChosen.add(doubleCannonPosList.get(1));
               batteryPosChosen.add(batteryPosList.get(0));
               batteryPosChosen.add(batteryPosList.get(0));


        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                        doubleCannonPosChosen,
                        batteryPosChosen),

                        new CollectRewardsResponse(true)


                )));

        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen)
                )));

        i++;

        doubleCannonPosChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                        doubleCannonPosChosen,
                        batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen)
                )));
        return responses;
    }
    public static Map<String, ArrayList<NetworkMessage>> forPirates_AllPlayerLost(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        ArrayList<Position>  doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        ArrayList<Position>  batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

        ArrayList<Position> doubleCannonPosChosen = new ArrayList<>();
        ArrayList<Position> batteryPosChosen = new ArrayList<>();


        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen),

                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen)


                )));

        i++;
        doubleCannonPosChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();
        ArrayList<Position> shieldChosen;
        shieldChosen = new ArrayList<>();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen)

                )));

        i++;


        shieldChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen)
                )));
        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forPirates_TestTruck(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        ArrayList<Position>  doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        ArrayList<Position>  batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

        ArrayList<Position> doubleCannonPosChosen = new ArrayList<>();
        ArrayList<Position> batteryPosChosen = new ArrayList<>();


        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen),

                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new AskTrunkResponse(0,playerANickname),
                        new AskTrunkResponse(0,playerANickname),
                        new AskTrunkResponse(0,playerANickname)

                )));

        i++;
        doubleCannonPosChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();
        ArrayList<Position> shieldChosen;
        shieldChosen = new ArrayList<>();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen)

                )));

        i++;


        shieldChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen),
                        new ActivateComponentResponse(ActivatableComponent.Shield,
                                shieldChosen,
                                batteryPosChosen)

                )));
        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forSlavers_PlayerADefeatedEnemy(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        ArrayList<Position>  doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        ArrayList<Position>  batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

        ArrayList<Position> doubleCannonPosChosen = new ArrayList<>();
        ArrayList<Position> batteryPosChosen = new ArrayList<>();

        doubleCannonPosChosen.add(doubleCannonPosList.get(0));
        doubleCannonPosChosen.add(doubleCannonPosList.get(1));
        batteryPosChosen.add(batteryPosList.get(0));
        batteryPosChosen.add(batteryPosList.get(0));


        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen),
                        new CollectRewardsResponse(true)

                        //new DiscardCrewMembersResponse()
                )));

        i++;
       doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
       batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(

                )));

        i++;
        doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                )));
        return responses;
    }
    public static Map<String, ArrayList<NetworkMessage>> forSlavers_AllPlayersWereDefeated(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Ship shipA = players.get(i).getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                    new ActivateComponentResponse(
                            ActivatableComponent.DoubleCannon,
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                        new DiscardCrewMembersResponse(discardCrewPositionsA)

                )));

        i++;
        Ship shipB = players.get(i).getShip();
        ArrayList<Position>  modularHousingUnitB = shipB.getComponentPositionsFromName("ModularHousingUnit");
        ArrayList<Position>  centralHousingUnitB = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsB = new ArrayList<>();
        discardCrewPositionsB.add(modularHousingUnitB.get(0));
        discardCrewPositionsB.add(modularHousingUnitB.get(0));
        discardCrewPositionsB.add(centralHousingUnitB.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new DiscardCrewMembersResponse(discardCrewPositionsB)

                )));

        i++;

        Ship shipC = players.get(i).getShip();
        ArrayList<Position>  modularHousingUnitC = shipC.getComponentPositionsFromName("ModularHousingUnit");
        ArrayList<Position>  discardCrewPositionsC = new ArrayList<>();
        discardCrewPositionsC.add(modularHousingUnitC.get(0));
        discardCrewPositionsC.add(modularHousingUnitC.get(0));
        discardCrewPositionsC.add(modularHousingUnitC.get(1));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(  new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new DiscardCrewMembersResponse(discardCrewPositionsC)

                )));
        return responses;
    }



    public static Map<String, ArrayList<NetworkMessage>> forSlavers_PlayerC_DefeatedEnemy(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Ship shipA = players.get(i).getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new DiscardCrewMembersResponse(discardCrewPositionsA)

                )));

        i++;

        ArrayList<Position>  doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        ArrayList<Position>  batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

        ArrayList<Position> doubleCannonPosChosen = new ArrayList<>();
        ArrayList<Position> batteryPosChosen = new ArrayList<>();

        doubleCannonPosChosen.add(doubleCannonPosList.get(0));
        batteryPosChosen.add(batteryPosList.get(0));



        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen
                        )


                )));

        i++;

        Ship shipC = players.get(i).getShip();

        doubleCannonPosList =  players.get(i).getShip().getComponentPositionsFromName("DoubleCannon");
        batteryPosList = players.get(i).getShip().getComponentPositionsFromName("BatterySlot");

        doubleCannonPosChosen = new ArrayList<>();
        batteryPosChosen = new ArrayList<>();

        doubleCannonPosChosen.add(doubleCannonPosList.get(0));
        batteryPosChosen.add(batteryPosList.get(0));


        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(  new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                doubleCannonPosChosen,
                                batteryPosChosen
                        ),
                        new CollectRewardsResponse(true)

                )));
        return responses;
    }


    public static Map<String, ArrayList<NetworkMessage>> forSmugglers_PlayerA_DefeatedEnemy(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Player playerA = players.get(i);
        Ship shipA = playerA.getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),

                        new CollectRewardsResponse(true),
                        new ShipUpdate(shipA,playerA.getNickName())

                )));


        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forSmugglers_PlayerA_DefeatedEnemyB(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Player playerA = players.get(i);
        Ship shipA = playerA.getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),

                        new CollectRewardsResponse(false),
                        new ShipUpdate(shipA,playerA.getNickName())

                )));


        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forSmugglers_PlayerA_TieCondition(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Player playerA = players.get(i);
        Ship shipA = playerA.getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        )

                )));
        i++;
        Player playerB = players.get(i);
        Ship shipB = playerB.getShip();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new CollectRewardsResponse(true),
                        new ShipUpdate(shipB,playerB.getNickName())

                )));
        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(


                )));


        return responses;
    }


    public static Map<String, ArrayList<NetworkMessage>> forSmugglers_PlayerA_SmugglersWin(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Player playerA = players.get(i);
        Ship shipA = playerA.getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        )

                )));
        i++;
        Player playerB = players.get(i);
        Ship shipB = playerB.getShip();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new CollectRewardsResponse(true),
                        new ShipUpdate(shipB,playerB.getNickName())

                )));
        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(


                )));


        return responses;
    }


    public static Map<String, ArrayList<NetworkMessage>> forMeteorSwarm(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;
        Player playerA = players.get(i);
        Ship shipA = playerA.getShip();
        ArrayList<Position>  centralHousingUnit = shipA.getComponentPositionsFromName("CentralHousingUnit");
        ArrayList<Position>  discardCrewPositionsA = new ArrayList<>();
        discardCrewPositionsA.add(centralHousingUnit.get(0));
        discardCrewPositionsA.add(centralHousingUnit.get(0));

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new ActivateComponentResponse(
                                ActivatableComponent.Shield,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new AskTrunkResponse(0,playerANickname)

                )));
        i++;
        Player playerB = players.get(i);
        Ship shipB = playerB.getShip();
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new ActivateComponentResponse(
                                ActivatableComponent.Shield,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new AskTrunkResponse(0,playerBNickname)

                )));
        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(

                        new ActivateComponentResponse(
                                ActivatableComponent.DoubleCannon,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new ActivateComponentResponse(
                                ActivatableComponent.Shield,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ),
                        new AskTrunkResponse(0,playerCNickname)


                )));


        return responses;
    }

    public static Map<String, ArrayList<NetworkMessage>> forCombatZone(ArrayList<Player> players) {
        Map<String, ArrayList<NetworkMessage>> responses = new HashMap<>();

        int i=0;

        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleEngine,
                                players.get(i).getShip().getComponentPositionsFromName("DoubleEngine"),
                                players.get(i).getShip().getComponentPositionsFromName("Battery")),
                        new AskTrunkResponse(0,playerANickname),
                        new AskTrunkResponse(0,playerANickname),
                        new AskTrunkResponse(0,playerANickname)
                        //new DiscardCrewMembersResponse()
                )));

        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleEngine,
                                players.get(i).getShip().getComponentPositionsFromName("DoubleEngine"),
                                players.get(i).getShip().getComponentPositionsFromName("Battery")),
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                players.get(i).getShip().getComponentPositionsFromName("DoubleCannon"),
                                players.get(i).getShip().getComponentPositionsFromName("Battery")),
                        new DiscardCrewMembersResponse(
                                players.get(i).getShip().getComponentPositionsFromName("CentralHousingUnit")
                        ),
                        new AskTrunkResponse(0,playerBNickname),
                        new AskTrunkResponse(0,playerBNickname),
                        new AskTrunkResponse(0,playerBNickname)


                )));

        i++;
        responses.put(players.get(i).getNickName(), new ArrayList<>(
                List.of(
                        new ActivateComponentResponse(ActivatableComponent.DoubleEngine,
                        players.get(i).getShip().getComponentPositionsFromName("DoubleEngine"),
                        players.get(i).getShip().getComponentPositionsFromName("Battery")),
                        new DiscardCrewMembersResponse(players.get(i).getShip().getComponentPositionsFromName("CentralHousingUnit")),
                        new ActivateComponentResponse(ActivatableComponent.DoubleCannon,
                                players.get(i).getShip().getComponentPositionsFromName("DoubleCannon"),
                                players.get(i).getShip().getComponentPositionsFromName("Battery")),
                        new AskTrunkResponse(0,playerBNickname),
                        new AskTrunkResponse(0,playerBNickname),
                        new AskTrunkResponse(0,playerBNickname)

                                )));
        return responses;
    }
    private static void loadGoodsIntoShip(Ship ship, List<Good> goods, List<Position> normal, List<Position> special) {
        int n = 0, s = 0;
        for (Good good : goods) {
            boolean isSpecial = false;
            if(good.getColor().equals(Color.RED)) {
                isSpecial = true;
            }

            Position pos = isSpecial ? (s < special.size() ? special.get(s++) : null) : (n < normal.size() ? normal.get(n++) : null);
            if (pos == null) {
                System.err.println("[MockResponsesFactory] No more space for " + good);
                continue;
            }

            Component c = ship.getComponentFromPosition(pos);
            if (c instanceof GenericCargoHolds hold) {
                hold. playerLoadGood(good);
            } else {
                System.err.println("[MockResponsesFactory] Position " + pos + " is not a valid cargo hold.");
            }
        }
    }



}