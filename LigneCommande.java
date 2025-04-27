package model;

/**
 * Classe représentant une ligne de commande
 * Correspond à un article particulier dans une commande avec sa quantité
 */
public class LigneCommande {
    // Identifiant unique de la ligne de commande
    private int id;
    // Référence vers la commande parente
    private int commandeId;
    // Référence vers l'article commandé
    private int articleId;
    // Nombre d'unités commandées pour cet article
    private int quantite;
    // Objet Article associé pour un accès direct aux informations de l'article
    private Article article;  // Pour faciliter le calcul de prix et l'affichage

    // Constructeur par défaut
    public LigneCommande() {
    }

    // Constructeur avec tous les identifiants et la quantité
    public LigneCommande(int id, int commandeId, int articleId, int quantite) {
        this.id = id;
        this.commandeId = commandeId;
        this.articleId = articleId;
        this.quantite = quantite;
    }

    // Constructeur pratique pour créer une ligne de commande directement avec un objet Article
    public LigneCommande(Article article, int quantite) {
        this.article = article;
        this.articleId = article.getId();
        this.quantite = quantite;
    }

    // Récupère l'identifiant de la ligne
    public int getId() {
        return id;
    }

    // Définit l'identifiant de la ligne
    public void setId(int id) {
        this.id = id;
    }

    // Récupère l'identifiant de la commande parente
    public int getCommandeId() {
        return commandeId;
    }

    // Définit l'identifiant de la commande parente
    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    // Récupère l'identifiant de l'article
    public int getArticleId() {
        return articleId;
    }

    // Définit l'identifiant de l'article
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    // Récupère la quantité commandée
    public int getQuantite() {
        return quantite;
    }

    // Modifie la quantité commandée
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Récupère l'objet Article associé
    public Article getArticle() {
        return article;
    }

    // Définit l'objet Article et met à jour automatiquement l'ID de l'article
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
            // Délègue le calcul du prix total à l'objet Article avec la quantité fournie
            return article.calculerPrix(quantite);
        }
        return 0;
    }

    // Affichage formaté de la ligne de commande
    @Override
    public String toString() {
        // Utilise le nom de l'article s'il est disponible, sinon utilise son ID
        String nomArticle = (article != null) ? article.getNom() : "Article " + articleId;
        return nomArticle + " x " + quantite;
    }
}
