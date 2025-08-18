package DB;

import Core.LoyaltyAccount;
import Interfaces.ILoyaltyAccount;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoyaltyAccountDAO implements ILoyaltyAccount {
    private static final Logger logger = Logger.getLogger(LoyaltyAccountDAO.class.getName());
    private final Connection conn;

    public LoyaltyAccountDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    // Constructor for dependency injection
    public LoyaltyAccountDAO(Connection connection) {
        this.conn = connection;
    }

    public int save(LoyaltyAccount account) {
        String sql = "INSERT INTO loyalty_accounts(points) VALUES(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, account.getPoints());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    account.setId(id); // set id in the object
                    return id;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving loyalty account", e);
        }
        return 0;
    }

    @Override
    public void update(LoyaltyAccount account) {
        String sql = "UPDATE loyalty_accounts SET points=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getPoints());
            ps.setInt(2, account.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating loyalty account with id: " + account.getId(), e);
        }
    }

    @Override
    public LoyaltyAccount findById(int id) {
        String sql = "SELECT id, points FROM loyalty_accounts WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoyaltyAccount acc = new LoyaltyAccount();
                    acc.setId(rs.getInt("id"));
                    acc.setPoints(rs.getInt("points"));
                    return acc;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding loyalty account with id: " + id, e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM loyalty_accounts WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting loyalty account with id: " + id, e);
        }
    }
}
