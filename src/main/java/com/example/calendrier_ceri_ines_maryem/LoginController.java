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
            new User("Doe", "John", "Développeur", "johndoe", "password123"),
            new User("Smith", "Jane", "Designer", "janesmith", "password456"),
            new User("Smith", "Jane", "Designer", "1", "1")
    );
    public void showCalendarPage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("calendrier.fxml"));
            Parent root = loader.load();

            // Configuration du contrôleur avec les détails de l'utilisateur
            AppPageController appPageController = loader.getController();
            appPageController.setUserDetails(user.getNom(), user.getPrenom(), user.getFonction(), user.getInitiales());

            // Affichage de la scène
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
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
                showCalendarPage(user);
                return; // Quitter la boucle après la connexion réussie
            }
        }


        // je dois ajouter le cas où les identifiants sont incorrects !!!
    }
}

