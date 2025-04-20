package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant le panier d'achat d'un client
 */
public class Panier {
    private Client client;
    private List<LigneCommande> lignes;

    public Panier(Client client) {
        this.client = client;
        this.lignes = new ArrayList<>();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    /**
     * Ajoute un article au panier
     * @param article l'article à ajouter
     * @param quantite la quantité
     */
    public void ajouterArticle(Article article, int quantite) {
        // Vérifier si l'article existe déjà dans le panier
        for (LigneCommande ligne : lignes) {
            if (ligne.getArticleId() == article.getId()) {
                // Mettre à jour la quantité
                ligne.setQuantite(ligne.getQuantite() + quantite);
                return;
            }
        }
        
        // Si l'article n'est pas dans le panier, créer une nouvelle ligne
        LigneCommande nouvelleLigne = new LigneCommande(article, quantite);
        lignes.add(nouvelleLigne);
    }

    /**
     * Supprime un article du panier
     * @param articleId l'ID de l'article à supprimer
     */
    public void supprimerArticle(int articleId) {
        lignes.removeIf(ligne -> ligne.getArticleId() == articleId);
    }

    /**
     * Met à jour la quantité d'un article
     * @param articleId l'ID de l'article
     * @param quantite la nouvelle quantité
     */
    public void mettreAJourQuantite(int articleId, int quantite) {
        for (LigneCommande ligne : lignes) {
            if (ligne.getArticleId() == articleId) {
                if (quantite <= 0) {
                    supprimerArticle(articleId);
                } else {
                    ligne.setQuantite(quantite);
                }
                return;
            }
        }
    }

    /**
     * Vide le panier
     */
    public void vider() {
        lignes.clear();
    }

    /**
     * Calcule le montant total du panier
     * @return le montant total
     */
    public double calculerTotal() {
        double total = 0;
        for (LigneCommande ligne : lignes) {
            total += ligne.calculerPrix();
        }
        return total;
    }

    /**
     * Convertit le panier en commande
     * @return une nouvelle commande basée sur le contenu du panier
     */
    public Commande convertirEnCommande() {
        Commande commande = new Commande();
        commande.setClientId(client.getId());
        for (LigneCommande ligne : lignes) {
            commande.ajouterLigne(ligne);
        }
        return commande;
    }

    /**
     * Vérifie si le panier est vide
     * @return true si le panier est vide
     */
    public boolean estVide() {
        return lignes.isEmpty();
    }

    /**
     * Retourne le nombre d'articles dans le panier
     * @return le nombre d'articles
     */
    public int getNombreArticles() {
        int total = 0;
        for (LigneCommande ligne : lignes) {
            total += ligne.getQuantite();
        }
        return total;
    }
}