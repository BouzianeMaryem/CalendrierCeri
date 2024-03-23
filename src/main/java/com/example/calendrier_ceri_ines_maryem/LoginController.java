package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Liste d'utilisateurs
    private static final List<User> users = Arrays.asList(
            new User("Noe", "Cecillon", "Enseignant", "Noececillon", "123456"),
            new User("Naima", "elMamoudi", "Etudiant", "janesmith", "123456"),
            new User("Sara", "Wardi", "Enseignant", "1", "1"));

    public void showCalendarPage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/pagePrincipale.fxml"));
            Parent root = loader.load();

            PrincipaleControlleur appPageController = loader.getController();
            appPageController.setUserDetails(user.getNom(), user.getPrenom(), user.getFonction(), user.getInitiales());

            Scene currentScene = usernameField.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            currentScene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLoginButtonClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                SessionManager.getInstance().loginUser(user.getPrenom(), user.getNom(), user.getFonction());
                showCalendarPage(user);
                return;
            }
        }
    }

}
