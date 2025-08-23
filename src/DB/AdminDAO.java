package DB;

import Core.Admin;
import Services.PasswordHasher;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AdminDAO {
    private static final Logger logger = Logger.getLogger(AdminDAO.class.getName());
    private final Connection conn;

    public AdminDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    public AdminDAO(Connection connection) {
        this.conn = connection;
    }

    public void save(Admin admin) {
        String sql = "INSERT INTO admins (username, password_hash, name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPasswordHash());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getRole());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    admin.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving admin: " + admin.getUsername(), e);
        }
    }

    public Admin findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, name, role FROM admins WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPasswordHash(rs.getString("password_hash"));
                    admin.setName(rs.getString("name"));
                    admin.setRole(rs.getString("role"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding admin: " + username, e);
        }
        return null;
    }

    public boolean adminExists() {
        String sql = "SELECT COUNT(*) FROM admins";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if admin exists", e);
        }
        return false;
    }

    public void createDefaultAdmin() {
        // Fix: Store plain text password instead of hashing
        Admin defaultAdmin = new Admin("admin", "admin123", "System Administrator", "ADMIN");
        save(defaultAdmin);
    }
}
