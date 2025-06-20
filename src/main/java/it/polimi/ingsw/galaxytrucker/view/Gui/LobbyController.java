package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.PlayerInfo;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LobbyController extends GenericSceneController {

    @FXML
    private Label lblErr;
    @FXML private Label lblTitle;
    @FXML private Label lblNPlayer;
    @FXML private Label nome1;
    @FXML  private Label nome2;
    @FXML  private Label nome3;
    @FXML private Label nome4;
    @FXML private ImageView imgTipoVolo;
    @FXML  private ImageView avatar1;
    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;

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
    public String pageName() {
        return "LobbyPage";
    }

    @Override
    public void ShowGenericMessage(String message) {
        lblErr.setText(message);
    }


    public void updatePlayersList(ArrayList<PlayerInfo> playerInfo){
        List<Label> labels = List.of(nome1, nome2, nome3, nome4);
        List<ImageView> avatars = List.of(avatar1, avatar2, avatar3, avatar4);

        for (int i = 0; i < 4; i++) {
            if (i < playerInfo.size()) {
                labels.get(i).setText(playerInfo.get(i).getNickName());
                switch (playerInfo.get(i).getColor()){
                    case RED ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarRosso.png").toExternalForm()));
                    case YELLOW ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarGiallo.png").toExternalForm()));
                    case BLUE ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarBlu.png").toExternalForm()));
                    case GREEN ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarVerde.png").toExternalForm()));
                }
            } else {
                labels.get(i).setText(""); // oppure "Vuoto", oppure nascondi
                avatars.get(i).setImage(null);
            }
        }

    }

}
