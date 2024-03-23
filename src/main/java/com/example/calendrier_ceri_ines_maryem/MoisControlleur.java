package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoisControlleur {
    @FXML
    private GridPane dynamicGridPane;
    @FXML
    private Text displayedMonthText;

    private List<CalendarEvent> events = new ArrayList<>();
    private YearMonth currentMonth = YearMonth.now();

    public void initialize() {
        updateMonthView();
    }

    public void setEvents(List<CalendarEvent> events) {
        this.events = events;
        updateMonthView();
    }
    private void updateMonthView() {
        displayedMonthText.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        dynamicGridPane.getChildren().removeIf(node -> node instanceof VBox);

        LocalDate firstVisibleDay = startOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY ?
                startOfMonth.plusDays(1) :
                startOfMonth.getDayOfWeek() == DayOfWeek.SATURDAY ?
                        startOfMonth.plusDays(2) :
                        startOfMonth;

        int firstDayOfWeek = firstVisibleDay.getDayOfWeek().getValue();
        int offset = (firstDayOfWeek - DayOfWeek.MONDAY.getValue() + 7) % 7;

        for (int i = 0; i < offset; i++) {
            VBox emptyBox = createEmptyDayBox();
            emptyBox.getStyleClass().add("autremois");
            dynamicGridPane.add(emptyBox, i, 1);
        }

        int column = offset;
        int row = 1;

        for (LocalDate date = firstVisibleDay; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                VBox dayBox = createDayBox(date);
                dynamicGridPane.add(dayBox, column, row);

                column++;
                if (column > 4) {
                    column = 0;
                    row++;
                }
            }
        }
    }


    private VBox createEmptyDayBox() {
        VBox emptyBox = new VBox();
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPrefSize(100, 100);

        return emptyBox;
    }

    private VBox createDayBox(LocalDate date) {
        VBox dayBox = new VBox(20);
        dayBox.setAlignment(Pos.TOP_CENTER);
        dayBox.setPadding(new Insets(20));
        dayBox.setPrefSize(100, 100);
        dayBox.getStyleClass().add("row");

        Label dateLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dateLabel.getStyleClass().add("labelJour");
        dayBox.getChildren().add(dateLabel);

        List<CalendarEvent> dayEvents = events.stream()
                .filter(e -> e.getDateDebut().equals(date) ||
                        (e.getDateDebut().isBefore(date) && e.getDateFin().isAfter(date)))
                .filter(e -> !"Férié".equals(e.getSummary()) && !"Vacances".equals(e.getSummary()))
                .collect(Collectors.toList());

        if (!dayEvents.isEmpty()) {
            HBox circlesBox = new HBox(5);
            for (CalendarEvent event : dayEvents) {
                Circle eventCircle = new Circle(5);
                LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#0097B2")),
                        new Stop(1, Color.web("#7BD759")));
                eventCircle.setFill(gradient);

                Tooltip tooltip = createEventTooltip(event);
                Tooltip.install(eventCircle, tooltip);

                circlesBox.getChildren().add(eventCircle);
            }
            dayBox.getChildren().add(circlesBox);
        }

        return dayBox;
    }


    public void loadPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateMonthView();
    }

    public void loadNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateMonthView();
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
