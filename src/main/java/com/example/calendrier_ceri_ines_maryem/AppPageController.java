package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
}

