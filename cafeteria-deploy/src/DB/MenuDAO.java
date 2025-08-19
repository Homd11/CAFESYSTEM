package DB;

import Core.MenuItem;
import Enums.Category;
import Enums.Currency;
import Interfaces.IMenuProvide;
import Values.Money;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuDAO implements IMenuProvide {
    private static final Logger logger = Logger.getLogger(MenuDAO.class.getName());
    private final Connection conn;

    public MenuDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    // Constructor for dependency injection (better design)
    public MenuDAO(Connection connection) {
        this.conn = connection;
    }

    @Override
    public List<MenuItem> listItems() {
        String sql = "SELECT id, name, description, price_amount, price_currency, category FROM menu_items";
        List<MenuItem> items = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));

                // Handle the price with currency
                double priceAmount = rs.getDouble("price_amount");
                String priceCurrency = rs.getString("price_currency");
                Money price = new Money(priceAmount, Currency.valueOf(priceCurrency));
                item.setPrice(price);

                item.setCategory(Category.valueOf(rs.getString("category")));
                items.add(item);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving menu items", e);
        }

        return items;
    }

    @Override
    public void add(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, description, price_amount, price_currency, category) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice().getAmount().doubleValue());
            ps.setString(4, item.getPrice().getCurrency().name());
            ps.setString(5, item.getCategory().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding menu item: " + item.getName(), e);
        }
    }

    @Override
    public void update(MenuItem item) {
        String sql = "UPDATE menu_items SET name=?, description=?, price_amount=?, price_currency=?, category=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getPrice().getAmount().doubleValue());
            ps.setString(4, item.getPrice().getCurrency().name());
            ps.setString(5, item.getCategory().name());
            ps.setInt(6, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating menu item with id: " + item.getId(), e);
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error removing menu item with id: " + id, e);
        }
    }
}
