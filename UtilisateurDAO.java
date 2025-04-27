package dao;

import model.Administrateur;
import model.Client;
import model.Utilisateur;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès aux données pour les utilisateurs (admins, clients)
 */
public class UtilisateurDAO {

    // Vérifie les infos de l'utilisateur pour l'authentification
    public Utilisateur authentifier(String email, String motDePasse) {
        String sql ="SELECT u.id, u.nom, u.email, u.mot_de_passe, " +
                "c.est_ancien_client, " +
                "CASE WHEN a.utilisateur_id IS NOT NULL THEN 'ADMIN' ELSE 'CLIENT' END as type " +
                "FROM utilisateur u " +
                "LEFT JOIN client c ON u.id = c.utilisateur_id " +
                "LEFT JOIN administrateur a ON u.id = a.utilisateur_id " +
                "WHERE u.email = ? AND u.mot_de_passe = ?";
        
        // Affiche une tentative d'authentification
        System.out.println("Tentative d'authentification pour: " + email);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email); // On place l'email
            stmt.setString(2, motDePasse); // On place le mot de passe
            
            // Affichage de la requête SQL pour debug
            System.out.println("Exécution de la requête SQL: " + stmt);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type"); // On récupère le type
                    System.out.println("Utilisateur trouvé, type: " + type);
                    
                    // Si admin, on retourne un admin
                    if ("ADMIN".equals(type)) {
                        Administrateur admin = new Administrateur();
                        admin.setId(rs.getInt("id"));
                        admin.setNom(rs.getString("nom"));
                        admin.setEmail(rs.getString("email"));
                        admin.setMotDePasse(rs.getString("mot_de_passe"));
                        return admin;
                    } else { // Sinon on retourne un client
                        Client client = new Client();
                        client.setId(rs.getInt("id"));
                        client.setNom(rs.getString("nom"));
                        client.setEmail(rs.getString("email"));
                        client.setMotDePasse(rs.getString("mot_de_passe"));
                        client.setEstAncienClient(rs.getBoolean("est_ancien_client"));
                        return client;
                    }
                } else {
                    // Aucun utilisateur trouvé
                    System.out.println("Aucun utilisateur trouvé avec ces identifiants");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Ajouter un client dans la base
    public boolean ajouterClient(Client client) {
        String sqlUtilisateur = "INSERT INTO utilisateur (nom, email, mot_de_passe) VALUES (?, ?, ?)";
        String sqlClient = "INSERT INTO client (utilisateur_id, est_ancien_client) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transactionnalité, on commence un bloc de travail
            
            // Ajouter l'utilisateur dans la table utilisateur
            try (PreparedStatement stmtUtilisateur = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS)) {
                stmtUtilisateur.setString(1, client.getNom());
                stmtUtilisateur.setString(2, client.getEmail());
                stmtUtilisateur.setString(3, client.getMotDePasse());
                
                int affectedRows = stmtUtilisateur.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("La création de l'utilisateur a échoué, aucune ligne affectée.");
                }
                
                try (ResultSet generatedKeys = stmtUtilisateur.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int utilisateurId = generatedKeys.getInt(1); // On récupère l'ID de l'utilisateur
                        client.setId(utilisateurId);
                        
                        // Ajouter le client dans la table client
                        try (PreparedStatement stmtClient = conn.prepareStatement(sqlClient)) {
                            stmtClient.setInt(1, utilisateurId);
                            stmtClient.setBoolean(2, client.isEstAncienClient());
                            stmtClient.executeUpdate();
                        }
                    } else {
                        throw new SQLException("La création de l'utilisateur a échoué, aucun ID obtenu.");
                    }
                }
            }
            
            conn.commit(); // Commit des changements
            return true;
        } catch (SQLException e) {
            // En cas d'erreur, on annule la transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erreur lors de l'ajout du client: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Réinitialisation de l'auto commit
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la réinitialisation de l'autocommit: " + e.getMessage());
                }
            }
        }
    }

    // Vérifie si un email existe déjà
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si le compte existe, on retourne vrai
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email: " + e.getMessage());
        }
        return false;
    }

    // Récupère tous les clients
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.email, u.mot_de_passe, c.est_ancien_client " +
                "FROM utilisateur u " +
                "JOIN client c ON u.id = c.utilisateur_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setNom(rs.getString("nom"));
                client.setEmail(rs.getString("email"));
                client.setMotDePasse(rs.getString("mot_de_passe"));
                client.setEstAncienClient(rs.getBoolean("est_ancien_client"));
                clients.add(client); // Ajouter le client à la liste
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des clients: " + e.getMessage());
        }
        return clients;
    }

    // Récupère un client par son ID
    public Client getClientById(int id) {
        String sql = "SELECT u.id, u.nom, u.email, u.mot_de_passe, c.est_ancien_client " +
                "FROM utilisateur u " +
                "JOIN client c ON u.id = c.utilisateur_id " +
                "WHERE u.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setNom(rs.getString("nom"));
                    client.setEmail(rs.getString("email"));
                    client.setMotDePasse(rs.getString("mot_de_passe"));
                    client.setEstAncienClient(rs.getBoolean("est_ancien_client"));
                    return client;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }
        return null;
    }
}
