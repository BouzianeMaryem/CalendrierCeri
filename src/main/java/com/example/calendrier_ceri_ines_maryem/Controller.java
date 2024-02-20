package com.example.calendrier_ceri_ines_maryem;
import java.util.Date;
import javafx.scene.layout.StackPane;
import java.util.Objects;
import net.fortuna.ical4j.data.CalendarBuilder;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import java.io.IOException;


import java.util.Map;
import java.time.LocalTime;
import javafx.scene.layout.BorderPane;

import javafx.geometry.Insets;

import net.fortuna.ical4j.util.Configurator;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;
import java.net.URL;
import javafx.application.Platform;
import java.time.DayOfWeek;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.time.LocalDate;
import javafx.scene.control.ScrollPane;

public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private Text displayedWeekText;
    @FXML
    private LocalDate currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
    private final int gridStartHour = 8; // Début de la grille à 8 heures
    private final int gridEndHour = 19; // Fin de la grille à 19 heures
    private final int rowsPerHour = 1; // Nombre de lignes par heure, pour les segments de 30 minutes



    private void displayHours(int startHour, int endHour) {
        for (int hour = startHour; hour <= endHour; hour++) {
            gridPane.setVgap(0);

            String hourText = String.format("%02d:00", hour);
            Text textNode = new Text(hourText);
            StackPane cellPane = new StackPane();
            cellPane.getChildren().add(textNode);
            cellPane.setStyle("-fx-background-color: #41c039; -fx-border-color: #72ef5d; -fx-border-width: 0 0 1 1;");
            cellPane.setPadding(new Insets(5));
            // Note: on suppose que la première rangée est réservée pour les noms des jours de la semaine
            gridPane.add(cellPane, 0, hour - startHour + 1); // +1 pour ajuster avec la rangée des jours
        }
    }


    @FXML
    public void initialize() {

        updateDisplayedWeekText();
        loadWeekEvents();
    }


    @FXML
    public void loadWeekEvents() {
        new Thread(() -> {

            System.setProperty("net.fortuna.ical4j.parsing.relaxed", "true");
            System.setProperty("net.fortuna.ical4j.validation.relaxed", "true");
            System.setProperty("net.fortuna.ical4j.unfolding.relaxed", "true");
            System.setProperty("net.fortuna.ical4j.compatibility.outlook", "true");
            System.setProperty("net.fortuna.ical4j.compatibility.notes", "true");

            String filePath = "https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020091b8dcd18c4a880befa7fb87040d42d985c6fbcd0d3011d32156bb496675b547057ce8bd7ab394051c9dc7ddacf147330c2eb43c80b23b683441d94670e7378664fbde1a4c9b5d82690722604f6ede365c941a53"; // Chemin vers le fichier iCalendar téléchargé

            try {
                InputStream inputStream = new URL(filePath).openStream();
                String icsContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator()));

                // Replace problematic tags
                String cleanedIcsContent = icsContent.replaceAll("<[^>]*>", "");
                System.out.println(cleanedIcsContent); // Imprimez le contenu iCalendar nettoyé pour vérification

                // Use ical4j to parse the cleaned iCalendar data
                CalendarBuilder builder = new CalendarBuilder();
                Calendar calendar = builder.build(new java.io.ByteArrayInputStream(cleanedIcsContent.getBytes(StandardCharsets.UTF_8)));

                // Calculate the earliest start time among all events
                int gridStartTime = calculateGridStartTime(calendar);

                // Now proceed with your calendar as before
                Platform.runLater(() -> displayEvents(calendar, gridStartTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Méthode utilitaire pour obtenir le nom du jour de la semaine
    @FXML
    private String getDayOfWeek(int day) {
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        return daysOfWeek[day];
    }
    @FXML
    private void loadPreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateDisplayedWeekText();
        loadWeekEvents();
    }

    @FXML
    private void loadNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateDisplayedWeekText();
        loadWeekEvents();
    }

    private void updateDisplayedWeekText() {
        if (displayedWeekText != null) {
            displayedWeekText.setText(
                    String.format("Semaine du %s au %s",
                            currentWeekStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            currentWeekStart.plusDays(4).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            );
        }
    }

    private int calculateGridStartTime(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm");
        Optional<Integer> earliestStartMinute = calendar.getComponents().stream()
                .filter(component -> component instanceof VEvent)
                .map(component -> (VEvent) component)
                .map(event -> {
                    DtStart start = event.getStartDate();
                    if (start != null) {
                        Date startDate = start.getDate();
                        String[] parts = dateFormat.format(startDate).split(":");
                        int hour = Integer.parseInt(parts[0]);
                        int minute = Integer.parseInt(parts[1]);
                        return hour * 60 + minute; // Convertir en minutes depuis minuit
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .min(Integer::compareTo);

        return earliestStartMinute.orElse(8 * 60); // Retourner en minutes depuis minuit (par défaut à 8h00 si aucun événement trouvé)
    }


    private void displayEvents(Calendar calendar,int gridStartTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = currentWeekStart.get(weekFields.weekOfWeekBasedYear());

        // Déclaration du tableau avec les noms des jours de la semaine
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

        Platform.runLater(() -> {
            gridPane.getChildren().clear();
            displayHours(gridStartHour, gridEndHour);

            // Ajoute les jours de la semaine dans la première ligne de la grille
            for (int i = 0; i < daysOfWeek.length; i++) {
                Text dayText = new Text(daysOfWeek[i]);
                StackPane cellPane = new StackPane();
                cellPane.getChildren().add(dayText);
                cellPane.setStyle("-fx-background-color: #7ace5e; -fx-border-color: #94e058; -fx-border-width: 0 0 1 1;");
                cellPane.setPadding(new Insets(5));
                gridPane.add(cellPane, i + 1, 0); // +1 pour ajuster avec la rangée des jours
            }

            calendar.getComponents().forEach(component -> {
                if (component instanceof VEvent) {
                    VEvent event = (VEvent) component;
                    DtStart start = event.getStartDate();
                    Summary summary = event.getSummary();

                    if (start != null && summary != null) {
                        LocalDateTime startDate = LocalDateTime.ofInstant(start.getDate().toInstant(), zoneId);
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getEndDate().getDate().toInstant(), zoneId);

                        // Calcul de l'index de la ligne de début et de fin de l'événement
                        int eventStartIndex = (startDate.getHour() - gridStartHour) * rowsPerHour;
                        if (startDate.getMinute() >= 30) {
                            eventStartIndex += 1; // Ajouter une demi-heure si nécessaire
                        }

                        int eventEndIndex = (endDate.getHour() - gridStartHour) * rowsPerHour;
                        if (endDate.getMinute() > 0) {
                            eventEndIndex += 1; // Ajouter une demi-heure si nécessaire
                        }

                        // Calcul de la durée de l'événement en nombre de rangées
                        int eventDurationRows = eventEndIndex - eventStartIndex;

                        if (startDate.get(WeekFields.of(Locale.FRANCE).weekOfWeekBasedYear()) == currentWeekNumber) {
                            String eventName = summary.getValue();
                            String eventDetails = String.format("%s\n%s - %s", eventName, startDate.format(timeFormatter), endDate.format(timeFormatter));

                            // Traitement des détails de l'événement pour une meilleure présentation
                            String[] eventDetailsArray = eventDetails.split("\\s+");
                            StringBuilder eventDetailsBuilder = new StringBuilder();
                            int wordCount = 0;
                            for (String word : eventDetailsArray) {
                                if (wordCount >= 4) {
                                    eventDetailsBuilder.append("\n"); // Nouvelle ligne après 4 mots
                                    wordCount = 0;
                                }
                                eventDetailsBuilder.append(word).append(" ");
                                wordCount++;
                            }

                            Text eventText = new Text(eventDetailsBuilder.toString().trim());
                            eventText.setStyle("-fx-font-size: 8px; -fx-fill: black;");
                            StackPane eventPane = new StackPane();
                            eventPane.getChildren().add(eventText);
                            eventPane.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc;");
                            eventPane.setPadding(new Insets(3));

                            // Ajouter l'événement à la grille
                            gridPane.add(eventPane, startDate.getDayOfWeek().getValue(), eventStartIndex + 1, 1, eventDurationRows);
                        }
                    }
                }
            });
        });
    }
}