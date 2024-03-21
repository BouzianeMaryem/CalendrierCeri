package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    EmailFormController formManager = new EmailFormController();


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

        for (CalendarEvent event : events) {
            LocalTime heureDebut = event.getHeureDebut() == null ? LocalTime.of(gridStartHour, 0) : event.getHeureDebut();
            LocalTime heureFin = event.getHeureFin() == null ? LocalTime.of(22, 0) : event.getHeureFin();
            String key = heureDebut.toString();

            int startRowIndex = (heureDebut.getHour() - gridStartHour) * 2 + (heureDebut.getMinute() / 30) + 9;
            int endRowIndex = (heureFin.getHour() - gridStartHour) * 2 + (heureFin.getMinute() / 30) + 9;
            int rowSpan = endRowIndex - startRowIndex;

            HBox hbox = timeSlotToHBoxMap.computeIfAbsent(key, k -> new HBox(2));
            hbox.setMinWidth(100);

            // Création du bouton de l'événement avec le nom de l'enseignant
            String eventText = event.getSummary() +
                    (event.isAllDayEvent() ? "" : "\n" + heureDebut + " - " + heureFin) +
                    "\n" + event.getEnseignant(); // Utilisation de getEnseignant() pour inclure le nom de l'enseignant
            Button eventButton = new Button(eventText);
            eventButton.setMaxWidth(Double.MAX_VALUE);
            eventButton.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(eventButton, Priority.ALWAYS);
            eventButton.setStyle("-fx-text-fill: white;");

            // Ajout d'un gestionnaire d'événements pour traiter les clics sur le nom de l'enseignant
            eventButton.setOnAction(e -> {
                // Insérez ici la logique à exécuter lors du clic sur le nom de l'enseignant
                System.out.println("Enseignant cliqué: " + event.getEnseignant());
            });

            // Configuration du style et du tooltip
            String styleClass = switch (event.getType()) {
                case "TP" -> "event-tp";
                case "CM" -> "event-cm";
                case "TD" -> "event-td";
                case "Evaluation" -> "event-evaluation";
                default -> "event-autre";
            };
            eventButton.getStyleClass().add(styleClass);
            Tooltip tooltip = createEventTooltip(event);
            Tooltip.install(eventButton, tooltip);

            HBox.setMargin(eventButton, new Insets(2));
            hbox.getChildren().add(eventButton);

            if (!dynamicGridPane.getChildren().contains(hbox)) {
                GridPane.setConstraints(hbox, 1, startRowIndex, 1, rowSpan);
                GridPane.setVgrow(hbox, Priority.ALWAYS);
                dynamicGridPane.getChildren().add(hbox);
            }
            eventButton.setOnAction(e -> formManager.showEmailForm(event));
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
