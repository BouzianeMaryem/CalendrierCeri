package com.example.calendrier_ceri_ines_maryem;

public class User {
    private String nom;
    private String prenom;
    private String fonction;
    private String username;
    private String password;
    private String initiales;


    public User(String nom, String prenom, String fonction, String username, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.username = username;
        this.password = password;
        this.initiales = extractInitiales(nom, prenom);
    }

    // Méthode pour extraire les initiales à partir du nom et du prénom
    // cela me retourne la première lettre du prénom et du nom en majuscules
    private String extractInitiales(String nom, String prenom) {
        if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty()) {
            return "";
        }
        return prenom.substring(0, 1).toUpperCase() + nom.substring(0, 1).toUpperCase();
    }
    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getInitiales() {
        return initiales;
    }


}
