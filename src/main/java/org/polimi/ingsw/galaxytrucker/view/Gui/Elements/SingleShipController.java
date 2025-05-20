package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class SingleShipController {

    @FXML private StackPane shipGridContainer;
    @FXML private GridPane shipGrid;

    private String nicknameOfPlayer;

    public void initialize(String nicknameOfPlayer,StackPane shipGridContainer) {
        this.nicknameOfPlayer = nicknameOfPlayer;
        double aspectRatio = 7.0 / 5.0;
        this.shipGridContainer = shipGridContainer;
        this.shipGridContainer.widthProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        this.shipGridContainer.heightProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        //DOPO per il primo setup
        Platform.runLater(() -> adjustGridPaneSize(aspectRatio));
    }

    public GridPane getShipGrid() {
        return shipGrid;
    }

    public String getNicknameOfPlayer() {
        return nicknameOfPlayer;
    }

    public void setNicknameOfPlayer(String nicknameOfPlayer) {
        this.nicknameOfPlayer = nicknameOfPlayer;
    }

    private void adjustGridPaneSize(double aspectRatio) {

        System.out.println("AdjustSize Chiamata");


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
