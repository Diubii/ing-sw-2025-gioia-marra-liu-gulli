package org.polimi.ingsw.galaxytrucker.view.Gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleLobbyInfoController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleShipController;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BuildingController extends GenericGamePhaseSceneController {


    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    @FXML private StackPane myShipZone;

    private ArrayList<SingleShipController> shipControllers;

    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController,ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

        shipControllers = new ArrayList<>();

        //Mettere tutti sottoElementi
        //MyShip e anche altre poi in loop
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleShip.fxml"));
            Parent shipNode = loader.load();

            SingleShipController controller = loader.getController();
            controller.initialize(mymodel.getMyInfo().getNickName(),myShipZone);
            shipControllers.add(controller);

            myShipZone.getChildren().add(shipNode);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }




    @Override
    public void ShowGenericMessage(String message) {

    }


    @Override
    public void showShip(Ship ship, String Nickname) {

        //Riferimenti / elenco di tutte le "Single ship"
        //Scorre lista fino a nickname equivalente e poi si chiama
        // zUtils.showShipInGrid(ship,singleShip giusta.getGrid);

        //FOR per tutti
        //Qui fatto solo per mia momentaneamente
        if(shipControllers.get(0).getNicknameOfPlayer().equals(Nickname)) {

            //TEST STAMPA DA TOGLIERE
            Ship testShip = new Ship(false);

            //Prendo lista tiles e metto in ship per testare
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<Tile> tiles = new ArrayList<>();
            try{
                FileInputStream fis = new FileInputStream("src/main/resources/tiledata.json");
                tiles = mapper.readValue(fis, new TypeReference<ArrayList<Tile>>(){});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Good exGood = new Good(Color.BLUE);
            ((GenericCargoHolds)tiles.get(18).getMyComponent()).loadGood(exGood);
            ((GenericCargoHolds)tiles.get(18).getMyComponent()).loadGood(exGood);
            ((GenericCargoHolds)tiles.get(18).getMyComponent()).loadGood(exGood);


            try{
                for(int i =0; i<7; i++){
                    for(int j =0; j<5; j++){
                        if(j!= 3) {
                            testShip.putTile(tiles.get(i * 5 * j), new Position(j, i));
                        }
                    }
                }

                testShip.putTile(tiles.get(18),new Position(3,0));
                testShip.putTile(tiles.get(54),new Position(3,1));
                testShip.putTile(tiles.get(64),new Position(3,2));
                testShip.putTile(tiles.get(93),new Position(3,3));
                testShip.putTile(tiles.get(152),new Position(4,3));
                testShip.putTile(tiles.get(136),new Position(5,3));
                testShip.putTile(tiles.get(137),new Position(6,3));

                testShip.getShipBoard()[0][3].getTile().rotate(90);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            zUtils.showShipInGrid(testShip, shipControllers.get(0).getShipGrid());
            ShipPrintUtils.printShip(testShip);
        }
        else{

        }
    }


}
