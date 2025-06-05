package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.Planets;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.common.NetworkMessages.requests.DrawAdventureCardRequest;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.*;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.FlightBoardPrintUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FlightController extends GenericGamePhaseSceneController {

    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    private SingleShipController shipController;
    private FlightBoardController flightBoardController;

    @FXML private ImageView cardView;
    @FXML private HBox imageBox;
    @FXML private HBox subMenu;
    @FXML private StackPane shipZone;
    @FXML private StackPane boardZone;
    @FXML private ImageView avatar1;
    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;
    @FXML private Label name1;
    @FXML private Label name2;
    @FXML private Label name3;
    @FXML private Label name4;
    //Todo: aggiungere crediti a player info e mostrarli di lato?
    @FXML private RadioButton radio1;
    @FXML private RadioButton radio2;
    @FXML private RadioButton radio3;
    @FXML private RadioButton radio4;



    @Override
    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

        //Inizializzazione di tutta lista di player e di default mia ship,a.
        List<RadioButton> radioButtons = List.of(radio1, radio2, radio3 , radio4);
        List<Label> names = List.of(name1,name2, name3, name4);
        List<ImageView> avatars = List.of(avatar1,avatar2, avatar3 , avatar4);

        for(int i =0 ; i< mymodel.getPlayerInfos().size(); i++){
            names.get(i).setText(mymodel.getPlayerInfos().get(i).getNickName());
            radioButtons.get(i).visibleProperty().set(true);
            if(mymodel.getPlayerInfos().get(i).getNickName().equals(mymodel.getMyInfo().getNickName())){
                radioButtons.get(i).selectedProperty().set(true);
            }
            switch (mymodel.getPlayerInfos().get(i).getColor()){
                case RED ->  avatars.get(i).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarRosso.png").toExternalForm()));
                case YELLOW ->  avatars.get(i).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarGiallo.png").toExternalForm()));
                case BLUE ->  avatars.get(i).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarBlu.png").toExternalForm()));
                case GREEN ->  avatars.get(i).setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarVerde.png").toExternalForm()));
            }
        }


        //CONFIGURAZIONE SHIP, con DEFAULT MIA
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
        Parent shipNode = null;
        try {
            shipNode = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        shipController = loader.getController();
        shipController.initialize(mymodel.getMyInfo().getNickName(),shipZone);

        shipZone.getChildren().add(shipNode);
        //Mostro la mia inizialmente
        showShip(mymodel.getMyInfo().getShip(),mymodel.getMyInfo().getNickName());

        //Binding per immagine deck/carta attuale
        cardView.fitWidthProperty().bind(imageBox.widthProperty().divide(1.5));
        cardView.fitHeightProperty().bind(imageBox.widthProperty().divide(1.3));

        //Inizializzazione FlightBoard
        flightBoardController = new FlightBoardController();
        flightBoardController.initialize(boardZone,clientController.getMyModel().getFlightBoard());
        FlightBoardPrintUtils.printFlightBoard(mymodel.getFlightBoard(),mymodel.getPlayerInfos(),mymodel.getMyInfo());

        //Inizializza il menu di inizio/Fine Turno
        Platform.runLater(() -> {
            //C'è altro oltre al layout di default (Altri menu left aperti)
            if(subMenu.getChildren().size() > 0 ){
                subMenu.getChildren().removeLast();
            }
            VBox root;
            FXMLLoader secondloader;
            try {
                //1-Prima caricare FXML
                secondloader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMavanzaTurno.fxml"));
                root = secondloader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                SMavanzaTurnoController pageController = secondloader.getController();
                pageController.initialize(clientController,subMenu);
                root.setMaxWidth(Double.MAX_VALUE);
                root.setMaxHeight(Double.MAX_VALUE);
                //3-impostare la nuova root alla scena principale
                HBox.setHgrow(root, Priority.ALWAYS);
                subMenu.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


    @Override
    public void ShowGenericMessage(String message) {

    }

    public void askDrawCard(){
        cardView.setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/cards/deck.png").toExternalForm()));
        cardView.setOnMouseClicked(event -> {
            DrawAdventureCardRequest request = new DrawAdventureCardRequest();
            try {
                clientController.getClient().sendMessage(request);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateBoard(){
        flightBoardController.updateBoard(mymodel.getFlightBoard());
    }

    public void showCurrentAdventureCard(){
        int id = mymodel.getCurrentAdventureCard().getID();
        cardView.setImage(new Image(getClass().getResource("/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/cards/GT-cards_"+id+".jpg").toExternalForm()));

    }

    public void handlePlanetChoice(ArrayList<Planet> planetChoices){
        //Qui mostra sotto menu
        Platform.runLater(() -> {
            //C'è altro oltre al layout di default (Altri menu left aperti)
            if(subMenu.getChildren().size() > 0 ){
                subMenu.getChildren().removeLast();
            }
            VBox root;
            FXMLLoader secondloader;
            try {
                //1-Prima caricare FXML
                secondloader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SMplanetChoice.fxml"));
                root = secondloader.load();
                //2-Poi imposare il Cotnroller se ne ha bisogno passando ad esempio il controller principale o lo stage o altro
                SMplanetChoiceController pageController = secondloader.getController();
                pageController.initialize(clientController,subMenu,planetChoices);
                root.setMaxWidth(Double.MAX_VALUE);
                root.setMaxHeight(Double.MAX_VALUE);
                //3-impostare la nuova root alla scena principale
                HBox.setHgrow(root, Priority.ALWAYS);
                subMenu.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void selected0(){
        updateShownShip(0);
    }
    public void selected1(){
        updateShownShip(1);
    }
    public void selected2(){
        updateShownShip(2);
    }
    public void selected3(){
        updateShownShip(3);
    }



    public void updateShownShip(int i){


        if(mymodel.getPlayerInfos().size() > i && mymodel.getPlayerInfos().get(i) != null) {
            if(mymodel.getMyInfo().getNickName().equals(mymodel.getPlayerInfos().get(i).getNickName())){
                showShip(mymodel.getMyInfo().getShip(),mymodel.getMyInfo().getNickName());
            }
            else{
                showShip(mymodel.getPlayerInfos().get(i).getShip(), mymodel.getPlayerInfos().get(i).getNickName());
            }

        }
    }

    @Override
    public void showShip(Ship ship, String Nickname) {

        boolean editable = false;
        if(Nickname.equals(mymodel.getMyInfo().getNickName())){
            editable = true;
        }


        zUtils.showShipInGrid(ship, shipController.getShipGrid(), clientController,editable,true);

    }

    @Override
    public String pageName() {
        return "Flight";
    }
}
