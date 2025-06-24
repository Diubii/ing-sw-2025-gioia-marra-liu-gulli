package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import it.polimi.ingsw.galaxytrucker.enums.AlienColor;
import it.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import it.polimi.ingsw.galaxytrucker.model.Ship;
import it.polimi.ingsw.galaxytrucker.model.essentials.Position;
import it.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import it.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import it.polimi.ingsw.galaxytrucker.model.utils.Util;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentGuiDetailsRotationVisitor;
import it.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class zUtils {

    /**
     * Shows the ship in the gridpane creating for each gridslot this Strucure
     * Stackpane
     *    -> ImageView for the tile image
     *    -> AnchorPane
     *            -> Image for storage slots batteries ...
     * To handle the tile orientation the stackpane is rotated
     * and each subimage that needs it like crew or goods is counterrotated accordingly
     *
     * @param ship
     * @param griglia
     */
    public static void showShipInGrid(Ship ship, GridPane griglia, ClientController clientController, Boolean editable, Boolean viewDetails, FlightController flightController, ActivatableComponent activatableComponent) {

        //Empty the grid from previous configuration
        griglia.getChildren().clear();
        Slot[][] shipboard =  ship.getShipBoard();

        //Go over each Slot of the grid
        for (int x = 0; x < shipboard.length; x++) {
            for (int y = 0; y < shipboard[x].length; y++) {

                final int fX = x;
                final int fY = y;
                final Position pos = new Position(fX, fY);

                Tile tile = shipboard[x][y].getTile();
                Image img;
                int rotation = 0;
                StackPane stackPane = new StackPane();

                if(tile != null) {
                    //Load corresponding tile image
                    String tileIdVal = String.valueOf(tile.getId());
                    String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
                    img = new Image(Objects.requireNonNull(zUtils.class.getResource(imagePath)).toExternalForm());

                    rotation = tile.getRotation();
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));

                    //DetailRotationVisitor handles all of the "pieces" on the tiles, all the indicators
                    if(viewDetails) {
                        ComponentGuiDetailsRotationVisitor visitor = new ComponentGuiDetailsRotationVisitor(clientController,flightController,stackPane,imageView,rotation,editable);
                        tile.getMyComponent().accept(visitor);
                    }
                    stackPane.setRotate(rotation);

                }
                else if(clientController.getMyShip().getInvalidPositions().contains(new Position(x,y))){
                    //INVALID POSIITON
                    String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web157.jpg";
                    img = new Image(Objects.requireNonNull(zUtils.class.getResource(imagePath)).toExternalForm());
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);
                    imageView.setPreserveRatio(false);
                    imageView.setSmooth(true);

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));


                }
                else{
                    //EMPTY VALID POSITION
                    String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/empty.jpg";
                    img = new Image(Objects.requireNonNull(zUtils.class.getResource(imagePath)).toExternalForm());
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);
                    imageView.setPreserveRatio(false);
                    imageView.setSmooth(true);

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));
                }

                GridPane.setHgrow(stackPane, Priority.ALWAYS);
                GridPane.setVgrow(stackPane, Priority.ALWAYS);
                GridPane.setFillWidth(stackPane, true);
                GridPane.setFillHeight(stackPane, true);


                //Handling of click event for various functions of the game
                ComponentNameVisitor namevisitor = new ComponentNameVisitor();
                if(editable){
                    //Click on whole tile has different effect depending on phase and other settings
                    stackPane.setOnMouseClicked(event -> {
                        if( event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY  ) {

                            switch (clientController.getPhase()) {
                                case SHIP_CHECK:

                                    if (tile != null) {
                                        clientController.getMyModel().addTileToRemove(tile.getId());
                                        ship.removeTile(pos, true);
                                        showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);

                                    }
                                    break;
                                case CREW_INIT:

                                    //Only if a cab has a Life support system connected
                                    if (tile != null && Objects.equals(tile.getMyComponent().accept(namevisitor), "ModularHousingUnit") &&
                                            (Util.checkNearLFS(new Position(fX, fY), AlienColor.BROWN, ship) ||
                                                    Util.checkNearLFS(new Position(fX, fY), AlienColor.PURPLE, ship))) {

                                        //Editare a giro Crew tra varie possibilità e tenere aggiornato CrewInitUpdate
                                        ((GuiJavaFx) clientController.getView()).editPositionCrew(fX, fY);
                                        //Redraw
                                        showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);
                                    }

                                    break;

                                //FLIGHT activating component and discard crew, goods handled at 2nd layer of the stack not whole tile
                                case FLIGHT:

                                    //For activating component
                                    if(activatableComponent != null) {
                                        if( tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("DoubleEngine") && tile.getMyComponent().accept(namevisitor).equals(activatableComponent.name()) && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((DoubleEngine)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }
                                        if(tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("DoubleCannon") &&tile.getMyComponent().accept(namevisitor).equals(activatableComponent.name()) && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((DoubleCannon)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }
                                        if( tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("Shield") &&tile.getMyComponent().accept(namevisitor).equals(activatableComponent.name()) && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((Shield)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }

                                        //Metto batteria in mano
                                        if( tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("BatterySlot")) {
                                            if(((BatterySlot)tile.getMyComponent()).getBatteriesLeft() > 0 && flightController.getInHandBattery() == false){
                                                ((BatterySlot)tile.getMyComponent()).removeBattery();
                                                flightController.showBattery(new Position(fX,fY));
                                                showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                            }
                                        }
                                    }


                                    //CABS: for discarding crew

                                    if(flightController.getIsDiscardingCrewTime()){
                                        if( tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("ModularHousingUnit")){
                                            ModularHousingUnit modularHousingUnit = (ModularHousingUnit) tile.getMyComponent();
                                            if(modularHousingUnit.getNCrewMembers() > 0){
                                                //Aggiorno model locale
                                                System.out.println("Prima la cab ha: "+modularHousingUnit.getNBrownAlien()+" marroni "+modularHousingUnit.getNPurpleAlien()+" viola e "+modularHousingUnit.getNCrewMembers()+" membri crew in generale");

                                                modularHousingUnit.removeCrewMember();

                                                System.out.println("Dopo la cab ha: "+modularHousingUnit.getNBrownAlien()+" marroni "+modularHousingUnit.getNPurpleAlien()+" viola e "+modularHousingUnit.getNCrewMembers()+" membri crew in generale");

                                                flightController.addHousingPosition(new Position(fX, fY));
                                                System.out.println("Ultima inserita Posizione: "+fX+" "+fY);
                                                showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                            }
                                        } else if(tile != null && tile.getMyComponent() != null && tile.getMyComponent().accept(namevisitor).equals("CentralHousingUnit")){
                                            CentralHousingUnit centralHousingUnit = (CentralHousingUnit) tile.getMyComponent();
                                            if(centralHousingUnit.getNCrewMembers() > 0){
                                                //Aggiorno model locale
                                                centralHousingUnit.removeCrewMember();
                                                flightController.addHousingPosition(new Position(fX, fY));
                                                showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                            }
                                        }
                                    }
                                    break;


                                case null, default:
                                    if (clientController.getCurrentTileInHand() != null) {
                                        try {
                                            if (clientController.getMyShip().getShipBoard()[fX][fY].getTile() == null) {

                                                clientController.setCurrentPos(fX, fY);
                                                clientController.handleTilePlacement();
                                            }
                                        } catch ( Exception e ) {
                                            System.out.println("Posizione non valida");
                                        }
                                    } else {
                                        if (clientController.getMyShip().getShipBoard()[fX][fY].getTile() != null && !clientController.getMyShip().getShipBoard()[fX][fY].getTile().getFixed()) {
                                            clientController.reclaimTile();
                                        }
                                    }
                                    break;

                            }
                            event.consume();
                        }

                    });
                }

                griglia.add(stackPane, x, y);

            }
        }
    }

}
