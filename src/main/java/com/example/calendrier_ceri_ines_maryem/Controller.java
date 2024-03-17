package com.example.calendrier_ceri_ines_maryem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Controller implements Initializable {
    @FXML
    private void addNewEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddCourse.fxml"));
            Parent root = loader.load();

            EventDialogController eventDialogController = loader.getController();
            eventDialogController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un cours");
            stage.setScene(new Scene(root,400,600));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(dynamicGridPane.getScene().getWindow()); // Utilisez un élément UI présent pour obtenir la fenêtre

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private GridPane dynamicGridPane;

    @FXML
    private Text displayedWeekText;

    private LocalDate currentWeek;
    private List<CalendarEvent> allEvents = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentWeek = LocalDate.now(); // Initialize the week to the current date
        updateDisplayedWeek(currentWeek); // Display the current week
        // Add sample events or load them as needed
        // Example event for demonstration
        allEvents.add(new CalendarEvent(
                "2024-03-29", // Event date
                "2024-03-20", // Same day event
                "10:00", // Start time
                "11:30", // End time
                "Lecture on Algebra", // Summary
                "Lecture", // Type
                "Mathematics", // Subject
                "Professor Smith", // Instructor
                false // Not an all-day event
        ));
        updateCalendarView(); // Update the view to show events for the current week
    }

    @FXML
    private void loadPreviousWeek() {
        currentWeek = currentWeek.minusWeeks(1);
        updateDisplayedWeek(currentWeek);
        updateCalendarView();
    }

    @FXML
    private void loadNextWeek() {
        currentWeek = currentWeek.plusWeeks(1);
        updateDisplayedWeek(currentWeek);
        updateCalendarView();
    }

    private void updateDisplayedWeek(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate friday = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Semaine du' dd 'au' ");
        String formattedDate = formatter.format(monday);
        String formattedEndDate = friday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        displayedWeekText.setText(formattedDate + formattedEndDate);
    }

    public void addEventToCalendar(CalendarEvent event) {
        if (event.getDateDebut().isBefore(currentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) ||
                event.getDateDebut().isAfter(currentWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)))) {
            return; // L'événement n'est pas dans la semaine courante, donc pas ajouté à l'affichage
        }

        System.out.println("Ajout de l'événement: " + event.getSummary()); // Confirmation dans la console
        allEvents.add(event); // Ajoute l'événement à la liste

        updateCalendarView(); // Mise à jour de l'affichage pour inclure le nouvel événement
    }

    private int convertirHeureEnLigne(String heure) {
        String[] parts = heure.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        // Assuming the grid starts at 8:00 AM, and each row represents 30 minutes
        return (hour - 8) * 2 + (minutes >= 30 ? 1 : 0);
    }

    private void clearEventsFromGrid() {
        dynamicGridPane.getChildren().removeIf(node -> node instanceof VBox);
    }

    private void updateCalendarView() {
        clearEventsFromGrid(); // Clear current events
        allEvents.forEach(this::addEventToCalendar); // Add events for the current week
    }
}

