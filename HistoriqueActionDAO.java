package dao;

import model.HistoriqueAction;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour gerer l'acces aux donnees de l'historique des actions
 */
public class HistoriqueActionDAO {

    /**
     * Methode pour ajouter une action dans l'historique
     * @param historiqueAction l'action a enregistrer
     * @return true si tout s'est bien passe
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
            System.err.println("Probleme lors de l'ajout de l'action: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupere toutes les actions effectuees par un utilisateur specifique
     * @param utilisateurId identifiant de l'utilisateur
     * @return liste des actions trouvees
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
            System.err.println("Erreur pendant la recuperation des actions: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Ici on recupere toutes les actions faites par les administrateurs
     * @return liste des actions admin
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
            System.err.println("Impossible de recuperer les actions des admins: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Rassemble tout l'historique, peu importe l'utilisateur
     * @return toutes les actions
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
            System.err.println("Probleme lors du chargement de toutes les actions: " + e.getMessage());
        }
        return actions;
    }

    /**
     * Methode qui convertit un ResultSet en objet HistoriqueAction
     * @param rs les donnees SQL a lire
     * @return un objet HistoriqueAction
     * @throws SQLException si erreur SQL
     */
    private HistoriqueAction mapResultSetToHistoriqueAction(ResultSet rs) throws SQLException {
        HistoriqueAction action = new HistoriqueAction();
        action.setId(rs.getInt("id"));
        action.setUtilisateurId(rs.getInt("utilisateur_id"));
        action.setAction(rs.getString("action"));
        action.setDateHeure(rs.getTimestamp("date_heure"));
        
        // On essaye de recuperer le nom de l'utilisateur s'il est dispo
        try {
            action.setNomUtilisateur(rs.getString("nom_utilisateur"));
        } catch (SQLException e) {
            // On ignore si jamais la colonne est manquante
        }
        
        return action;
    }
}
