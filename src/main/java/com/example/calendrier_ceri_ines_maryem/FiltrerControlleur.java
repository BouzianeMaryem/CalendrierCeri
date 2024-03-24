package com.example.calendrier_ceri_ines_maryem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    @FXML
    private ComboBox<String> enseignantCombo;
    public void setMainController(PrincipaleControlleur controller) {
        this.mainController = controller;
    }

    @FXML
    private void onValiderFiltres() {
        String selectedGroupe = null;
        if (groupeCombo != null) {
            selectedGroupe = groupeCombo.getValue();
        }

        String selectedSalle = null;
        if (salleCombo != null) {
            selectedSalle = salleCombo.getValue();
        }

        String selectedTypeCours = null;
        if (typeCoursCombo != null) {
            selectedTypeCours = typeCoursCombo.getValue();
        }

        String selectedMatiere = null;
        if (matiereCombo != null) {
            selectedMatiere = matiereCombo.getValue();
        }
        String selectedEnseignant = null;
        if (enseignantCombo != null) {
            selectedEnseignant = enseignantCombo.getValue();
        }


        String finalSelectedGroupe = selectedGroupe;
        String finalSelectedSalle = selectedSalle;
        String finalSelectedTypeCours = selectedTypeCours;
        String finalSelectedMatiere = selectedMatiere;
        String finalSelectedEnseignant = selectedEnseignant;
        List<CalendarEvent> filteredEvents = mainController.getEvents().stream()
                .filter(event -> (finalSelectedGroupe == null || event.getGroupe().contains(finalSelectedGroupe)))
                .filter(event -> (finalSelectedSalle == null || event.getSalle().contains(finalSelectedSalle)))
                .filter(event -> (finalSelectedTypeCours == null || event.getType().contains(finalSelectedTypeCours)))
                .filter(event -> (finalSelectedMatiere == null || event.getMatiere().contains(finalSelectedMatiere)))
                .filter(event -> (finalSelectedEnseignant == null || event.getEnseignant().contains(finalSelectedEnseignant)))
                .collect(Collectors.toList());

        mainController.updateEvents(filteredEvents);
        closeWindowAutomatically();
    }

    @FXML
    private void minimizeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void maximizeRestoreWindow(ActionEvent event) {
        Stage stage = ((Stage)((Button)event.getSource()).getScene().getWindow());
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }
    @FXML
    private HBox titleBar;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage)((HBox)event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void closeWindowAutomatically() {
        Stage stage = (Stage) groupeCombo.getScene().getWindow();
        stage.close();
    }

}
