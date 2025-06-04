package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.GameState;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleShipController;

import java.io.IOException;

public class FlightController extends GenericGamePhaseSceneController {

    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    private SingleShipController shipController;
    @FXML private StackPane shipZone;


    @Override
    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

        //Inizializzazione di tutta lista di player e di default mia ship, unico ship controller con griglia unica.
        //Poi arriveranno show ship diversi cambiando il radio button
        //TODO in config iniziale di lista player metti selected te stesso controllando nick e oridine in playerinfos

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

    }

    //TODO metodi per gestione della Board direttamente qui
    //Si inizializza un Gridpane in base alla tipologia di gioco
    //Poi ci sono gli update in cui si gestisce ma direi direttamente qui


    @Override
    public void ShowGenericMessage(String message) {

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
        System.out.println("updateshipdi: "+i);

        if(mymodel.getPlayerInfos().size() > i && mymodel.getPlayerInfos().get(i) != null) {
            System.out.println("Nickname di updateShip: "+mymodel.getPlayerInfos().get(i).getNickName());
            showShip(mymodel.getPlayerInfos().get(i).getShip(), mymodel.getPlayerInfos().get(i).getNickName());
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
