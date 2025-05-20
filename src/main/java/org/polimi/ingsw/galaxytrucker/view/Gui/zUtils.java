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

    public static void showShipInGrid(Ship ship, GridPane griglia) {
        griglia.getChildren().clear();

        Slot[][] shipboard =  ship.getShipBoard();

        for (int x = 0; x < shipboard.length; x++) {
            for (int y = 0; y < shipboard[x].length; y++) {
                
                Tile tile = shipboard[x][y].getTile();
                Image img;
                int rotation = 0;
                if(tile != null) {
                    String tileIdVal = String.valueOf(tile.getId());
                    String imagePath = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
                    img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                    //Tenere conto di rotazione
                    rotation = tile.getRotation();
                    //image view ruoti quella
                    //
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
                griglia.add(imageView, x, y);

                // Dopo aver aggiunto l’immagine alla griglia, lega dinamicamente la dimensione
                imageView.fitWidthProperty().bind(griglia.widthProperty().divide(7));
                imageView.fitHeightProperty().bind(griglia.heightProperty().divide(5));
                imageView.setRotate(rotation);
            }
        }
    }

}
