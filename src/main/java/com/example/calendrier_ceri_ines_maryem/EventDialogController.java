package com.example.calendrier_ceri_ines_maryem;

        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.image.Image;
        import javafx.scene.layout.HBox;
        import javafx.scene.paint.Color;
        import javafx.stage.Stage;

        import java.io.File;
        import java.io.IOException;
        import java.io.RandomAccessFile;
        import java.time.LocalDate;
        import java.time.LocalTime;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.List;

public class EventDialogController {

    @FXML private DatePicker dateDebutField;
    @FXML private TextField heureDebutField;
    @FXML private TextField heureFinField;
    @FXML private TextField summaryField;
    @FXML
    private ComboBox<String> salleField;
    @FXML private ColorPicker couleurPicker;
    private List<String> eventList = new ArrayList<>();


    private PrincipaleControlleur mainController;


    public void setMainController(PrincipaleControlleur controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSubmit() {
        try {
            SessionManager sessionManager = SessionManager.getInstance();
            LocalDate dateDebut = dateDebutField.getValue();
            LocalDate dateFin = dateDebutField.getValue();
            LocalTime heureDebut = LocalTime.parse(heureDebutField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime heureFin = LocalTime.parse(heureFinField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            Color couleur = couleurPicker.getValue();
            String couleurHex = String.format("#%02X%02X%02X",
                    (int) (couleur.getRed() * 255),
                    (int) (couleur.getGreen() * 255),
                    (int) (couleur.getBlue() * 255));
            String summary = summaryField.getText();
            String salle = salleField.getValue();
            String enseignant = sessionManager.getNom()+" "+sessionManager.getPrenom();
            String jsonEvent = String.format(
                    "{\n" +

                            "    \"DateFin\": \"%s\",\n" +
                            "    \"HeureFin\": \"%s\",\n" +
                            "    \"Salle\": \"%s\",\n" +
                            "    \"Summary\": \"%s\",\n" +
                            "    \"Enseignant\": \"%s\",\n" +
                            "    \"DateDebut\": \"%s\",\n" +
                            "    \"HeureDebut\": \"%s\",\n" +
                            "    \"Couleur\": \"%s\"\n" +
                            "}",
                     dateFin, heureFin, salle, summary, enseignant, dateDebut, heureDebut, couleurHex);
            eventList.add(jsonEvent);
            File file = new File("events.json");
            boolean fileExists = file.exists() && file.length() > 0;
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                long pos = fileExists ? file.length() - 1 : 0;
                raf.seek(pos);
                if (fileExists) {
                    raf.writeBytes(",\n");
                } else {
                    raf.writeBytes("[\n");
                }
                raf.writeBytes(jsonEvent + "\n]");
            }
            showAddYes();
        } catch (Exception e) {
            showAlert("Une erreur est survenue: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAddYes() throws IOException {
        FXMLLoader loader = new FXMLLoader(EmailFormController.class.getResource("Alert/ajoutOK.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        Image icon = new Image(EmailFormController.class.getResourceAsStream("Alert/bell.png"));
        stage.getIcons().add(icon);
        stage.setTitle("notification !");
        stage.showAndWait();
    }
    @FXML
    private void minimizeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void maximizeRestoreWindow(ActionEvent event) {
        Stage stage = ((Stage)((Button)event.getSource()).getScene().getWindow());
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }
    @FXML
    private HBox titleBar;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage)((HBox)event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void closeWindowAutomatically() {
        Stage stage = (Stage) salleField.getScene().getWindow();
        stage.close();
    }
}
