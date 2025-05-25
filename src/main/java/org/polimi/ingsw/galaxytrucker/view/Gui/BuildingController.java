package org.polimi.ingsw.galaxytrucker.view.Gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.CentralHousingUnit;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.GenericCargoHolds;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.ModularHousingUnit;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericGamePhaseSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleLobbyInfoController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleShipController;
import org.polimi.ingsw.galaxytrucker.view.Tui.util.ShipPrintUtils;


import java.io.FileInputStream;
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
    @FXML private HBox menu2;

    private ArrayList<SingleShipController> shipControllers;

    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController,ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

        menu2.visibleProperty().set(false);
        listaTiles.prefWidthProperty().bind(scrollListaTiles.widthProperty());

        shipControllers = new ArrayList<>();

        //Mettere tutti sottoElementi
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





    }




    @Override
    public void ShowGenericMessage(String message) {

    }

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
             listaTiles.getChildren().add(imgView);
         });

    }

    public void pescaRandom(ActionEvent actionEvent){
        clientController.handleDrawFaceDownTile();
    }


    @Override
    public void showShip(Ship ship, String Nickname) {

        for( int i = 0; i<shipControllers.size(); i++){

            if(shipControllers.get(i).getNicknameOfPlayer().equals(Nickname)) {

                if (mymodel.getMyInfo().getNickName().equals(Nickname)) {

                    zUtils.showShipInGrid(mymodel.getMyInfo().getShip(), shipControllers.get(i).getShipGrid());

                    //TEST STAMPA DA TOGLIERE
                    Ship testShip = new Ship(false);

                    //Prendo lista tiles e metto in ship per testare
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList<Tile> tiles = new ArrayList<>();
                    try {
                        FileInputStream fis = new FileInputStream("src/main/resources/tiledata.json");
                        tiles = mapper.readValue(fis, new TypeReference<ArrayList<Tile>>() {
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Good exGood = new Good(Color.BLUE);
                    ((GenericCargoHolds) tiles.get(18).getMyComponent()).loadGood(exGood);
                    ((GenericCargoHolds) tiles.get(18).getMyComponent()).loadGood(exGood);
                    ((GenericCargoHolds) tiles.get(18).getMyComponent()).loadGood(exGood);


                    try {
                        for (int h = 0; h < 7; h++) {
                            for (int j = 0; j < 5; j++) {
                                if (j != 3) {
                                    testShip.putTile(tiles.get(h * 5 * j), new Position(j, h));
                                }
                            }
                        }

                        testShip.putTile(tiles.get(18), new Position(3, 0));
                        testShip.putTile(tiles.get(54), new Position(3, 1));
                        testShip.putTile(tiles.get(64), new Position(3, 2));
                        testShip.putTile(tiles.get(93), new Position(3, 3));
                        testShip.putTile(tiles.get(152), new Position(4, 3));
                        testShip.putTile(tiles.get(136), new Position(5, 3));
                        testShip.putTile(tiles.get(137), new Position(6, 3));

                        testShip.getShipBoard()[0][3].getTile().rotate(90);
                        testShip.getShipBoard()[1][3].getTile().rotate(270);
                        testShip.getShipBoard()[4][2].getTile().rotate(270);
                        ((CentralHousingUnit)testShip.getShipBoard()[4][3].getTile().getMyComponent()).setHumanCrewNumber(2);
                        ((ModularHousingUnit)testShip.getShipBoard()[4][2].getTile().getMyComponent()).addPurpleAlien();
                        ((GenericCargoHolds)testShip.getShipBoard()[2][3].getTile().getMyComponent()).playerLoadGood(exGood);
                        ((GenericCargoHolds)testShip.getShipBoard()[2][3].getTile().getMyComponent()).playerLoadGood(exGood);
                        exGood = new Good(Color.GREEN);
                        testShip.getShipBoard()[2][2].getTile().rotate(90);
                        ((GenericCargoHolds)testShip.getShipBoard()[2][2].getTile().getMyComponent()).playerLoadGood(exGood);
                        ((GenericCargoHolds)testShip.getShipBoard()[2][2].getTile().getMyComponent()).playerLoadGood(exGood);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    zUtils.showShipInGrid(testShip, shipControllers.get(i).getShipGrid());


                } else {
                    zUtils.showShipInGrid(mymodel.getPlayerInfoByNickname(Nickname).getShip(), shipControllers.get(i).getShipGrid());
                }
            }
        }

    }

    public void finishBuilding(ActionEvent e){
        //Disable di tutto ciò che è interagibile aparte la clessidra in teoria
        clientController.handleBuildingMenuChoice("j");
    }


}
