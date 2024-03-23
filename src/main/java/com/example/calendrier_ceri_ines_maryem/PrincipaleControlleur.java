package com.example.calendrier_ceri_ines_maryem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static com.example.calendrier_ceri_ines_maryem.CalendarService.downloadAndSaveJson;
import static com.example.calendrier_ceri_ines_maryem.EventsCreateur.creationListEventsJson;

public class PrincipaleControlleur {


    public MenuButton gestionEventBtn;

    public enum DisplayMode {
        DAY, WEEK, MONTH
    }

    private List<CalendarEvent> events = new ArrayList<>();
    // Mode par défaut
    private DisplayMode currentDisplayMode = DisplayMode.WEEK;

    @FXML private VBox centerVBox;
    @FXML private BorderPane mainPane;
    @FXML
    private Label prenomText;
    @FXML
    private Label fonctionText;
    @FXML
    private Label initialesText;

    public void setUserDetails(String nom, String prenom, String fonction,String initiales) {
        prenomText.setText(prenom+" "+nom);
        fonctionText.setText(fonction);
        initialesText.setText(initiales);

    }
    @FXML
    private void initialize() {
        SessionManager sessionManager = SessionManager.getInstance();

        if (sessionManager.getNom() != null && sessionManager.getPrenom() != null) {
            setUserDetails(sessionManager.getNom(), sessionManager.getPrenom(), sessionManager.getFonction(), sessionManager.getPrenom().substring(0, 1) + sessionManager.getNom().substring(0, 1));
        }
        //System.out.println(sessionManager.getFonction());
        if (!sessionManager.getFonction().equals("Enseignant")) {
            gestionEventBtn.setDisable(true);
            gestionEventBtn.setVisible(false);
        }

    }

    @FXML
    private void onFormationM1_IA_CL() throws IOException {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020091b8dcd18c4a880befa7fb87040d42d985c6fbcd0d3011d32156bb496675b547057ce8bd7ab394051c9dc7ddacf147330c2eb43c80b23b683441d94670e7378664fbde1a4c9b5d82690722604f6ede365c941a53","eventsM1-IA-CL.json");
        events = creationListEventsJson("eventsM1-IA-CL.json");
        setDisplayMode(currentDisplayMode);
    }

    @FXML
    private void onAmphi_ADA() throws IOException {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200554dcd5e4c15e4dbcdd2e6a3afd78170c6171878a4c3a33cd9331302d645d787e3869758154caa878a55157b6514110371239edb1212a9e49714f269ed234d75d1efe47ca1a449724490e265a69e754d544c51999010d709","events-salle-ADA.json");
        events = creationListEventsJson("events-salle-ADA.json");
        setDisplayMode(currentDisplayMode);
    }

    @FXML
    private void onNOE_CECILLON() throws IOException {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/enseignant/def5020014cf744f63f7181931e243c5139c5d8427de488f3da5b30b52905edfe9de85e8da750e291f852c095f6fd05f93658cbbf3260bf1308a84c444accdb9ab8f67de5f5758e0b59200e3c78068a677fc5055644c4635","events-prof-NOE.json");
        events = creationListEventsJson("events-prof-NOE.json");
        List<CalendarEvent> eventsajouts = creationListEventsJson("events.json");
        events.addAll(eventsajouts);
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

    @FXML
    private void addNewEvent() {
       try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/AddCourse.fxml"));
            Parent root = loader.load();

            EventDialogController eventDialogController = loader.getController();
            eventDialogController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un cours");
            stage.setScene(new Scene(root,480,360));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainPane.getScene().getWindow());

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
