// Progetto completo in JavaFX che rispecchia la TUI
// File: JavaFXView.java

package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GuiJavaFx implements View {

    private final Stage primaryStage;
    private final ClientController controller;

    public GuiJavaFx(Stage primaryStage, ClientController controller) {
        this.primaryStage = primaryStage;
        this.controller = controller;
    }


    public void askServerInfo() {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            TextField addressField = new TextField("localhost");
            TextField portField = new TextField(controller.getIsSocket() ? "5000" : "1099");
            Button connectButton = new Button("Connect");

            connectButton.setOnAction(e -> {
                String address = addressField.getText().trim();
                int port;
                try {
                    port = Integer.parseInt(portField.getText().trim());
                    controller.handleServerInfo(new SERVER_INFO(address, port));
                } catch (NumberFormatException ex) {
                    showGenericMessage("Invalid port number.");
                    return;
                }
            });

            layout.getChildren().addAll(
                    new Label("Server Address:"),
                    addressField,
                    new Label("Server Port:"),
                    portField,
                    connectButton
            );

            Scene scene = new Scene(layout, 300, 200);
            primaryStage.setScene(scene);
        });
    }


    @Override
    public void forceReset() {

    }

    @Override
    public void askNickname() {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            Label label = new Label("Enter your nickname:");
            TextField input = new TextField();
            Button submit = new Button("Submit");
            submit.setOnAction(e -> {
                try {
                    controller.handleNicknameInput(input.getText().trim());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ExecutionException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
            layout.getChildren().addAll(label, input, submit);
            primaryStage.setScene(new Scene(layout, 300, 150));
        });
    }

    @Override
    public void askJoinOrCreateRoom() {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            Button create = new Button("Create Room");
            Button join = new Button("Join Room");
            create.setOnAction(e -> {
                try {
                    controller.handleCreateOrJoinChoice("a");
                } catch (ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            });
            join.setOnAction(e -> {
                try {
                    controller.handleCreateOrJoinChoice("b");
                } catch (ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
            });
            layout.getChildren().addAll(new Label("Choose an option:"), create, join);
            primaryStage.setScene(new Scene(layout, 300, 150));
        });
    }

    @Override
    public void askCreateRoom() {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            TextField players = new TextField();
            players.setPromptText("Max Players (2-4)");
            CheckBox learning = new CheckBox("Learning Match");
            Button create = new Button("Create");
            create.setOnAction(e -> {
                try {
                    int max = Integer.parseInt(players.getText());
                    controller.handleCreateChoice(max, learning.isSelected());
                } catch (Exception ex) {
                    showGenericMessage("Invalid input: " + ex.getMessage());
                }
            });
            layout.getChildren().addAll(players, learning, create);
            primaryStage.setScene(new Scene(layout, 300, 200));
        });
    }

    @Override
    public void askRoomCode() {
        // now embedded inside showLobbies scene, this method can be left empty or removed
        // kept for interface compliance

    }


    @Override
    public void showLobbies(List<LobbyInfo> lobbies) {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);

            Label instruction = new Label("Click on a lobby to join it, or enter an ID manually:");
            layout.getChildren().add(instruction);

            for (LobbyInfo info : lobbies) {
                Button b = new Button("Lobby " + info.getLobbyID() + ": " + info.getHost());
                b.setOnAction(e -> controller.handleJoinChoice(info.getLobbyID()));
                layout.getChildren().add(b);
            }

            HBox manualJoin = new HBox(10);
            TextField roomIdField = new TextField();
            roomIdField.setPromptText("Enter Room ID");
            Button joinBtn = new Button("Join");
            joinBtn.setOnAction(e -> {
                try {
                    int id = Integer.parseInt(roomIdField.getText().trim());
                    controller.handleJoinChoice(id);
                } catch (NumberFormatException ex) {
                    showGenericMessage("Invalid room ID format.");
                }
            });
            manualJoin.getChildren().addAll(roomIdField, joinBtn);
            layout.getChildren().add(manualJoin);

            primaryStage.setScene(new Scene(layout, 400, 400));
        });
    }

    @Override
    public void toShowCurrentMenu() {

    }


    @Override
    public void showPlayerJoined(PlayerInfo playerInfo) {
        showGenericMessage("Player joined: " + playerInfo);
    }

    @Override
    public void showPlayersLobby(PlayerInfo myinfo, ArrayList<PlayerInfo> playerInfo) {
        //showGenericMessage("Player joined: " + playerInfo);
    }

    @Override
    public void handleChoiceForPhase(GameState phase) {

    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate update) {
        showGenericMessage("Phase changed: " + update.getState().name());
    }

    @Override
    public void showBuildingMenu() {
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            String[] options = {"a) Fetch Ship", "d) Draw Tile", "e) Show Tile", "f) Rotate", "g) Move", "h) Place", "j) Finish"};
            for (String op : options) {
                Button b = new Button(op);
                b.setOnAction(e -> controller.handleBuildingMenuChoice(op.substring(0, 1)));
                layout.getChildren().add(b);
            }
            primaryStage.setScene(new Scene(layout, 400, 300));
        });
    }

    @Override
    public void showFaceUpTiles() {

    }

    @Override
    public void FetchMyShip() {
        return;
    }

    @Override
    public void askShowFaceUpTiles() {

    }

    @Override
    public void askFetchShip() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void askRotation() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void askPosition() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void askViewAdventureDecks() {

    }

    @Override
    public void showTile(Tile tile) {
        showGenericMessage("Tile: " + tile.getId() + ", Type: " + tile.getMyComponent().getClass().getSimpleName());
    }

    @Override
    public void askChooseTile() {

    }

    @Override
    public void askPickOrPlaceReservedTile(boolean isPicking) {

    }

    @Override
    public void askDrawTile() {
        controller.handleBuildingMenuChoice("d");
    }

    @Override
    public void askTilePlacement() {
        controller.handleBuildingMenuChoice("h");
    }

    @Override
    public void askFinishBuilding() {
        controller.handleBuildingMenuChoice("j");
    }

    @Override
    public void showcheckShipMenu() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void showembarkCrewMenu() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void askRemoveTile(Ship ship) {

    }

    @Override
    public void chooseComponent(Ship myShip, ActivatableComponent component) throws ExecutionException, InterruptedException {

    }

    @Override
    public void chooseDiscardCrew(Ship myShip, int nCrewToDiscard) throws ExecutionException, InterruptedException {

    }

    @Override
    public void chooseTroncone(ArrayList<Ship> tronconi) throws ExecutionException, InterruptedException {

    }

    @Override
    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {

    }

    @Override
    public void askDrawCard() {

    }

    @Override
    public void askActivateAdventureCard() {

    }

    @Override
    public void showFlightBoard(FlightBoard flightBoard, ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo) {

    }

    @Override
    public void showCurrentAdventureCard() {

    }

    @Override
    public void showShip(Ship targetShipView) {
        return;
    }

    @Override
    public void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException {
        Platform.runLater(() -> {
            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(validPositions.get(0), validPositions);
            dialog.setTitle("Select Position");
            dialog.setHeaderText("Choose your flight board position");
            dialog.showAndWait().ifPresent(pos -> {
                try {
                    controller.getClient().sendMessage(new org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse(id, pos));
                } catch (Exception e) {
                    showGenericMessage("Error sending position: " + e.getMessage());
                }
            });
        });
    }

    @Override
    public void askDiscardCrew(int nCrewToDiscard, Ship myShip) {

    }

    @Override
    public void askSelectPlanetChoice(ArrayList<Planet> planetChoices) {

    }

    @Override
    public void askLoadGood(Planet selectedPlanet, Ship myShip) {

    }

    @Override
    public void showGenericMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.showAndWait();
        });
    }

    @Override
    public void askLoadGoodChoice() {

    }

    @Override
    public void askSelectGoodToLoad(Planet selectedPlanet, Ship myShip) {

    }

    @Override
    public void askSelectGoodToDiscard(Planet selectedPlanet, Ship myShip) {

    }

    @Override
    public void showEndTurnMenu(boolean amLeader) {

    }

    @Override
    public void askEndTurnMenuChoice(boolean amLeader) {

    }
}
