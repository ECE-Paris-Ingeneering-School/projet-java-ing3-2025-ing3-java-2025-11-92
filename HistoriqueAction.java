package model;

import java.util.Date;

/**
 * Classe représentant une entrée dans l'historique des actions utilisateur
 * Permet de tracer toutes les opérations effectuées par les utilisateurs dans le système
 */
public class HistoriqueAction {
    // Identifiant unique de l'entrée dans l'historique
    private int id;
    // Référence vers l'utilisateur qui a effectué l'action
    private int utilisateurId;
    // Description de l'action réalisée
    private String action;
    // Moment précis où l'action a été effectuée
    private Date dateHeure;
    // Nom de l'utilisateur pour faciliter l'affichage sans avoir à joindre les tables
    private String nomUtilisateur;  // Pour l'affichage

    // Constructeur par défaut qui initialise l'action avec la date actuelle
    public HistoriqueAction() {
        this.dateHeure = new Date();
    }

    // Constructeur complet avec tous les paramètres nécessaires
    public HistoriqueAction(int id, int utilisateurId, String action, Date dateHeure) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.action = action;
        this.dateHeure = dateHeure;
    }

    // Constructeur simplifié pour créer rapidement une nouvelle entrée d'historique
    public HistoriqueAction(int utilisateurId, String action) {
        this.utilisateurId = utilisateurId;
        this.action = action;
        this.dateHeure = new Date();
    }

    // Récupère l'identifiant de l'entrée
    public int getId() {
        return id;
    }

    // Définit l'identifiant de l'entrée
    public void setId(int id) {
        this.id = id;
    }

    // Récupère l'identifiant de l'utilisateur concerné
    public int getUtilisateurId() {
        return utilisateurId;
    }

    // Modifie l'identifiant de l'utilisateur concerné
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    // Récupère la description de l'action effectuée
    public String getAction() {
        return action;
    }

    // Définit la description de l'action effectuée
    public void setAction(String action) {
        this.action = action;
    }

    // Récupère la date et l'heure de l'action
    public Date getDateHeure() {
        return dateHeure;
    }

    // Modifie la date et l'heure de l'action
    public void setDateHeure(Date dateHeure) {
        this.dateHeure = dateHeure;
    }

    // Récupère le nom de l'utilisateur pour l'affichage
    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    // Définit le nom de l'utilisateur pour l'affichage
    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    // Formatage personnalisé pour l'affichage de l'entrée d'historique
    @Override
    public String toString() {
        // Utilise le nom d'utilisateur s'il est disponible, sinon utilise l'ID
        String utilisateur = (nomUtilisateur != null) ? nomUtilisateur : "Utilisateur " + utilisateurId;
        return utilisateur + " - " + action + " - " + dateHeure;
    }
}
