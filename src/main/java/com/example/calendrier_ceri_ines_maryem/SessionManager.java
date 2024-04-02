package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SessionManager {
    private static SessionManager instance = new SessionManager();

    private String prenom;
    private String nom;
    private String fonction;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public void loginUser(String prenom, String nom, String fonction) {
        this.prenom = prenom;
        this.nom = nom;
        this.fonction = fonction;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getFonction() {
        return fonction;
    }

    public void logoutUser(Window currentWindow) {
        prenom = null;
        nom = null;
        fonction = null;
        Stage stage = (Stage) currentWindow;
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login/fxml/login2.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

