package it.polimi.ingsw.galaxytrucker.visitors.components;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.enums.Color;
import it.polimi.ingsw.galaxytrucker.model.essentials.Component;
import it.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import it.polimi.ingsw.galaxytrucker.view.Gui.FlightController;
import it.polimi.ingsw.galaxytrucker.view.Gui.zUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class ComponentGuiDetailsRotationVisitor implements ComponentVisitorInterface<StackPane>{


    private StackPane stackPane;
    private ImageView imageView;
    private int rotation;
    private FlightController flightController;
    private ClientController clientController;
    private Boolean editable=false;


    public ComponentGuiDetailsRotationVisitor(ClientController clientController,FlightController flightController, StackPane stackPane, ImageView imageView, int rotation,Boolean editable) {
        this.stackPane = stackPane;
        this.imageView = imageView;
        this.rotation = rotation;
        this.flightController = flightController;
        this.clientController = clientController;
        this.editable = editable;
    }

    @Override
    public StackPane visit(Component component) {
        return null;
    }

    @Override
    public StackPane visit(BatterySlot component) {

        String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/batteryCharge.png";
        Image segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());
        //VBOX per gli indicatori batteria
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vbox);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        for(int i=1; i< component.getBatteriesLeft()+1;i++){
            ImageView viewSegnalino = new ImageView(segnalino);
            //Binding delle dimensioni e posizione
            viewSegnalino.fitWidthProperty().bind(stackPane.widthProperty().divide(3));
            viewSegnalino.fitHeightProperty().bind(stackPane.heightProperty().divide(5));

            //Aggiunta a anchor pane
            vbox.getChildren().add(viewSegnalino);

        }
        //CounterRotazione per segnalini
        vbox.setRotate(-rotation);
        return null;
    }

    @Override
    public StackPane visit(Cannon component) {
        return null;
    }

    @Override
    public StackPane visit(CentralHousingUnit component) {
        String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AstronautaPedina.png";
        Image segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());
        //VBOX per gli indicatori batteria
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(hBox);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        for(int i=1; i< component.getNCrewMembers()+1;i++){
            ImageView viewSegnalino = new ImageView(segnalino);
            //Binding delle dimensioni e posizione
            viewSegnalino.fitWidthProperty().bind(imageView.fitWidthProperty().divide(3));
            viewSegnalino.fitHeightProperty().bind(imageView.fitHeightProperty().divide(4));

            //Aggiunta a anchor pane
            hBox.getChildren().add(viewSegnalino);

        }
        //CounterRotazione per segnalini
        hBox.setRotate(-rotation);
        return null;
    }

    @Override
    public StackPane visit(DoubleCannon component) {
        if(component.isCharged()){
            String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/activeOverlay.png";
            ImageView imgCharged = new ImageView( new Image(zUtils.class.getResource(imagePath).toExternalForm()));
            imgCharged.setPreserveRatio(true);
            imgCharged.setSmooth(true);
            imgCharged.fitWidthProperty().bind(imageView.fitWidthProperty());
            imgCharged.fitHeightProperty().bind(imageView.fitHeightProperty());
            stackPane.getChildren().add(imgCharged);
        }
        return null;
    }

    @Override
    public StackPane visit(DoubleEngine component) {
        if(component.isCharged()){
            String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/activeOverlay.png";
            ImageView imgCharged = new ImageView( new Image(zUtils.class.getResource(imagePath).toExternalForm()));
            imgCharged.setPreserveRatio(true);
            imgCharged.setSmooth(true);
            imgCharged.fitWidthProperty().bind(imageView.fitWidthProperty());
            imgCharged.fitHeightProperty().bind(imageView.fitHeightProperty());
            stackPane.getChildren().add(imgCharged);
        }
        return null;
    }

    @Override
    public StackPane visit(Engine component) {
        //In teoria sempre attivo, opzionale mettere fiammella
        return null;
    }

    @Override
    public StackPane visit(GenericCargoHolds component) {
        Group group = new Group();
        List<ImageView> slots = new ArrayList<>();

        String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/vuoto.png";
        Image segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());
        ImageView uno;
        ImageView due;


        double relativeWidth = 0.35;
        double normalizedX; //CM photoshop
        double normalizedY; //CM photoshop

        switch (component.getnMaxContainers()){
            case 1:
                normalizedX = 0.99 / 3.06; //CM photoshop
                normalizedY = 0.44 / 3.06; //CM photoshop
                uno = new ImageView(segnalino);
                slots.add(uno);
                uno.setPreserveRatio(true);
                group.getChildren().add(uno);
                uno.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * normalizedX,
                                imageView.boundsInParentProperty())
                );
                uno.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getHeight() * normalizedY,
                                imageView.boundsInParentProperty())
                );

                // Binding larghezza (scalata con il magazzino)
                uno.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                break;
            case 2:
                //PRIMA
                normalizedX = 0.99 / 3.06; //CM photoshop
                normalizedY = 0.44 / 3.06; //CM photoshop
                uno = new ImageView(segnalino);
                slots.add(uno);
                uno.setPreserveRatio(true);
                group.getChildren().add(uno);
                uno.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * normalizedX,
                                imageView.boundsInParentProperty())
                );
                uno.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getHeight() * normalizedY,
                                imageView.boundsInParentProperty())
                );

                // Binding larghezza (scalata con il magazzino)
                uno.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                //SECONDA
                double normalizedY2 = 1.53 / 3.06; //CM photoshop
                due = new ImageView(segnalino);
                slots.add(due);
                due.setPreserveRatio(true);
                group.getChildren().add(due);
                due.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * normalizedX,
                                imageView.boundsInParentProperty())
                );
                due.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getHeight() * normalizedY2,
                                imageView.boundsInParentProperty())
                );
                // Binding larghezza (scalata con il magazzino)
                due.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                break;
            case 3:
                //PRIMA
                normalizedX = 0.50 / 3.06; //CM photoshop
                normalizedY = 0.99 / 3.06; //CM photoshop
                uno = new ImageView(segnalino);
                slots.add(uno);
                uno.setPreserveRatio(true);
                group.getChildren().add(uno);
                uno.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * normalizedX,
                                imageView.boundsInParentProperty())
                );
                uno.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getHeight() * normalizedY,
                                imageView.boundsInParentProperty())
                );

                // Binding larghezza (scalata con il magazzino)
                uno.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                        imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                //SECONDA
                double normalizedX2 = 1.57/3.06; //CM photoshop
                normalizedY2 = 0.42 / 3.06; //CM photoshop
                due = new ImageView(segnalino);
                slots.add(due);
                due.setPreserveRatio(true);
                group.getChildren().add(due);
                due.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getWidth() * normalizedX2,
                                imageView.boundsInParentProperty())
                );
                due.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getHeight() * normalizedY2,
                                imageView.boundsInParentProperty())
                );
                // Binding larghezza (scalata con il magazzino)
                due.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                //TERZA
                double normalizedY3 = 1.57 / 3.06; //CM photoshop
                ImageView tre = new ImageView(segnalino);
                slots.add(tre);
                tre.setPreserveRatio(true);
                group.getChildren().add(tre);
                tre.layoutXProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getWidth() * normalizedX2,
                                imageView.boundsInParentProperty())
                );
                tre.layoutYProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getHeight() * normalizedY3,
                                imageView.boundsInParentProperty())
                );
                // Binding larghezza (scalata con il magazzino)
                tre.fitWidthProperty().bind(
                        Bindings.createDoubleBinding(() ->
                                imageView.getBoundsInParent().getWidth() * relativeWidth,
                                imageView.boundsInParentProperty())
                );
                break;
        }

        String pathMerce = null;

        if( flightController != null && flightController.getIsManagingGoodTime() == true && editable == true){
            for(int k = 0; k < slots.size(); k++){
                int finalK = k;
                slots.get(k).setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY){
                        //Posiziono
                        if (flightController.getCurrentInHandGood() != null) {
                            if (flightController.getCurrentInHandGood().getColor() != Color.RED ||
                                    (flightController.getCurrentInHandGood().getColor() == Color.RED && component.isSpecial())) {
                                if (!component.isFull()) {
                                    component.playerLoadGood(flightController.getCurrentInHandGood());
                                    flightController.setCurrentInHandGood(null);
                                    flightController.hideHand();
                                    flightController.showShip(clientController.getMyModel().getMyInfo().getShip(),clientController.getMyModel().getMyInfo().getNickName());
                                }
                            }
                        }
                        else if(flightController.getCurrentInHandGood() == null && component.getGoods().size() > finalK && component.getGoods().get(finalK) != null){
                            //Riprendo
                            flightController.setCurrentInHandGood(component.getGoods().get(finalK));
                            flightController.showPickedGood();
                            component.removeGood(component.getGoods().get(finalK));
                            flightController.showShip(clientController.getMyModel().getMyInfo().getShip(),clientController.getMyModel().getMyInfo().getNickName());
                        }
                    }
                });
            }
        }


        for(int i=0; i< component.getnMaxContainers();i++){
            System.out.println("Dimensione goods in cargoHold: "+component.getGoods().size());
            if( i < component.getGoods().size()){
                System.out.println("Sto assegnando una merce");
                switch (component.getGoods().get(i).getColor()){
                    case YELLOW -> pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceGialla.png";
                    case RED -> pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceRossa.png";
                    case GREEN -> pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceVerde.png";
                    case BLUE -> pathMerce = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/merceBlu.png";

                }

                Image merce = new Image(zUtils.class.getResource(pathMerce).toExternalForm());
                slots.get(i).setImage(merce);
            }
        }
        stackPane.getChildren().add(group);
        for(int i=0;i<slots.size();i++){
            slots.get(i).setRotate(-rotation);
        }
        return null;
    }

    @Override
    public StackPane visit(LifeSupportSystem component) {
        //Nulla
        return null;
    }

    @Override
    public StackPane visit(ModularHousingUnit component) {
        String imagePath;
        Image segnalino = null;
        switch (component.getAlienColor()){
            case BROWN:
                imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AlienoMarronePedina.png";
                segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());

                break;
            case PURPLE:
                imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AlienoViolaPedina.png";
                segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                break;
            case EMPTY:
                imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AstronautaPedina.png";
                segnalino = new Image(zUtils.class.getResource(imagePath).toExternalForm());
                break;
        }

        //HBOX per gli indicatori Crew
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(hBox);
        HBox.setHgrow(hBox, Priority.ALWAYS);


        for(int i=1; i< component.getNCrewMembers()+1;i++){
            ImageView viewSegnalino = new ImageView(segnalino);
            viewSegnalino.setPreserveRatio(true);
            viewSegnalino.setSmooth(true);
            viewSegnalino.setCache(true);
            //Binding delle dimensioni
            viewSegnalino.fitWidthProperty().bind(stackPane.widthProperty().divide(3));
            viewSegnalino.fitHeightProperty().bind(stackPane.heightProperty().divide(4));
            //Aggiunta a anchor pane
            hBox.getChildren().add(viewSegnalino);
        }

        //CounterRotazione per segnalini
        hBox.setRotate(-rotation);
        return null;
    }

    @Override
    public StackPane visit(Shield component) {
        if(component.isCharged()){
            String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/tiles/activeOverlay.png";
            ImageView imgCharged = new ImageView( new Image(zUtils.class.getResource(imagePath).toExternalForm()));
            imgCharged.setPreserveRatio(true);
            imgCharged.setSmooth(true);
            imgCharged.fitWidthProperty().bind(imageView.fitWidthProperty());
            imgCharged.fitHeightProperty().bind(imageView.fitHeightProperty());
            stackPane.getChildren().add(imgCharged);
        }
        return null;
    }
}
