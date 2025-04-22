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

    /**
     * Récupère tous les articles
     * @return la liste des articles
     */
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article ORDER BY marque, nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
        }
        return articles;
    }

    /**
     * Récupère les articles par marque
     * @param marque la marque à filtrer
     * @return la liste des articles de cette marque
     */
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

    /**
     * Récupère toutes les marques distinctes
     * @return la liste des marques
     */
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

    /**
     * Ajoute un nouvel article
     * @param article l'article à ajouter
     * @return true si l'ajout est réussi
     */
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

    /**
     * Met à jour un article existant
     * @param article l'article à mettre à jour
     * @return true si la mise à jour est réussie
     */
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

    /**
     * Supprime un article
     * @param id l'identifiant de l'article à supprimer
     * @return true si la suppression est réussie
     */
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

    /**
     * Met à jour le stock d'un article
     * @param id l'identifiant de l'article
     * @param quantite la quantité à déduire du stock
     * @return true si la mise à jour est réussie
     */
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

    /**
     * Mappe un ResultSet à un objet Article
     * @param rs le ResultSet contenant les données
     * @return l'objet Article créé
     * @throws SQLException en cas d'erreur de lecture
     */
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

    /**
     * Récupère les articles les plus vendus
     * @param limit le nombre d'articles à récupérer
     * @return la liste des articles les plus vendus
     */
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

    /**
     * Récupère les ventes par article pour les graphiques
     * @return la liste des ventes par article
     */
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