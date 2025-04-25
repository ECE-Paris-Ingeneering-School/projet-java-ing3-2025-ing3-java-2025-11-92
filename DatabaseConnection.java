package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer la connexion à la base de données
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    /**
     * Retourne une instance de connexion à la base de données
     * @return Connection l'objet de connexion
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}