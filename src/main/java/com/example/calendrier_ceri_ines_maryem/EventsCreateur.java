package com.example.calendrier_ceri_ines_maryem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class EventsCreateur {

    public static List<CalendarEvent> creationListEventsJson(String jsonFilePath) {
        List<CalendarEvent> events = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Boolean allDay = jsonObject.optBoolean("AllDayEvent", false);
                String dateDebut = jsonObject.getString("DateDebut");
                String dateFin = jsonObject.optString("DateFin", dateDebut);
                String heureDebut = allDay ? null : jsonObject.optString("HeureDebut");
                String heureFin = allDay ? null : jsonObject.optString("HeureFin");
                String summary = jsonObject.getString("Summary");
                String type = jsonObject.optString("Type", "Non spécifié");
                String matiere = jsonObject.optString("Matière", "");
                String salle = jsonObject.optString("Salle", "");
                String enseignant = jsonObject.optString("Enseignant", "");

                CalendarEvent event = new CalendarEvent(
                        dateDebut,
                        dateFin,
                        heureDebut,
                        heureFin,
                        summary,
                        type,
                        matiere,
                        enseignant,
                        salle,
                        allDay
                );
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
