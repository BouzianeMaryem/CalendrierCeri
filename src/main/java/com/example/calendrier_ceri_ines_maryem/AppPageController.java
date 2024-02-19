package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class AppPageController {

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

    }
    }



