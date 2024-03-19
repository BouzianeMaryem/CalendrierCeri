package com.example.calendrier_ceri_ines_maryem;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SemaineControlleur {
    Map<LocalDate, List<CalendarEvent>> eventsByDate = new HashMap<>();
    private List<CalendarEvent> events;
    @FXML
    private Label lunDate;

    @FXML
    private Label marDate;

    @FXML
    private Label merDate;

    @FXML
    private Label jeuDate;

    @FXML
    private Label venDate;

    @FXML
    private Button previousWeekButton;

    @FXML
    private Button nextWeekButton;

    @FXML
    private Text displayedWeekText;
    @FXML
    private GridPane dynamicGridPane;
    private LocalDate startDate;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public void setEvents(List<CalendarEvent> events) {
        for (CalendarEvent event : events) {
            LocalDate date = event.getDateDebut();
            eventsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
        }
        updateGridWithEvents();
    }

    public void initialize() {
        startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        updateDisplayedWeek();
    }


    private void clearGrid() {
        dynamicGridPane.getChildren().removeIf(child -> child instanceof HBox && GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) >= 9);
    }

    private void updateDisplayedWeek() {
        LocalDate endDate = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        displayedWeekText.setText(String.format("Du %s au %s", startDate.format(dateFormatter), endDate.format(dateFormatter)));
        lunDate.setText(String.valueOf(startDate.getDayOfMonth()));
        marDate.setText(String.valueOf(startDate.plusDays(1).getDayOfMonth()));
        merDate.setText(String.valueOf(startDate.plusDays(2).getDayOfMonth()));
        jeuDate.setText(String.valueOf(startDate.plusDays(3).getDayOfMonth()));
        venDate.setText(String.valueOf(startDate.plusDays(4).getDayOfMonth()));
    }

    private void updateGridWithEvents() {
        clearGrid();
        LocalDate currentDate = startDate;
        for (int i = 0; i < 5; i++) {
            List<CalendarEvent> dailyEvents = eventsByDate.getOrDefault(currentDate, new ArrayList<>());
            if (!dailyEvents.isEmpty()) {
                displayEvent(dailyEvents);
            }
            currentDate = currentDate.plusDays(1);
        }
    }



    @FXML
    private void loadPreviousWeek() {
        startDate = startDate.minusWeeks(1);
        updateDisplayedWeek();
        clearGrid();
        updateGridWithEvents();
    }

    @FXML
    private void loadNextWeek() {
        startDate = startDate.plusWeeks(1);
        updateDisplayedWeek();
        clearGrid();
        updateGridWithEvents();
    }




    public void displayEvent(List<CalendarEvent> events) {
        int gridStartHour = 8;
        int totalGridHours = 14;
        Map<String, HBox> timeSlotToHBoxMap = new HashMap<>();

        for (CalendarEvent event : events) {
            LocalDate dateDebut = event.getDateDebut();
            LocalTime heureDebut = event.getHeureDebut() == null ? LocalTime.of(gridStartHour, 0) : event.getHeureDebut();
            LocalTime heureFin = event.getHeureFin() == null ? LocalTime.of(gridStartHour + totalGridHours - 1, 59) : event.getHeureFin();

            int dayIndex = dateDebut.getDayOfWeek().getValue() - 1; // Lundi = 0
            String key = dayIndex + "_" + heureDebut;

            int startRowIndex = (heureDebut.getHour() - gridStartHour) * 2 + (heureDebut.getMinute() / 30) + 9;
            int endRowIndex = (heureFin.getHour() - gridStartHour) * 2 + (heureFin.getMinute() / 30) + 9;
            int rowSpan = endRowIndex - startRowIndex;

            HBox hbox = timeSlotToHBoxMap.computeIfAbsent(key, k -> new HBox(2));
            hbox.setMinWidth(100);

            Button eventButton = new Button(event.getSummary() + (event.isAllDayEvent() ? "" : "\n" + heureDebut + " - " + heureFin));
            eventButton.setMaxWidth(Double.MAX_VALUE);
            eventButton.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(eventButton, Priority.ALWAYS);
            eventButton.setStyle("-fx-text-fill: white;");
            HBox.setMargin(eventButton, new Insets(2));
            hbox.getChildren().add(eventButton);
            //mes logs :

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


            if (!dynamicGridPane.getChildren().contains(hbox)) {
                GridPane.setConstraints(hbox, dayIndex + 1, startRowIndex, 1, rowSpan);
                GridPane.setVgrow(hbox, Priority.ALWAYS);
                dynamicGridPane.getChildren().add(hbox);
            }
        }
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
