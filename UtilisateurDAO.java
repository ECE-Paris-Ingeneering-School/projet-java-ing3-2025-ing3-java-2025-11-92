package dao;

import model.Administrateur;
import model.Client;
import model.Utilisateur;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // méthode pour authentifier un utilisateur
    public Utilisateur authentifier(String email, String motDePasse) {
        String sql = "SELECT u.id, u.nom, u.email, u.mot_de_passe, " +
                "c.est_ancien_client, " +
                "CASE WHEN a.utilisateur_id IS NOT NULL THEN 'ADMIN' ELSE 'CLIENT' END as type " +
                "FROM utilisateur u " +
                "LEFT JOIN client c ON u.id = c.utilisateur_id " +
                "LEFT JOIN administrateur a ON u.id = a.utilisateur_id " +
                "WHERE u.email = ? AND u.mot_de_passe = ?";

        System.out.println("tentative d'authentification pour: " + email);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, motDePasse);

            System.out.println("execution de la requete sql: " + stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type");
                    System.out.println("utilisateur trouve, type: " + type);

                    if ("ADMIN".equals(type)) {
                        // si c'est un admin, on renvoie un administrateur
                        Administrateur admin = new Administrateur();
                        admin.setId(rs.getInt("id"));
                        admin.setNom(rs.getString("nom"));
                        admin.setEmail(rs.getString("email"));
                        admin.setMotDePasse(rs.getString("mot_de_passe"));
                        return admin;
                    } else {
                        // sinon on renvoie un client
                        Client client = new Client();
                        client.setId(rs.getInt("id"));
                        client.setNom(rs.getString("nom"));
                        client.setEmail(rs.getString("email"));
                        client.setMotDePasse(rs.getString("mot_de_passe"));
                        client.setEstAncienClient(rs.getBoolean("est_ancien_client"));
                        return client;
                    }
                } else {
                    System.out.println("aucun utilisateur trouve avec ces identifiants");
                }
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // méthode pour ajouter un nouveau client
    public boolean ajouterClient(Client client) {
        String sqlUtilisateur = "INSERT INTO utilisateur (nom, email, mot_de_passe) VALUES (?, ?, ?)";
        String sqlClient = "INSERT INTO client (utilisateur_id, est_ancien_client) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // on gère la transaction manuellement

            // insertion dans la table utilisateur
            try (PreparedStatement stmtUtilisateur = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS)) {
                stmtUtilisateur.setString(1, client.getNom());
                stmtUtilisateur.setString(2, client.getEmail());
                stmtUtilisateur.setString(3, client.getMotDePasse());

                int affectedRows = stmtUtilisateur.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("la creation de l'utilisateur a echoue, aucune ligne affectee");
                }

                // récupération de l'id généré
                try (ResultSet generatedKeys = stmtUtilisateur.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int utilisateurId = generatedKeys.getInt(1);
                        client.setId(utilisateurId);

                        // insertion dans la table client
                        try (PreparedStatement stmtClient = conn.prepareStatement(sqlClient)) {
                            stmtClient.setInt(1, utilisateurId);
                            stmtClient.setBoolean(2, client.isEstAncienClient());
                            stmtClient.executeUpdate();
                        }
                    } else {
                        throw new SQLException("la creation de l'utilisateur a echoue, aucun id retourne");
                    }
                }
            }

            conn.commit(); // on valide la transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // si erreur, on rollback
                } catch (SQLException ex) {
                    System.err.println("erreur lors du rollback: " + ex.getMessage());
                }
            }
            System.err.println("erreur lors de l'ajout du client: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // on remet l'autocommit
                } catch (SQLException e) {
                    System.err.println("erreur lors de la reinitialisation de l'autocommit: " + e.getMessage());
                }
            }
        }
    }

    // méthode pour vérifier si un email existe déjà
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true si compte > 0
                }
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la verification de l'email: " + e.getMessage());
        }
        return false;
    }

    // méthode pour récupérer tous les clients
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
                clients.add(client); // on ajoute le client à la liste
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la recuperation des clients: " + e.getMessage());
        }
        return clients;
    }

    // méthode pour récupérer un client par son id
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
                    return client; // on retourne le client trouve
                }
            }
        } catch (SQLException e) {
            System.err.println("erreur lors de la recuperation du client: " + e.getMessage());
        }
        return null;
    }
}
