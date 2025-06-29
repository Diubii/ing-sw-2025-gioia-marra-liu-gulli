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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Utility class for GUI operations related to displaying a ship in a GridPane.
 * <p>
 * It dynamically generates JavaFX UI elements to represent the ship layout,
 * tiles, invalid/valid positions, and supports various game phases
 * including editing, flight phase actions, and component activation.
 */
public class zUtils {

    /**
     * Populates a JavaFX GridPane with the graphical representation of a player's ship.
     * <p>
     * Depending on the game phase and state (editable, flight, discarding crew, etc.), it:
     * <ul>
     *     <li>Displays correct tile images and rotations</li>
     *     <li>Marks invalid/valid slots</li>
     *     <li>Allows interaction with components (batteries, shields, engines, etc.)</li>
     *     <li>Handles mouse click events for editing, crew setup, and flight actions</li>
     * </ul>
     *
     * @param ship                The player's ship model.
     * @param griglia             The JavaFX GridPane to be populated.
     * @param clientController    The main game controller handling player actions.
     * @param editable            If true, allows modifying ship (e.g., during ship building phase).
     * @param viewDetails         If true, displays crew/goods/batteries on top of tiles.
     * @param flightController    The current flight phase GUI controller.
     * @param activatableComponent The component currently being activated by the user, if any.
     */
    public static void showShipInGrid(Ship ship, GridPane griglia, ClientController clientController, Boolean editable, Boolean viewDetails, FlightController flightController, ActivatableComponent activatableComponent) {

        //Empty the grid from previous configuration
        griglia.getChildren().clear();
        Slot[][] shipboard =  ship.getShipBoard();
        int cornice=30;

        //Cornice
        for(int i = 1; i < 8; i++){
            StackPane stackPane = new StackPane();
            stackPane.alignmentProperty().setValue(Pos.CENTER);
            Label l = new Label(String.valueOf(i+3));
            l.prefHeightProperty().setValue(cornice);
            l.setStyle("-fx-text-fill: white;");
            stackPane.getChildren().add(l);
            griglia.add(stackPane, i, 0);
        }
        for(int i = 1; i < 6; i++) {
            StackPane stackPane = new StackPane();
            stackPane.alignmentProperty().setValue(Pos.CENTER);
            Label l = new Label(String.valueOf(i + 4));
            l.maxWidth(cornice);
            l.setStyle("-fx-text-fill: white;");
            stackPane.maxWidth(cornice);
            stackPane.getChildren().add(l);
            griglia.add(stackPane, 0, i);
        }
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

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().subtract(cornice).divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().subtract(cornice).divide(5));

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

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().subtract(cornice).divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().subtract(cornice).divide(5));


                }
                else{
                    //EMPTY VALID POSITION
                    String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/empty.jpg";
                    img = new Image(Objects.requireNonNull(zUtils.class.getResource(imagePath)).toExternalForm());
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);
                    imageView.setPreserveRatio(false);
                    imageView.setSmooth(true);

                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().subtract(cornice).divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().subtract(cornice).divide(5));
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

                griglia.add(stackPane, x+1, y+1);

            }
        }
    }

}
