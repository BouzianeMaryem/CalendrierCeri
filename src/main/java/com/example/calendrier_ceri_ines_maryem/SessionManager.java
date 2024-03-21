package com.example.calendrier_ceri_ines_maryem;

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

    public void logoutUser() {
        prenom = null;
        nom = null;
        fonction = null;
    }
}

