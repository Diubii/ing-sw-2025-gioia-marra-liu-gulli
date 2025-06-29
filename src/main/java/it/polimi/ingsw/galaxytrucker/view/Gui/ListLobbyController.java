package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import it.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleLobbyInfoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * GUI Controller for displaying available game lobbies.
 * <p>
 * Handles lobby list rendering, join actions, and transitions back to the main menu.
 */
public class ListLobbyController extends GenericSceneController {

    @FXML  private Label TxtErr;
    @FXML private VBox PnlLobbies;
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

    public void ShowGenericMessage(String message) {
        TxtErr.setText(message);
    }

    @Override
    public String pageName() {
        return "LobbyListPage";
    }


    /**
     * Returns to the main menu screen.
     */

    public void backToMainMenu(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        mainViewController.askJoinOrCreateRoom();
    }

    /**
     * Updates the GUI with a list of available lobbies.
     * Each lobby includes join button logic.
     */
    public void UpdateLobbyList(List<LobbyInfo> lobbies){
        for (LobbyInfo info : lobbies) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleLobbyInfo.fxml"));
                Parent lobbyNode = loader.load();

                SingleLobbyInfoController controller = loader.getController();
                controller.setData(info.getLobbyID(), info.getHost(), info.getConnectedPlayers(), info.getMaxPlayers(),info.isLearningMatch());

                controller.getJoinButton().setOnAction(e -> {clientController.handleJoinChoice(info.getLobbyID()); GuiJavaFx.playWavSoundEffect("ButtonClick.wav");});

                PnlLobbies.getChildren().add(lobbyNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
