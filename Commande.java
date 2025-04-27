package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant une commande client
 * Contient toutes les informations relatives à une commande passée par un client
 */
public class Commande {
    // Identifiant unique de la commande
    private int id;
    // Référence vers le client qui a passé la commande
    private int clientId;
    // Date à laquelle la commande a été créée
    private Date dateCommande;
    // Collection des produits commandés avec leurs quantités
    private List<LigneCommande> lignesCommande;

    // Constructeur par défaut qui initialise une commande vide avec la date actuelle
    public Commande() {
        this.lignesCommande = new ArrayList<>();
        this.dateCommande = new Date();
    }

    // Constructeur avec paramètres pour créer une commande complète
    public Commande(int id, int clientId, Date dateCommande) {
        this.id = id;
        this.clientId = clientId;
        this.dateCommande = dateCommande;
        this.lignesCommande = new ArrayList<>();
    }

    // Récupère l'identifiant de la commande
    public int getId() {
        return id;
    }

    // Définit l'identifiant de la commande
    public void setId(int id) {
        this.id = id;
    }

    // Récupère l'identifiant du client associé à la commande
    public int getClientId() {
        return clientId;
    }

    // Définit l'identifiant du client pour cette commande
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    // Récupère la date de création de la commande
    public Date getDateCommande() {
        return dateCommande;
    }

    // Modifie la date de la commande
    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    // Récupère la liste des produits commandés
    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    // Remplace la liste complète des lignes de commande
    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    /**
     * Ajoute une ligne de commande
     * @param ligneCommande la ligne à ajouter
     */
    public void ajouterLigne(LigneCommande ligneCommande) {
        this.lignesCommande.add(ligneCommande);
    }

    /**
     * Calcule le montant total de la commande
     * @return le montant total
     */
    public double calculerTotal() {
        double total = 0;
        for (LigneCommande ligne : lignesCommande) {
            // Accumule le prix de chaque ligne pour obtenir le total
            total += ligne.calculerPrix();
        }
        return total;
    }

    // Représentation textuelle de la commande pour l'affichage
    @Override
    public String toString() {
        return "Commande #" + id + " - Client: " + clientId + " - Date: " + dateCommande;
    }
}
