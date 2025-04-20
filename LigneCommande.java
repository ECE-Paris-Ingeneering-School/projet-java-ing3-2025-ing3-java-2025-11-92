package model;

/**
 * Classe représentant une ligne de commande
 */
public class LigneCommande {
    private int id;
    private int commandeId;
    private int articleId;
    private int quantite;
    private Article article;  // Pour faciliter le calcul de prix et l'affichage

    public LigneCommande() {
    }

    public LigneCommande(int id, int commandeId, int articleId, int quantite) {
        this.id = id;
        this.commandeId = commandeId;
        this.articleId = articleId;
        this.quantite = quantite;
    }

    public LigneCommande(Article article, int quantite) {
        this.article = article;
        this.articleId = article.getId();
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
        this.articleId = article.getId();
    }

    /**
     * Calcule le prix de cette ligne en tenant compte des remises
     * @return le prix total calculé
     */
    public double calculerPrix() {
        if (article != null) {
            return article.calculerPrix(quantite);
        }
        return 0;
    }

    @Override
    public String toString() {
        String nomArticle = (article != null) ? article.getNom() : "Article " + articleId;
        return nomArticle + " x " + quantite;
    }
}