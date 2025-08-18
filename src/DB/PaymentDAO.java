package DB;

import Core.Payment;
import Enums.PaymentMethod;
import Enums.Currency;
import Values.Money;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Payment Data Access Object
 * Handles all database operations for payments
 */
public class PaymentDAO {
    private static final Logger logger = Logger.getLogger(PaymentDAO.class.getName());
    private final Connection conn;

    public PaymentDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    public PaymentDAO(Connection connection) {
        this.conn = connection;
    }

    /**
     * Save a payment record to the database
     */
    public void save(Payment payment) {
        String sql = "INSERT INTO payments (orderId, amount, currency, success, txId) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getOrderId());
            ps.setDouble(2, payment.getAmount().getAmount().doubleValue());
            ps.setString(3, payment.getAmount().getCurrency().name());
            ps.setBoolean(4, payment.isSuccessful());
            ps.setString(5, payment.getTransactionId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving payment", e);
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    /**
     * Find payment by ID
     */
    public Payment findById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding payment with id: " + id, e);
        }

        return null;
    }

    /**
     * Find payments by order ID
     */
    public List<Payment> findByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE orderId = ? ORDER BY id DESC";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding payments for order: " + orderId, e);
        }

        return payments;
    }

    /**
     * Find all successful payments
     */
    public List<Payment> findSuccessfulPayments() {
        String sql = "SELECT * FROM payments WHERE success = true ORDER BY id DESC";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving successful payments", e);
        }

        return payments;
    }

    /**
     * Map ResultSet to Payment object
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setOrderId(rs.getInt("orderId"));

        // Since paymentMethod is not stored in DB, we'll set a default
        payment.setPaymentMethod(PaymentMethod.CASH);

        Money amount = new Money(
            rs.getDouble("amount"),
            Currency.valueOf(rs.getString("currency"))
        );
        payment.setAmount(amount);

        payment.setTransactionId(rs.getString("txId"));
        // Since authorizationCode and paymentDate are not in DB, set defaults
        payment.setAuthorizationCode(null);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSuccessful(rs.getBoolean("success"));

        return payment;
    }

    /**
     * Create the payments table if it doesn't exist
     */
    public void createPaymentsTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS payments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                order_id INT NOT NULL,
                payment_method ENUM('CASH', 'VISA', 'MASTERCARD') NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                currency VARCHAR(10) DEFAULT 'EGP',
                transaction_id VARCHAR(255),
                authorization_code VARCHAR(255),
                payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                successful BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
            )
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
            logger.info("Payments table created or already exists");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating payments table", e);
        }
    }
}
