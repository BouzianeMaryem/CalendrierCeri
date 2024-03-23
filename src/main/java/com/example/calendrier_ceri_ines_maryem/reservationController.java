package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class reservationController {

    @FXML
    private ChoiceBox<String> nomSalleChoiceBox;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private TextField heureDebutTextField;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private TextField heureFinTextField;
    @FXML
    private Button checkAvailabilityButton;


    @FXML
    private void handleCheckAvailabilityAndReservation() {
        String selectedSalle = nomSalleChoiceBox.getValue();
        if (dateDebutPicker.getValue() == null || heureDebutTextField.getText().isEmpty() || heureFinTextField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        try {
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateDebutPicker.getValue();
            LocalTime heureDebut = LocalTime.parse(heureDebutTextField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime heureFin = LocalTime.parse(heureFinTextField.getText(), DateTimeFormatter.ofPattern("HH:mm"));

            boolean isAvailable = checkAvailability(selectedSalle, dateDebut, heureDebut, dateFin, heureFin);

            if (isAvailable) {
                performReservation(selectedSalle, dateDebut, heureDebut, dateFin, heureFin);
                showAlert("Réservation effectuée", "La salle a été réservée avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Non disponible", "La salle n'est pas disponible pour les dates et heures sélectionnées.", Alert.AlertType.ERROR);
            }

        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format de date ou d'heure invalide.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors du traitement de votre demande.", Alert.AlertType.ERROR);
        }

        closeCurrentWindow();
    }


    private boolean checkAvailability(String salle, LocalDate dateDebut, LocalTime heureDebut, LocalDate dateFin, LocalTime heureFin) {

        String fileName = salle.equals("S3") ? "S3-reservation.json" : "amphiAda-reservation.json";
        List<CalendarEvent> events = EventsCreateur.creationListEventsJson(fileName);

        return events.stream().noneMatch(event -> {
            LocalDate eventDateDebut = event.getDateDebut();
            LocalDate eventDateFin = event.getDateFin();
            LocalTime eventHeureDebut = event.getHeureDebut();
            LocalTime eventHeureFin = event.getHeureFin();

            boolean isSameDay = !dateDebut.isAfter(eventDateFin) && !dateFin.isBefore(eventDateDebut);
            boolean isTimeOverlap = heureDebut.isBefore(eventHeureFin) && heureFin.isAfter(eventHeureDebut);

            return isSameDay && isTimeOverlap;
        });
    }

    private void performReservation(String salle, LocalDate dateDebut, LocalTime heureDebut, LocalDate dateFin, LocalTime heureFin) {
        try {
            JSONObject eventJson = new JSONObject();
            eventJson.put("DateDebut", dateDebut.toString());
            eventJson.put("DateFin", dateDebut.toString());
            eventJson.put("HeureDebut", heureDebut.toString());
            eventJson.put("HeureFin", heureFin.toString());
            eventJson.put("Summary", "Reservation de salles");
            eventJson.put("Matiere", "Reservation de salles");
            SessionManager sessionManager = SessionManager.getInstance();
            eventJson.put("Enseignant", sessionManager.getNom()+" "+sessionManager.getPrenom());
            eventJson.put("Salle", salle);


            String fileName = salle.equals("S3") ? "S3-reservation.json" : "amphiAda-reservation.json";
            Path filePath = Paths.get(fileName);

            List<JSONObject> events = new ArrayList<>();
            if (Files.exists(filePath)) {
                String content = new String(Files.readAllBytes(filePath));
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    events.add(jsonArray.getJSONObject(i));
                }
            }

            events.add(eventJson);

            JSONArray updatedJsonArray = new JSONArray(events);
            Files.write(filePath, updatedJsonArray.toString(2).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) checkAvailabilityButton.getScene().getWindow();
        stage.close();
    }
}
