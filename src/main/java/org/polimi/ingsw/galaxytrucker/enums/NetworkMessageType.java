package org.polimi.ingsw.galaxytrucker.enums;

public enum NetworkMessageType {
    NetworkMessage,

    //Lobby
    NicknameRequest, NicknameResponse,
    JoinRoomRequest, JoinRoomResponse,
    JoinRoomOptionsRequest, JoinRoomOptionsResponse,
    CreateRoomRequest, CreateRoomResponse,
    CloseConnectionRequest, CloseConnectionResponse,
    ChooseColorRequest, ChooseColorResponse,
    PlayerReadyRequest, PlayerReadyResponse,

    //Building
    DrawTileRequest, DrawTileResponse,
    FetchShipStatusRequest, FetchShipStatusResponse,
    PlaceTileRequest, PlaceTileResponse,
    DiscardTileRequest, DiscardTileResponse,
    ViewAdventureDeckRequest, ViewAdventureDeckResponse,
    FinishBuildingRequest, FinishBuildingResponse,

    //Flight
    DrawAdventureCardRequest, DrawAdventureCardResponse,
    FlightMoveRequest, FlightMoveResponse,
    LandOnPlanetRequest, LandOnPlanetResponse,
    PickPlanetGoodsRequest, PickPlanetGoodsResponse,
    ActivateComponentRequest, ActivateComponentResponse,
    DiscardCrewRequest, DiscardCrewResponse,
    DiscardGoodsRequest, DiscardGoodsResponse,
    ClaimDerelictRewardRequest, ClaimDerelictRewardResponse,
    PlayerEndFlightRequest, PlayerEndFlightResponse,

    ShipViewRequest, ShipViewResponse,

    //End Game
    EndGameRequest, EndGameResponse,

    //Updates
    PlayerJoinedUpdate,
    PlayerLeftUpdate,
    GameStartedUpdate,
    ShipViewUpdate,
    TileReturnedUpdate,
    FinishBuildingUpdate,
    BuildingPhaseEndUpdate,
    StartFlightPhaseUpdate,
    ForcedEndFlightPhaseUpdate,
    AllAdventureCardsDrawnUpdate,
    GameOverScoringUpdate,

    AbandonedShipUpdate,
    AbandonedStationUpdate,
    SmugglersUpdated,
    SlaversUpdate,
    PiratesUpdate,
    EpidemicUpdate,
    OpenSpaceUpdate,
    CombatZoneUpdate,
    MeteorSwarmUpdate,
    StardustUpdate,
    PlanetsUpdate,
}
