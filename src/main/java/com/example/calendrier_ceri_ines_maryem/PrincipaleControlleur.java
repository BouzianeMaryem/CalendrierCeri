package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;

public class PrincipaleControlleur {

    @FXML
    private Text nomText;
    @FXML
    private Text prenomText;
    @FXML
    private Text fonctionText;
    @FXML
    private Label initialesText;

    public void setUserDetails(String nom, String prenom, String fonction,String initiales) {

        prenomText.setText(prenom+" "+nom); // Initialiser le Text du prénom avec le prénom de l'utilisateur
        fonctionText.setText(fonction); // Initialiser le Text de la fonction avec la fonction de l'utilisateur
        initialesText.setText(initiales);
    }
//help
    @FXML
    private ImageView iconHelp;
    @FXML
    private Button helpButton;
//deconnexion
    @FXML
    private ImageView iconDeconnexion;
    @FXML
    private Button DeconnexionButton;
    //formation
    @FXML
    private ImageView iconFormation;
    @FXML
    private MenuButton formationButton;
    //salle
    @FXML
    private ImageView iconSalle;
    @FXML
    private MenuButton salleButton;

    //prof
    @FXML
    private ImageView iconProf;
    @FXML
    private MenuButton profButton;
    //dark mode
    @FXML
    private Pane bigPane;

    @FXML
    private Button darkMode;

    @FXML
    private ImageView iconeMode;

    // Supposons que c'est le mode initial
    private boolean isDarkMode = false;
    public void initialize() {
        // configuration icone help
        Image imageNormalHelp = new Image(getClass().getResourceAsStream("images/noHover/helpWhite.png"));
        Image imageHoverHelp = new Image(getClass().getResourceAsStream("images/hover/help.png"));

        helpButton.setOnMouseEntered(e -> iconHelp.setImage(imageHoverHelp)); // Change to hover image
        helpButton.setOnMouseExited(e -> iconHelp.setImage(imageNormalHelp)); // Revert to normal image

        //configuration icon deconnexion
        Image imageNormalDeconnexion = new Image(getClass().getResourceAsStream("images/noHover/deconnexionwhite.png"));
        Image imageHoverDeconnexion = new Image(getClass().getResourceAsStream("images/hover/deconnexionBlack.png"));

        DeconnexionButton.setOnMouseEntered(e -> iconDeconnexion.setImage(imageHoverDeconnexion)); // Change to hover image
        DeconnexionButton.setOnMouseExited(e -> iconDeconnexion.setImage(imageNormalDeconnexion)); // Revert to normal image
        //configuration icon formation
        Image imageNormalformation = new Image(getClass().getResourceAsStream("images/noHover/formationIconeWhite.png"));
        Image imageHoverformation = new Image(getClass().getResourceAsStream("images/hover/formationIcone.png"));

        formationButton.setOnMouseEntered(e -> iconFormation.setImage(imageHoverformation)); // Change to hover image
        formationButton.setOnMouseExited(e -> iconFormation.setImage(imageNormalformation)); // Revert to normal image
        //configuration icon salle
        Image imageNormalsalle = new Image(getClass().getResourceAsStream("images/noHover/salleIconeWhite.png"));
        Image imageHoversalle = new Image(getClass().getResourceAsStream("images/hover/salleIcon.png"));

        salleButton.setOnMouseEntered(e -> iconSalle.setImage(imageHoversalle)); // Change to hover image
        salleButton.setOnMouseExited(e -> iconSalle.setImage(imageNormalsalle)); // Revert to normal image
        //configuration icon prof
        Image imageNormalprof = new Image(getClass().getResourceAsStream("images/noHover/profIconeWhite.png"));
        Image imageHoverprof = new Image(getClass().getResourceAsStream("images/hover/profIcon.png"));

        profButton.setOnMouseEntered(e -> iconProf.setImage(imageHoverprof)); // Change to hover image
        profButton.setOnMouseExited(e -> iconProf.setImage(imageNormalprof)); // Revert to normal image
        // Configurer le gestionnaire d'événements pour le bouton darkMode
        darkMode.setOnAction(event -> toggleDarkMode());

        bigPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { // La scène est maintenant disponible
                newValue.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.D) {
                        // Vérifie si Ctrl+D est pressé pour activer le mode sombre
                        isDarkMode = false; // Assurez-vous que le mode sombre est désactivé avant de basculer
                        toggleDarkMode();
                    } else if (event.isControlDown() && event.getCode() == KeyCode.L) {
                        // Vérifie si Ctrl+L est pressé pour activer le mode clair
                        isDarkMode = true; // Assurez-vous que le mode sombre est activé avant de basculer
                        toggleDarkMode();
                    }
                });
            }
        });

    }
    private void toggleDarkMode() {
        if (isDarkMode) {
            // Passer au mode clair
            bigPane.getStylesheets().clear();
            bigPane.getStylesheets().add(getClass().getResource("appCalendrier.css").toExternalForm());
            iconeMode.setImage(new Image(getClass().getResourceAsStream("images/nightMode.png")));
            isDarkMode = false;
        } else {
            // Passer au mode sombre
            bigPane.getStylesheets().clear();
            bigPane.getStylesheets().add(getClass().getResource("darkMode.css").toExternalForm());
            iconeMode.setImage(new Image(getClass().getResourceAsStream("images/lightMode.png")));
            isDarkMode = true;
        }
    }
    }



