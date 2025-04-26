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
 * Classe d'accès aux données pour les commandes
 */
public class CommandeDAO {

    /**
     * Ajoute une nouvelle commande
     * @param commande la commande à ajouter
     * @return true si l'ajout est réussi
     */
    public boolean ajouterCommande(Commande commande) {
        String sqlCommande = "INSERT INTO commande (client_id, date_commande) VALUES (?, ?)";
        String sqlLigneCommande = "INSERT INTO ligne_commande (commande_id, article_id, quantite) VALUES (?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // On désactive l'autocommit pour gérer manuellement la transaction
            
            // On prépare l'insertion de la commande
            try (PreparedStatement stmtCommande = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmtCommande.setInt(1, commande.getClientId());
                stmtCommande.setTimestamp(2, new Timestamp(commande.getDateCommande().getTime()));
                
                int affectedRows = stmtCommande.executeUpdate(); // On exécute l'insertion de la commande
                if (affectedRows == 0) {
                    throw new SQLException("La création de la commande a échoué, aucune ligne affectée.");
                }
                
                try (ResultSet generatedKeys = stmtCommande.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int commandeId = generatedKeys.getInt(1);
                        commande.setId(commandeId); // On récupère et assigne l'ID généré à la commande
                        
                        // On prépare l'insertion des lignes de commande
                        try (PreparedStatement stmtLigneCommande = conn.prepareStatement(sqlLigneCommande)) {
                            for (LigneCommande ligne : commande.getLignesCommande()) {
                                stmtLigneCommande.setInt(1, commandeId);
                                stmtLigneCommande.setInt(2, ligne.getArticleId());
                                stmtLigneCommande.setInt(3, ligne.getQuantite());
                                stmtLigneCommande.addBatch(); // On ajoute la requête au batch
                                
                                updateStock(conn, ligne.getArticleId(), ligne.getQuantite()); // On met à jour le stock de l'article
                            }
                            stmtLigneCommande.executeBatch(); // On exécute toutes les insertions en une seule fois
                        }
                    } else {
                        throw new SQLException("La création de la commande a échoué, aucun ID obtenu.");
                    }
                }
            }
            
            conn.commit(); // On valide la transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // On annule la transaction en cas d'erreur
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'ajout de la commande: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // On réactive l'autocommit
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la réinitialisation de l'autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Met à jour le stock d'un article
     * @param conn la connexion à la base de données
     * @param articleId l'ID de l'article
     * @param quantite la quantité à déduire
     * @throws SQLException en cas d'erreur SQL
     */
    private void updateStock(Connection conn, int articleId, int quantite) throws SQLException {
        String sql = "UPDATE article SET stock = stock - ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, articleId);
            stmt.executeUpdate(); // On met à jour le stock de l'article
        }
    }

    /**
     * Récupère les commandes d'un client
     * @param clientId l'ID du client
     * @return la liste des commandes
     */
    public List<Commande> getCommandesByClient(int clientId) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE client_id = ? ORDER BY date_commande DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs); // On mappe la commande
                    commande.setLignesCommande(getLignesCommande(commande.getId())); // On récupère ses lignes de commande
                    commandes.add(commande); // On ajoute la commande à la liste
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }

    /**
     * Récupère toutes les commandes
     * @return la liste de toutes les commandes
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
                Commande commande = mapResultSetToCommande(rs); // On mappe la commande
                commande.setLignesCommande(getLignesCommande(commande.getId())); // On récupère ses lignes de commande
                commandes.add(commande); // On ajoute la commande à la liste
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }

    /**
     * Récupère les détails d'une commande
     * @param commandeId l'ID de la commande
     * @return la commande avec ses détails
     */
    public Commande getCommandeById(int commandeId) {
        String sql = "SELECT * FROM commande WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commandeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs); // On mappe la commande
                    commande.setLignesCommande(getLignesCommande(commande.getId())); // On récupère ses lignes de commande
                    return commande;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupère les lignes d'une commande
     * @param commandeId l'ID de la commande
     * @return la liste des lignes de commande
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
                    
                    // On crée l'article associé à la ligne de commande
                    Article article = new Article();
                    article.setId(rs.getInt("article_id"));
                    article.setNom(rs.getString("nom"));
                    article.setMarque(rs.getString("marque"));
                    article.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    article.setPrixGros(rs.getDouble("prix_gros"));
                    article.setSeuilGros(rs.getInt("seuil_gros"));
                    article.setStock(rs.getInt("stock"));
                    
                    ligne.setArticle(article); // On associe l'article à la ligne
                    lignes.add(ligne); // On ajoute la ligne à la liste
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des lignes de commande: " + e.getMessage());
        }
        return lignes;
    }

    /**
     * Mappe un ResultSet à un objet Commande
     * @param rs le ResultSet contenant les données
     * @return l'objet Commande créé
     * @throws SQLException en cas d'erreur de lecture
     */
    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setId(rs.getInt("id"));
        commande.setClientId(rs.getInt("client_id"));
        commande.setDateCommande(rs.getTimestamp("date_commande"));
        return commande; // On retourne l'objet Commande créé
    }
    
    /**
     * Récupère les meilleurs clients
     * @param limit le nombre de clients à récupérer
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
                    data[0] = rs.getString("nom"); // On récupère le nom du client
                    data[1] = rs.getInt("nb_commandes"); // On récupère le nombre de commandes
                    data[2] = rs.getInt("nb_articles"); // On récupère le nombre total d'articles commandés
                    resultat.add(data); // On ajoute les données à la liste
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des meilleurs clients: " + e.getMessage());
        }
        return resultat;
    }
}
