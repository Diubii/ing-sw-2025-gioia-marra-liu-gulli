package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;


public class SingleLobbyInfoController {

    @FXML private Text txtId;
    @FXML private Text txtHost;
    @FXML private Text txtNumPlayer;
    @FXML private ImageView imgMatchType;
    @FXML private Button btnJoin;
    private Image imgType;

    public void setData(int ID, String host, int playerNum, int maxPlayers, Boolean isLearningMatch) {
        txtId.setText(Integer.toString(ID));
        txtHost.setText("Host: "+host);
        txtNumPlayer.setText(playerNum+"/"+maxPlayers);
        if(isLearningMatch){
            imgType = new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/sleepy.png").toExternalForm());
            imgMatchType.setImage(imgType);
        }
        else{
            imgType = new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/ganzo.png").toExternalForm());
            imgMatchType.setImage(imgType);
        }
    }

    public Button getJoinButton() {
        return btnJoin;
    }
}
