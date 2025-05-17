// Progetto completo in JavaFX che rispecchia la TUI
// File: JavaFXView.java

package org.polimi.ingsw.galaxytrucker.view.Gui;

import com.sun.java.accessibility.util.GUIInitializedListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.SERVER_INFO;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs.ConfirmDialogController;
import org.polimi.ingsw.galaxytrucker.view.View;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GuiJavaFx implements View {

    private final Stage primaryStage;
    private  Scene primaryScene;
    private final ClientController controller;
    private GenericSceneController actualPageController;
    private MusicManager musicManager;
    private Boolean firstTimeMainMenu = true;


    public GuiJavaFx(Stage primaryStage, ClientController controller,Scene primaryScene) {
        this.primaryStage = primaryStage;
        this.controller = controller;
        this.primaryScene = primaryScene;
        musicManager= new MusicManager();
        primaryStage.setOnCloseRequest(event -> {
            playWavSoundEffect("nenenee.wav");
            if (ShowCustomConfirmDialog("Vuoi davvero andartene?")) {
                CloseApplication();
            } else {
                event.consume(); // annulla la chiusura
            }
        });
    }

    static void playWavSoundEffect(String sound){
        try {
            InputStream raw = MainMenuController.class.getResourceAsStream("/org/polimi/ingsw/galaxytrucker/Sounds/SoundEffects/"+sound);
            if (raw == null) {
                throw new IllegalArgumentException("File audio non trovato!");
            }

            // Wrappa in BufferedInputStream
            BufferedInputStream bufferedIn = new BufferedInputStream(raw);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean ShowCustomConfirmDialog( String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Conferma");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(page));

            ConfirmDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMessage(message);

            dialogStage.showAndWait();
            return controller.isConfirmed();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void CloseApplication(){
        musicManager.stopBackgroundMusic();
        primaryStage.close();
        Platform.exit();
        System.exit(0);
    }

    //<editor-fold desc="FOLD: LoginConnect">
    public void askServerInfo() {

        //TEST caricare una pagina qualunque per vedere formato
//            System.out.println("DEBUG: askServerInfo");
//            //1-Prima caricare FXML
//            Parent root;
//            FXMLLoader loader;
//            try{
//                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/ListLobby.fxml"));
//                root = loader.load();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
//            ListLobbyController pageController = loader.getController();
//            pageController.initialSetup(controller);
//            Scene scene = new Scene(root);
//           // primaryStage.setFullScreen(true);
//           // primaryStage.setMaximized(true);
//            primaryStage.setScene(scene);

        Platform.runLater(() -> {
            System.out.println("DEBUG: askServerInfo");

            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/LoginConnect.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                LoginConnectController pageController = loader.getController();
                pageController.initialSetup(this,controller,primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
    @Override
    public void askNickname() {
        System.out.println("DEBUG: askNickname");
        // now embedded inside LoginConnect scene, this method can be left empty or removed
        // kept for interface compliance
    }
    //</editor-fold>



    @Override
    public void forceReset() {

    }



    @Override
    public void askJoinOrCreateRoom() {
        System.out.println("DEBUG: askJoinOrCreateRoom");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/MainMenu.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                MainMenuController pageController = loader.getController();
                pageController.initialSetup(this,controller,primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Di default passato a FullScreen quandoe entra il Main Menu
            if(firstTimeMainMenu){
                firstTimeMainMenu = false;
                primaryStage.setFullScreen(true);
            }
            //Todo Riattivare musica
           // musicManager.playBackgroundMusic("CRMIntroMenu.wav",true);
        });
        /*Platform.runLater(() -> {
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
        });*/
    }

    @Override
    public void askCreateRoom() {
        System.out.println("DEBUG: askJoinOrCreateRoom");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/CreateLobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                CreateLobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

            /*VBox layout = new VBox(10);
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
            primaryStage.setScene(new Scene(layout, 300, 200));*/

    }

    @Override
    public void askRoomCode() {
        // now embedded inside showLobbies scene, this method can be left empty or removed
        // kept for interface compliance

    }


@Override
public void showLobbies(List<LobbyInfo> lobbies) {
    System.out.println("DEBUG: askJoinOrCreateRoom");
    Platform.runLater(() -> {
        Parent root;
        FXMLLoader loader;
        try {
            //1-Prima caricare FXML
            loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/ListLobby.fxml"));
            root = loader.load();
            //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
            ListLobbyController pageController = loader.getController();
            pageController.initialSetup(this,controller,primaryStage,musicManager);
            pageController.UpdateLobbyList(lobbies);
            actualPageController = pageController;
            //3-impostare la nuova root alla scena principale
            primaryScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    /*
    Platform.runLater(() -> {
        //Può passare lista lobby a controller pagina da metodo inizializzazione
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
    */

}

    @Override
    public void toShowCurrentMenu() {

    }



    @Override
    public void showPlayerJoined(PlayerInfo playerInfo) {
        showGenericMessage("Player joined: " + playerInfo);
    }

    @Override
    public void showPlayersLobby( PlayerInfo myinfo , ArrayList<PlayerInfo> playerInfo) {
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
    public void askShowFaceUpTiles()  {

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
    public void showFlightBoard(FlightBoard flightBoard,ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo) {

    }

    @Override
    public void showCurrentAdventureCard() {

    }

    @Override
    public void showFlightMenu() {

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
    public void askSelectPlanetChoice(ArrayList<Planet> planetChoices) {

    }

    @Override
    public void showGenericMessage(String message) {
        Platform.runLater(() -> {
            actualPageController.ShowGenericMessage(message);
            //Ogni page controller gestisce come mostrare
            // Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
           // alert.showAndWait();
        });
    }

    @Override
    public void askLoadGoodChoice() {

    }

    @Override
    public void askSelectGoodToLoad(ArrayList<Good> goods, Ship myShip) {

    }

    @Override
    public void askSelectGoodToDiscard(Ship myShip) {

    }

    @Override
    public void showEndGame(ArrayList<PlayerScore> scores) {

    }

    @Override
    public void askCollectRewards() {

    }
}
