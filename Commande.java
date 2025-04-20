package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant une commande client
 */
public class Commande {
    private int id;
    private int clientId;
    private Date dateCommande;
    private List<LigneCommande> lignesCommande;

    public Commande() {
        this.lignesCommande = new ArrayList<>();
        this.dateCommande = new Date();
    }

    public Commande(int id, int clientId, Date dateCommande) {
        this.id = id;
        this.clientId = clientId;
        this.dateCommande = dateCommande;
        this.lignesCommande = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

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
            total += ligne.calculerPrix();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Commande #" + id + " - Client: " + clientId + " - Date: " + dateCommande;
    }
}