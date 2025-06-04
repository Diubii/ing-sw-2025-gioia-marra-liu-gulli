// Progetto completo in JavaFX che rispecchia la TUI
// File: JavaFXView.java

package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
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
import java.util.concurrent.ExecutionException;

/**
 * MAIN "View controller" for the GUI, handles page switching and all interaction between different pages.
 */
public class GuiJavaFx implements View {

    private final Stage primaryStage;
    private  Scene primaryScene;
    //Controller
    private final ClientController controller;
    //Model
    private final ClientModel mymodel;
    //The current controller of the main node tree displayed
    private GenericSceneController actualPageController;

    private MusicManager musicManager;
    private Boolean firstTimeMainMenu = true;




    public GuiJavaFx(Stage primaryStage,Scene primaryScene, ClientController controller, ClientModel mymodel) {
        this.primaryStage = primaryStage;
        this.controller = controller;
        this.mymodel = mymodel;
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

        primaryScene.setOnKeyPressed(event -> {
            if(controller.getCurrentTileInHand() != null) {
                if (event.getCode() == KeyCode.Q) {
                    controller.rotateCurrentTile(-90);

                } else if (event.getCode() == KeyCode.E) {
                    controller.rotateCurrentTile(+90);

                }
            }
        });


    }



    @Override
    public Boolean autoShowUpdates() {
        return true;
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

//        //TEST caricare una pagina qualunque per vedere formato
//            System.out.println("DEBUG: askServerInfo");
//            //1-Prima caricare FXML
//            Parent root;
//            FXMLLoader loader;
//            try{
//                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Flight.fxml"));
//                root = loader.load();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
//            FlightController pageController = loader.getController();
//            pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
//           // primaryStage.setFullScreen(true);
//           // primaryStage.setMaximized(true);
//             primaryScene.setRoot(root);

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
                pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
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
                pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Di default passato a FullScreen quandoe entra il Main Menu
            if(firstTimeMainMenu){
                firstTimeMainMenu = false;
                //Todo Riattivare FullScreen Automatico
               // primaryStage.setFullScreen(true);
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
                pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
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
    System.out.println("DEBUG: showLobbies");
    Platform.runLater(() -> {
        Parent root;
        FXMLLoader loader;
        try {
            //1-Prima caricare FXML
            loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/ListLobby.fxml"));
            root = loader.load();
            //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
            ListLobbyController pageController = loader.getController();
            pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
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
        System.out.println("DEBUG: showPlayerJoined");
        ArrayList<PlayerInfo> app = new ArrayList<>();
        app.add(playerInfo);
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Lobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                LobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
                pageController.updatePlayersList(app);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        showGenericMessage("Player joined: " + playerInfo);
    }

    @Override
    public void showPlayersLobby( PlayerInfo myInfo,ArrayList<PlayerInfo> playerInfo) {
        //Todo: Quando chiamo questo metodo, a volte lo faccio solo perchè è entrato un nuovo giocatore
        //Todo: non perchè devo cambiare scena/pagina.
        //Todo: bisognerebbe fare in modo che se sono già su questa scena/pagina chiamo soltanto pagecontroller.updatePlayersList(playerInfo)
        //Todo: praticamente è la stessa differenza di pagine sincrone e asincrone per il web
        System.out.println("DEBUG: showPlayersLobby");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Lobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                LobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,mymodel,primaryStage,musicManager);
                pageController.updatePlayersList(playerInfo);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void handleChoiceForPhase(GameState phase) {

    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate update) {
        System.out.println("DEBUG: handlePhaseUpdate "+update.getState().name());
        //Fare rendering totali per tutto quel che serve di preparare quando carichi nuove pagine
        switch (update.getState()){
            case BUILDING_START:
                showBuildingMenu();
            break;
            case CREW_INIT:
                try {
                    chooseCrew(mymodel.getMyInfo().getShip());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            case BUILDING_END:
            case BUILDING_TIMER:
            case SHIP_CHECK:
                ((BuildingController)actualPageController).updateBuildingPageInterface(update.getState());
            break;
            case FLIGHT:
                showFlightMenu();
            break;
            case END:
               // showEndGame();
            break;
            case null, default: showGenericMessage("Phase changed: " + update.getState().name());
            break;
        }
    }

    @Override
    public void showBuildingMenu() {

        System.out.println("DEBUG: showBuildingMenu");
        if(!actualPageController.pageName().equals("BuildingPage")){
            Platform.runLater(() -> {
                Parent root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Building.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    BuildingController pageController = loader.getController();
                    pageController.initialSetup(this, controller, mymodel, primaryStage, musicManager);
                    actualPageController = pageController;
                    //Impostare tutto il rendering iniziale
                    for (PlayerInfo playerInfo : mymodel.getPlayerInfos()) {
                        showShip(playerInfo.getShip(), playerInfo.getNickName());
                    }
                    //3-impostare la nuova root alla scena principale
                    primaryScene.setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


        /*
        Platform.runLater(() -> {
            VBox layout = new VBox(10);
            String[] options = {"a) Fetch Ship", "d) Draw Tile", "e) Show Tile", "f) Rotate", "g) Move", "h) Place", "j) Finish"};
            for (String op : options) {
                Button b = new Button(op);
                b.setOnAction(e -> controller.handleBuildingMenuChoice(op.substring(0, 1)));
                layout.getChildren().add(b);
            }
            primaryStage.setScene(new Scene(layout, 400, 300));
        });*/
    }

    @Override
    public void handleFaceUpTilesUpdate(){
        showFaceUpTiles();
    }

    @Override
    public void showFaceUpTiles() {
        System.out.println("DEBUG: showFaceUpTiles");
        Platform.runLater(() -> {
            ((BuildingController) actualPageController).updateFaceUpTiles();
        });
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
    //Todo: rinominare a showInHandTile?
    public void showTile(Tile tile) {
        //Associare al cursore per poi poter rilasciare su nave in pratica
        //Chiamato solo per la inHandTile
        if(tile != null) {
            System.out.println("DEBUG: showTile");
            Platform.runLater(() -> {
                        //Gestisce il controller della pagina building:
                        ((BuildingController) actualPageController).showDrawnTile(tile);
                    });
            showGenericMessage("Tile: " + tile.getId() + ", Type: " + tile.getMyComponent().getClass().getSimpleName());
        }
        else{
            ((BuildingController) actualPageController).hideZonaPescata();
        }


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
        //Nulla semplicemente rimane con il menu di checkship
    }

    @Override
    public void showembarkCrewMenu() {
        showGenericMessage("Not yet implemented");
    }

    @Override
    public void askRemoveTile(Ship ship) {
        //Not implemented, used click event on tile
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

    private CrewInitUpdate crewInitUpdate;
    @Override
    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {
        //Inizializza il crew init update
        crewInitUpdate = new CrewInitUpdate();
    }

    public void editPositionCrew(int x,int y){
        //Verificare che sia una cabina
        //Verificare se ha un colore o un supporto vitale vicino mi sa con utils .checknearlfs
        //prendere se c'è già nell'update e scambiare tra umani e alieni
        //modificare nella SHIP Locale e farla ridisegnare
    }

    public void confirmCrew(){
        //Senda l'update
        try {
            controller.handleCrewInitUpdate(crewInitUpdate);
            System.out.println("CrewInitSent");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        System.out.println("DEBUG: showFlightMenu");
        if(!actualPageController.pageName().equals("Flight")){
            Platform.runLater(() -> {
                Parent root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Flight.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    FlightController pageController = loader.getController();
                    pageController.initialSetup(this, controller, mymodel, primaryStage, musicManager);
                    actualPageController = pageController;
                    //Impostare tutto il rendering iniziale
                   /* for (PlayerInfo playerInfo : mymodel.getPlayerInfos()) {
                        showShip(playerInfo.getShip(), playerInfo.getNickName());
                    }*/
                    //3-impostare la nuova root alla scena principale
                    primaryScene.setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Override
    public void showShip(Ship targetShipView, String Nickname) {
        System.out.println("DEBUG: showShip ");

        //showShip exists only in the pages of Building and Flight so it's possible to Cast the actualPage in the
        //GenericGamePhaseSceneController designed for those phases of actual gameplay
        Platform.runLater(() -> {
            ((GenericGamePhaseSceneController) actualPageController).showShip(targetShipView, Nickname);
        });
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
        //simile a lobby ma con gli scores quindi ok
    }

    @Override
    public void askCollectRewards() {

    }


    @NeedsToBeChecked
    @Override
    public void showTimerInfos() {

    }
}
