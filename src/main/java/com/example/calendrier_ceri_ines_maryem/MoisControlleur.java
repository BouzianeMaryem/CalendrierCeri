package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        long eventsCount = events.stream()
                .filter(e -> e.getDateDebut().equals(date) ||
                        (e.getDateDebut().isBefore(date) &&
                                e.getDateFin().isAfter(date)))
                .filter(e -> !"Férié".equals(e.getSummary()) && !"Vacances".equals(e.getSummary()))
                .count();

        if (eventsCount > 0) {
            HBox circlesBox = new HBox(5);
            for (int i = 0; i < eventsCount; i++) {
                Circle eventCircle = new Circle(5);
                LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#0097B2")),
                        new Stop(1, Color.web("#7BD759")));
                eventCircle.setFill(gradient);
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
}
