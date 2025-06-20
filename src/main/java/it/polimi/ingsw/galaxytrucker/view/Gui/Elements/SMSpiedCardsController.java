package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import it.polimi.ingsw.galaxytrucker.model.adventurecards.CardDeck;
import it.polimi.ingsw.galaxytrucker.view.Gui.zUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SMSpiedCardsController {

    @FXML ImageView card1;
    @FXML ImageView card2;
    @FXML ImageView card3;
    @FXML VBox cardViewMenu;

    private StackPane container;

    public void initialize(CardDeck deck, StackPane container) {
        this.container = container;
        List<ImageView> imageviews = List.of(card1, card2, card3);

        for (int i = 0; i < imageviews.size(); i++) {
            String cardIdVal = String.valueOf(deck.getCards().get(i).getID());
            String imagePath = "/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/cards/GT-cards_".concat(cardIdVal).concat(".jpg");
            Image img = new Image(zUtils.class.getResource(imagePath).toExternalForm());
            imageviews.get(i).setImage(img);
        }

    }

    public void close(){
        container.getChildren().remove(cardViewMenu);
    }



}
