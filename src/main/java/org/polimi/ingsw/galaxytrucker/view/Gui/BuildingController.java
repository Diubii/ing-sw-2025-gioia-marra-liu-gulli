package org.polimi.ingsw.galaxytrucker.view.Gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;

import java.io.FileInputStream;
import java.util.ArrayList;

public class BuildingController extends GenericGamePhaseSceneController {


    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    @FXML private StackPane gridContainer;
    @FXML private GridPane myShipGrid;

    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController,ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;


        double aspectRatio = 7.0 / 5.0;
        gridContainer.widthProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        gridContainer.heightProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));





    }


    private void adjustGridPaneSize(double aspectRatio) {
        double containerWidth = gridContainer.getWidth();
        double containerHeight = gridContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) return;

        double newWidth, newHeight;

        if (containerWidth / containerHeight > aspectRatio) {
            newHeight = containerHeight;
            newWidth = newHeight * aspectRatio;
        } else {
            newWidth = containerWidth;
            newHeight = newWidth / aspectRatio;
        }

        myShipGrid.setMaxWidth(newWidth);
        myShipGrid.setPrefWidth(newWidth);


        myShipGrid.setMaxHeight(newHeight);
        myShipGrid.setPrefHeight(newHeight);
    }

    @Override
    public void ShowGenericMessage(String message) {

    }


    @Override
    public void showShip(Ship ship, String Nickname) {
        if(mymodel.getMyInfo().getNickName().equals(Nickname)) {
            //My SHIP


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

            zUtils.showShipInGrid(testShip, myShipGrid);
            ShipPrintUtils.printShip(testShip);
        }
        else{

        }
    }


}
