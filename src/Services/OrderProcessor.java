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
import java.util.Scanner;

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

    /**
     * View all orders with formatted output for admin
     */
    public void viewAllOrders(Scanner scanner) {
        System.out.println("\n📋 ALL ORDERS");
        System.out.println("=".repeat(80));

        try {
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();

            if (orders.isEmpty()) {
                System.out.println("📭 No orders found.");
            } else {
                System.out.printf("%-8s | %-12s | %-12s | %-15s | %-20s | %s%n",
                    "Order ID", "Student ID", "Total", "Status", "Date", "Items");
                System.out.println("-".repeat(80));

                for (Order order : orders) {
                    String itemsText = order.getItems().size() + " item(s)";
                    String dateText = order.getOrderDate().toString().substring(0, 16);

                    System.out.printf("%-8d | %-12d | %-12s | %-15s | %-20s | %s%n",
                        order.getId(),
                        order.getStudentId(),
                        order.total() != null ? order.total().toString() : "N/A",
                        order.getStatus(),
                        dateText,
                        itemsText);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading orders: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private MenuItem findMenuItemById(int itemId) {
        List<MenuItem> items = menu.listItems();
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }
}
