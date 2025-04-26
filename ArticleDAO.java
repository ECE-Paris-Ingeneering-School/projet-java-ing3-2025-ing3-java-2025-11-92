package dao;

import model.Article;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données pour les articles
 */
public class ArticleDAO {

    // méthode pour récupérer tous les articles, triés par marque et nom
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article ORDER BY marque, nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {            
                // Pour chaque ligne du résultat, on crée un objet Article
                Article article = mapResultSetToArticle(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return articles;
    }

    // On recupere les article par marque 
    
    public List<Article> getArticlesByMarque(String marque) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE marque = ? ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, marque);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Article article = mapResultSetToArticle(rs);
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles par marque: " + e.getMessage());
        }
        return articles;
    }

    // On récupère toutes les marques distinctes
    public List<String> getAllMarques() {
        List<String> marques = new ArrayList<>();
        String sql = "SELECT DISTINCT marque FROM article ORDER BY marque";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                marques.add(rs.getString("marque"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des marques: " + e.getMessage());
        }
        return marques;
    }

    /**
     * Récupère un article par son ID
     * @param id l'identifiant de l'article
     * @return l'article correspondant ou null
     */
    public Article getArticleById(int id) {
        String sql = "SELECT * FROM article WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArticle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'article: " + e.getMessage());
        }
        return null;
    }

    // Pour ajouter un nouvel Article 
    public boolean ajouterArticle(Article article) {
        String sql = "INSERT INTO article (nom, marque, prix_unitaire, prix_gros, seuil_gros, stock) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getMarque());
            stmt.setDouble(3, article.getPrixUnitaire());
            stmt.setDouble(4, article.getPrixGros());
            stmt.setInt(5, article.getSeuilGros());
            stmt.setInt(6, article.getStock());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'article: " + e.getMessage());
            return false;
        }
    }

    // Met à jour un article existant
     // @param article l'article à mettre à jour
     // @return true si la mise à jour est réussie
    
    public boolean mettreAJourArticle(Article article) {
        String sql = "UPDATE article SET nom = ?, marque = ?, prix_unitaire = ?, " +
                "prix_gros = ?, seuil_gros = ?, stock = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getMarque());
            stmt.setDouble(3, article.getPrixUnitaire());
            stmt.setDouble(4, article.getPrixGros());
            stmt.setInt(5, article.getSeuilGros());
            stmt.setInt(6, article.getStock());
            stmt.setInt(7, article.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'article: " + e.getMessage());
            return false;
        }
    }

    // Pour supp un article
    public boolean supprimerArticle(int id) {
        String sql = "DELETE FROM article WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article: " + e.getMessage());
            return false;
        }
    }

     // Méthode pour mettre à jour le stock d'un article après un achat
    public boolean mettreAJourStock(int id, int quantite) {
        String sql = "UPDATE article SET stock = stock - ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantite);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }

    // Transformer une ligne de résultat SQL en objet Article
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getInt("id"));
        article.setNom(rs.getString("nom"));
        article.setMarque(rs.getString("marque"));
        article.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        article.setPrixGros(rs.getDouble("prix_gros"));
        article.setSeuilGros(rs.getInt("seuil_gros"));
        article.setStock(rs.getInt("stock"));
        return article;
    }

    // On récupère les articles les plus vendus, triés par quantité totale vendue
    public List<Object[]> getArticlesPlusVendus(int limit) {
        List<Object[]> resultat = new ArrayList<>();
        String sql = "SELECT a.nom, a.marque, SUM(lc.quantite) as total_vendu " +
                "FROM ligne_commande lc " +
                "JOIN article a ON lc.article_id = a.id " +
                "GROUP BY a.id " +
                "ORDER BY total_vendu DESC " +
                "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[3];
                    data[0] = rs.getString("nom");
                    data[1] = rs.getString("marque");
                    data[2] = rs.getInt("total_vendu");
                    resultat.add(data);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles les plus vendus: " + e.getMessage());
        }
        return resultat;
    }

    // Méthode pour récupérer les quantités totales vendues par article
    public List<Object[]> getVentesParArticle() {
        List<Object[]> resultat = new ArrayList<>();
        String sql = "SELECT a.nom, SUM(lc.quantite) as quantite_vendue " +
                "FROM ligne_commande lc " +
                "JOIN article a ON lc.article_id = a.id " +
                "GROUP BY a.id " +
                "ORDER BY quantite_vendue DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] data = new Object[2];
                data[0] = rs.getString("nom");
                data[1] = rs.getInt("quantite_vendue");
                resultat.add(data);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ventes par article: " + e.getMessage());
        }
        return resultat;
    }
}
