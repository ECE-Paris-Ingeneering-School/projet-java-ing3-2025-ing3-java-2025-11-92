package dao;

import model.Article;
import model.Client;
import model.Commande;
import model.LigneCommande;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gérer les accès aux données liées aux commandes
 */
public class CommandeDAO {

    /**
     * Ajoute une nouvelle commande à la base de données
     * @param commande la commande à ajouter
     * @return true si l'ajout a bien été effectué, false sinon
     */
    public boolean ajouterCommande(Commande commande) {
        String sqlCommande = "INSERT INTO commande (client_id, date_commande) VALUES (?, ?)";
        String sqlLigneCommande = "INSERT INTO ligne_commande (commande_id, article_id, quantite) VALUES (?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // On insère d'abord la commande
            try (PreparedStatement stmtCommande = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmtCommande.setInt(1, commande.getClientId());
                stmtCommande.setTimestamp(2, new Timestamp(commande.getDateCommande().getTime()));
                
                int affectedRows = stmtCommande.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la création de la commande, aucune ligne affectée.");
                }
                
                try (ResultSet generatedKeys = stmtCommande.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int commandeId = generatedKeys.getInt(1);
                        commande.setId(commandeId);
                        
                        // On insère les lignes de commande
                        try (PreparedStatement stmtLigneCommande = conn.prepareStatement(sqlLigneCommande)) {
                            for (LigneCommande ligne : commande.getLignesCommande()) {
                                stmtLigneCommande.setInt(1, commandeId);
                                stmtLigneCommande.setInt(2, ligne.getArticleId());
                                stmtLigneCommande.setInt(3, ligne.getQuantite());
                                stmtLigneCommande.addBatch();
                                
                                // On met à jour le stock de chaque article
                                updateStock(conn, ligne.getArticleId(), ligne.getQuantite());
                            }
                            stmtLigneCommande.executeBatch();
                        }
                    } else {
                        throw new SQLException("Échec de la création de la commande, aucun ID récupéré.");
                    }
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Problème avec le rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'ajout de la commande: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erreur réinitialisation autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Met à jour le stock après ajout d'une commande
     * @param conn la connexion à la base de données
     * @param articleId l'ID de l'article
     * @param quantite la quantité d'articles à déduire
     * @throws SQLException en cas d'erreur SQL
     */
    private void updateStock(Connection conn, int articleId, int quantite) throws SQLException {
        String sql = "UPDATE article SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, articleId);
            stmt.executeUpdate();
        }
    }

    /**
     * Récupère les commandes d'un client spécifique
     * @param clientId l'ID du client
     * @return liste des commandes passées par ce client
     */
    public List<Commande> getCommandesByClient(int clientId) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE client_id = ? ORDER BY date_commande DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs);
                    
                    // On récupère les lignes associées à cette commande
                    commande.setLignesCommande(getLignesCommande(commande.getId()));
                    
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }

    /**
     * Récupère toutes les commandes
     * @return toutes les commandes existantes
     */
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.nom as nom_client " +
                "FROM commande c " +
                "JOIN utilisateur u ON c.client_id = u.id " +
                "ORDER BY date_commande DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Commande commande = mapResultSetToCommande(rs);
                
                // On récupère les lignes de commande
                commande.setLignesCommande(getLignesCommande(commande.getId()));
                
                commandes.add(commande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }

    /**
     * Récupère une commande spécifique par son ID
     * @param commandeId l'ID de la commande
     * @return la commande correspondante avec ses lignes
     */
    public Commande getCommandeById(int commandeId) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commandeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs);
                    
                    // Ajout des lignes de commande
                    commande.setLignesCommande(getLignesCommande(commande.getId()));
                    
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère les lignes d'une commande par son ID
     * @param commandeId l'ID de la commande
     * @return les lignes de la commande
     */
    private List<LigneCommande> getLignesCommande(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, a.nom, a.marque, a.prix_unitaire, a.prix_gros, a.seuil_gros, a.stock " +
                "FROM ligne_commande lc " +
                "JOIN article a ON lc.article_id = a.id " +
                "WHERE lc.commande_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commandeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LigneCommande ligne = new LigneCommande();
                    ligne.setId(rs.getInt("id"));
                    ligne.setCommandeId(rs.getInt("commande_id"));
                    ligne.setArticleId(rs.getInt("article_id"));
                    ligne.setQuantite(rs.getInt("quantite"));
                    
                    // Construction de l'objet Article
                    Article article = new Article();
                    article.setId(rs.getInt("article_id"));
                    article.setNom(rs.getString("nom"));
                    article.setMarque(rs.getString("marque"));
                    article.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    article.setPrixGros(rs.getDouble("prix_gros"));
                    article.setSeuilGros(rs.getInt("seuil_gros"));
                    article.setStock(rs.getInt("stock"));
                    
                    ligne.setArticle(article);
                    
                    lignes.add(ligne);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de commande: " + e.getMessage());
        }
        return lignes;
    }

    /**
     * Mappe un ResultSet vers un objet Commande
     * @param rs le ResultSet contenant les données
     * @return l'objet Commande
     * @throws SQLException si erreur de lecture
     */
    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setId(rs.getInt("id"));
        commande.setClientId(rs.getInt("client_id"));
        commande.setDateCommande(rs.getTimestamp("date_commande"));
        return commande;
    }
    
    /**
     * Récupère les meilleurs clients en fonction du nombre de commandes
     * @param limit limite du nombre de clients à récupérer
     * @return la liste des meilleurs clients
     */
    public List<Object[]> getMeilleursClients(int limit) {
        List<Object[]> resultat = new ArrayList<>();
        String sql = "SELECT u.nom, COUNT(c.id) as nb_commandes, SUM(lc.quantite) as nb_articles " +
                "FROM commande c " +
                "JOIN utilisateur u ON c.client_id = u.id " +
                "JOIN ligne_commande lc ON c.id = lc.commande_id " +
                "GROUP BY c.client_id " +
                "ORDER BY nb_commandes DESC, nb_articles DESC " +
                "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[3];
                    data[0] = rs.getString("nom");
                    data[1] = rs.getInt("nb_commandes");
                    data[2] = rs.getInt("nb_articles");
                    resultat.add(data);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des meilleurs clients: " + e.getMessage());
        }
        return resultat;
    }
}
