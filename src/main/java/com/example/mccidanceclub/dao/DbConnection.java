package com.example.mccidanceclub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String URL = "jdbc:postgresql://postgresql-dimitrimeyepa.alwaysdata.net:5432/dimitrimeyepa_mcdd";
    private static final String USER = "dimitrimeyepa";
    private static final String PASSWORD = "Dimitri2005@";


    private static Connection connection = null;

    // 🔒 Constructeur privé pour empêcher l’instanciation
    private DbConnection() {}

    /**
     * Récupère une instance unique de la connexion à la base de données.
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion réussie à la base AlwaysData !");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ Pilote PostgreSQL introuvable !");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion à la base de données !");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Ferme proprement la connexion si elle est ouverte.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("🔒 Connexion fermée avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur lors de la fermeture de la connexion !");
            e.printStackTrace();
        }
    }
}
