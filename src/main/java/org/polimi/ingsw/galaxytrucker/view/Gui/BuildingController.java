package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildingController extends GenericGamePhaseSceneController {


    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    @FXML private StackPane myShipZone;

    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;
    @FXML private Label name2;
    @FXML private Label name3;
    @FXML private Label name4;
    @FXML private StackPane shipZone2;
    @FXML private StackPane shipZone3;
    @FXML private StackPane shipZone4;
    @FXML private FlowPane listaTiles;
    @FXML private ScrollPane scrollListaTiles;
    @FXML private StackPane StackCenterMenu;
    @FXML private StackPane StackLeftMenu;
    @FXML private StackPane  mainStackPane;
    @FXML private HBox learningMatchOverlay;
    @FXML private HBox learningMatchOverlay2;
    @FXML private ImageView inHandTileImage;
    @FXML private Pane overlayPane;
    @FXML private ImageView asideTile1;
    @FXML private ImageView asideTile2;
    @FXML private Button BtnFinishBuilding;

    private ArrayList<SingleShipController> shipControllers;
    private Boolean finishedBuilding;

    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController,ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;
        finishedBuilding = false;

        listaTiles.prefWidthProperty().bind(scrollListaTiles.widthProperty());

        shipControllers = new ArrayList<>();



        //Mettere tutti sottoElementi
        //Se learning match niente deck da spiare
        if(mymodel.isLearningMatch()){
            learningMatchOverlay.visibleProperty().set(true);
            learningMatchOverlay2.visibleProperty().set(true);
        }
        //MyShip e anche altre poi in loop
        int j = 0;
        List<StackPane> shipZones = List.of(shipZone2, shipZone3, shipZone4);
        List<Label> names = List.of(name2, name3, name4);
        List<ImageView> avatars = List.of(avatar2, avatar3 , avatar4);
        try {

            j = 0;
            for(int i = 0; i< mymodel.getPlayerInfos().size(); i++){

                if(mymodel.getPlayerInfos().get(i).getNickName().equals(mymodel.getMyInfo().getNickName())){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
                    Parent shipNode = loader.load();

                    SingleShipController controller = loader.getController();
                    controller.initialize(mymodel.getMyInfo().getNickName(),myShipZone);
                    shipControllers.add(controller);

                    myShipZone.getChildren().add(shipNode);
                }
                else{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
                    Parent shipNode = loader.load();

                    SingleShipController controller = loader.getController();
                    controller.initialize(mymodel.getPlayerInfos().get(i).getNickName(),shipZones.get(j));
                    shipControllers.add(controller);
                    shipZones.get(j).getChildren().add(shipNode);
                    names.get(j).setText(mymodel.getPlayerInfos().get(i).getNickName());
                    switch (mymodel.getPlayerInfos().get(i).getColor()){
                        case RED ->  avatars.get(j).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarRosso.png").toExternalForm()));
                        case YELLOW ->  avatars.get(j).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarGiallo.png").toExternalForm()));
                        case BLUE ->  avatars.get(j).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarBlu.png").toExternalForm()));
                        case GREEN ->  avatars.get(j).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarVerde.png").toExternalForm()));
                    }
                    j++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        scrollListaTiles.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY){
                if (clientController.getCurrentTileInHand() != null) {
                    // esegui le tue istruzioni qui
                    clientController.handleBuildingMenuChoice("h");
                    inHandTileImage.visibleProperty().set(false);
                }
                event.consume();
            }
        });

        mainStackPane.setOnMouseMoved(event -> {
            inHandTileImage.setLayoutX(event.getX() - inHandTileImage.getFitWidth() / 2);
            inHandTileImage.setLayoutY(event.getY() - inHandTileImage.getFitHeight() / 2);

        });

    }

    @Override
    public String pageName() {
        return "BuildingPage";
    }


    @Override
    public void ShowGenericMessage(String message) {
        if(clientController.getPhase() == GameState.SHIP_CHECK || clientController.getPhase() == GameState.CREW_INIT) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Comunicazione di servizio");
                alert.setHeaderText("Sig. " + clientController.getMyModel().getMyInfo().getNickName() + ":");
                alert.setContentText(message);

                // Mostra l'alert e aspetta che venga chiuso
                alert.showAndWait();
            });
        }

    }

    /**
     * Takes faceUp tiles from model and redraws them
     */
    public void updateFaceUpTiles(){
        listaTiles.getChildren().clear();
        System.out.println("Building controller DEBUG: showFaceUpTiles");
        //Todo handle resizing maybe?
         mymodel.getFaceUpTiles().forEach(tile -> {
             String tileIdVal = String.valueOf(tile.getId());
             String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
             Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
             ImageView imgView = new ImageView(img);
             imgView.fitWidthProperty().bind(listaTiles.widthProperty().divide(4.5));
             imgView.fitHeightProperty().bind(listaTiles.widthProperty().divide(4.5));
             imgView.setOnMouseClicked(event -> {
                 if(clientController.getCurrentTileInHand() == null){
                     clientController.handleChooseFaceUpTile(tile);
                 }
                 // Puoi fare qualsiasi altra azione qui
             });

             listaTiles.getChildren().add(imgView);
         });

    }


    public void pescaRandom(ActionEvent actionEvent){
        if(clientController.getCurrentTileInHand() == null){
            clientController.handleDrawFaceDownTile();
            GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        }
    }

    /**
     * Hides the overlay with the current inHand tile
     */
    public void hideZonaPescata(){
        inHandTileImage.setVisible(false);
    }


    /**
     * Shows the ship in the correct "slot" of the page based on the Nickname
     * @param ship
     * @param Nickname
     */
    @Override
    public void showShip(Ship ship, String Nickname) {
        Boolean details =false;
        Boolean editable = true;
        //Se ho finito e non sono oltre la fase di building non più editabile
        if(finishedBuilding && clientController.getPhase() != GameState.CREW_INIT && clientController.getPhase() != GameState.SHIP_CHECK) {
            editable= false;
        }

        if(clientController.getPhase() == GameState.CREW_INIT){
            details = true;
        }

        for( int i = 0; i<shipControllers.size(); i++){

            if(shipControllers.get(i).getNicknameOfPlayer().equals(Nickname)) {

                if (mymodel.getMyInfo().getNickName().equals(Nickname)) {

                    zUtils.showShipInGrid(mymodel.getMyInfo().getShip(), shipControllers.get(i).getShipGrid(), clientController,editable,details,null,null);

                } else {
                    zUtils.showShipInGrid(mymodel.getPlayerInfoByNickname(Nickname).getShip(), shipControllers.get(i).getShipGrid(),clientController,false,details,null,null);
                }
            }
        }

        //potrebbe essere uno ship update con questa modifica
        updateSetAsideTiles();

    }

    @Override
    public void chooseTroncone(ArrayList<Ship> tronconi) {
        //Fa uscire sotto menu in cui creo una shipView con radioButton per ogni ship
        Platform.runLater(() -> {
            ScrollPane root;
            FXMLLoader loader;
            try {
                //1-Prima caricare FXML
                loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMtronconi.fxml"));
                root = loader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                SMtronconiController pageController = loader.getController();
                pageController.initialize(clientController,tronconi,StackCenterMenu);
                root.setMaxWidth(Double.MAX_VALUE);
                root.setMaxHeight(Double.MAX_VALUE);
                //3-impostare la nuova root alla scena principale
                StackCenterMenu.getChildren().add(root);
                showShip(mymodel.getMyInfo().getShip(), mymodel.getMyInfo().getNickName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void showWaitOtherPlayers() {
        System.out.println("debug: showWaitOtherPlayers");

        StackCenterMenu.getChildren().removeLast();
        VBox root = null;
        FXMLLoader loader;
        try {
            //1-Prima caricare FXML
            loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/WaitOverlay.fxml"));
            root = loader.load();

            root.setMaxWidth(Double.MAX_VALUE);
            root.setMaxHeight(Double.MAX_VALUE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StackCenterMenu.getChildren().add(root);

    }


    public void finishBuilding(ActionEvent e){
        //Disable di tutto ciò che è interagibile aparte la clessidra in teoria
        clientController.handleBuildingMenuChoice("i");
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        BtnFinishBuilding.setDisable(true);
        finishedBuilding = true;
        HBox overlay = new HBox();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        StackCenterMenu.getChildren().add(overlay);
    }

    /**
     * Updates the secondary menus based on the current Game phase
     * @param state
     */
    public void updateBuildingPageInterface(GameState state){
        switch(state){
            case SHIP_CHECK:
                Platform.runLater(() -> {
                    if(StackCenterMenu.getChildren().size() > 1 ){
                        StackCenterMenu.getChildren().removeLast();
                    }
                    VBox root;
                    FXMLLoader loader;
                    try {
                        //1-Prima caricare FXML
                        loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMCheckShip.fxml"));
                        root = loader.load();
                        //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                        SMCheckShipController pageController = loader.getController();
                        pageController.initialize(clientController);
                        root.setMaxWidth(Double.MAX_VALUE);
                        root.setMaxHeight(Double.MAX_VALUE);
                        //3-impostare la nuova root alla scena principale
                        StackCenterMenu.getChildren().add(root);
                        showShip(mymodel.getMyInfo().getShip(), mymodel.getMyInfo().getNickName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            break;
            case CREW_INIT:
                Platform.runLater(() -> {
                    if(StackCenterMenu.getChildren().size() > 1 ){
                        StackCenterMenu.getChildren().removeLast();
                    }
                    VBox root;
                    FXMLLoader loader;
                    try {
                        //1-Prima caricare FXML
                        loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMLoadCrew.fxml"));
                        root = loader.load();
                        //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                        SMLoadCrewController pageController = loader.getController();
                        pageController.initialize(clientController,mainViewController,StackCenterMenu);
                        root.setMaxWidth(Double.MAX_VALUE);
                        root.setMaxHeight(Double.MAX_VALUE);
                        //3-impostare la nuova root alla scena principale
                        StackCenterMenu.getChildren().add(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showShip(mymodel.getMyInfo().getShip(), mymodel.getMyInfo().getNickName());
                });
            break;

            case null, default:
                break;
        }

    }

    public void viewMazzoUno(){
        viewMazzo(0);
    }

    public void viewMazzoDue(){
        viewMazzo(1);
    }

    public void viewMazzoTre(){
        viewMazzo(2);
    }

    /**
     * Shows the corresponding deck in an overlay
     * @param num
     */
    public void viewMazzo(int num){
        //Non fa un tubo, va bene per la Tui ma qui no
        if(clientController.viewAdventureCardDeck(num)){
            Platform.runLater(() -> {
                //C'è altro oltre al layout di default (Altri menu left aperti)
                if(StackLeftMenu.getChildren().size() > 1 ){
                    StackLeftMenu.getChildren().removeLast();
                }
                VBox root;
                FXMLLoader loader;
                try {
                    //1-Prima caricare FXML
                    loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMSpiedCards.fxml"));
                    root = loader.load();
                    //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                    SMSpiedCardsController pageController = loader.getController();
                    pageController.initialize(mymodel.getCardDecks().get(num),StackLeftMenu);
                    root.setMaxWidth(Double.MAX_VALUE);
                    root.setMaxHeight(Double.MAX_VALUE);
                    //3-impostare la nuova root alla scena principale
                    StackLeftMenu.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Shows the tile in the "inHand" slot that follows the cursor
     * @param tile
     */
    public void showDrawnTile(Tile tile){
        String tileIdVal = String.valueOf(tile.getId());
        String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
        Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());

        inHandTileImage.setImage(img);
        inHandTileImage.setRotate(tile.getRotation());
        inHandTileImage.setVisible(true);
        inHandTileImage.setFitHeight(100.00);
        inHandTileImage.setFitWidth(100.00);

    }

    public void pickedAside1(MouseEvent event){
        if(event.getButton() == MouseButton.PRIMARY){
            handlePickedAsideTile(0);
        }

    }

    public void pickedAside2(MouseEvent event){
        if(event.getButton() == MouseButton.PRIMARY){
            handlePickedAsideTile(1);
        }
    }

    /**
     * Handles interaction with pickAside slots for tiles,
     * based on their content and what is inHand decides if clicking means
     * drawing or placing.
     * @param pos
     */
    public void handlePickedAsideTile(int pos){
       //Se ho già finito di costruire non tocco più
        if(finishedBuilding == false) {
           if (clientController.getCurrentTileInHand() != null) {
               //Se ho in mano ed è vuota metto li
               if (clientController.getReservedTiles()[pos] == null) {
                   clientController.handlePickReservedTile(pos, false);
               }
           } else {
               //Se ho mano vuota e li c'è qualcosa la prendo
               if (clientController.getReservedTiles()[pos] != null) {
                   clientController.handlePickReservedTile(pos, true);
               }
           }
       }
    }

    /**
     * Redraws setAside tiles slots
     */
    public void updateSetAsideTiles(){
        List<ImageView> imageViews = List.of(asideTile1,asideTile2);
        for(int i =0; i< clientController.getMyShip().getAsideTiles().length; i++){
            if(clientController.getMyShip().getAsideTiles()[i] != null){
                String tileIdVal = String.valueOf(clientController.getMyShip().getAsideTiles()[i].getId());
                String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
                Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                imageViews.get(i).setImage(img);
                imageViews.get(i).setRotate(clientController.getMyShip().getAsideTiles()[i].getRotation());
            }
            else{
                String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/empty.jpg";
                Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                imageViews.get(i).setImage(img);
            }


        }


    }

}
