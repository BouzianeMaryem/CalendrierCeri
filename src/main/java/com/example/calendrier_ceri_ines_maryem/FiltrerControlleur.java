package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;
import java.util.stream.Collectors;

public class FiltrerControlleur {
    private PrincipaleControlleur mainController;

    @FXML
    private ComboBox<String> groupeCombo;
    @FXML
    private ComboBox<String> salleCombo;
    @FXML
    private ComboBox<String> typeCoursCombo;
    @FXML
    private ComboBox<String> matiereCombo;
    public void setMainController(PrincipaleControlleur controller) {
        this.mainController = controller;
    }

    @FXML
    private void onValiderFiltres() {
        String selectedGroupe = groupeCombo.getValue();
        String selectedSalle = salleCombo.getValue();
        String selectedTypeCours = typeCoursCombo.getValue();
        String selectedMatiere = matiereCombo.getValue();

        List<CalendarEvent> filteredEvents = mainController.getEvents().stream()
                .filter(event -> (selectedGroupe == null || event.getGroupe().contains(selectedGroupe)))
                .filter(event -> (selectedSalle == null || event.getSalle().equals(selectedSalle)))
                .filter(event -> (selectedTypeCours == null || event.getType().equals(selectedTypeCours)))
                .filter(event -> (selectedMatiere == null || event.getMatiere().equals(selectedMatiere)))
                .collect(Collectors.toList());

        mainController.updateEvents(filteredEvents);
    }

}
