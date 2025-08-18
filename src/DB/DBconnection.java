package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DBconnection implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(DBconnection.class.getName());
    private static final String URL = "jdbc:mysql://localhost:3306/cafeteriadb";
    private static final String USER = "root";
    private static final String PASSWORD = "ab1ab2ab";

    private Connection con;

    public DBconnection() {
        connect();
    }

    private void connect() {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. Please add mysql-connector-java to classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database. Please check if MySQL server is running and database 'cafeteriadb' exists.", e);
        }
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check connection status", e);
        }
        return con;
    }

    @Override
    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing database connection", e);
        }
    }
}
