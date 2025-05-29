package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import javafx.event.ActionEvent;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;

public class CreateLobbyController extends GenericSceneController {

    @FXML private Text TxtErr;
    @FXML private ToggleGroup Nplayers;
    @FXML private ToggleGroup MatchType;

    private GuiJavaFx mainViewController;
    private ClientController clientController;  // Riferimento al controller del client
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;


    public void initialSetup(GuiJavaFx mainViewController,ClientController clientController,ClientModel mymodel,Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

    }

    @Override
    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }

    public void backToMainMenu(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        mainViewController.askJoinOrCreateRoom(); //Main menu
    }

    @Override
    public String pageName() {
        return "CreateLobbyPage";
    }

    public void confirm(ActionEvent e) {
        Toggle selectedToggleNumber = Nplayers.getSelectedToggle();
        Toggle selectedToggleMatch = MatchType.getSelectedToggle();
        if (selectedToggleNumber != null && selectedToggleMatch != null) {
            RadioButton selectedRadioButtonNumber = (RadioButton) selectedToggleNumber;
            RadioButton selectedRadioButtonMatch = (RadioButton) selectedToggleMatch;
            Boolean learning = false;
            Integer max = 2;
            switch (selectedRadioButtonMatch.getId()){
                case "radioLearning": learning = true;
                    break;
                case "radioLv2": learning = false;
                  break;
                default:
                    ShowGenericMessage("Tipo match non valido");
                break;

            }
            switch (selectedRadioButtonNumber.getId()){
                case "radio2": max = 2;
                    break;
                case "radio3": max = 3;
                    break;
                case "radio4": max = 4;
                    break;
                default:
                    ShowGenericMessage("Numero giocatori non valido");
                break;
            }
            clientController.handleCreateChoice(max, learning);
        } else {
            ShowGenericMessage("Seleziona Numero Giocatori e tipo di match.");
        }

    }

}
