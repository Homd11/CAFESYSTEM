package Services;

import Core.Order;
import Core.Student;
import Core.MenuItem;
import DB.OrderDAO;
import DB.MenuDAO;
import Interfaces.IOrderRepository;
import Interfaces.IMenuProvide;
import Interfaces.ILoyaltyService;
import Values.Selection;
import Enums.OrderStatus;

import java.util.List;

public class OrderProcessor {
    private final IOrderRepository orders;
    private final IMenuProvide menu;
    private final ILoyaltyService loyalty;

    public OrderProcessor() {
        this.orders = new OrderDAO();
        this.menu = new MenuDAO();
        this.loyalty = new LoyaltyProgram();
    }

    public OrderProcessor(IOrderRepository orders, IMenuProvide menu, ILoyaltyService loyalty) {
        this.orders = orders;
        this.menu = menu;
        this.loyalty = loyalty;
    }

    public Order placeOrder(Student student, List<Selection> selections) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (selections == null || selections.isEmpty()) {
            throw new IllegalArgumentException("Selections cannot be null or empty");
        }

        Order order = new Order(student.getId());

        // Add items to order based on selections
        for (Selection selection : selections) {
            MenuItem menuItem = findMenuItemById(selection.getItemId());
            if (menuItem == null) {
                throw new IllegalArgumentException("Menu item not found: " + selection.getItemId());
            }
            order.addItem(menuItem, selection.getQty());
        }

        // Save the order
        orders.save(order);

        // Award loyalty points
        if (order.total() != null) {
            loyalty.awardPoints(student, order.total());
        }

        return order;
    }

    public void advanceStatus(int orderId, OrderStatus newStatus) {
        Order order = orders.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        order.setStatus(newStatus);

        switch (newStatus) {
            case PREPARING:
                order.markPreparing();
                break;
            case READY:
                order.markReady();
                break;
        }
    }

    private MenuItem findMenuItemById(int itemId) {
        List<MenuItem> items = menu.listItems();
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }
}
