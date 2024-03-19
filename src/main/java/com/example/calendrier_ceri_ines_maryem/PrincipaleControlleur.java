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

        prenomText.setText(prenom+" "+nom);
        fonctionText.setText(fonction);
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

        helpButton.setOnMouseEntered(e -> iconHelp.setImage(imageHoverHelp));
        helpButton.setOnMouseExited(e -> iconHelp.setImage(imageNormalHelp));

        //configuration icon deconnexion
        Image imageNormalDeconnexion = new Image(getClass().getResourceAsStream("images/noHover/deconnexionwhite.png"));
        Image imageHoverDeconnexion = new Image(getClass().getResourceAsStream("images/hover/deconnexionBlack.png"));

        DeconnexionButton.setOnMouseEntered(e -> iconDeconnexion.setImage(imageHoverDeconnexion));
        DeconnexionButton.setOnMouseExited(e -> iconDeconnexion.setImage(imageNormalDeconnexion));
        //configuration icon formation
        Image imageNormalformation = new Image(getClass().getResourceAsStream("images/noHover/formationIconeWhite.png"));
        Image imageHoverformation = new Image(getClass().getResourceAsStream("images/hover/formationIcone.png"));

        formationButton.setOnMouseEntered(e -> iconFormation.setImage(imageHoverformation));
        formationButton.setOnMouseExited(e -> iconFormation.setImage(imageNormalformation));
        //configuration icon salle
        Image imageNormalsalle = new Image(getClass().getResourceAsStream("images/noHover/salleIconeWhite.png"));
        Image imageHoversalle = new Image(getClass().getResourceAsStream("images/hover/salleIcon.png"));

        salleButton.setOnMouseEntered(e -> iconSalle.setImage(imageHoversalle));
        salleButton.setOnMouseExited(e -> iconSalle.setImage(imageNormalsalle));
        //configuration icon prof
        Image imageNormalprof = new Image(getClass().getResourceAsStream("images/noHover/profIconeWhite.png"));
        Image imageHoverprof = new Image(getClass().getResourceAsStream("images/hover/profIcon.png"));

        profButton.setOnMouseEntered(e -> iconProf.setImage(imageHoverprof));
        profButton.setOnMouseExited(e -> iconProf.setImage(imageNormalprof));

        darkMode.setOnAction(event -> toggleDarkMode());

        bigPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.D) {
                        isDarkMode = false;
                        toggleDarkMode();
                    } else if (event.isControlDown() && event.getCode() == KeyCode.L) {

                        isDarkMode = true;
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



