package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant le panier d'achat d'un client
 * Permet de gérer temporairement les articles sélectionnés avant de finaliser la commande
 */
public class Panier {
    // Client associé à ce panier
    private Client client;
    // Liste des articles ajoutés au panier avec leurs quantités
    private List<LigneCommande> lignes;

    // Constructeur qui initialise un panier vide pour un client spécifique
    public Panier(Client client) {
        this.client = client;
        this.lignes = new ArrayList<>();
    }

    // Récupère le client propriétaire du panier
    public Client getClient() {
        return client;
    }

    // Change le client propriétaire du panier
    public void setClient(Client client) {
        this.client = client;
    }

    // Récupère la liste des articles dans le panier
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
        // Utilise la méthode removeIf pour supprimer l'article correspondant
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
                // Si la quantité est négative ou nulle, on supprime l'article
                if (quantite <= 0) {
                    supprimerArticle(articleId);
                } else {
                    // Sinon on met à jour la quantité
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
        // Supprime tous les articles du panier
        lignes.clear();
    }

    /**
     * Calcule le montant total du panier
     * @return le montant total
     */
    public double calculerTotal() {
        double total = 0;
        for (LigneCommande ligne : lignes) {
            // Additionne le prix de chaque ligne de commande
            total += ligne.calculerPrix();
        }
        return total;
    }

    /**
     * Convertit le panier en commande
     * @return une nouvelle commande basée sur le contenu du panier
     */
    public Commande convertirEnCommande() {
        // Crée une nouvelle commande
        Commande commande = new Commande();
        // Associe la commande au client du panier
        commande.setClientId(client.getId());
        // Transfère toutes les lignes du panier à la commande
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
        // Utilise la méthode isEmpty() de la liste
        return lignes.isEmpty();
    }

    /**
     * Retourne le nombre d'articles dans le panier
     * @return le nombre d'articles
     */
    public int getNombreArticles() {
        int total = 0;
        for (LigneCommande ligne : lignes) {
            // Additionne les quantités de chaque article
            total += ligne.getQuantite();
        }
        return total;
    }
}
