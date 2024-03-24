package com.example.calendrier_ceri_ines_maryem;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;


public class EmailFormController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;

    private CalendarEvent selectedEvent;

    private String destinationEmail = "bouziane.meryem11@gmail.com";


    public void showEmailForm(CalendarEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
        this.destinationEmail = selectedEvent.getEnseignant();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("principale/email_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            EmailFormController controller = loader.getController();
            controller.setEmailField(this.destinationEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEmailField(String email) {
        emailField.setText(email);
    }

    @FXML
    protected void handleSendButton(ActionEvent event) {

        sendEmail(this.destinationEmail, subjectField.getText(), messageArea.getText());
    }
    public static void sendEmail(String to, String subject, String body) {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "inelmahi@gmail.com";
        String password = "vfme ljdz kloj vvlw";


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(props, new Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("E-mail envoyé avec succès !");
            showAlert(AlertType.INFORMATION, "Succès", "E-mail envoyé avec succès !");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'envoi de l'e-mail.\nVeuillez vérifier vos informations d'identification.");
        }
    }


    private static void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void minimizeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void maximizeRestoreWindow(ActionEvent event) {
        Stage stage = ((Stage)((Button)event.getSource()).getScene().getWindow());
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }
    @FXML
    private HBox titleBar;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage)((HBox)event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void closeWindowAutomatically() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}