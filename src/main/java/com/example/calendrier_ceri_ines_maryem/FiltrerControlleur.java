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
                .filter(event -> (selectedSalle == null || event.getSalle().contains(selectedSalle)))
                .filter(event -> (selectedTypeCours == null || event.getType().contains(selectedTypeCours)))
                .filter(event -> (selectedMatiere == null || event.getMatiere().contains(selectedMatiere)))
                .collect(Collectors.toList());

        mainController.updateEvents(filteredEvents);
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


}
