package com.example.calendrier_ceri_ines_maryem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarEvent {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String summary;
    private String type;
    private String matiere;
    private String enseignant;
    private boolean allDayEvent;

    // Constructeur complet
    public CalendarEvent(String dateDebutStr, String dateFinStr, String heureDebutStr, String heureFinStr,
                         String summary, String type, String matiere, String enseignant, Boolean allDayEvent) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.dateDebut = LocalDate.parse(dateDebutStr, dateFormatter);
        this.dateFin = LocalDate.parse(dateFinStr, dateFormatter);
        this.heureDebut = (heureDebutStr != null) ? LocalTime.parse(heureDebutStr, timeFormatter) : null;
        this.heureFin = (heureFinStr != null) ? LocalTime.parse(heureFinStr, timeFormatter) : null;
        this.summary = summary;
        this.type = type;
        this.matiere = matiere;
        this.enseignant = enseignant;
        this.allDayEvent = (allDayEvent != null) && allDayEvent;
    }

    // Getters
    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public String getSummary() {
        return summary;
    }

    public String getType() {
        return type;
    }

    public String getMatiere() {
        return matiere;
    }

    public String getEnseignant() {
        return enseignant;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    // Setters
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public void setEnseignant(String enseignant) {
        this.enseignant = enseignant;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }
}