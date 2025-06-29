package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.essentials.Good;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import it.polimi.ingsw.galaxytrucker.view.Gui.GuiJavaFx;
import it.polimi.ingsw.galaxytrucker.view.Gui.zUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;


/**
 * Handles load good confirmation during the flight phase.
 */
public class SMloadGoodsController {

    private ClientController clientController;
    private FlightController flightController;
    private HBox container;

    private ArrayList<Good> goods;
    @FXML private FlowPane listaGoods;

    /**
     * Initializes the controller with required references.
     *
     * @param clientController     the client-side controller
     * @param container            the UI container
     * @param flightController   reference to the flight phase controller
     * @param goods              the goods which need to be load
     */
    public void initialize(ClientController clientController, FlightController flightController, ArrayList<Good> goods, HBox container) {
        this.clientController = clientController;
        this.flightController = flightController;
        this.goods = goods;
        this.container = container;

        //Disegnare goods in lista
        updateGoodView();
    }

    /**
     * Ends the good loading phase and tells the clientController and Flight controller to handle it and
     * send the required updates to the server.
     */
    public void fineLoad(){
        clientController.handleLoadGoodChoice("f");
        flightController.endManagingGoodTime();
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        container.getChildren().clear();

    }

    /**
     * Adds the good currently inHand to the discarded goods list
     */
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
                        pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceGialla.png";
                case RED ->
                        pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceRossa.png";
                case GREEN ->
                        pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceVerde.png";
                case BLUE ->
                        pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceBlu.png";

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
