package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.enums.ActivatableComponent;
import org.polimi.ingsw.galaxytrucker.enums.AlienColor;
import org.polimi.ingsw.galaxytrucker.enums.Color;
import org.polimi.ingsw.galaxytrucker.exceptions.InvalidTilePosition;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.model.utils.Util;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentGuiDetailsRotationVisitor;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.polimi.ingsw.galaxytrucker.view.Tui.util.InputUtils.parseCoordinate;

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
    //TODO magari aggiungere action o altri parametri per indicare nella fase di volo cosa si può fare:
    //TODO ad esempio un tipo di componente da selezionare oppure che tipi di interazioni, sacrifica crew o altro...
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
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());

                    rotation = tile.getRotation();
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);


                    // Dopo aver aggiunto l’immagine alla griglia, lega dinamicamente la dimensione
                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));

                    if(viewDetails) {
                        ComponentGuiDetailsRotationVisitor visitor = new ComponentGuiDetailsRotationVisitor(clientController,flightController,stackPane,imageView,rotation);
                        tile.getMyComponent().accept(visitor);
                    }
                    stackPane.setRotate(rotation);

                    //Visitor a cui passi tutto e in base al tipo di componente decide se ruotare tutto, ruotare solo immagine, ruotare con counterrotazioni
                    //e aggiunge i vari sottoelementi
                }
                else if(clientController.getMyShip().getInvalidPositions().contains(new Position(x,y))){
                    //INVALID POSIITON
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web157.jpg";
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);
                    // Fa sì che l'immagine si adatti alle dimensioni
                    imageView.setPreserveRatio(false); // oppure true, se vuoi mantenerle
                    imageView.setSmooth(true);


                    // Dopo aver aggiunto l’immagine alla griglia, lega dinamicamente la dimensione
                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));


                }
                else{
                    //EMPTY VALID POSITION
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/empty.jpg";
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                    ImageView imageView = new ImageView(img);

                    stackPane.getChildren().add(imageView);
                    // Fa sì che l'immagine si adatti alle dimensioni
                    imageView.setPreserveRatio(false); // oppure true, se vuoi mantenerle
                    imageView.setSmooth(true);


                    // Dopo aver aggiunto l’immagine alla griglia, lega dinamicamente la dimensione
                    imageView.fitWidthProperty().bind(griglia.prefWidthProperty().divide(7));
                    imageView.fitHeightProperty().bind(griglia.prefHeightProperty().divide(5));
                }

                // Permetti alla cella di espandersi
                GridPane.setHgrow(stackPane, Priority.ALWAYS);
                GridPane.setVgrow(stackPane, Priority.ALWAYS);
                GridPane.setFillWidth(stackPane, true);
                GridPane.setFillHeight(stackPane, true);


                ComponentNameVisitor namevisitor = new ComponentNameVisitor();
                if(editable){
                    //Tutto fatto con click ma dipende dalla fase:
                    stackPane.setOnMouseClicked(event -> {
                        if(event.getButton() == MouseButton.PRIMARY) {

                            switch (clientController.getPhase()) {
                                case SHIP_CHECK:
                                    //RIMOZIONE DI QUALUNQUE TILE

                                    //Todo: fatto lato client e poi solo al checkship server viene avvisato, ci potrebbe anche stare eh
                                    //Edito una copia locale , poi dico quali ho cancellato e server mi ridà. (in teoria)
                                    clientController.getMyModel().addTileToRemove(tile.getId());
                                    ship.removeTile(pos, true);
                                    showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                    break;
                                case CREW_INIT:


                                    //Solo se cabina con supporto vitale vicino
                                    if (tile != null && tile.getMyComponent().accept(namevisitor) == "ModularHousingUnit" &&
                                            (Util.checkNearLFS(new Position(fX, fY), AlienColor.BROWN, ship) ||
                                                    Util.checkNearLFS(new Position(fX, fY), AlienColor.PURPLE, ship))) {

                                        //Editare a giro Crew tra varie possibilità e tenere aggiornato CrewInitUpdate
                                        ((GuiJavaFx) clientController.getView()).editPositionCrew(fX, fY);
                                        //Redraw
                                        showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);
                                    }
                                    else if(tile.getMyComponent().accept(namevisitor) == "ModularHousingUnit"){
                                        ((GuiJavaFx) clientController.getView()).editPositionCrew(fX, fY);
                                    }

                                    break;

                                //FLIGHT sacrificare Crew, caricare merci quindi qualcosa per indicare quello.
                                case FLIGHT:

                                    //Componenti Attivabili
                                    if(activatableComponent != null) {
                                        if(tile.getMyComponent().accept(namevisitor).equals(activatableComponent.name()) && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((DoubleEngine)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }
                                        if(tile.getMyComponent().accept(namevisitor).equals("DoubleCannon") && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((DoubleCannon)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }
                                        if(tile.getMyComponent().accept(namevisitor).equals("Shield") && flightController.getInHandBattery() == true && tile.getMyComponent().isCharged() == false){
                                            ((Shield)tile.getMyComponent()).setCharged(true);
                                            flightController.useInHandBattery();
                                            flightController.addActivatedPosition( new Position(fX,fY));
                                            showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController, activatableComponent);
                                        }

                                        //Metto batteria in mano
                                        if(tile.getMyComponent().accept(namevisitor).equals("BatterySlot")) {
                                            if(((BatterySlot)tile.getMyComponent()).getBatteriesLeft() > 0 && flightController.getInHandBattery() == false){
                                                ((BatterySlot)tile.getMyComponent()).removeBattery();
                                                flightController.showBattery(new Position(fX,fY));
                                                showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                            }
                                        }
                                    }




                                    //per magazzino controlla la inHandGood,
                                    //ma su ogni singolo slot, eventi in ComponentGuiDetailsRotation


                                    //per cabine fa scartare uno di crew
                                    //Controlla se flightController.isDiscardingCrewTime;
                                    //Modifica model locale e chiama redraw. poi sono mandate solo le posizioni e viene mandato
                                    //uno shipUpdate con le pos verificate dal server quindi ok.
                                    //Metto evento soltanto se la cabina ha ancora crew tanto richiamo il draw con la ship locale modificata e controllo
                                    if(flightController.getIsDiscardingCrewTime()){
                                        if(tile.getMyComponent().accept(namevisitor).equals("ModularHousingUnit")){
                                            ModularHousingUnit modularHousingUnit = (ModularHousingUnit) tile.getMyComponent();
                                            if(modularHousingUnit.getNCrewMembers() > 0){
                                                //Aggiorno model locale
                                                modularHousingUnit.removeCrewMember();
                                                flightController.addHousingPosition(new Position(fX, fY));
                                                showShipInGrid(ship, griglia, clientController, editable, viewDetails, flightController,activatableComponent);

                                            }
                                        }
                                        if(tile.getMyComponent().accept(namevisitor).equals("CentralHousingUnit")){
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
                                                clientController.handleTilePlacement(true);
                                            }
                                        } catch (ExecutionException e) {
                                            throw new RuntimeException(e);
                                        } catch (InvalidTilePosition e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        if (clientController.getMyShip().getShipBoard()[fX][fY].getTile() != null && clientController.getMyShip().getShipBoard()[fX][fY].getTile().getFixed() == false) {
                                            //Todo: rimozione tile già piazzata
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
