package org.polimi.ingsw.galaxytrucker.view.Gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.controller.ClientController;
import org.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import org.polimi.ingsw.galaxytrucker.network.common.LobbyInfo;
import org.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import org.polimi.ingsw.galaxytrucker.view.Gui.Elements.SingleLobbyInfoController;

import java.io.IOException;
import java.util.List;

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


    public void backToMainMenu(ActionEvent e) {
        GuiJavaFx.playWavSoundEffect("ButtonClick.wav");
        mainViewController.askJoinOrCreateRoom();
    }

    public void UpdateLobbyList(List<LobbyInfo> lobbies){
        for (LobbyInfo info : lobbies) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/polimi/ingsw/galaxytrucker/GuiPages/Elements/SingleLobbyInfo.fxml"));
                Parent lobbyNode = loader.load();

                SingleLobbyInfoController controller = loader.getController();
                controller.setData(info.getLobbyID(), info.getHost(), info.getConnectedPlayers(), info.getMaxPlayers(),info.isLearningMatch());

                controller.getJoinButton().setOnAction(e -> clientController.handleJoinChoice(info.getLobbyID()));

                PnlLobbies.getChildren().add(lobbyNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
