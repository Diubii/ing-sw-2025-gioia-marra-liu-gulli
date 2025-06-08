package org.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import org.polimi.ingsw.galaxytrucker.view.Gui.zUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SMloadGoodsController {

    private ClientController clientController;
    private FlightController flightController;

    private ArrayList<Good> goods;
    @FXML private FlowPane listaGoods;

    public void initialize(ClientController clientController, FlightController flightController, ArrayList<Good> goods) {
        this.clientController = clientController;
        this.flightController = flightController;
        this.goods = goods;

        //Disegnare goods in lista
        updateGoodView();


    }

    public void fineLoad(){
        try {
            clientController.sendShipForGoodUpdate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropGood() {
        //Se c'è in mano lo mette nella lista
        if(flightController.getCurrentInHandGood() != null){
            goods.add(flightController.getCurrentInHandGood());
            flightController.setCurrentInHandGood(null);
            flightController.hideHand();
            updateGoodView();
        }
    }

    /**
     * Redraws goods list
     */
    public void updateGoodView(){
        listaGoods.getChildren().clear();
        System.out.println("Building controller DEBUG: updateGoodView");
        String pathMerce = null;
        for(int i=0;i<goods.size();i++) {
            Good good = goods.get(i);

            switch (good.getColor()) {
                case YELLOW ->
                        pathMerce = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceGialla.png";
                case RED ->
                        pathMerce = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceRossa.png";
                case GREEN ->
                        pathMerce = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceVerde.png";
                case BLUE ->
                        pathMerce = "/org/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceBlu.png";

            }

            Image merce = new Image(zUtils.class.getResource(pathMerce).toExternalForm());
            ImageView imgView = new ImageView(merce);
            imgView.fitWidthProperty().bind(listaGoods.widthProperty().divide(3.5));
            imgView.fitHeightProperty().bind(listaGoods.widthProperty().divide(3.5));
            int finalI = i;
            imgView.setOnMouseClicked(event -> {
                if (flightController.getCurrentInHandGood() == null) {
                    goods.remove(finalI);
                    flightController.setCurrentInHandGood(good);
                    flightController.showPickedGood();
                    updateGoodView();
                    event.consume();
                }
            });
            listaGoods.getChildren().add(imgView);
        }

    }


}
