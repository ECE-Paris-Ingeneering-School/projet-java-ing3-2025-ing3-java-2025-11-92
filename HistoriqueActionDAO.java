package dao;

import model.HistoriqueAction;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données pour l'historique des actions
 */
public class HistoriqueActionDAO {

    /**
     * Ajoute une nouvelle action à l'historique
     * @param historiqueAction l'action à ajouter
     * @return true si l'ajout est réussi
     */
    public boolean ajouterAction(HistoriqueAction historiqueAction) {
        String sql = "INSERT INTO historique_action (utilisateur_id, action, date_heure) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, historiqueAction.getUtilisateurId());
            stmt.setString(2, historiqueAction.getAction());
            stmt.setTimestamp(3, new Timestamp(historiqueAction.getDateHeure().getTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    historiqueAction.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'action: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère l'historique des actions d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return la liste des actions
     */
    public List<HistoriqueAction> getActionsByUtilisateur(int utilisateurId) {
        List<HistoriqueAction> actions = new ArrayList<>();
        String sql = "SELECT ha.*, u.nom as nom_utilisateur " +
                "FROM historique_action ha " +
                "JOIN utilisateur u ON ha.utilisateur_id = u.id " +
                "WHERE ha.utilisateur_id = ? " +
                "ORDER BY ha.date_heure DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistoriqueAction action = mapResultSetToHistoriqueAction(rs);
                    actions.add(action);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des actions: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Récupère toutes les actions des administrateurs
     * @return la liste des actions des administrateurs
     */
    public List<HistoriqueAction> getActionsAdministrateurs() {
        List<HistoriqueAction> actions = new ArrayList<>();
        String sql = "SELECT ha.*, u.nom as nom_utilisateur " +
                "FROM historique_action ha " +
                "JOIN utilisateur u ON ha.utilisateur_id = u.id " +
                "JOIN administrateur a ON ha.utilisateur_id = a.utilisateur_id " +
                "ORDER BY ha.date_heure DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HistoriqueAction action = mapResultSetToHistoriqueAction(rs);
                actions.add(action);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des actions des administrateurs: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Récupère toutes les actions
     * @return la liste de toutes les actions
     */
    public List<HistoriqueAction> getAllActions() {
        List<HistoriqueAction> actions = new ArrayList<>();
        String sql = "SELECT ha.*, u.nom as nom_utilisateur " +
                "FROM historique_action ha " +
                "JOIN utilisateur u ON ha.utilisateur_id = u.id " +
                "ORDER BY ha.date_heure DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HistoriqueAction action = mapResultSetToHistoriqueAction(rs);
                actions.add(action);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des actions: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Mappe un ResultSet à un objet HistoriqueAction
     * @param rs le ResultSet contenant les données
     * @return l'objet HistoriqueAction créé
     * @throws SQLException en cas d'erreur de lecture
     */
    private HistoriqueAction mapResultSetToHistoriqueAction(ResultSet rs) throws SQLException {
        HistoriqueAction action = new HistoriqueAction();
        action.setId(rs.getInt("id"));
        action.setUtilisateurId(rs.getInt("utilisateur_id"));
        action.setAction(rs.getString("action"));
        action.setDateHeure(rs.getTimestamp("date_heure"));
        
        // Le nom de l'utilisateur, s'il est disponible
        try {
            action.setNomUtilisateur(rs.getString("nom_utilisateur"));
        } catch (SQLException e) {
            // Ignorer si la colonne n'existe pas
        }
        
        return action;
    }
}