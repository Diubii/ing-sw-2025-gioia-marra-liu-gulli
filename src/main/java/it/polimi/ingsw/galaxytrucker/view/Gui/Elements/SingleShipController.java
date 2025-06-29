package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;


/**
 * Controller for displaying a single player's ship in the GUI.
 * Maintains grid aspect ratio and stores player nickname.
 */
public class SingleShipController {

    @FXML private StackPane shipGridContainer;
    @FXML private GridPane shipGrid;

    private String nicknameOfPlayer;

    /**
     * Initializes the ship view with player's nickname and container.
     *
     * @param nicknameOfPlayer the player's nickname
     * @param shipGridContainer the container for the ship grid
     */
    public void initialize(String nicknameOfPlayer,StackPane shipGridContainer) {
        this.nicknameOfPlayer = nicknameOfPlayer;
        double aspectRatio = 7.0 / 5.0;
        this.shipGridContainer = shipGridContainer;
        this.shipGridContainer.widthProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        this.shipGridContainer.heightProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        //DOPO per il primo setup
        Platform.runLater(() -> adjustGridPaneSize(aspectRatio));
    }

    /**
     * Returns the GridPane used to represent the ship
     * @return
     */
    public GridPane getShipGrid() {
        return shipGrid;
    }

    /**
     * Returns the Nickname of the currently displayed ship
     * @return
     */
    public String getNicknameOfPlayer() {
        return nicknameOfPlayer;
    }

    public void setNicknameOfPlayer(String nicknameOfPlayer) {
        this.nicknameOfPlayer = nicknameOfPlayer;
    }

    private void adjustGridPaneSize(double aspectRatio) {

        double containerWidth = shipGridContainer.getWidth();
        double containerHeight = shipGridContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0){
            return;
        }

        double newWidth, newHeight;

        if (containerWidth / containerHeight > aspectRatio) {
            newHeight = containerHeight;
            newWidth = newHeight * aspectRatio;
        } else {
            newWidth = containerWidth;
            newHeight = newWidth / aspectRatio;
        }


        shipGrid.setMinWidth(0);
        shipGrid.setMinHeight(0);
        shipGrid.setPrefWidth(newWidth);
        shipGrid.setPrefHeight(newHeight);

    }
}
