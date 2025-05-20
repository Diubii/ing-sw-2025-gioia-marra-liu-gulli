package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Slot;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;

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
    public static void showShipInGrid(Ship ship, GridPane griglia) {
        //No check che sia di dimensione giusta ma si suppone di si

        //Empty the grid from previous configuration
        griglia.getChildren().clear();
        Slot[][] shipboard =  ship.getShipBoard();

        //Go over each Slot of the grid
        for (int x = 0; x < shipboard.length; x++) {
            for (int y = 0; y < shipboard[x].length; y++) {

                Tile tile = shipboard[x][y].getTile();
                Image img;
                int rotation = 0;

                if(tile != null) {
                    //Load corresponding tile image
                    String tileIdVal = String.valueOf(tile.getId());
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                    //Consider rotation
                    rotation = tile.getRotation();
                    //Visitor a cui passi tutto e in base al tipo di componente decide se ruotare tutto, ruotare solo imamgine, ruotare con counterrotazioni

                }
                else{
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web157.jpg";
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                }
                ImageView imageView = new ImageView(img);

                // Fa sì che l'immagine si adatti alle dimensioni
                imageView.setPreserveRatio(false); // oppure true, se vuoi mantenerle
                imageView.setSmooth(true);

                // Permetti alla cella di espandersi
                GridPane.setHgrow(imageView, Priority.ALWAYS);
                GridPane.setVgrow(imageView, Priority.ALWAYS);
                GridPane.setFillWidth(imageView, true);
                GridPane.setFillHeight(imageView, true);

                // Aggiungi l'immagine direttamente alla griglia
                //Aggiungere uno stack pane
                //Aggiungere immagine
                griglia.add(imageView, x, y);
                //Aggiungere Anchor Pane?
                //aggiungere elementi extra

                // Dopo aver aggiunto l’immagine alla griglia, lega dinamicamente la dimensione
                imageView.fitWidthProperty().bind(griglia.widthProperty().divide(7));
                imageView.fitHeightProperty().bind(griglia.heightProperty().divide(5));

                //Rotate direttamente allo StackPane?
                //Per pile e storage ok
                //ma per cabine girerebbe anche umani quindi si ruota solo Imageview
                imageView.setRotate(rotation);
            }
        }
    }

}
