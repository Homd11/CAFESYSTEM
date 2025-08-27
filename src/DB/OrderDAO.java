package DB;

import Core.Order;
import Core.Order.OrderItem;
import Core.MenuItem;
import Core.Student;
import Enums.Category;
import Enums.Currency;
import Enums.OrderStatus;
import Interfaces.IOrderRepository;
import Values.Money;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class OrderDAO implements IOrderRepository {
    private static final Logger logger = Logger.getLogger(OrderDAO.class.getName());
    private final Connection conn;
    private final StudentDAO studentDAO;
    private final MenuDAO menuDAO;

    public OrderDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
        this.studentDAO = new StudentDAO(this.conn);
        this.menuDAO = new MenuDAO(this.conn);
    }

    public OrderDAO(Connection connection) {
        this.conn = connection;
        this.studentDAO = new StudentDAO(connection);
        this.menuDAO = new MenuDAO(connection);
    }

    @Override
    public void save(Order order) {
        String orderSql = "INSERT INTO orders (studentId, status, createdAt) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getStudentId());
                ps.setString(2, order.getStatus().name());
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        order.setId(orderId);

                        // Save order items
                        saveOrderItems(orderId, order.getItems());
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
            logger.log(Level.SEVERE, "Error saving order", e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error resetting auto-commit", e);
            }
        }
    }

    private void saveOrderItems(int orderId, List<OrderItem> items) throws SQLException {
        String itemSql = "INSERT INTO order_items (orderId, menuItemId, nameSnapshot, unitPrice_amount, unitPrice_currency, qty) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
            for (OrderItem item : items) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getMenuItem().getId());
                ps.setString(3, item.getMenuItem().getName());
                ps.setDouble(4, item.getUnitPrice().getAmount().doubleValue());
                ps.setString(5, item.getUnitPrice().getCurrency().name());
                ps.setInt(6, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT id, studentId, status, createdAt FROM orders WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order(rs.getInt("studentId"));
                    order.setId(rs.getInt("id"));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                    order.setOrderDate(rs.getTimestamp("createdAt").toLocalDateTime());

                    // Load order items
                    loadOrderItems(order);

                    return order;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding order with id: " + id, e);
        }

        return null;
    }

    private void loadOrderItems(Order order) throws SQLException {
        String sql = "SELECT menuItemId, nameSnapshot, unitPrice_amount, unitPrice_currency, qty FROM order_items WHERE orderId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Create a simplified MenuItem for the order item
                    MenuItem menuItem = new MenuItem();
                    menuItem.setId(rs.getInt("menuItemId"));
                    menuItem.setName(rs.getString("nameSnapshot"));

                    Money unitPrice = new Money(
                        rs.getDouble("unitPrice_amount"),
                        Currency.valueOf(rs.getString("unitPrice_currency"))
                    );

                    // Set the price on the MenuItem to avoid null pointer exception
                    menuItem.setPrice(unitPrice.getAmount().doubleValue());

                    int quantity = rs.getInt("qty");

                    order.addItem(menuItem, quantity);
                }
            }
        }
    }



    @Override
    public List<Order> findAll() {
        String sql = "SELECT id, studentId, status, createdAt FROM orders ORDER BY createdAt DESC";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(rs.getInt("studentId"));
                order.setId(rs.getInt("id"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setOrderDate(rs.getTimestamp("createdAt").toLocalDateTime());

                loadOrderItems(order);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all orders", e);
        }

        return orders;
    }

    @Override
    public List<Order> findPendingOrders() {
        String sql = "SELECT id, studentId, status, createdAt FROM orders WHERE status IN ('NEW', 'PREPARING') ORDER BY createdAt ASC";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(rs.getInt("studentId"));
                order.setId(rs.getInt("id"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setOrderDate(rs.getTimestamp("createdAt").toLocalDateTime());

                loadOrderItems(order);
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving pending orders", e);
        }

        return orders;
    }

    public List<Order> findOrdersByStudent(int studentId) {
        String sql = "SELECT id, studentId, status, createdAt FROM orders WHERE studentId = ? ORDER BY createdAt DESC";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(rs.getInt("studentId"));
                    order.setId(rs.getInt("id"));
                    order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                    order.setOrderDate(rs.getTimestamp("createdAt").toLocalDateTime());

                    loadOrderItems(order);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving orders for student: " + studentId, e);
        }

        return orders;
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getStatus().name());
            ps.setInt(2, order.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                logger.log(Level.WARNING, "No order found with id: " + order.getId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating order status for id: " + order.getId(), e);
            throw new RuntimeException("Failed to update order status", e);
        }
    }
}
