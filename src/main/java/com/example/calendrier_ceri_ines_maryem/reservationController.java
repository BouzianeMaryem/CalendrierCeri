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

import java.io.File;
import java.io.IOException;
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
import java.util.Optional;

public class reservationController {

    @FXML
    private ChoiceBox<String> nomSalleChoiceBox;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private TextField heureDebutTextField;

    @FXML
    private TextField heureFinTextField;
    @FXML
    private Button checkAvailabilityButton;




    @FXML
    private void handleCheckAvailability() {
        String selectedSalle = nomSalleChoiceBox.getValue();
        if ("S3".equals(selectedSalle) || "amphi ada".equals(selectedSalle)) {
            if (dateDebutPicker.getValue() == null || heureDebutTextField.getText().isEmpty() || heureFinTextField.getText().isEmpty()) {

                return;
            }

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                String dateDebutStr = dateDebutPicker.getValue().format(dateFormatter);
                String dateFinStr = dateDebutPicker.getValue().format(dateFormatter);
                LocalTime heureDebutUser = LocalTime.parse(heureDebutTextField.getText(), timeFormatter);
                LocalTime heureFinUser = LocalTime.parse(heureFinTextField.getText(), timeFormatter);

                String url;
                String fileName;
                String fileName2;
                if ("S3".equals(selectedSalle)) {
                    url = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def5020067c53113622ab5cb8df37352ce1f7896738f2e260377892d3bcd6f36372c0eeab08e34522c71ea92310adb743d42ed0632d00cd75dd1a9baeb1a3eccc2faf484634a99f1731089a4ab923402b410076813d14e9eda46d0";
                    fileName2 = "s3-reservation.json";
                    fileName= "events-salle-S3.json";

                } else {
                    url = "https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def502004c59af25cab3f4851f0984ff3a68c7fd573df0f5e81d580f9ee7891f97296f848b0c22792d744abed70631908bdd6888904453c5e1fb34350435d5c614aa681093db1c96698851bc2d2a3a9eb51171bc8d035969707950eb";
                    fileName2 = "amphiAda-reservation.json";
                    fileName = "events-salle-ADA.json";

                }

                CalendarService.downloadAndSaveJson(url, fileName);

                List<CalendarEvent> events = EventsCreateur.creationListEventsJson(fileName);
                List<CalendarEvent> eventsres = EventsCreateur.creationListEventsJson(fileName2);
                events.addAll(eventsres);
                Optional<CalendarEvent> overlappingEvent = events.stream().filter(event -> {
                    LocalTime eventStartTime = event.getHeureDebut();
                    LocalTime eventEndTime = event.getHeureFin();

                    LocalDate eventStartDate = event.getDateDebut();
                    LocalDate eventEndDate = event.getDateFin();

                    LocalDate parsedDateDebut = LocalDate.parse(dateDebutStr, dateFormatter);
                    LocalDate parsedDateFin = LocalDate.parse(dateFinStr, dateFormatter);

                    return eventStartDate.equals(parsedDateDebut) &&
                            eventEndDate.equals(parsedDateFin) &&
                            ((heureDebutUser.isBefore(eventEndTime) && heureDebutUser.isAfter(eventStartTime)) ||
                                    (heureFinUser.isAfter(eventStartTime) && heureFinUser.isBefore(eventEndTime)) ||
                                    (heureDebutUser.equals(eventStartTime) && heureFinUser.equals(eventEndTime)) ||
                                    (heureDebutUser.isBefore(eventStartTime) && heureFinUser.isAfter(eventEndTime)));
                }).findFirst();


                if (overlappingEvent.isPresent()) {
                    CalendarEvent event = overlappingEvent.get();
                    LocalTime eventStartTime = event.getHeureDebut();
                    LocalTime eventEndTime = event.getHeureFin();
                    showAlert("Non disponible", "La salle n'est pas disponible pour les dates et heures sélectionnées.", Alert.AlertType.ERROR);
                } else {
                    performReservation();
                    showAlert("Réservation effectuée", "La salle a été réservée avec succès.", Alert.AlertType.INFORMATION);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            closeCurrentWindow();
        }
    }
    @FXML
    private void performReservation() {
                String selectedSalle = nomSalleChoiceBox.getValue();
                if ("S3".equals(selectedSalle) || "amphi ada".equals(selectedSalle)) {
                    try {
                        JSONObject eventJson = new JSONObject();
                        eventJson.put("DateDebut", dateDebutPicker.getValue().toString());
                        eventJson.put("DateFin",  dateDebutPicker.getValue().toString());
                        eventJson.put("HeureDebut", heureDebutTextField.getText());
                        eventJson.put("HeureFin", heureFinTextField.getText());
                        eventJson.put("HeureFin", heureFinTextField.getText());
                        eventJson.put("Summary", "Reservation de salles");
                        eventJson.put("Matiere", "Reservation de salles");
                        SessionManager sessionManager = SessionManager.getInstance();
                        eventJson.put("Enseignant", sessionManager.getNom()+" "+sessionManager.getPrenom());
                        eventJson.put("Salle", selectedSalle);

                        String fileName = "S3".equals(selectedSalle) ? "s3-reservation.json" : "amphiAda-reservation.json";
                        File file = new File(fileName);

                        if (!file.exists() || file.length() == 0) {
                            String content = "[\n  " + eventJson.toString(2) + "\n]";
                            Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.CREATE);
                        } else {
                            String content = new String(Files.readAllBytes(Paths.get(fileName)));
                            content = content.substring(0, content.length() - 1) + ",\n  " + eventJson.toString(2) + "\n]";
                            Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
