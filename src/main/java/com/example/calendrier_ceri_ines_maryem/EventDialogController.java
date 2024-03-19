package com.example.calendrier_ceri_ines_maryem;
import java.time.format.DateTimeParseException;
import javafx.scene.control.Alert;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.stage.Stage;
public class EventDialogController {
    @FXML
    private TextField dateDebutField, dateFinField, heureDebutField, heureFinField, summaryField, typeField, matiereField, enseignantField,salleField,TDField,Couleur;
    private Controller mainController;

    public void setMainController(Controller controller) {
        this.mainController = controller;
        System.out.println("MainController est défini."); // Ou utilisez un breakpoint ici
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

            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText());
            LocalDate dateFin = LocalDate.parse(dateFinField.getText());
            LocalTime heureDebut = LocalTime.parse(heureDebutField.getText());
            LocalTime heureFin = LocalTime.parse(heureFinField.getText());
            String summary = summaryField.getText();
            String type = typeField.getText();
            String matiere = matiereField.getText();
            String enseignant = enseignantField.getText();
            String salle = salleField.getText();
            String td = TDField.getText();
            String couleur = Couleur.getText();

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
                                    "    \"HeureDebut\": \"%s\"\n" +
                                    "    \"Couleur\": \"%s\"\n" +

                                    "}",
                    td, dateFin, type, heureFin, salle, summary, matiere, enseignant, id, dateDebut, heureDebut,couleur);

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
