package model;

/**
 * Classe représentant un article du catalogue
 */
public class Article {
    private int id;
    private String nom;
    private String marque;
    private double prixUnitaire;
    private double prixGros;
    private int seuilGros;
    private int stock;
    private String imageUrl;

    public Article() {
    }

    public Article(int id, String nom, String marque, double prixUnitaire, double prixGros, int seuilGros, int stock, String imageUrl) {
        this.id = id;
        this.nom= nom;
        this.marque= marque;
        this.prixUnitaire= prixUnitaire;
        this.prixGros =prixGros;
        this.seuilGros =seuilGros;
        this.stock = stock;
        this.imageUrl= imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id=id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom=nom;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque= marque;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getPrixGros() {
        return prixGros;
    }

    public void setPrixGros(double prixGros) {
        this.prixGros= prixGros;
    }

    public int getSeuilGros() {
        return seuilGros;
    }

    public void setSeuilGros(int seuilGros) {
        this.seuilGros =seuilGros;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock =stock;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl =imageUrl;
    }

    /**
     * Calcule le prix pour une quantité donnée en tenant compte des remises
     * @param quantite la quantité commandée
     * @return le prix total calculé
     */
    public double calculerPrix(int quantite) {
        if (quantite >= seuilGros) {
            int qteAvecRemise = quantite / seuilGros;
            int qteNormale =quantite % seuilGros;
            return (qteAvecRemise * seuilGros * prixGros) + (qteNormale * prixUnitaire);
        } else {
            return quantite * prixUnitaire;
        }
    }

    @Override
    public String toString() {
        return nom + " (" + marque + ")";
    }
}
