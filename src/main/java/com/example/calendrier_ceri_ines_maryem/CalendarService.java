package com.example.calendrier_ceri_ines_maryem;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

/*
class CalendarService :
pour parser nos donner dans un fichier json en temps reel
à partir du lien
*/

public class CalendarService {


    public static void downloadAndSaveJson(String urlString, String jsonFileName) {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }

            String jsonResult = parseICalendarData(content.toString());
            writeJsonToFile(jsonResult, jsonFileName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (con != null) con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeJsonToFile(String jsonData, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String parseICalendarData(String icalData) {
        JSONArray eventsArray = new JSONArray();
        String[] lines = icalData.split("\n");

        JSONObject currentEvent = null;
        StringBuilder descriptionBuilder = new StringBuilder();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");
        DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZoneId avignonZoneId = ZoneId.of("Europe/Paris");

        for (String line : lines) {
            if (line.startsWith("BEGIN:VEVENT")) {
                currentEvent = new JSONObject();
                descriptionBuilder = new StringBuilder(); // Reset the description builder
            } else if (line.startsWith("END:VEVENT") && currentEvent != null) {
                processDescription(descriptionBuilder.toString(), currentEvent);
                eventsArray.put(currentEvent);
            } else if (currentEvent != null) {
                if (line.startsWith("DTSTART:")) {
                    ZonedDateTime startDateTime = ZonedDateTime.parse(line.substring(8), dateTimeFormatter).withZoneSameInstant(avignonZoneId);
                    currentEvent.put("DateDebut", startDateTime.toLocalDate().toString());
                    currentEvent.put("HeureDebut", startDateTime.toLocalTime().toString());
                } else if (line.startsWith("DTSTART;VALUE=DATE:")) {
                    LocalDate startDate = LocalDate.parse(line.substring(19), dateOnlyFormatter);
                    currentEvent.put("DateDebut", startDate.toString());
                    currentEvent.put("AllDayEvent", true);
                } else if (line.startsWith("DTEND:")) {
                    ZonedDateTime endDateTime = ZonedDateTime.parse(line.substring(6), dateTimeFormatter).withZoneSameInstant(avignonZoneId);
                    currentEvent.put("DateFin", endDateTime.toLocalDate().toString());
                    currentEvent.put("HeureFin", endDateTime.toLocalTime().toString());
                } else if (line.startsWith("DTEND;VALUE=DATE:")) {
                    LocalDate endDate = LocalDate.parse(line.substring(17), dateOnlyFormatter);
                    currentEvent.put("DateFin", endDate.toString());
                } else if (line.startsWith("SUMMARY;LANGUAGE=fr:")) {
                    String summary = line.substring("SUMMARY;LANGUAGE=fr:".length());
                    currentEvent.put("Summary", summary);
                } else if (line.startsWith("X-ALT-DESC;FMTTYPE=text/html:")) {
                    descriptionBuilder.append(line.substring("X-ALT-DESC;FMTTYPE=text/html:".length()));
                } else if (line.startsWith(" ")) {
                    descriptionBuilder.append(line.trim());
                }
            }
        }

        return eventsArray.toString(4);
    }

    private static void processDescription(String description, JSONObject currentEvent) {
        String[] parts = description.split("<br/>");
        for (String part : parts) {
            if (part.contains("Matière :")) {
                currentEvent.put("Matière", part.split("Matière :")[1].trim());
            } else if (part.contains("Enseignant :")) {
                currentEvent.put("Enseignant", part.split("Enseignant :")[1].trim());
            } else if (part.contains("TD :")) {
                currentEvent.put("TD", part.split("TD :")[1].trim().replace("\\,", ","));
            } else if (part.contains("Salle :")) {
                currentEvent.put("Salle", part.split("Salle :")[1].trim());
            } else if (part.contains("Type :")) {
                currentEvent.put("Type", part.split("Type :")[1].trim());
            }
        }
    }


}
