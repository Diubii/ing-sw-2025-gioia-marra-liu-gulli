package it.polimi.ingsw.galaxytrucker.view.Gui.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;


/**
 * Controller for a single lobby row element in the GUI lobby list.
 * <p>
 * Displays basic information about a lobby (ID, host, number of players, type)
 * and provides a button to join the lobby.
 */
public class SingleLobbyInfoController {

    @FXML private Text txtId;
    @FXML private Text txtHost;
    @FXML private Text txtNumPlayer;
    @FXML private ImageView imgMatchType;
    @FXML private Button btnJoin;
    private Image imgType;

    /**
     * Sets the content of the lobby row with the given lobby information.
     *
     * @param ID              the unique identifier of the lobby
     * @param host            the nickname of the host player
     * @param playerNum       the current number of players in the lobby
     * @param maxPlayers      the maximum number of players allowed in the lobby
     * @param isLearningMatch {@code true} if the lobby is a tutorial/learning match;
     *                        {@code false} for a normal match
     */
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

    /**
     * Returns the join button associated with this lobby row.
     * Useful for registering external event handlers.
     *
     * @return the "Join" button
     */
    public Button getJoinButton() {
        return btnJoin;
    }
}
