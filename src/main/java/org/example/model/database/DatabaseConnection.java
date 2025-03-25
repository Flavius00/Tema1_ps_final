package org.example.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tema1_ps";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    private DatabaseConnection() {
        // Constructorul privat pentru Singleton
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexiune reușită la baza de date!");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL neîncărcat: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Eroare la conectarea la baza de date: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexiune închisă cu succes!");
            } catch (SQLException e) {
                System.err.println("Eroare la închiderea conexiunii: " + e.getMessage());
            }
        }
    }
}