package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static com.example.calendrier_ceri_ines_maryem.CalendarService.downloadAndSaveJson;
import static com.example.calendrier_ceri_ines_maryem.EventsCreateur.creationListEventsJson;

public class PrincipaleControlleur {



    public enum DisplayMode {
        DAY, WEEK, MONTH
    }

    private List<CalendarEvent> events = new ArrayList<>();
    // Mode par défaut
    private DisplayMode currentDisplayMode = DisplayMode.WEEK;

    @FXML private VBox centerVBox;
    @FXML private BorderPane mainPane;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onFormationM1_IA_CL() throws IOException {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020091b8dcd18c4a880befa7fb87040d42d985c6fbcd0d3011d32156bb496675b547057ce8bd7ab394051c9dc7ddacf147330c2eb43c80b23b683441d94670e7378664fbde1a4c9b5d82690722604f6ede365c941a53","eventsM1-IA-CL.json");
        events = creationListEventsJson("eventsM1-IA-CL.json");
        setDisplayMode(currentDisplayMode);
    }


    @FXML
    private void onJourFilterButtonClicked() throws IOException {
        setDisplayMode(DisplayMode.DAY);
    }
    @FXML
    private void onSemaineFilterButtonClicked() throws IOException {
        setDisplayMode(DisplayMode.WEEK);
    }

    @FXML
    private void onMoisFilterButtonClicked() throws IOException {
        setDisplayMode(DisplayMode.MONTH);
    }
    public void setDisplayMode(DisplayMode mode) throws IOException {
        this.currentDisplayMode = mode;

        switch (mode) {
            case DAY:
                loadJourView();
                break;
            case WEEK:
                loadSemaineView();
                break;
            case MONTH:
                loadMoisView();
                break;
            default:
                throw new IllegalStateException("Mode d'affichage non supporté: " + mode);
        }
    }

    private void loadJourView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltreJSM/jour/jour.fxml"));
        Node content = loader.load();
        centerVBox.getChildren().setAll(content);
        JourControlleur jourController = loader.getController();
        jourController.setEvents(events);
    }

    private void loadSemaineView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltreJSM/semaine/semaine.fxml"));
        Node content = loader.load();
        centerVBox.getChildren().setAll(content);
        SemaineControlleur semaineController = loader.getController();
        semaineController.setEvents(events);
    }

    private void loadMoisView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltreJSM/mois/mois.fxml"));
        Node content = loader.load();
        centerVBox.getChildren().setAll(content);
        MoisControlleur moisController = loader.getController();
        moisController.setEvents(events);
    }


}
