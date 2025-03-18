package org.polimi.ingsw.galaxytrucker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.text.Text;
import org.controlsfx.control.spreadsheet.Grid;

import java.util.Queue;
import java.util.Stack;

public class HelloController {
    @FXML
    private Text welcomeText;
    public static Stack<String> messages = new Stack<>();


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(messages.pop());
    }
}