package it.polimi.ingsw.galaxytrucker.view.Gui;

import it.polimi.ingsw.galaxytrucker.controller.ClientController;
import it.polimi.ingsw.galaxytrucker.model.PlayerScore;
import it.polimi.ingsw.galaxytrucker.network.client.ClientModel;
import it.polimi.ingsw.galaxytrucker.view.Gui.Abstract.GenericSceneController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ScoresController extends GenericSceneController {

    private GuiJavaFx mainViewController;
    private ClientController clientController;
    private ClientModel mymodel;
    private Stage primaryStage;
    private MusicManager musicManager;

    @FXML private ImageView avatar1;
    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;
    @FXML private Label nome1;
    @FXML private Label nome2;
    @FXML private Label nome3;
    @FXML private Label nome4;
    @FXML private Label bellezza1;
    @FXML private Label bellezza2;
    @FXML private Label bellezza3;
    @FXML private Label bellezza4;
    @FXML private Label ordine1;
    @FXML private Label ordine2;
    @FXML private Label ordine3;
    @FXML private Label ordine4;
    @FXML private Label merci1;
    @FXML private Label merci2;
    @FXML private Label merci3;
    @FXML private Label merci4;
    @FXML private Label perdite1;
    @FXML private Label perdite2;
    @FXML private Label perdite3;
    @FXML private Label perdite4;
    @FXML private Label soldi1;
    @FXML private Label soldi2;
    @FXML private Label soldi3;
    @FXML private Label soldi4;
    @FXML private Label totale1;
    @FXML private Label totale2;
    @FXML private Label totale3;
    @FXML private Label totale4;




    @Override
    public void initialSetup(GuiJavaFx mainViewController, ClientController clientController, ClientModel mymodel, Stage primaryStage, MusicManager musicManager) {
        this.mainViewController = mainViewController;
        this.clientController = clientController;
        this.mymodel = mymodel;
        this.primaryStage = primaryStage;
        this.musicManager = musicManager;

    }

    public void updateScores(ArrayList<PlayerScore> scores){
        List<ImageView> avatars = List.of(avatar1,avatar2, avatar3, avatar4);
        List<Label> names = List.of(nome1,nome2, nome3, nome4);
        List<Label> bellezze = List.of(bellezza1,bellezza2, bellezza3, bellezza4);
        List<Label> ordine = List.of(ordine1,ordine2, ordine3, ordine4);
        List<Label> perdite = List.of(perdite1,perdite2, perdite3, perdite4);
        List<Label> merci = List.of(merci1,merci2, merci3, merci4);
        List<Label> soldi = List.of(soldi1,soldi2, soldi3, soldi4);
        List<Label> totale = List.of(totale1,totale2, totale3, totale4);
        for(int i =0 ; i < scores.size(); i++){
            switch (mymodel.getPlayerInfoByNickname(scores.get(i).getNickName()).getColor()){
                case RED ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarRosso.png").toExternalForm()));
                case YELLOW ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarGiallo.png").toExternalForm()));
                case BLUE ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarBlu.png").toExternalForm()));
                case GREEN ->  avatars.get(i).setImage(new Image(getClass().getResource("/it/polimi/ingsw/galaxytrucker/galaxy_trucker_imgs/pedineSegnalini/AvatarVerde.png").toExternalForm()));
            }
            names.get(i).setText(scores.get(i).getNickName());
            bellezze.get(i).setText(String.valueOf(scores.get(i).getBestLookingShipScore()));
            ordine.get(i).setText(String.valueOf(scores.get(i).getFinishOrderScore()));
            perdite.get(i).setText("- "+String.valueOf(scores.get(i).getLossesScore()));
            merci.get(i).setText(String.valueOf(scores.get(i).getGoodRewardScore()));
            soldi.get(i).setText(String.valueOf(scores.get(i).getCredits()));
            totale.get(i).setText(String.valueOf(scores.get(i).getTotalScore()));

        }
    }

    @Override
    public void ShowGenericMessage(String message) {

    }

    @Override
    public String pageName() {
        return "Classifica";
    }
}
