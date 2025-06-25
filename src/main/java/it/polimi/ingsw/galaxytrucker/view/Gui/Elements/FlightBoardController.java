package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.FlightBoard;
import it.polimi.ingsw.galaxytrucker.view.Gui.zUtils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class FlightBoardController {

    private int size;
    private int width;
    private int height;
    private double aspectRatio;

    private StackPane boardContainer;
    private GridPane grid;

    public void initialize(StackPane boardContainer , FlightBoard flightBoard) {
        this.boardContainer = boardContainer;

        size = flightBoard.getFlightBoardMap().getFlightBoardMapSlots().size();
        if (size == 18) {
            width = 8;
            height = 3;
            aspectRatio = 8.0 / 3.0;
        } else {
            width = 10;
            height = 4;
            aspectRatio = 10.0 / 4.0;
        }
        grid = new GridPane();

        for (int i = 0; i < height; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / height);
            grid.getRowConstraints().add(rc);
        }

        for (int j = 0; j < width; j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / width);
            grid.getColumnConstraints().add(cc);
        }

        // Riempie la griglia con StackPane vuoti
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                StackPane cell = new StackPane();
                grid.add(cell, col, row);
            }
        }


        this.boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));
        this.boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> adjustGridPaneSize(aspectRatio));


        boardContainer.getChildren().add(grid);
        updateBoard(flightBoard);
        Platform.runLater(() -> adjustGridPaneSize(aspectRatio));

    }

    /**
     * Shows the flightboard in the container
     * @param flightBoard
     */
    public void updateBoard(FlightBoard flightBoard) {

        System.out.println("Debug: FlightBoardController.updateBoard");
        int x=0;
        int y=0;
        int position=0;


        for (x=0,y=0; x<width; x++){
          compileBoardCell(x,y,position,flightBoard);
            position++;
        }
        for(y=1,x=width-1; y<height; y++){
            compileBoardCell(x,y,position,flightBoard);
            position++;
        }
        for (x=width-2,y=height-1; x>-1; x--){
            compileBoardCell(x,y,position,flightBoard);
            position++;
        }
        for(y=height-2,x=0; y>0; y--){
            compileBoardCell(x,y,position,flightBoard);
            position++;
        }
    }

    private void compileBoardCell(int x, int y,int position,FlightBoard flightBoard) {
        clearStack(x,y);
        //Immagine vuota
        String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/emptyBoardSlot.png";
        if(x==0 && y== 0){
            imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/emptyBoardSlotFirst.png";
        }
        Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
        String pathSegnalino = "";
        addImageAt(x,y,img);
        if(flightBoard.getFlightBoardMap().getFlightBoardMapSlots().get(position).getPlayerToken() != Color.EMPTY){
            switch (flightBoard.getFlightBoardMap().getFlightBoardMapSlots().get(position).getPlayerToken()) {
                case RED -> pathSegnalino = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/razzoRosso.png";
                case BLUE -> pathSegnalino = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/razzoBlu.png";
                case GREEN -> pathSegnalino = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/razzoVerde.png";
                case YELLOW -> pathSegnalino = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/razzoGiallo.png";
            }
            Image segnalino = new Image(zUtils.class.getResource(pathSegnalino).toExternalForm());
            addImageAt(x,y,segnalino);
            System.out.println("Aggiunto segnalino in: "+position);
        }

    }

    private void clearStack(int col, int row) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                int nodeCol = GridPane.getColumnIndex(node);
                int nodeRow = GridPane.getRowIndex(node);
                if (nodeCol == col && nodeRow == row ) {
                    StackPane cell = (StackPane) node;
                    cell.getChildren().clear();
                    break;
                }
            }
        }
    }

    private void addImageAt(int col, int row, Image image) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                int nodeCol = GridPane.getColumnIndex(node);
                int nodeRow = GridPane.getRowIndex(node);
                if (nodeCol == col && nodeRow == row ) {
                    StackPane cell = (StackPane) node;
                    ImageView imageView = new ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.fitWidthProperty().bind(grid.prefWidthProperty().divide(width));
                    imageView.fitHeightProperty().bind(grid.prefHeightProperty().divide(height));
                    cell.getChildren().add(imageView);
                    break;
                }
            }
        }
    }

    private void adjustGridPaneSize(double aspectRatio) {

        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();

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


        grid.setMinWidth(0);
        grid.setMinHeight(0);
        grid.setPrefWidth(newWidth);
        grid.setPrefHeight(newHeight);
        grid.setMaxWidth(newWidth);
        grid.setMaxHeight(newHeight);

    }
}
