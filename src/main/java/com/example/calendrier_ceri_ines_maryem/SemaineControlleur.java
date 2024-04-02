package com.example.calendrier_ceri_ines_maryem;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


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

    private PrincipaleControlleur mainController;

    public void setMainController(PrincipaleControlleur mainController) {
        this.mainController = mainController;
    }
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
    private static long calculateDurationInHours(CalendarEvent event) {
        if (event.getHeureDebut() != null && event.getHeureFin() != null) {
            return ChronoUnit.HOURS.between(event.getHeureDebut(), event.getHeureFin());
        }
        return 0;
    }


    public void displayEvent(List<CalendarEvent> events) {
        int gridStartHour = 8;
        int totalGridHours = 14;
        Map<String, HBox> timeSlotToHBoxMap = new HashMap<>();
        // filtre l'unicité des evenements
        //car j'ai identifié des doublons dans le lien source
        List<CalendarEvent> filteredEvents = events.stream()
                .collect(Collectors.toMap(
                        e -> e.getDateDebut() + "_" + e.getHeureDebut() + "_" + e.getHeureFin() + "_" + e.getSummary(),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                )).values().stream().collect(Collectors.toList());

       //regroupement selon la durée
        Map<String, List<CalendarEvent>> groupedEvents = filteredEvents.stream()
                .collect(Collectors.groupingBy(e -> e.getDateDebut() + "_" + e.getHeureDebut() + "_" + e.getHeureFin()));
        List<List<CalendarEvent>> sortedGroupedEvents = groupedEvents.values().stream()
                .sorted((list1, list2) -> {
                    boolean list1HasReservation = list1.stream().anyMatch(e -> "Reservation de salles".equals(e.getMatiere()));
                    boolean list2HasReservation = list2.stream().anyMatch(e -> "Reservation de salles".equals(e.getMatiere()));

                    if ((list1HasReservation && list2HasReservation) || (!list1HasReservation && !list2HasReservation)) {
                        boolean list1Has4HourDuration = list1.stream().anyMatch(e -> calculateDurationInHours(e) == 4);
                        boolean list2Has4HourDuration = list2.stream().anyMatch(e -> calculateDurationInHours(e) == 4);

                        if ((list1Has4HourDuration && list2Has4HourDuration) || (!list1Has4HourDuration && !list2Has4HourDuration)) {
                            boolean list1Has3HourDuration = list1.stream().anyMatch(e -> calculateDurationInHours(e) == 3);
                            boolean list2Has3HourDuration = list2.stream().anyMatch(e -> calculateDurationInHours(e) == 3);
                            return Boolean.compare(list2Has3HourDuration, list1Has3HourDuration);
                        }

                        return Boolean.compare(list2Has4HourDuration, list1Has4HourDuration);
                    }

                    return Boolean.compare(list2HasReservation, list1HasReservation);
                })
                .collect(Collectors.toList());

        sortedGroupedEvents.forEach(group -> {
            CalendarEvent representativeEvent = group.get(0);

            LocalDate dateDebut = representativeEvent.getDateDebut();
            LocalTime heureDebut = representativeEvent.getHeureDebut() == null ? LocalTime.of(gridStartHour, 0) : representativeEvent.getHeureDebut();
            LocalTime heureFin = representativeEvent.getHeureFin() == null ? LocalTime.of(19, 0) : representativeEvent.getHeureFin();


            int dayIndex = dateDebut.getDayOfWeek().getValue() - 1;
            int startRowIndex = (heureDebut.getHour() - gridStartHour) * 2 + (heureDebut.getMinute() / 30) + 9;
            int endRowIndex = (heureFin.getHour() - gridStartHour) * 2 + (heureFin.getMinute() / 30) + 9;
            int rowSpan = endRowIndex - startRowIndex;
            String timeSlotKey = dateDebut + "_" + heureDebut + "_" + heureFin;
            System.out.println(timeSlotKey);
            HBox hbox = timeSlotToHBoxMap.computeIfAbsent(timeSlotKey, k -> new HBox(2));
            hbox.setMinWidth(100);

            group.forEach(event -> {
                Button eventButton = new Button(event.getSummary() + " (" + group.size() + ")"+"\n"+event.getEnseignant());
                eventButton.setMaxWidth(Double.MAX_VALUE);
                eventButton.setMaxHeight(Double.MAX_VALUE);
                HBox.setHgrow(eventButton, Priority.ALWAYS);
                HBox.setMargin(eventButton, new Insets(2));
                eventButton.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/email_form.fxml"));
                        Parent root = loader.load();

                        EmailFormController controller = loader.getController();
                        controller.setIsDarkMode(mainController.isDarkMode);
                        controller.showEmailForm(event);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                hbox.getChildren().add(eventButton);

                applyEventStyle(event, eventButton);
                Tooltip tooltip = createEventTooltip(event);
                Tooltip.install(eventButton, tooltip);
            });


            if (!dynamicGridPane.getChildren().contains(hbox)) {
                GridPane.setConstraints(hbox, dayIndex + 1, startRowIndex, 1, rowSpan);
                GridPane.setVgrow(hbox, Priority.ALWAYS);
                dynamicGridPane.getChildren().add(hbox);
            }
        });
    }


    public void applyEventStyle(CalendarEvent event, Button eventButton) {
        if ("Reservation de salles".equals(event.getMatiere())) {
            eventButton.getStyleClass().add("event-allday");
        } else if (event.getColor().isEmpty()) {
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
            eventButton.setStyle(String.format("-fx-background-color: %s%02x;", hexColor, (int) (opacity * 255)));
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
        tooltipText.append("Groupe : ").append(event.getGroupe()).append("\n");
        Tooltip tooltip = new Tooltip(tooltipText.toString());
        tooltip.setShowDelay(Duration.seconds(0.1));
        return tooltip;
    }


}
