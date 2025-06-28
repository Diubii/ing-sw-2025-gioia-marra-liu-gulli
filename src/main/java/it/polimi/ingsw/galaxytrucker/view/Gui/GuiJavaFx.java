// Progetto completo in JavaFX che rispecchia la TUI
// File: JavaFXView.java

package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.enums.GameState;
import it.polimi.ingsw.galaxytrucker.enums.ViewType;
import it.polimi.ingsw.galaxytrucker.model.*;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import it.polimi.ingsw.galaxytrucker.model.game.TimerInfo;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs.ConfirmDialogController;
import it.polimi.ingsw.galaxytrucker.view.Gui.Dialogs.InfoDialogController;
import it.polimi.ingsw.galaxytrucker.view.View;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * MAIN "View controller" for the GUI, handles page switching and all interaction between different pages.
 */
public class GuiJavaFx implements View {

    private final Stage primaryStage;
    private  Scene primaryScene;
    //Controller
    private ClientController controller;
    //The current controller of the main node tree displayed
    private GenericSceneController actualPageController;

    private MusicManager musicManager;
    private Boolean firstTimeMainMenu = true;

    //Config variable to disable feature music and other setting during testing
    private final static Boolean testing = true;





    public GuiJavaFx(Stage primaryStage,Scene primaryScene ) {
        this.primaryStage = primaryStage;
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

    /**
     * Connects Mouse and Keyboard events to the clientController for rotating Tiles
     * @param controller
     */
    public void initializeController(ClientController controller) {
        this.controller = controller;
        primaryScene.setOnKeyPressed(event -> {
            if(controller.getCurrentTileInHand() != null) {
                if (event.getCode() == KeyCode.Q || event.getCode() == KeyCode.LEFT) {
                    controller.rotateCurrentTile(-90);

                } else if (event.getCode() == KeyCode.E || event.getCode() == KeyCode.RIGHT) {
                    controller.rotateCurrentTile(+90);

                }
            }
        });

        primaryScene.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY){
                controller.rotateCurrentTile(+90);
            }
        });
    }

    @Override
    public Boolean autoShowUpdates() {
        return true;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.GUI;
    }

    public static void playWavSoundEffect(String sound){
        try {
            InputStream raw = MainMenuController.class.getResourceAsStream("/it/polimi/ingsw/galaxytrucker/Sounds/SoundEffects/"+sound);
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

    /**
     * Shows a custom dialog for asking a Yes or No input to the user.
     * @param message
     * @return
     */
    public Boolean ShowCustomConfirmDialog( String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
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

    /**
     * Closes the app
     */
    public void CloseApplication(){
        musicManager.stopBackgroundMusic();
        primaryStage.close();
        Platform.exit();
        System.exit(0);
    }


    //<editor-fold desc="FOLD: LoginConnect">

    /**
     * Shows the interface to "login" and connect to the server
     */
    public void askServerInfo() {
        if(actualPageController == null || actualPageController.pageName() != "LoginConnectPage"){
            Platform.runLater(() -> {
                System.out.println("DEBUG: askServerInfo");

                Parent root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/LoginConnect.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    LoginConnectController pageController = loader.getController();
                    pageController.initialSetupSmall(this,primaryStage,musicManager);
                    actualPageController = pageController;
                    //3-impostare la nuova root alla scena principale
                    primaryScene.setRoot(root);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
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
        //Not used for GUI
    }


    /**
     * Shows the main Menu with the options to create a Lobby or Join one
     */
    @Override
    public void askJoinOrCreateRoom() {
        System.out.println("DEBUG: askJoinOrCreateRoom");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/MainMenu.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                MainMenuController pageController = loader.getController();
                pageController.initialSetup(this,controller,controller.getMyModel(),primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Di default passato a FullScreen quandoe entra il Main Menu
            if(firstTimeMainMenu){
                firstTimeMainMenu = false;
               if(!testing) primaryStage.setFullScreen(true);
            }
            if(!testing) musicManager.playBackgroundMusic("CRMIntroMenu.wav",true);
        });
    }

    /**
     * Shows the interface to create a new Lobby
     */
    @Override
    public void askCreateRoom() {
        System.out.println("DEBUG: askJoinOrCreateRoom");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/CreateLobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                CreateLobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,controller.getMyModel(),primaryStage,musicManager);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void askRoomCode() {
        // now embedded inside showLobbies scene, this method can be left empty or removed
        // kept for interface compliance
    }


    /**
     * Show the interface that displays the list of lobbies of the server
     * @param lobbies
     */
    @Override
    public void showLobbies(List<LobbyInfo> lobbies) {
        System.out.println("DEBUG: showLobbies");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/ListLobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                ListLobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,controller.getMyModel(),primaryStage,musicManager);
                pageController.UpdateLobbyList(lobbies);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void toShowCurrentMenu() {
        //Non utilizzato perchè sono sempre visibili i menu della Gui
    }


    /**
     * Shows a message that the player joined
     * @param playerInfo
     */
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
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Lobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                LobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,controller.getMyModel(),primaryStage,musicManager);
                pageController.updatePlayersList(app);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        showGenericMessage("Player joined: " + playerInfo.getNickName(),false);
    }

    /**
     * Shows the lobby interface with the specified list of players
     * @param myInfo
     * @param playerInfo
     */
    @Override
    public void showPlayersLobby( PlayerInfo myInfo,ArrayList<PlayerInfo> playerInfo) {
        System.out.println("DEBUG: showPlayersLobby");
        Platform.runLater(() -> {
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Lobby.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                LobbyController pageController = loader.getController();
                pageController.initialSetup(this,controller,controller.getMyModel(),primaryStage,musicManager);
                if(playerInfo.size() == 0){
                    playerInfo.add(myInfo);
                }
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
        //Gestite con sottomenu prefatti e controller di quelle pagine specifici
    }

    /**
     * Handles updates of the game phase changing the interface or signaling interface controllers to
     * modify their appearence. Starts a Thread for the Timer functionality to update their status every second.
     * @param update
     */
    @Override
    public void handlePhaseUpdate(PhaseUpdate update) {
        System.out.println("DEBUG: handlePhaseUpdate "+update.getState().name());
        //Useful if the ClientController does not invoke directly a showSomething method specific for menu
        switch (update.getState()){
            case BUILDING_START:
                showBuildingMenu();
            break;
            case BUILDING_TIMER:
            case CREW_INIT:
                try {
                    chooseCrew(controller.getMyModel().getMyInfo().getShip());
                } catch (ExecutionException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            case BUILDING_END:

            case SHIP_CHECK:
                ((BuildingController)actualPageController).updateBuildingPageInterface(update.getState());
            break;
            case FLIGHT:
                showFlightMenu();
            break;
            case null, default: showGenericMessage("Phase changed: " + update.getState().name(),false);
            break;
        }
    }

    /**
     * Shows the interface for the Building Phase
     */
    @Override
    public void showBuildingMenu() {
        Platform.runLater(() -> {
             System.out.println("DEBUG: showBuildingMenu");
             if(!actualPageController.pageName().equals("BuildingPage")){
                System.out.println("Debug: rifaccio il building menu");

                Parent root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Building.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    BuildingController pageController = loader.getController();
                    pageController.initialSetup(this, controller, controller.getMyModel(), primaryStage, musicManager);
                    actualPageController = pageController;
                    //Impostare tutto il rendering iniziale
                    for (PlayerInfo playerInfo : controller.getMyModel().getPlayerInfos()) {
                        showShip(playerInfo.getShip(), playerInfo.getNickName());
                    }
                    //3-impostare la nuova root alla scena principale
                    primaryScene.setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void handleFaceUpTilesUpdate(){
        showFaceUpTiles();
    }

    /**
     * Asks the BuildingController to update the tiles with the current model
     */
    @Override
    public void showFaceUpTiles() {
        System.out.println("DEBUG: showFaceUpTiles");
        Platform.runLater(() -> ((BuildingController) actualPageController).updateFaceUpTiles());
    }

    @Override
    public void showFinishedBuildingMenu() {
        showBuildingMenu();
    }

    @Override
    public void FetchMyShip() {
        //Automaticamente fatto a ogni ship update, non si può fare manualmente
    }

    @Override
    public void askShowFaceUpTiles()  {
        //Automaticamente fatto a ogni update, non si fa a mano
    }

    @Override
    public void askFetchShip() {
        //Automaticamente fatto a ogni ship update, non si può fare manualmente
    }

    @Override
    public void askRotation() {
        //Gestito con eventi di click
    }

    @Override
    public void askPosition() {
        //Gesito con eventi di click
    }

    @Override
    public void askViewAdventureDecks() {
        //Gestito con eventi di click
    }

    /**
     * Signals the Building Controller to show/update the tile currently in hand
     * @param tile
     */
    @Override
    public void showTile(Tile tile) {
        //Associare al cursore per poi poter rilasciare su nave in pratica
        //Chiamato solo per la inHandTile
        if(tile != null) {
            System.out.println("DEBUG: showTile");
            Platform.runLater(() -> {
                //Gestisce il controller della pagina building:
                ((BuildingController) actualPageController).showDrawnTile(tile);
                 showGenericMessage("Tile: " + tile.getId() + ", Type: " + tile.getMyComponent().getClass().getSimpleName(),false);
            });
        }
        else{
            Platform.runLater(() -> {
                ((BuildingController) actualPageController).hideZonaPescata();
            });
        }
    }

    @Override
    public void askChooseTile() {
        //Gestito con Eventi di click e Drag/Drop
    }

    @Override
    public void askPickOrPlaceReservedTile(boolean isPicking) {
        //Gestito con eventi di click e Drag/Drop
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
    public void showCheckShipMenu() {
        //Gestita internamente a BuildingController in base a gamePhase
    }

    @Override
    public void showEmbarkCrewMenu() {
        //Gestita internamente a BuildingController in base a gamePhase
    }

    @Override
    public void askRemoveTile(Ship ship) {
        //Not implemented, used click event on tile
        System.out.println("Debug: askRemoveTile");
    }

    @Override
    public void chooseComponent(Ship myShip, ActivatableComponent component) throws ExecutionException, InterruptedException {
        System.out.println("Debug: chooseComponent");
        ((FlightController)actualPageController).handleChooseComponent( component);
    }

    @Override
    public void chooseDiscardCrew(Ship myShip, int nCrewToDiscard) throws ExecutionException, InterruptedException {
        System.out.println("Debug: chooseDiscardCrew");
        //invoco metodo di FlightController
        ((FlightController) actualPageController).handleDiscradCrew(nCrewToDiscard);
    }

    @Override
    public void chooseTroncone(ArrayList<Ship> tronconi) throws ExecutionException, InterruptedException {
        System.out.println("Debug: chooseTroncone");
        //Accade in fase di Building e Flight quindi metti in Abstract e casta con quello.
        ((GenericGamePhaseSceneController)actualPageController).chooseTroncone(tronconi);
    }

    private CrewInitUpdate crewInitUpdate;
    @Override
    /**
     * Sets up the CrewInitUpdate
     */
    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {
        //Inizializza il crew init update
        crewInitUpdate = new CrewInitUpdate();
        //metti già 2 umani a tutte le posizioni non vicine a LFS
        ComponentNameVisitor namevisitor = new ComponentNameVisitor();
        Slot[][] shipboard =  controller.getMyModel().getMyInfo().getShip().getShipBoard();

        //Go over each Slot of the grid
        for (int x = 0; x < shipboard.length; x++) {
            for (int y = 0; y < shipboard[x].length; y++) {

                Tile tile = shipboard[x][y].getTile();

                if (tile != null && Objects.equals(tile.getMyComponent().accept(namevisitor), "ModularHousingUnit")) {

                    //Editare a giro Crew tra varie possibilità e tenere aggiornato CrewInitUpdate
                    ((GuiJavaFx) controller.getView()).editPositionCrew(x, y);
                    //Redraw

                }
            }
        }

        showShip(controller.getMyModel().getMyInfo().getShip(), controller.getMyModel().getMyInfo().getNickName());

    }

    /**
     * Edits the position in the CrewInitUpdate based on the defined rotations of possible crews
     * @param x
     * @param y
     */
    public void editPositionCrew(int x,int y){
        ModularHousingUnit currentHousingUnit = ((ModularHousingUnit) controller.getMyModel().getMyInfo().getShip().getShipBoard()[x][y].getTile().getMyComponent());

        int nBrownAlien = controller.getMyModel().getMyInfo().getShip().getNBrownAlien();
        int nPurpleAlien= controller.getMyModel().getMyInfo().getShip().getNPurpleAlien();

        //debug

       System.out.println("CABIN AT " + x + " " + y + " has brown ? ->" +  Util.checkNearLFS(new Position(x,y), AlienColor.BROWN,controller.getMyModel().getMyInfo().getShip()));
        System.out.println("CABIN AT " + x + " " + y + " has purple ? ->" +  Util.checkNearLFS(new Position(x,y), AlienColor.PURPLE,controller.getMyModel().getMyInfo().getShip()));


        //modificare nella SHIP Locale
        if(Util.checkNearLFS(new Position(x,y), AlienColor.BROWN,controller.getMyModel().getMyInfo().getShip()) && Util.checkNearLFS(new Position(x,y), AlienColor.PURPLE,controller.getMyModel().getMyInfo().getShip())){
            //Vicino a entrambi
            if(currentHousingUnit.getNPurpleAlien() == 0 && currentHousingUnit.getNBrownAlien() == 0 && nPurpleAlien == 0){
                //Ci sono umani vado a viola
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addPurpleAlien();
            }
            else if(currentHousingUnit.getNPurpleAlien() == 1 && nBrownAlien == 0){
                //C'è viola vado a marrone
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addBrownAlien();
            }
            else if( nBrownAlien == 0){
                //Ci sono umani, viola occupato e vado a marrone
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addBrownAlien();
            }
            else{
                //C'è marrone vado a umani
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addHumanCrew();
            }
        }
        else if(Util.checkNearLFS(new Position(x,y), AlienColor.PURPLE,controller.getMyModel().getMyInfo().getShip())){
            //Vicino a viola
            if(currentHousingUnit.getNPurpleAlien() == 0 && nPurpleAlien == 0){
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addPurpleAlien();
            }
            else{
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addHumanCrew();
            }

        }
        else if(Util.checkNearLFS(new Position(x,y), AlienColor.BROWN,controller.getMyModel().getMyInfo().getShip())){
            //Vicino a marrone
            if(currentHousingUnit.getNBrownAlien() == 0 && nBrownAlien == 0){
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addBrownAlien();
            }
            else{
                currentHousingUnit.removeAllCrew();
                currentHousingUnit.addHumanCrew();
            }
        }
        else{
            currentHousingUnit.addHumanCrew();
        }

        //Todo togli debug
        System.out.println("La cab ha: "+currentHousingUnit.getNBrownAlien()+" marroni "+currentHousingUnit.getNPurpleAlien()+" viola e "+currentHousingUnit.getNCrewMembers()+" membri crew in generale");


        Position position = new Position(x,y);
        crewInitUpdate.getCrewPos().removeIf(current -> current.getKey().equals(position));
        crewInitUpdate.addCrewPos( new Pair<>(position,currentHousingUnit.getAlienColor()));
    }

    /**
     * Asks the clientController to send the CrewInitUpdate
     */
    public void confirmCrew(){
        //Senda l'update
        controller.handleCrewInitUpdate(crewInitUpdate);
        System.out.println("CrewInitSent");
    }

    @Override
    public void askDrawCard() {
        //Mostra il deck girato con l'evento di click
        System.out.println("Debug: askDrawCard");
        ((FlightController)actualPageController).askDrawCard();
    }

    @Override
    public void askActivateAdventureCard() {
        //Todo
        //Chiamata a flight controller che tira fuori sottomenu
        System.out.println("Debug: askActivateAdventureCard");
        ((FlightController)actualPageController).handleAskActivateCard();
    }

    /**
     * Updates the flightboard
     * @param flightBoard
     * @param infoPlayers
     * @param myinfo
     */
    @Override
    public void showFlightBoard(FlightBoard flightBoard,ArrayList<PlayerInfo> infoPlayers, PlayerInfo myinfo) {
        System.out.println("Debug: showFlightboard");
        Platform.runLater(() -> {
            ((FlightController) actualPageController).updateBoard();
        });
    }

    /**
     * SelfExpl
     */
    @Override
    public void showCurrentAdventureCard() {
        System.out.println("Debug: showCurrentAdventureCard");
        Platform.runLater(() -> {
            ((FlightController) actualPageController).showCurrentAdventureCard();
        });

    }

    /**
     *  Changes the root of the scene with the flightMenu nodeTree
     */
    @Override
    public void showFlightMenu() {
        System.out.println("DEBUG: showFlightMenu");
        if(!actualPageController.pageName().equals("Flight")){
            Platform.runLater(() -> {
                Parent root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Flight.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    FlightController pageController = loader.getController();
                    pageController.initialSetup(this, controller, controller.getMyModel(), primaryStage, musicManager);
                    actualPageController = pageController;
                    //3-impostare la nuova root alla scena principale
                    primaryScene.setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        else{
            //Richiamato a ogni fine turno dal controller
            ((FlightController)actualPageController).showEndTurnMenu();
        }
    }


    /**
     * SelfExpl
     */
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
    public void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) {

        Platform.runLater(() -> {
            //FINE CERTA DI FASE BUILDING ANCHE DETTATA DAL SERVER IN CASO FINE TIMER:
            ((BuildingController)actualPageController).handleFinishBuilding();
            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(validPositions.get(0), validPositions);
            dialog.getDialogPane().setStyle("-fx-background-color: Navy;");
            dialog.setTitle("Scegli la posizione");
            dialog.setHeaderText("Scegli la posizione di partenza al decollo");
            //Niente bottone annulla che non ha senso averlo
            dialog.getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);

            dialog.setOnShown(ev -> {
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.setOnCloseRequest(closeEvent -> {
                    closeEvent.consume(); // Previene la chiusura predefinita
                    Integer selected = dialog.getSelectedItem(); // Prende la selezione corrente
                    try {
                        controller.getClient().sendMessage(new it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse(id, selected));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    stage.close(); // Chiude manualmente
                });
            });
            dialog.showAndWait().ifPresent(pos -> {
                try {
                    controller.getClient().sendMessage(new it.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse(id, pos));
                } catch (Exception e) {
                    showGenericMessage("Errore nell'inviare la posizione: " + e.getMessage(),false);
                }
            });
        });
    }


    @Override
    public void askSelectPlanetChoice(HashMap<Integer, Planet> landablePlanets) {
        System.out.println("Debug: askSelectPlanetChoice");
        ((FlightController)actualPageController).handlePlanetChoice(landablePlanets);
    }

    /**
     * If a message is important and needs to be shown in an alert it's shown otherwise it redirects generic messages to each page to handle,
     * @param message
     * @param important
     */
    @Override
    public void showGenericMessage(String message,Boolean important) {
        Platform.runLater(() -> {
            if(important){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/InfoDialog.fxml"));
                    Parent page = loader.load();

                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Comunicazione");
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.initStyle(StageStyle.UTILITY);
                    dialogStage.setResizable(false);
                    dialogStage.setScene(new Scene(page));

                    InfoDialogController controller = loader.getController();
                    controller.setDialogStage(dialogStage);
                    controller.setMessage(null,message);

                    dialogStage.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                actualPageController.ShowGenericMessage(message);
                //Ogni singolo page controller gestisce come mostrare
            }
        });
    }

    @Override
    public void showWaitOtherPlayers() {
        //Devo dire a quelli di fase di gioco di mostrare un overlay di attesa
        Platform.runLater(() -> {
            ((GenericGamePhaseSceneController) actualPageController).showWaitOtherPlayers(true);
        });
    }

    @Override
    public void askLoadGoodChoice() {
        System.out.println("Debug: askLoadGoodChoice");
        //Sempre come se accettasse. massimo poi decide subito di fare termina e scartare tutto
        askSelectGoodToLoad( controller.getMyModel().getUnplacedGoods(), controller.getMyModel().getMyInfo().getShip());

    }

    @Override
    public void askSelectGoodToLoad(ArrayList<Good> goods, Ship myShip) {
        System.out.println("Debug: askSelectGoodToLoad");
        ((FlightController)actualPageController).handleGoodsLoading(goods);
    }

    @Override
    public void askSelectGoodToDiscard(Ship myShip) {
        System.out.println("Debug: askSelectGoodToDiscard");
        //Realizzata in altro modo, i goods lasciati li semplicemente non appaiono nello shipupdateforgoods
        //quindi spariscono, e in ogni fase di caricamento posso scambiare i good come voglio.
    }

    @Override
    public void showEndGame(ArrayList<PlayerScore> scores) {
        //simile a lobby ma con gli scores quindi ok
        System.out.println("Debug: showEndGame");

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
                Parent page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("ATTENZIONE");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.setResizable(false);
                dialogStage.setScene(new Scene(page));

                ConfirmDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMessage("Che tu lo voglia o no la partita è terminata");

                dialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Scores.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                ScoresController pageController = loader.getController();
                pageController.initialSetup(this, controller, controller.getMyModel(), primaryStage, musicManager);
                pageController.updateScores(scores);
                actualPageController = pageController;
                //3-impostare la nuova root alla scena principale
                primaryScene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void askCollectRewards() {
        System.out.println("Debug: askCollectRewards");
        Platform.runLater(()->{
            if (ShowCustomConfirmDialog("Vuoi prendere la ricompensa perdendo i giorni di volo indicati? ")) {
                controller.sendCollectRewardsResponse(true);
            } else {
                controller.sendCollectRewardsResponse(false);
            }
        });

    }


    @NeedsToBeChecked
    @Override
    public void showTimerInfos(ArrayList<TimerInfo> timerInfos) {
        Platform.runLater(() -> {
            if(actualPageController.pageName().equals("BuildingPage")){
                ((BuildingController) actualPageController).showTimerInfo(timerInfos);
            }
        });
    }

    @Override
    public void showYouAreNowSpectating() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
                Parent page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("ATTENZIONE");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.setResizable(false);
                dialogStage.setScene(new Scene(page));

                ConfirmDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMessage("Che tu lo voglia o no ora sei uno spettatore");

                dialogStage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void autoShowShipInTui(Ship shipView, String Nickname) {

    }
}
