package org.polimi.ingsw.galaxytrucker;


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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TileCardsJsonUtility extends Application {

    @FXML private StackPane contentPane;  // Area centrale che cambia contenuto
    private Pane pedinePane;
    private Pane cartePane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creazione del layout principale
        BorderPane root = new BorderPane();

        // Creazione della barra menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Seleziona Vista");
        MenuItem pedineMenuItem = new MenuItem("Gestisci Pedine");
        MenuItem carteMenuItem = new MenuItem("Gestisci Carte");
        menu.getItems().addAll(pedineMenuItem, carteMenuItem);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);  // Aggiunge la barra menu in alto

        // StackPane per contenere le viste
        contentPane = new StackPane();
        root.setCenter(contentPane);  // Lo posiziona al centro

        // Caricamento dei due pannelli dagli FXML
        try {
            pedinePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/TilePanel.fxml"));
            cartePane = FXMLLoader.load(getClass().getResource("JsonUtilityPages/CardsPanel.fxml"));
        } catch (IOException e) {
            System.err.println("Errore nel caricamento degli FXML: " + e.getMessage());
            e.printStackTrace();
            return;  // Se fallisce, fermiamo l'avvio
        }

        // Mostriamo inizialmente il pannello delle pedine
        contentPane.getChildren().add(pedinePane);

        // Azioni per cambiare vista
        pedineMenuItem.setOnAction(e -> showPedinePane());
        carteMenuItem.setOnAction(e -> showCartePane());

        // Configurazione della scena e della finestra principale
        primaryStage.setTitle("Gestione Oggetti di Gioco");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @FXML
    private void showPedinePane() {
        contentPane.getChildren().setAll(pedinePane);
    }

    @FXML
    private void showCartePane() {
        contentPane.getChildren().setAll(cartePane);
    }

    public static void main(String[] args) {
        launch(args);
    }


    @FXML private ImageView imageViewFronte;
    @FXML private ImageView imageViewRetro;
    @FXML private TextField textFieldNomeCarta;
    @FXML private ComboBox<String> comboBoxTipoCarta;
    private File immagineFronte;
    private File immagineRetro;

    @FXML
    private void handleLoadFronte() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineFronte = file;
            imageViewFronte.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleLoadRetro() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineRetro = file;
            imageViewRetro.setImage(new Image(file.toURI().toString()));
        }
    }


    public void handleSavePedina(ActionEvent actionEvent) {
    }

    public void handleSaveCarta(ActionEvent actionEvent) {
    }
}






















/*import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class TileCardsJsonUtility extends Application {


    @FXML private ImageView imageView;
    @FXML private TextField textFieldNome;
    @FXML private ComboBox<String> comboBoxTipo;
    @FXML private Button btnCarica;
    @FXML private Button btnSalva;

    private File immagineSelezionata;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("JsonUtility.fxml"));
        VBox root = loader.load();
        primaryStage.setTitle("Gestione Pedine");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            immagineSelezionata = file;
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleSavePedina() {
        String nome = textFieldNome.getText();
        String tipo = comboBoxTipo.getValue();

        if (nome.isEmpty() || immagineSelezionata == null || tipo == null) {
            System.out.println("Compila tutti i campi!");
            return;
        }

        System.out.println("Pedina Salvata!");
        System.out.println("Nome: " + nome);
        System.out.println("Tipo: " + tipo);
        System.out.println("Immagine: " + immagineSelezionata.getAbsolutePath());
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/
