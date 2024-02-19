package com.example.calendrier_ceri_ines_maryem;
import java.util.Date;
import javafx.scene.layout.StackPane;
import java.util.Objects;
import net.fortuna.ical4j.data.CalendarBuilder;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;

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

public class Controller {

    @FXML
    private GridPane gridPane;

    @FXML
    private Text displayedWeekText;
    @FXML
    private LocalDate currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);







    @FXML
    public void initialize() {
        // Assurez-vous que currentWeekStart est initialisé ici ou dans un bloc d'initialisation
        updateDisplayedWeekText();
        loadWeekEvents();
    }


    @FXML
    public void loadWeekEvents() {
        new Thread(() -> {
            // Désactiver la validation stricte
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
        System.out.println("Current Week Start: " + currentWeekStart);
        displayedWeekText.setText(
                String.format("Semaine du %s au %s",
                        currentWeekStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        currentWeekStart.plusDays(4).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        );
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


    private void displayEvents(Calendar calendar, int gridStartTime) {
        // Définissez les noms des jours de la semaine dans l'ordre souhaité
        String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

        int gridStartHour = 8; // Début de la journée à 8h
        int gridEndHour = 19; // Fin de la journée à 19h

        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        int currentWeekNumber = currentWeekStart.get(weekFields.weekOfWeekBasedYear());

        Platform.runLater(() -> {
            gridPane.getChildren().clear();

            // Ajoutez les jours de la semaine dans la première ligne de la grille
            for (int i = 0; i < daysOfWeek.length; i++) {
                Text dayText = new Text(daysOfWeek[i]);
                StackPane cellPane = new StackPane();
                cellPane.getChildren().add(dayText);
                cellPane.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 1;");
                cellPane.setPadding(new Insets(5));
                gridPane.add(cellPane, i + 1, 0); // Commencez à partir de la deuxième colonne
            }

            // Map pour suivre la dernière ligne utilisée pour chaque heure et jour
            Map<LocalDate, Map<LocalTime, Integer>> lastUsedRowByTimeAndDay = new HashMap<>();

            // Ajouter des événements au GridPane
            calendar.getComponents().forEach(component -> {
                if (component instanceof VEvent) {
                    VEvent event = (VEvent) component;
                    DtStart start = event.getStartDate();
                    Summary summary = event.getSummary();

                    if (start != null && summary != null) {
                        LocalDateTime startDate = LocalDateTime.ofInstant(start.getDate().toInstant(), zoneId);
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getEndDate().getDate().toInstant(), zoneId);

                        int eventWeekNumber = startDate.get(weekFields.weekOfWeekBasedYear());
                        if (eventWeekNumber == currentWeekNumber) {
                            int dayOfWeek = startDate.getDayOfWeek().getValue();
                            LocalDate date = startDate.toLocalDate();
                            LocalTime time = startDate.toLocalTime();

                            int rowIndex = lastUsedRowByTimeAndDay
                                    .computeIfAbsent(date, k -> new HashMap<>())
                                    .compute(time, (k, v) -> (v == null) ? 1 : v + 1);

                            // Correction : Assurez-vous que rowIndex commence au bon endroit
                            rowIndex += (time.getHour() - gridStartHour) * 2; // Multiplier par 2 si chaque heure est divisée en 2 rangées (30 min chacune)

                            if (dayOfWeek >= 1 && dayOfWeek <= 5 && time.getHour() >= gridStartHour && time.getHour() <= gridEndHour) {
                                String eventName = summary.getValue();
                                String startTime = startDate.format(timeFormatter);
                                String endTime = endDate.format(timeFormatter);
                                String[] eventDetailsArray = String.format("%s\n%s - %s", eventName, startTime, endTime).split("\\s+");
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
                                eventText.setStyle("-fx-font-size: 12px; -fx-fill: black;");
                                StackPane cellPane = new StackPane();
                                cellPane.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 1;");
                                cellPane.getChildren().add(eventText);
                                cellPane.setPadding(new Insets(5));

                                // Ajouter l'événement à partir de la deuxième colonne
                                gridPane.add(cellPane, dayOfWeek, rowIndex);
                            }
                        }
                    }
                }
            });
        });
    }

}