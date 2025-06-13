// Progetto completo in JavaFX che rispecchia la TUI
// File: JavaFXView.java

package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.polimi.ingsw.galaxytrucker.annotations.NeedsToBeChecked;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.CrewInitUpdate;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.updates.PhaseUpdate;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Dialogs.ConfirmDialogController;
import org.polimi.ingsw.galaxytrucker.view.View;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static void playWavSoundEffect(String sound){
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
        //Not used for GUI
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
    }

    @Override
    public void toShowCurrentMenu() {
        //Non utilizzato perchè sono sempre visibili i menu della Gui
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
        //Gestite con sottomenu prefatti e controller di quelle pagine specifici
    }

    @Override
    public void handlePhaseUpdate(PhaseUpdate update) {
        System.out.println("DEBUG: handlePhaseUpdate "+update.getState().name());
        //Useful if the ClientController does not invoke directly a showSomething method specific for menu
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
    public void askFinishBuilding() {
        controller.handleBuildingMenuChoice("j");
    }

    @Override
    public void showcheckShipMenu() {
        //Gestita internamente a BuildingController in base a gamePhase
    }

    @Override
    public void showembarkCrewMenu() {
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
    public void chooseCrew(Ship myShip) throws ExecutionException, InterruptedException, IOException {
        //Inizializza il crew init update
        crewInitUpdate = new CrewInitUpdate();
        //metti già 2 umani a tutte le posizioni non vicine a LFS
        ComponentNameVisitor namevisitor = new ComponentNameVisitor();
        Slot[][] shipboard =  mymodel.getMyInfo().getShip().getShipBoard();

        //Go over each Slot of the grid
        for (int x = 0; x < shipboard.length; x++) {
            for (int y = 0; y < shipboard[x].length; y++) {

                Tile tile = shipboard[x][y].getTile();

                if (tile != null && tile.getMyComponent().accept(namevisitor) == "ModularHousingUnit" &&
                        ( !Util.checkNearLFS(new Position(x, y), AlienColor.BROWN, mymodel.getMyInfo().getShip()) &&
                                !Util.checkNearLFS(new Position(x, y), AlienColor.PURPLE, mymodel.getMyInfo().getShip()))) {

                    //Editare a giro Crew tra varie possibilità e tenere aggiornato CrewInitUpdate
                    ((GuiJavaFx) controller.getView()).editPositionCrew(x, y);
                    //Redraw

                }
            }
        }

        showShip(mymodel.getMyInfo().getShip(), mymodel.getMyInfo().getNickName());

    }

    public void editPositionCrew(int x,int y){
        ModularHousingUnit currentHousingUnit = ((ModularHousingUnit) mymodel.getMyInfo().getShip().getShipBoard()[x][y].getTile().getMyComponent());
        //modificare nella SHIP Locale
        if(Util.checkNearLFS(new Position(x,y), AlienColor.BROWN,mymodel.getMyInfo().getShip()) && Util.checkNearLFS(new Position(x,y), AlienColor.PURPLE,mymodel.getMyInfo().getShip())){
            //Vicino a entrambi
            if(currentHousingUnit.getNPurpleAlien() == 0 && currentHousingUnit.getNBrownAlien() == 0){
                //Ci sono umani vado a viola
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.addPurpleAlien();
                currentHousingUnit.setAlienColor(AlienColor.PURPLE);
            }
            else if(currentHousingUnit.getNPurpleAlien() == 1){
                //C'è viola vado a marrone
                currentHousingUnit.removePurpleAlien();
                currentHousingUnit.addBrownAlien();
                currentHousingUnit.setAlienColor(AlienColor.BROWN);
            }
            else{
                //C'è marrone vado a umani
                currentHousingUnit.removeBrownAlien();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.setAlienColor(AlienColor.EMPTY);
            }
        }
        else if(Util.checkNearLFS(new Position(x,y), AlienColor.PURPLE,mymodel.getMyInfo().getShip())){
            //Vicino a viola
            if(currentHousingUnit.getNPurpleAlien() == 0){
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.addPurpleAlien();
                currentHousingUnit.setAlienColor(AlienColor.PURPLE);
            }
            else{
                currentHousingUnit.removePurpleAlien();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.setAlienColor(AlienColor.EMPTY);
            }

        }
        else if(Util.checkNearLFS(new Position(x,y), AlienColor.BROWN,mymodel.getMyInfo().getShip())){
            //Vicino a marrone
            if(currentHousingUnit.getNBrownAlien() == 0){
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.removeCrewMember();
                currentHousingUnit.addBrownAlien();
                currentHousingUnit.setAlienColor(AlienColor.BROWN);
            }
            else{
                currentHousingUnit.removeBrownAlien();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.addHumanCrew();
                currentHousingUnit.setAlienColor(AlienColor.EMPTY);
            }
        }
        else{
            currentHousingUnit.addHumanCrew();
            currentHousingUnit.addHumanCrew();
        }

        Position position = new Position(x,y);
        for(int i=0; i< crewInitUpdate.getCrewPos().size() ; i++){
            if(crewInitUpdate.getCrewPos().get(i).getKey().equals(position)){
                crewInitUpdate.getCrewPos().remove(i);
            }
        }
        crewInitUpdate.addCrewPos( new Pair<>(position,currentHousingUnit.getAlienColor()));

    }

    public void confirmCrew(){
        //Senda l'update
        try {
            controller.handleCrewInitUpdate(crewInitUpdate);
            System.out.println("CrewInitSent");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Flight.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    FlightController pageController = loader.getController();
                    pageController.initialSetup(this, controller, mymodel, primaryStage, musicManager);
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



    //NON DOVREBBE ESSERE MAI UTILIZZATO IN TEORIA
    @Override
    public void askFlightBoardPosition(ArrayList<Integer> validPositions, int id) throws ExecutionException, InterruptedException, IOException {
        Platform.runLater(() -> {
            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(validPositions.get(0), validPositions);
            dialog.setTitle("Scegli la posizione");
            dialog.setHeaderText("Scegli la posizione di partenza al decollo");
            dialog.showAndWait().ifPresent(pos -> {
                try {
                    controller.getClient().sendMessage(new org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.responses.AskPositionResponse(id, pos));
                } catch (Exception e) {
                    showGenericMessage("Errore nell'inviare la posizione: " + e.getMessage());
                }
            });
        });
    }


    @Override
    public void askSelectPlanetChoice(HashMap<Integer, Planet> landablePlanets) {
        System.out.println("Debug: askSelectPlanetChoice");
        ((FlightController)actualPageController).handlePlanetChoice(landablePlanets);
    }

    @Override
    public void showGenericMessage(String message) {
        Platform.runLater(() -> {
            actualPageController.ShowGenericMessage(message);
            //Ogni singolo page controller gestisce come mostrare
        });
    }

    @Override
    public void showWaitOtherPlayers() {
        //Devo dire a quelli di fase di gioco di mostrare un overlay di attesa
        Platform.runLater(() -> {
            ((GenericGamePhaseSceneController) actualPageController).showWaitOtherPlayers();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
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
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Scores.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                ScoresController pageController = loader.getController();
                pageController.initialSetup(this, controller, mymodel, primaryStage, musicManager);
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
        //Accetta sempre... massimo poi scarta tutte le merci e via
        controller.sendCollectRewardsResponse(true);
    }


    @NeedsToBeChecked
    @Override
    public void showTimerInfos() {
        //Todo
    }

    @Override
    public void showYouAreNowSpectating() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Dialogs/ConfirmDialog.fxml"));
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
    }
}
