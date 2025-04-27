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

    // Récupère tous les articles
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article ORDER BY marque, nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // On parcourt le résultat et on crée les articles
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return articles;
    }

    // Récupère les articles d'une marque donnée
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

    // Récupère toutes les marques disponibles
    public List<String> getAllMarques() {
        List<String> marques = new ArrayList<>();
        String sql = "SELECT DISTINCT marque FROM article ORDER BY marque";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // On ajoute chaque marque trouvée
            while (rs.next()) {
                marques.add(rs.getString("marque"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des marques: " + e.getMessage());
        }
        return marques;
    }

    // Récupère un article par son identifiant
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

    // Ajoute un nouvel article à la base de données
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
                return false;  // Si aucune ligne n'est affectée, ça a échoué
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getInt(1)); // On récupère l'ID généré
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'article: " + e.getMessage());
            return false;
        }
    }

    // Met à jour les informations d'un article
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
            
            return stmt.executeUpdate() > 0;  // Retourne true si au moins une ligne est modifiée
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'article: " + e.getMessage());
            return false;
        }
    }

    // Supprime un article de la base
    public boolean supprimerArticle(int id) {
        String sql = "DELETE FROM article WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;  // On retourne true si l'article a été supprimé
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article: " + e.getMessage());
            return false;
        }
    }

    // Met à jour le stock d'un article (on soustrait la quantité)
    public boolean mettreAJourStock(int id, int quantite) {
        String sql = "UPDATE article SET stock = stock - ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantite);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;  // On retourne true si le stock a été mis à jour
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
            return false;
        }
    }

    // Mappe le ResultSet aux attributs d'un article
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

    // Récupère les articles les plus vendus, limités à un nombre défini
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

    // Récupère les ventes par article pour créer des graphiques
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
