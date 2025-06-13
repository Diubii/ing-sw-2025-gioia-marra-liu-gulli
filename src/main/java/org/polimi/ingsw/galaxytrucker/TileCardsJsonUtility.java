package org.polimi.ingsw.galaxytrucker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.polimi.ingsw.galaxytrucker.enums.*;
import org.polimi.ingsw.galaxytrucker.model.Planet;
import org.polimi.ingsw.galaxytrucker.model.Projectile;
import org.polimi.ingsw.galaxytrucker.model.adventurecards.*;
import org.polimi.ingsw.galaxytrucker.model.essentials.Component;
import org.polimi.ingsw.galaxytrucker.model.essentials.Good;
import org.polimi.ingsw.galaxytrucker.model.essentials.Tile;
import org.polimi.ingsw.galaxytrucker.model.essentials.components.*;
import org.polimi.ingsw.galaxytrucker.visitors.components.ComponentNameVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Utility used during development to configure the Json for all the Tiles
 */
public class TileCardsJsonUtility extends Application {

    //Condivisi
    @FXML
    private StackPane contentPane;  // Area centrale che cambia contenuto


    //Tiles
    private Pane tilePane;
    @FXML
    private TextField tileId;
    @FXML
    private TextField extraVal;
    @FXML
    private ComboBox<String> tileComponentType;
    @FXML
    private ComboBox<String> comboBoxTopConn;
    @FXML
    private ComboBox<String> comboBoxRConn;
    @FXML
    private ComboBox<String> comboBoxBotConn;
    @FXML
    private ComboBox<String> comboBoxLConn;
    @FXML
    private ComboBox<String> centralHousingUnitColor;
    @FXML
    private CheckBox specialCargo;


    @FXML
    private ImageView tileImageView;
    private Image tileImage;

    //Carte
    private Pane cartePane;
    @FXML
    private TextField cardId;
    @FXML
    private ImageView imageViewFronte;
    private File immagineFronte;
    @FXML
    private ImageView imageViewRetro;
    private File immagineRetro;

    ObjectMapper mapper = new ObjectMapper();

    //Lista di Tiles
    ArrayList<Tile> tiles = new ArrayList<>();
    int tileIndex = 0;
    ComponentNameVisitor componentNameVisitor = new ComponentNameVisitor();


    //Lista di Carte
    ArrayList<AdventureCard> cards = new ArrayList<>();
    int cardIndex = 0;
    //<editor-fold desc="Condiviso">


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creazione del layout principale
        BorderPane root = new BorderPane();

        // Creazione della barra menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Seleziona Vista");
        MenuItem tileMenuItem = new MenuItem("Gestisci Tiles");
        MenuItem carteMenuItem = new MenuItem("Gestisci Carte");
        menu.getItems().addAll(tileMenuItem, carteMenuItem);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);  // Aggiunge la barra menu in alto

        // StackPane per contenere le viste
        contentPane = new StackPane();
        root.setCenter(contentPane);  // Lo posiziona al centro

        // Caricamento dei due pannelli dagli FXML
        try {
            tilePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/TilePanel.fxml"));
            cartePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/CardsPanel.fxml"));
        } catch (IOException e) {
            System.err.println("Errore nel caricamento degli FXML: " + e.getMessage());
            e.printStackTrace();
            return;  // Se fallisce, fermiamo l'avvio
        }

        // Mostriamo inizialmente il pannello delle pedine
        contentPane.getChildren().add(tilePane);

        // Azioni per cambiare vista
        tileMenuItem.setOnAction(e -> showPedinePane());
        carteMenuItem.setOnAction(e -> showCartePane());

        // Configurazione della scena e della finestra principale
        primaryStage.setTitle("Gestione Oggetti di Gioco");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

    }

    @FXML
    private void showPedinePane() {
        contentPane.getChildren().setAll(tilePane);
    }

    @FXML
    private void showCartePane() {
        contentPane.getChildren().setAll(cartePane);
    }

    public static void main(String[] args) {
        launch(args);
    }


    //</editor-fold>

    //<editor-fold desc="Per Tiles">

    public void handleShowTileId(ActionEvent actionEvent) {
        String tileIdVal = tileId.getText();
        showTile(Integer.parseInt(tileIdVal) - 1);
    }

    public void handleNextTile(ActionEvent actionEvent) {
        String tileIdVal = tileId.getText();
        int currId = Integer.parseInt(tileIdVal);
        currId++;
        if (currId == 157) {
            currId = 1;
        }
        tileId.setText(String.valueOf(currId));
        showTile(currId - 1);
    }

    public void handlePrevTile(ActionEvent actionEvent) {
        String tileIdVal = tileId.getText();
        int currId = Integer.parseInt(tileIdVal);
        currId--;
        if (currId == 0) {
            currId = 156;
        }
        tileId.setText(String.valueOf(currId));
        showTile(currId - 1);
    }

    public void handleAddTileToList(ActionEvent actionEvent) {

        ArrayList<Connector> connectionsapp = new ArrayList<>();
        Tile tile;
        Component comp;

        String componentString = tileComponentType.getValue(); // Per ComboBox
        String tileIdVal = tileId.getText(); // Per TextField

        // Debug
        System.out.println("Valore ComboBox: " + componentString);
        System.out.println("Valore TextField: " + tileIdVal);


        try {
            connectionsapp.add(Connector.fromString(comboBoxTopConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxRConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxBotConn.getValue()));
            connectionsapp.add(Connector.fromString(comboBoxLConn.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connectors: " + connectionsapp);

        switch (tileComponentType.getValue()) {
            case "BatterySlot":
                comp = new BatterySlot(Integer.parseInt(extraVal.getText()));
                break;
            case "Cannon":
                comp = new Cannon((float) 1.0);
                break;
            case "CentralHousingUnit":
                comp = new CentralHousingUnit(Color.fromString(centralHousingUnitColor.getValue()));
                break;
            case "DoubleCannon":
                comp = new DoubleCannon(false, (float) 2.0);
                break;
            case "DoubleEngine":
                comp = new DoubleEngine(false, 2);
                break;
            case "Engine":
                comp = new Engine(1);
                break;
            case "GenericCargoHolds":
                comp = new GenericCargoHolds(specialCargo.isSelected(), Integer.parseInt(extraVal.getText()));
                break;
            case "PurpleLifeSupportSystem":
                comp = new LifeSupportSystem(AlienColor.PURPLE);
                break;
            case "BrownLifeSupportSystem":
                comp = new LifeSupportSystem(AlienColor.BROWN);
                break;
            case "ModularHousingUnit":
                comp = new ModularHousingUnit();
                break;
            case "Shield":
                comp = new Shield(false);
                break;

            default:
                comp = new Component(true);
                break;
        }

        tile = new Tile(Integer.parseInt(tileIdVal), 0, connectionsapp, comp);
        tiles.set(Integer.parseInt(tileIdVal) - 1, tile);

        /* //FILLARE
        for(int i = 0; i< 157; i++){
            tile = new Tile(i,0,connectionsapp,comp);
            tiles.add( i ,tile);
        }*/

        tile.testPrint();
        System.out.println("tiles: " + tiles);
        //Salva Direttamente lista ogni volta
        ActionEvent e = new ActionEvent();
        handleSaveTileList(e);
        handleLoadTileList(e);
    }

    @FXML
    /**
     * Loads the image of the tile with the id specified in the TextBox
     */
    private void handleLoadTileImage() {
        String tileIdVal = tileId.getText();
        if (tileIdVal != null && !tileIdVal.equals("")) {
            String imagePath = "galaxy_trucker_imgs/tiles/GT-new_tiles_16_for web".concat(tileIdVal).concat(".jpg");
            // Caricare l'immagine dal classpath
            tileImage = new Image(getClass().getResourceAsStream(imagePath));
            tileImageView.setImage(tileImage);
        }
    }

    /**
     * Shows the tile with that id from the tiles ArrayList
     *
     * @param id
     */
    private void showTile(int id) {
        //Svuoto tutti campi così se non usati appaiono vuoti e si capisce
        extraVal.setText("n");
        centralHousingUnitColor.setValue(Color.EMPTY.getColorString());
        specialCargo.setSelected(false);

        tileComponentType.setValue(tiles.get(id).getMyComponent().accept(componentNameVisitor));
        comboBoxTopConn.setValue(tiles.get(id).getSides().get(0).getConnectorString());
        comboBoxRConn.setValue(tiles.get(id).getSides().get(1).getConnectorString());
        comboBoxBotConn.setValue(tiles.get(id).getSides().get(2).getConnectorString());
        comboBoxLConn.setValue(tiles.get(id).getSides().get(3).getConnectorString());
        handleLoadTileImage();

        switch (tiles.get(id).getMyComponent().accept(componentNameVisitor)) {
            case "BatterySlot":
                extraVal.setText(String.valueOf(((BatterySlot) tiles.get(id).getMyComponent()).getBatteriesLeft()));
                break;

            case "CentralHousingUnit":
                centralHousingUnitColor.setValue(((CentralHousingUnit) tiles.get(id).getMyComponent()).getColor().getColorString());
                break;
            case "GenericCargoHolds":
                extraVal.setText(String.valueOf(((GenericCargoHolds) tiles.get(id).getMyComponent()).getnMaxContainers()));
                specialCargo.setSelected(((GenericCargoHolds) tiles.get(id).getMyComponent()).isSpecial());
                break;


        }
    }

    public void handleSaveTileList(ActionEvent actionEvent) {
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/tiledata.json");
            String json = mapper.writeValueAsString(tiles);
            fos.write(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLoadTileList(ActionEvent actionEvent) {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/tiledata.json");
            tiles = mapper.readValue(fis, new TypeReference<ArrayList<Tile>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //</editor-fold>

    //<editor-fold desc=" Per Carte ">

    public void handleJsonCiascuna(ActionEvent actionEvent) {

        //Esempio Proiettili
        Projectile EsProiettile = new Projectile(ProjectileType.Meteor, ProjectileDirection.UP, ProjectileSize.Big);
        ArrayList<Projectile> EsempioProj = new ArrayList<>();
        EsempioProj.add(EsProiettile);
        EsempioProj.add(EsProiettile);

        //Esempio Merci
        Good EsGood = new Good(Color.BLUE);
        ArrayList<Good> EsGoods = new ArrayList<>();
        EsGoods.add(EsGood);
        EsGoods.add(EsGood);

        //Esempio Pianeti
        Planet EsPianeta = new Planet(false, EsGoods);
        ArrayList<Planet> EsPlanets = new ArrayList<>();
        EsPlanets.add(EsPianeta);
        EsPlanets.add(EsPianeta);

        cards.add(new AbandonedShip(1, 1, 1, "Nave abbandonata", false, 1, 1, false));
        cards.add(new AbandonedStation(1, 1, 2, "Stazione abbandonata", false, EsGoods, 8, false));
        cards.add(new CombatZone(1, 1, 2, "Zona Guerra", false, 2, 2, EsempioProj, true));
        cards.add(new Epidemic(2, 1, 2, "Epidemia", true, true));
        cards.add(new MeteorSwarm(1, 1, 1, "Meteoriti", false, EsempioProj, true));
        cards.add(new OpenSpace(1, 1, 1, "Spazio aperto", false, true));
        cards.add(new Pirates(1, 1, 1, "Pirati", false, 1, EsempioProj, 2, false));
        cards.add(new Planets(1, 1, 1, "Pianeti", false, EsPlanets, false));
        cards.add(new Slavers(2, 1, 1, "Schiavisti", false, 1, 1, 1, false));
        cards.add(new Smugglers(1, 1, 1, "Contrabbandieri", false, 2, 2, EsGoods, false));
        cards.add(new Stardust(1, 1, 1, "Polvere di stelle", false, true));


        ActionEvent e = new ActionEvent();
        handleSaveCardList(e);
        handleLoadCardList(e);

    }

    public void handleShowCardId(ActionEvent actionEvent) {

    }

    public void handleNextCard(ActionEvent actionEvent) {

    }

    public void handlePrevCard(ActionEvent actionEvent) {

    }

    public void handleAddCardToList(ActionEvent actionEvent) {

    }

    @FXML
    private void handleLoadFront() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineFronte = file;
            imageViewFronte.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleLoadBack() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineRetro = file;
            imageViewRetro.setImage(new Image(file.toURI().toString()));
        }
    }

    public void handleSaveCardList(ActionEvent actionEvent) {
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/cardsdata.json");
            String json = mapper.writerFor(new TypeReference<ArrayList<AdventureCard>>() {
            }).writeValueAsString(cards);
            fos.write(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLoadCardList(ActionEvent actionEvent) {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/cardsdata.json");
            cards = mapper.readValue(fis, new TypeReference<ArrayList<AdventureCard>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showCard(int id) {

    }
    //</editor-fold>
}

