package documente.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv("DB_URL");
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Pentru Windows Auth, nu mai trimitem user și parola ca argumente
                connection = DriverManager.getConnection(URL);
                System.out.println("Conexiune reușită prin Windows Authentication!");
            } catch (SQLException e) {
                System.err.println("Eroare la conectare: " + e.getMessage());
                System.err.println("Asigură-te că fișierul mssql-jdbc_auth-13.4.0.x64.dll este configurat corect.");
            }
        }
        return connection;
    }
}