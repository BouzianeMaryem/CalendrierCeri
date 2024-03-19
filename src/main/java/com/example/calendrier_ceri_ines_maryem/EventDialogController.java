package com.example.calendrier_ceri_ines_maryem;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.Stage;

public class EventDialogController {
    @FXML
    private TextField dateDebutField, dateFinField, heureDebutField, heureFinField, summaryField, typeField, matiereField, enseignantField,salleField,TDField,Couleur;
    @FXML
    private ColorPicker couleurPicker;
    private Controller mainController;

    public void setMainController(Controller controller) {
        this.mainController = controller;
        System.out.println("MainController est défini."); // Ou utilisez un breakpoint ici
    }

    @FXML
    public void initialize() {
        applyDateTextFormatter(dateDebutField);
        applyDateTextFormatter(dateFinField);
        applyTimeTextFormatter(heureDebutField);
        applyTimeTextFormatter(heureFinField);

    }



    private void applyTimeTextFormatter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 2 && !oldValue.contains(":")) {
                textField.setText(newValue + ":");
            } else if (newValue.length() > 5) {
                // Pour éviter d'entrer plus de caractères que le format HH:mm le permet
                textField.setText(oldValue);
            }
        });
    }

    private void applyDateTextFormatter(TextField textField) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        TextFormatter<String> dateFormatterTextFormatter = new TextFormatter<>(
                change -> {
                    String newText = change.getControlText().substring(0, change.getRangeStart()) +
                            change.getText() +
                            change.getControlText().substring(change.getRangeEnd());
                    if (newText.length() == 2 || newText.length() == 5) {
                        // Just after the user types the second or fifth character, add a slash if it's not already there
                        if (!newText.endsWith("/")) {
                            change.setText(change.getText() + "/");
                            int newCaretPos = change.getCaretPosition() + 1; // Move caret forward to account for the slash
                            change.setCaretPosition(newCaretPos);
                            change.setAnchor(newCaretPos);
                        }
                    }
                    return change;
                }
        );

        textField.setTextFormatter(dateFormatterTextFormatter);
    }

    @FXML
    private void handleSubmit() {
        try {
            if (dateDebutField.getText().isEmpty() || dateFinField.getText().isEmpty() ||
                    heureDebutField.getText().isEmpty() || heureFinField.getText().isEmpty() ||
                    summaryField.getText().isEmpty() || typeField.getText().isEmpty() ||
                    matiereField.getText().isEmpty() || enseignantField.getText().isEmpty()) {
                showAlert("Tous les champs sont obligatoires.");
                return;
            }

            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate dateFin = LocalDate.parse(dateFinField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime heureDebut = LocalTime.parse(heureDebutField.getText());
            LocalTime heureFin = LocalTime.parse(heureFinField.getText());
            String summary = summaryField.getText();
            String type = typeField.getText();
            String matiere = matiereField.getText();
            String enseignant = enseignantField.getText();
            String salle = salleField.getText();
            String td = TDField.getText();
            // Convertir la couleur sélectionnée en format hexadécimal
            Color couleur = couleurPicker.getValue();
            String couleurHex = String.format("#%02X%02X%02X",
                    (int) (couleur.getRed() * 255),
                    (int) (couleur.getGreen() * 255),
                    (int) (couleur.getBlue() * 255));

            // Générez ou définissez un ID pour l'événement si nécessaire
            int id = generateEventId(); // Cette fonction doit être définie pour générer un ID unique

            // Construction de la chaîne JSON manuellement
            String jsonEvent = String.format(
                    "{\n" +
                            "    \"TD\": \"%s\",\n" +
                            "    \"DateFin\": \"%s\",\n" +
                            "    \"Type\": \"%s\",\n" +
                            "    \"HeureFin\": \"%s\",\n" +
                            "    \"Salle\": \"%s\",\n" +
                            "    \"Summary\": \"%s\",\n" +
                            "    \"Matière\": \"%s\",\n" +
                            "    \"Enseignant\": \"%s\",\n" +
                            "    \"ID\": %d,\n" +
                            "    \"DateDebut\": \"%s\",\n" +
                            "    \"HeureDebut\": \"%s\",\n" +
                            "    \"Couleur\": \"%s\"\n" +
                            "}",
                    td, dateFin, type, heureFin, salle, summary, matiere, enseignant, id, dateDebut, heureDebut, couleurHex);

            // Écriture de la chaîne JSON dans un fichier
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("events.json", true))) {
                writer.write(jsonEvent);
                writer.newLine();
            }

            Stage stage = (Stage) dateDebutField.getScene().getWindow();
            stage.close();
        } catch (DateTimeParseException e) {
            showAlert("Erreur de formatage de la date ou de l'heure.");
        } catch (IOException e) {
            showAlert("Erreur lors de l'écriture dans le fichier.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int generateEventId() {
        // Implémentation dépend de votre logique d'application
        return 0; // Remplacer par votre logique d'ID
    }
}
