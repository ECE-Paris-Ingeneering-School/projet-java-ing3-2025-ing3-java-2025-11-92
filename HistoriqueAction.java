package model;

import java.util.Date;

/**
 * Classe représentant une entrée dans l'historique des actions utilisateur
 */
public class HistoriqueAction {
    private int id;
    private int utilisateurId;
    private String action;
    private Date dateHeure;
    private String nomUtilisateur;  // Pour l'affichage

    public HistoriqueAction() {
        this.dateHeure = new Date();
    }

    public HistoriqueAction(int id, int utilisateurId, String action, Date dateHeure) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.action = action;
        this.dateHeure = dateHeure;
    }

    public HistoriqueAction(int utilisateurId, String action) {
        this.utilisateurId = utilisateurId;
        this.action = action;
        this.dateHeure = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Date dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    @Override
    public String toString() {
        String utilisateur = (nomUtilisateur != null) ? nomUtilisateur : "Utilisateur " + utilisateurId;
        return utilisateur + " - " + action + " - " + dateHeure;
    }
}