package model;

/**
 * Classe abstraite représentant un utilisateur du système
 * Sert de base pour tous les types d'utilisateurs avec les attributs communs
 */
public abstract class Utilisateur {
    // Identifiant unique de l'utilisateur
    private int id;
    // Nom complet de l'utilisateur
    private String nom;
    // Adresse email servant d'identifiant de connexion
    private String email;
    // Mot de passe de l'utilisateur (idéalement devrait être hashé)
    private String motDePasse;

    // Constructeur par défaut
    public Utilisateur() {
    }

    // Constructeur avec tous les paramètres nécessaires
    public Utilisateur(int id, String nom, String email, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Récupère l'identifiant de l'utilisateur
    public int getId() {
        return id;
    }

    // Définit l'identifiant de l'utilisateur
    public void setId(int id) {
        this.id = id;
    }

    // Récupère le nom de l'utilisateur
    public String getNom() {
        return nom;
    }

    // Modifie le nom de l'utilisateur
    public void setNom(String nom) {
        this.nom = nom;
    }

    // Récupère l'adresse email de l'utilisateur
    public String getEmail() {
        return email;
    }

    // Modifie l'adresse email de l'utilisateur
    public void setEmail(String email) {
        this.email = email;
    }

    // Récupère le mot de passe de l'utilisateur
    public String getMotDePasse() {
        return motDePasse;
    }

    // Modifie le mot de passe de l'utilisateur
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    // Méthode pour l'affichage des informations de l'utilisateur
    // Notez que le mot de passe n'est pas inclus pour des raisons de sécurité
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
