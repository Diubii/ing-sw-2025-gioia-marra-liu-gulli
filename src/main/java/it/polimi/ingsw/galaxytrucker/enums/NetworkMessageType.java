package it.polimi.ingsw.galaxytrucker.enums;


/**
 * Lists all possible types of network messages exchanged between client and server.
 * Covers requests, responses, and game state updates across all phases.
 */
public enum NetworkMessageType {
    ActivateAdventureCardRequest,

    ActivateAdventureCardResponse,
    ActivateComponentRequest,
    ActivateComponentResponse,
    AskPositionResponse,
    AskPositionUpdate,
    AskTimerInfoRequest,
    AskTrunkRequest,
    AskTrunkResponse,
    CheckShipStatusRequest,

    CheckShipStatusResponse,
    CollectRewardsRequest,
    CollectRewardsResponse,
    CreateRoomRequest,
    CreateRoomResponse,

    CrewInitUpdate,
    DecksUpdate,
    DiscardCrewMembersRequest,
    DiscardCrewMembersResponse,
    DiscardTileRequest,

    DiscardTileResponse,
    DrawAdventureCardRequest, //Flight
    DrawAdventureCardResponse,
    DrawTileRequest //Building
    ,
    DrawTileResponse,

    DrawnAdventureCardUpdate,
    EarlyLandingRequest,


    EndTimer,
    EndTurnUpdate,
    FaceUpTileUpdate,
    FinishBuildingRequest,
    FinishBuildingResponse,
    FlightBoardUpdate,

    FlipTimerRequest,
    FlipTimerResponse,
    GameEndUpdate,
    GameMessage,
    HeartbeatRequest,
    JoinRoomOptionsRequest,
    JoinRoomOptionsResponse,
    JoinRoomRequest,
    JoinRoomResponse,
    MatchInfoUpdate,
    NetworkMessage,
    NicknameRequest,
    NicknameResponse,
    PhaseUpdate,
    PlaceTileRequest,
    PlaceTileResponse,
    PlayerJoinedUpdate,
    PlayerKickedUpdate,
    PlayerLostUpdate,
    ReadyTurnRequest,
    SelectPlanetRequest,
    SelectPlanetResponse,
    SelectedPlanetUpdate,
    ServerInfo,
    ShipUpdate,
    TileDiscardedUpdate,
    TimerInfoResponse,

}
