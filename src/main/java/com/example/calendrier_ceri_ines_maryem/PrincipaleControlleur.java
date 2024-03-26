package com.example.calendrier_ceri_ines_maryem;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;


import static com.example.calendrier_ceri_ines_maryem.CalendarService.downloadAndSaveJson;
import static com.example.calendrier_ceri_ines_maryem.EventsCreateur.creationListEventsJson;

public class PrincipaleControlleur {

    private List<CalendarEvent> events = new ArrayList<>();
    private DisplayMode currentDisplayMode = DisplayMode.WEEK;

    @FXML private VBox centerVBox;
    @FXML private BorderPane mainPane;

    @FXML
    private Label fonctionText;
    @FXML
    private Label initialesText;

    @FXML

    private Label prenomText;
    @FXML
    private Button Filtrer;
    public MenuButton gestionEventBtn;


    public boolean isDarkMode = false;
    @FXML
    private ImageView iconeMode;

    public enum DisplayMode {
        DAY, WEEK, MONTH
    }


    @FXML
    private void initialize() {
        SessionManager sessionManager = SessionManager.getInstance();
        String userNom = sessionManager.getNom();

        if (sessionManager.getNom() != null && sessionManager.getPrenom() != null) {
            setUserDetails(sessionManager.getNom(), sessionManager.getPrenom(), sessionManager.getFonction(),
                    sessionManager.getPrenom().substring(0, 1) + sessionManager.getNom().substring(0, 1));
        }

        if (!"Enseignant".equals(sessionManager.getFonction())) {
            gestionEventBtn.setDisable(true);
            gestionEventBtn.setVisible(false);
        }

        mainPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.D) {
                        isDarkMode = false;
                        toggleDarkMode();
                    } else if (event.isControlDown() && event.getCode() == KeyCode.L) {
                        isDarkMode = true;
                        toggleDarkMode();
                    } else if (event.isControlDown() && event.getCode() == KeyCode.E) {
                        Window currentWindow = mainPane.getScene().getWindow();
                        SessionManager.getInstance().logoutUser(currentWindow);
                    }else if (event.isControlDown() && event.getCode() == KeyCode.R) {
                        clearPreferences(sessionManager.getNom());
                    }
                });
            }
        });
        restorePreferences();
    }
    private void clearPreferences(String userId) {
        Map<String, String> emptyPrefs = new HashMap<>();
        Preferences.savePreferences(userId, emptyPrefs);
        resetUI();
    }

    private void resetUI() {

        events.clear();
        isDarkMode = false;
        toggleDarkMode();

        centerVBox.getChildren().clear();
    }

    private void savePreferencesForAction(String userId, String actionType, String actionValue) {
        Map<String, String> prefs = Preferences.loadPreferences(userId);

        if ("formation".equals(actionType)) {
            prefs.put("formation", actionValue);
            prefs.remove("salle");
            prefs.remove("instructor");
        } else if ("salle".equals(actionType)) {
            prefs.put("salle", actionValue);
            prefs.remove("formation");
            prefs.remove("instructor");
        } else if ("instructor".equals(actionType)) {
            prefs.put("instructor", actionValue);
            prefs.remove("formation");
            prefs.remove("salle");
        }

        prefs.put("lastActionType", actionType);
        prefs.put("view", currentDisplayMode.name());

        Preferences.savePreferences(userId, prefs);
    }



    private void restorePreferences() {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();

        Map<String, String> prefs = Preferences.loadPreferences(userId);


        String lastActionType = prefs.get("lastActionType");
        try {
            if ("formation".equals(lastActionType)) {
                String formation = prefs.get("formation");
                restoreFormation(formation);
            } else if ("salle".equals(lastActionType)) {
                String salle = prefs.get("salle");
                restoreSalle(salle);
            } else if ("instructor".equals(lastActionType)) {
                String instructor = prefs.get("instructor");
                restoreInstructor(instructor);
            }

            String viewMode = prefs.get("view");
            if (viewMode != null) {
                setDisplayMode(DisplayMode.valueOf(viewMode.toUpperCase()));
            }
            String mode = prefs.get("mode");
            if ("dark".equals(mode)) {
                isDarkMode = true;
                toggleDarkMode();
            } else if ("light".equals(mode)) {
                isDarkMode = false;
                toggleDarkMode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void restoreInstructor(String instructor) throws IOException {
        if ("NOE_CECILLON".equals(instructor)) {
            onNOE_CECILLON();
        }

    }

    private void restoreFormation(String formation) throws IOException {
        switch (formation) {
            case "M1_IA":
                onFormationM1_IA();
                break;
            case "M2_IA":
                onFormationM2_IA();
                break;

        }
    }

    private void restoreSalle(String salle) throws IOException {
        switch (salle) {
            case "Amphi_ADA":
                onAmphi_ADA();
                break;
            case "S3":
                onSalleS3();
                break;

        }
    }


    @FXML
    private void toggleDarkMode() {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();

        if (isDarkMode) {
            // Apply the dark mode stylesheet
            mainPane.getStylesheets().clear();
            mainPane.getStylesheets().add(getClass().getResource("principale/principaleDark.css").toExternalForm());
            iconeMode.setImage(new Image(getClass().getResourceAsStream("principale/images/lightMode.png")));
             isDarkMode=false;
            saveModePreference(userId, "dark");
        } else {
            // Apply the light mode stylesheet
            mainPane.getStylesheets().clear();
            mainPane.getStylesheets().add(getClass().getResource("principale/principaleLight.css").toExternalForm());
            iconeMode.setImage(new Image(getClass().getResourceAsStream("principale/images/nightMode.png")));
            isDarkMode=true;
            saveModePreference(userId, "light");
        }

    }

    private void saveModePreference(String userId, String mode) {
        Map<String, String> prefs = Preferences.loadPreferences(userId);
        prefs.put("mode", mode);
        Preferences.savePreferences(userId, prefs);
    }


    public void setUserDetails(String nom, String prenom, String fonction,String initiales) {
        prenomText.setText(prenom+" "+nom);
        fonctionText.setText(fonction);
        initialesText.setText(initiales);

    }
    public List<CalendarEvent> getEvents() {
        return events;
    }

    public void updateEvents(List<CalendarEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        try {
            setDisplayMode(currentDisplayMode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/*
**************************************************** onclick sur formation ****************************************
 */

    @FXML
    private void onFormationM1_IA() throws IOException {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();
        try{
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020081984ebc4fc00ab58fd87071d1fa020cdbe66b532d29ba6e091d5a551d44c9fd89c1ab660e39233a747175a4b4153ec44062d2d4446141034f4aa7389a9769e531c44193b3b030461858a1fec7097a37e11206824c4af0f307","eventsM1-IA.json");
        events = creationListEventsJson("eventsM1-IA.json");
        setDisplayMode(currentDisplayMode);
        Filtrer.setOnAction(event -> onFiltrerButtonClickedM1());
        Map<String, String> prefs = new HashMap<>();
            prefs.put("lastActionType", "formation");
            prefs.put("formation", "M1_IA");
            prefs.put("view", currentDisplayMode.name());
            Preferences.savePreferences(userId, prefs);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @FXML
    private void onFormationM2_IA() throws IOException {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();
        try {
            downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def50200a303ca2f94bb2afc033d868d6ebcf2b0575394e7dd91a2951d0a4fc7149eb789b680e05721adafbc64ea264aa7b7aa520f3882cfaef2e61c73b6d2753da6d7f3f94fa5c0f3656a6eb2c618c57f7a85c1e94c659a9f2dbb9fd51c722e60c9774854a7eeaa9391ef70701c83a266b7cfc1e266", "eventsM2-IA.json");
            events = creationListEventsJson("eventsM2-IA.json");
            setDisplayMode(currentDisplayMode);
            Filtrer.setOnAction(event -> onFiltrerButtonClickedM2());
            Map<String, String> prefs = new HashMap<>();
            prefs.put("lastActionType", "formation");
            prefs.put("formation", "M2_IA");
            prefs.put("view", currentDisplayMode.name());
            Preferences.savePreferences(userId, prefs);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /*
     **************************************************** onclick sur Professeur ****************************************
     */

    @FXML
    private void onNOE_CECILLON() throws IOException {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/enseignant/def5020014cf744f63f7181931e243c5139c5d8427de488f3da5b30b52905edfe9de85e8da750e291f852c095f6fd05f93658cbbf3260bf1308a84c444accdb9ab8f67de5f5758e0b59200e3c78068a677fc5055644c4635","events-prof-NOE.json");
        events = creationListEventsJson("events-prof-NOE.json");
        List<CalendarEvent> eventsajouts = creationListEventsJson("events.json");
        events.addAll(eventsajouts);
        setDisplayMode(currentDisplayMode);
        Filtrer.setOnAction(event -> onFiltrerButtonClickedProf());

        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();
        savePreferencesForAction(userId, "instructor", "NOE_CECILLON");

    }

    /*
     **************************************************** onclick sur Salle ****************************************
     */
    @FXML
    private void onAmphi_ADA() throws IOException {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();

        try {
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def50200554dcd5e4c15e4dbcdd2e6a3afd78170c6171878a4c3a33cd9331302d645d787e3869758154caa878a55157b6514110371239edb1212a9e49714f269ed234d75d1efe47ca1a449724490e265a69e754d544c51999010d709","events-salle-ADA.json");
        events = creationListEventsJson("events-salle-ADA.json");
        List<CalendarEvent> eventsajouts = creationListEventsJson("amphiAda-reservation.json");
        events.addAll(eventsajouts);
        setDisplayMode(currentDisplayMode);
        Filtrer.setOnAction(event -> onFiltrerButtonClickedSalle());
            Map<String, String> prefs = new HashMap<>();
            prefs.put("lastActionType", "salle");
            prefs.put("salle", "Amphi_Ada");
            prefs.put("view", currentDisplayMode.name());
            Preferences.savePreferences(userId, prefs);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void onSalleS3() throws IOException {
        SessionManager sessionManager = SessionManager.getInstance();
        String userId = sessionManager.getNom();
        try{
        downloadAndSaveJson("https://edt-api.univ-avignon.fr/api/exportAgenda/salle/def5020067c53113622ab5cb8df37352ce1f7896738f2e260377892d3bcd6f36372c0eeab08e34522c71ea92310adb743d42ed0632d00cd75dd1a9baeb1a3eccc2faf484634a99f1731089a4ab923402b410076813d14e9eda46d0","events-salle-S3.json");
        events = creationListEventsJson("events-salle-S3.json");
        List<CalendarEvent> eventsajouts = creationListEventsJson("S3-reservation.json");
        events.addAll(eventsajouts);
        setDisplayMode(currentDisplayMode);
        Filtrer.setOnAction(event -> onFiltrerButtonClickedSalle());
            Map<String, String> prefs = new HashMap<>();
            prefs.put("lastActionType", "salle");
            prefs.put("salle", "S3");
            prefs.put("view", currentDisplayMode.name());
            Preferences.savePreferences(userId, prefs);
        } catch (Exception e) {
            e.printStackTrace();

        }
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
        Map<String, String> prefs = Preferences.loadPreferences(SessionManager.getInstance().getNom());
        prefs.put("view", mode.name());
        Preferences.savePreferences(SessionManager.getInstance().getNom(), prefs);


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
        }


    }

    private void loadJourView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltreJSM/jour/jour.fxml"));
        Node content = loader.load();
        centerVBox.getChildren().setAll(content);
        JourControlleur jourController = loader.getController();
        jourController.setEvents(events);
        jourController.setMainController(this);
    }

    private void loadSemaineView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FiltreJSM/semaine/semaine.fxml"));
        Node content = loader.load();
        centerVBox.getChildren().setAll(content);
        SemaineControlleur semaineController = loader.getController();
        semaineController.setEvents(events);
        semaineController.setMainController(this);
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
           if (isDarkMode) {
               root.getStylesheets().clear();
               root.getStylesheets().add(getClass().getResource("principale/reservationDark.css").toExternalForm());

           } else {
               root.getStylesheets().clear();
               root.getStylesheets().add(getClass().getResource("principale/reservationLight.css").toExternalForm());
           }
            EventDialogController eventDialogController = loader.getController();
            eventDialogController.setMainController(this);
           Stage stage = new Stage();
           stage.initStyle(StageStyle.UNDECORATED);
           Scene scene = new Scene(root);
           stage.setScene(scene);
           stage.initOwner(mainPane.getScene().getWindow());

           stage.showAndWait();


       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    public void reserverUneSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/reservation.fxml"));
            Parent root = loader.load();
            if (isDarkMode) {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("principale/reservationDark.css").toExternalForm());

            } else {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("principale/reservationLight.css").toExternalForm());
            }
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initOwner(mainPane.getScene().getWindow());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //fonctions pour les filtres:
    public void choisirTonEmploi() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Choix d'emploi");
        alert.setHeaderText(null);
        alert.setContentText("Veuillez choisir votre emploi dans l'application.");
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> alert.close());
        delay.play();
    }

    //filtre pour M1
    @FXML
    private EventHandler<ActionEvent> onFiltrerButtonClickedM1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filtre/formation/filtreM1.fxml"));
            Parent root = loader.load();
            if (isDarkMode) {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreDark.css").toExternalForm());

            } else {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreLight.css").toExternalForm());
            }
            FiltrerControlleur reservationControlleur = loader.getController();
            reservationControlleur.setMainController(this);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initOwner(mainPane.getScene().getWindow());

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//filtre pour M2
@FXML
private EventHandler<ActionEvent> onFiltrerButtonClickedM2() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("filtre/formation/filtreM2.fxml"));
        Parent root = loader.load();
        if (isDarkMode) {
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("filtre/formation/filtreDark.css").toExternalForm());

        } else {
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("filtre/formation/filtreLight.css").toExternalForm());
        }
        FiltrerControlleur reservationControlleur = loader.getController();
        reservationControlleur.setMainController(this);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initOwner(mainPane.getScene().getWindow());

        stage.showAndWait();


    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
    //filtre pour Prof
    @FXML
    private EventHandler<ActionEvent> onFiltrerButtonClickedProf() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filtre/prof/filtreProf.fxml"));
            Parent root = loader.load();
            if (isDarkMode) {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreDark.css").toExternalForm());

            } else {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreLight.css").toExternalForm());
            }
            FiltrerControlleur reservationControlleur = loader.getController();
            reservationControlleur.setMainController(this);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initOwner(mainPane.getScene().getWindow());

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //filtre pour Salle
    @FXML
    private EventHandler<ActionEvent> onFiltrerButtonClickedSalle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filtre/salle/filtreSalle.fxml"));
            Parent root = loader.load();
            if (isDarkMode) {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreDark.css").toExternalForm());

            } else {
                root.getStylesheets().clear();
                root.getStylesheets().add(getClass().getResource("filtre/formation/filtreLight.css").toExternalForm());
            }
            FiltrerControlleur filtreControlleur = loader.getController();
            filtreControlleur.setMainController(this);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initOwner(mainPane.getScene().getWindow());

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
