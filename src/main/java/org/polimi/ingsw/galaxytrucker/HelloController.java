package org.polimi.ingsw.galaxytrucker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.spreadsheet.Grid;
import org.polimi.ingsw.galaxytrucker.model.Ship;
import org.polimi.ingsw.galaxytrucker.model.essentials.Position;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class HelloController {
    @FXML
    private Text welcomeText;
    static Deque<String> messages = new LinkedList<>();
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private Button inviaButton;
    static Ship myShip;

    @FXML
    private void handleInvia() {
        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            myShip.removeTile(myShip.getShipBoard()[y][x].getTile(), new Position(y,x));
            System.out.println("Valori salvati: X = " + x + ", Y = " + y);
        } catch (NumberFormatException e) {
            System.out.println("Errore: inserire solo numeri interi!");
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(messages.pop());
    }
}