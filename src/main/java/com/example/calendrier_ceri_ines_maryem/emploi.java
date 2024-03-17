package com.example.calendrier_ceri_ines_maryem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class emploi extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/emploi.fxml"));
        primaryStage.setTitle("Emploi du Temps");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Charger les données du calendrier lorsque l'application démarre
        loadCalendarData();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void loadCalendarData() {
        try {
            // Ouvrir la connexion à l'URL
            URL url = new URL("https://edt-api.univ-avignon.fr/api/exportAgenda/tdoption/def5020091b8dcd18c4a880befa7fb87040d42d985c6fbcd0d3011d32156bb496675b547057ce8bd7ab394051c9dc7ddacf147330c2eb43c80b23b683441d94670e7378664fbde1a4c9b5d82690722604f6ede365c941a53");
            InputStream inputStream = url.openStream();

            // Lire les données de l'URL et les stocker dans une chaîne
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            }

            // Afficher les données récupérées
            System.out.println(stringBuilder.toString());

            // Fermer le flux d'entrée
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
