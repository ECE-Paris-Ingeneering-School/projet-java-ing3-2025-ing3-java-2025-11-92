package test;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM articles");

            while (rs.next()) {
                System.out.println(rs.getString("nom") + " - " + rs.getString("marque"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
