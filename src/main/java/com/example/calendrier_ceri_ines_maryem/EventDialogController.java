package com.example.calendrier_ceri_ines_maryem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.stage.Stage;

public class EventDialogController {
/*
    private List<String> eventList = new ArrayList<>();

    @FXML
    private TextField heureDebutField, heureFinField, summaryField, typeField, matiereField, enseignantField,salleField,TDField,Couleur;

    @FXML
    DatePicker dateDebutField;

    @FXML
    private ColorPicker couleurPicker;
    private PrincipaleControlleur mainController;

    public void setMainController(PrincipaleControlleur controller) {
        this.mainController = controller;
        System.out.println("MainController est défini.");
    }

    @FXML
    public void initialize() {
        applyDateTextFormatter(dateDebutField);
        applyTimeTextFormatter(heureDebutField);
        applyTimeTextFormatter(heureFinField);

    }



    private void applyTimeTextFormatter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 2 && !oldValue.contains(":")) {
                textField.setText(newValue + ":");
            } else if (newValue.length() > 5) {
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
                        if (!newText.endsWith("/")) {
                            change.setText(change.getText() + "/");
                            int newCaretPos = change.getCaretPosition() + 1;
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
            if (dateDebutField.getText().isEmpty()  ||
                    heureDebutField.getText().isEmpty() || heureFinField.getText().isEmpty() ||
                    summaryField.getText().isEmpty() || typeField.getText().isEmpty()) {
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

            Color couleur = couleurPicker.getValue();
            String couleurHex = String.format("#%02X%02X%02X",
                    (int) (couleur.getRed() * 255),
                    (int) (couleur.getGreen() * 255),
                    (int) (couleur.getBlue() * 255));

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
                            "    \"DateDebut\": \"%s\",\n" +
                            "    \"HeureDebut\": \"%s\",\n" +
                            "    \"Couleur\": \"%s\"\n" +
                            "}",
                    td, dateFin, type, heureFin, salle, summary, matiere, enseignant, dateDebut, heureDebut, couleurHex);
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

            Stage stage = (Stage) dateDebutField.getScene().getWindow();
            stage.close();
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
*/
}
