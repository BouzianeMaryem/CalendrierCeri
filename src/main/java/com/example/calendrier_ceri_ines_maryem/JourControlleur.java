package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JourControlleur {

    @FXML
    private Text displayedDayText;
    @FXML
    private GridPane dynamicGridPane;
    private LocalDate currentDate;
    private List<CalendarEvent> events;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initialize() {
        currentDate = LocalDate.now();
        updateDisplayedDay();
    }

    private void updateDisplayedDay() {
        displayedDayText.setText(currentDate.format(dateFormatter));
    }

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
        updateGridWithEvents();
    }

    private void clearGrid() {
        dynamicGridPane.getChildren().removeIf(child -> child instanceof HBox && GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) >= 9);
    }


    private void updateGridWithEvents() {
        clearGrid();
        List<CalendarEvent> dailyEvents = this.events.stream()
                .filter(event -> event.getDateDebut().equals(currentDate))
                .collect(Collectors.toList());
        displayEvent(dailyEvents);
    }


    public void displayEvent(List<CalendarEvent> events) {
        int gridStartHour = 8;
        Map<String, HBox> timeSlotToHBoxMap = new HashMap<>();

        events.sort((e1, e2) -> {
            boolean isE1ReservationSalle = "Reservation de salles".equals(e1.getMatiere());
            boolean isE2ReservationSalle = "Reservation de salles".equals(e2.getMatiere());
            if (isE1ReservationSalle && !isE2ReservationSalle) return -1;
            if (!isE1ReservationSalle && isE2ReservationSalle) return 1;

            long durationE1 = ChronoUnit.HOURS.between(e1.getHeureDebut(), e1.getHeureFin());
            long durationE2 = ChronoUnit.HOURS.between(e2.getHeureDebut(), e2.getHeureFin());
            if (durationE1 == 4 && durationE2 != 4) return -1;
            if (durationE1 != 4 && durationE2 == 4) return 1;
            if (durationE1 == 3 && durationE2 != 3) return -1;
            if (durationE1 != 3 && durationE2 == 3) return 1;

            return e1.getHeureDebut().compareTo(e2.getHeureDebut());
        });


        for (CalendarEvent event : events) {
            LocalTime heureDebut = event.getHeureDebut() == null ? LocalTime.of(gridStartHour, 0) : event.getHeureDebut();
            LocalTime heureFin = event.getHeureFin() == null ? LocalTime.of(22, 0) : event.getHeureFin();
            String key = heureDebut.toString();

            int startRowIndex = (heureDebut.getHour() - gridStartHour) * 2 + (heureDebut.getMinute() / 30) + 9;
            int endRowIndex = (heureFin.getHour() - gridStartHour) * 2 + (heureFin.getMinute() / 30) + 9;
            int rowSpan = endRowIndex - startRowIndex;

            HBox hbox = timeSlotToHBoxMap.computeIfAbsent(key, k -> new HBox(2));
            hbox.setMinWidth(100);

            Button eventButton = new Button(event.getSummary() + (event.isAllDayEvent() ? "" : "\n" + heureDebut + " - " + heureFin));
            eventButton.setMaxWidth(Double.MAX_VALUE);
            eventButton.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(eventButton, Priority.ALWAYS);
            eventButton.setStyle("-fx-text-fill: black;");
            applyEventStyle(event,eventButton);
            Tooltip tooltip = createEventTooltip(event);
            Tooltip.install(eventButton, tooltip);
            HBox.setMargin(eventButton, new Insets(2));

            // je viens d'ajouter cette fonction pour lancer le mail
            eventButton.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/email_form.fxml"));
                    Parent root = loader.load();

                    EmailFormController controller = loader.getController();
                    controller.showEmailForm(event);

                } catch (IOException er) {
                    er.printStackTrace();
                }
            });
            hbox.getChildren().add(eventButton);

            if (!dynamicGridPane.getChildren().contains(hbox)) {
                GridPane.setConstraints(hbox, 1, startRowIndex, 1, rowSpan);
                GridPane.setVgrow(hbox, Priority.ALWAYS);
                dynamicGridPane.getChildren().add(hbox);
            }
        }
    }

    public void applyEventStyle(CalendarEvent event, Button eventButton) {
        if (event.getColor().equals("")) {
            String styleClass = switch (event.getType()) {
                case "TP" -> "event-tp";
                case "CM" -> "event-cm";
                case "TD" -> "event-td";
                case "Evaluation" -> "event-evaluation";
                default -> "event-autre";
            };
            eventButton.getStyleClass().add(styleClass);
        } else {
            String hexColor = event.getColor();

            double opacity = 0.9;

            eventButton.setStyle(String.format("-fx-background-color: %s%02x;",
                    hexColor, (int) (opacity * 255)));
        }
    }

    @FXML
    private void loadPreviousDay() {
        currentDate = currentDate.minusDays(1);
        updateDisplayedDay();
        clearGrid();
        updateGridWithEvents();
    }

    @FXML
    private void loadNextDay() {
        currentDate = currentDate.plusDays(1);
        updateDisplayedDay();
        clearGrid();
        updateGridWithEvents();
    }

    private Tooltip createEventTooltip(CalendarEvent event) {
        StringBuilder tooltipText = new StringBuilder();
        tooltipText.append("Résumé : ").append(event.getSummary()).append("\n");
        if (!event.isAllDayEvent()) {
            tooltipText.append("Début : ").append(event.getHeureDebut()).append("\n");
            tooltipText.append("Fin : ").append(event.getHeureFin()).append("\n");
        } else {
            tooltipText.append("Toute la journée\n");
        }
        tooltipText.append("Type : ").append(event.getType()).append("\n");
        tooltipText.append("Description : ").append(event.getSummary()).append("\n");
        tooltipText.append("Enseignant : ").append(event.getEnseignant()).append("\n");
        tooltipText.append("Matiere : ").append(event.getMatiere()).append("\n");
        tooltipText.append("Salle : ").append(event.getSalle()).append("\n");

        Tooltip tooltip = new Tooltip(tooltipText.toString());
        tooltip.setShowDelay(Duration.seconds(0.1));
        return tooltip;
    }

}
