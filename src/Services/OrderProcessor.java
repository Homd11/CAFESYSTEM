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

        // Save the status change to database
        orders.update(order);
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

    /**
     * Manage order statuses - allows admin to change order status
     */
    public void manageOrderStatuses(Scanner scanner) {
        while (true) {
            System.out.println("\n🔄 MANAGE ORDER STATUSES");
            System.out.println("=".repeat(50));

            try {
                List<Order> pendingOrders = orders.findPendingOrders();

                if (pendingOrders.isEmpty()) {
                    System.out.println("📭 No pending orders found.");
                    System.out.print("\nPress Enter to return to main menu...");
                    scanner.nextLine();
                    return;
                }

                System.out.printf("%-8s | %-12s | %-15s | %-20s%n",
                    "Order ID", "Student ID", "Status", "Date");
                System.out.println("-".repeat(60));

                for (Order order : pendingOrders) {
                    String dateText = order.getOrderDate().toString().substring(0, 16);
                    System.out.printf("%-8d | %-12d | %-15s | %-20s%n",
                        order.getId(),
                        order.getStudentId(),
                        order.getStatus(),
                        dateText);
                }

                System.out.print("\nEnter Order ID to update status (or 0 to go back): ");
                String input = scanner.nextLine().trim();

                if (input.equals("0")) {
                    return;
                }

                try {
                    int orderId = Integer.parseInt(input);
                    Order selectedOrder = pendingOrders.stream()
                        .filter(order -> order.getId() == orderId)
                        .findFirst()
                        .orElse(null);

                    if (selectedOrder == null) {
                        System.out.println("❌ Invalid order ID or order is not pending.");
                        continue;
                    }

                    // Show status change options
                    System.out.println("\nCurrent status: " + selectedOrder.getStatus());
                    System.out.println("Available status changes:");

                    if (selectedOrder.getStatus() == OrderStatus.NEW) {
                        System.out.println("1. Mark as PREPARING");
                        System.out.println("2. Mark as READY");
                    } else if (selectedOrder.getStatus() == OrderStatus.PREPARING) {
                        System.out.println("1. Mark as READY");
                    }

                    System.out.print("Choose option: ");
                    String statusChoice = scanner.nextLine().trim();

                    OrderStatus newStatus = null;
                    if (selectedOrder.getStatus() == OrderStatus.NEW) {
                        if (statusChoice.equals("1")) {
                            newStatus = OrderStatus.PREPARING;
                        } else if (statusChoice.equals("2")) {
                            newStatus = OrderStatus.READY;
                        }
                    } else if (selectedOrder.getStatus() == OrderStatus.PREPARING) {
                        if (statusChoice.equals("1")) {
                            newStatus = OrderStatus.READY;
                        }
                    }

                    if (newStatus != null) {
                        advanceStatus(orderId, newStatus);
                        System.out.println("✅ Order #" + orderId + " status updated to " + newStatus);
                    } else {
                        System.out.println("❌ Invalid choice.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("❌ Please enter a valid order ID.");
                } catch (Exception e) {
                    System.out.println("❌ Error updating order status: " + e.getMessage());
                }

            } catch (Exception e) {
                System.out.println("❌ Error loading orders: " + e.getMessage());
                break;
            }
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
