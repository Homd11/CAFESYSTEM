package Services;

import Core.Order;
import Core.Student;
import Core.MenuItem;
import Core.Payment;
import DB.OrderDAO;
import DB.MenuDAO;
import DB.PaymentDAO;
import Interfaces.IOrderRepository;
import Interfaces.IMenuProvide;
import Interfaces.ILoyaltyService;
import Values.Selection;
import Enums.OrderStatus;
import Enums.PaymentMethod;
import Values.Money;
import Enums.Currency;

import java.util.ArrayList;
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

    /**
     * Place order with payment processing
     * @param student The student placing the order
     * @param selections List of menu item selections
     * @param scanner Scanner for payment input
     * @return Order object if successful, null if payment failed
     */
    public Order placeOrderWithPayment(Student student, List<Selection> selections, Scanner scanner) {
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

        // Process payment before saving the order
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean paymentSuccess = paymentProcessor.processInteractivePayment(scanner, order.total());

        if (!paymentSuccess) {
            System.out.println("❌ Order cancelled due to payment failure.");
            return null;
        }

        // Save the order only after successful payment
        orders.save(order);

        // Save payment record
        try {
            PaymentDAO paymentDAO = new PaymentDAO();

            // Create payment record
            Payment payment = new Payment(order.getId(), getPaymentMethodFromProcessor(paymentProcessor), order.total());
            payment.setSuccessful(true);

            // Set transaction details if available
            if (paymentProcessor.getLastUsedPaymentMethod() != null) {
                String details = paymentProcessor.getPaymentConfirmation();
                if (details.contains("Transaction ID:")) {
                    String transactionId = details.substring(details.indexOf("Transaction ID:") + 15).trim();
                    payment.setTransactionId(transactionId);
                } else if (details.contains("Auth Code:")) {
                    String authCode = details.substring(details.indexOf("Auth Code:") + 10).trim();
                    payment.setAuthorizationCode(authCode);
                }
            }

            paymentDAO.save(payment);

        } catch (Exception e) {
            System.out.println("⚠️ Warning: Payment processed but failed to save payment record: " + e.getMessage());
        }

        // Award loyalty points
        if (order.total() != null) {
            loyalty.awardPoints(student, order.total());
        }

        System.out.println("✅ Order placed successfully!");
        System.out.println("📧 Order ID: " + order.getId());
        System.out.println("🎁 Loyalty points awarded!");

        return order;
    }

    /**
     * GUI-compatible method for placing orders with payment
     * Does not require Scanner input - suitable for GUI applications
     */
    public boolean placeOrderWithPayment(Student student, List<Selection> selections, PaymentMethod paymentMethod) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (selections == null || selections.isEmpty()) {
            throw new IllegalArgumentException("Selections cannot be null or empty");
        }

        try {
            Order order = new Order(student.getId());

            // Add items to order based on selections
            for (Selection selection : selections) {
                // Selection class only has itemId, so always find by ID
                MenuItem menuItem = findMenuItemById(selection.getItemId());

                if (menuItem == null) {
                    throw new IllegalArgumentException("Menu item not found: " + selection.getItemId());
                }
                order.addItem(menuItem, selection.getQty());
            }

            // Process payment automatically (GUI doesn't need interactive payment)
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            boolean paymentSuccess = paymentProcessor.processPayment(order.total(), paymentMethod);

            if (!paymentSuccess) {
                return false;
            }

            // Save the order only after successful payment
            orders.save(order);

            // Save payment record
            try {
                PaymentDAO paymentDAO = new PaymentDAO();
                Payment payment = new Payment(order.getId(), paymentMethod, order.total());
                payment.setSuccessful(true);
                payment.setTransactionId("GUI-" + System.currentTimeMillis()); // Simple transaction ID for GUI
                paymentDAO.save(payment);
            } catch (Exception e) {
                // Payment processed but failed to save record - log but don't fail the order
                System.err.println("Warning: Payment processed but failed to save payment record: " + e.getMessage());
            }

            // Award loyalty points
            if (order.total() != null) {
                loyalty.awardPoints(student, order.total());
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    /**
     * GUI-compatible method for placing orders with payment and discount
     * Discount is an absolute EGP value to deduct from the order total.
     */
    public boolean placeOrderWithPayment(Student student, List<Selection> selections, PaymentMethod paymentMethod, double discountAmount) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (selections == null || selections.isEmpty()) {
            throw new IllegalArgumentException("Selections cannot be null or empty");
        }
        if (discountAmount < 0) {
            discountAmount = 0.0; // clamp
        }

        try {
            Order order = new Order(student.getId());

            // Add items to order based on selections
            for (Selection selection : selections) {
                MenuItem menuItem = findMenuItemById(selection.getItemId());
                if (menuItem == null) {
                    throw new IllegalArgumentException("Menu item not found: " + selection.getItemId());
                }
                order.addItem(menuItem, selection.getQty());
            }

            if (order.total() == null) {
                return false;
            }

            // Calculate payable amount after discount
            double gross = order.total().getAmount().doubleValue();
            double payable = Math.max(0.0, gross - discountAmount);
            Money amountToPay = new Money(payable, order.total().getCurrency());

            // Process payment
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            boolean paymentSuccess = paymentProcessor.processPayment(amountToPay, paymentMethod);
            if (!paymentSuccess) {
                return false;
            }

            // Save the order
            orders.save(order);

            // Save payment record with discounted amount
            try {
                PaymentDAO paymentDAO = new PaymentDAO();
                Payment payment = new Payment(order.getId(), paymentMethod, amountToPay);
                payment.setSuccessful(true);
                payment.setTransactionId("GUI-DC-" + System.currentTimeMillis());
                paymentDAO.save(payment);
            } catch (Exception e) {
                System.err.println("Warning: Payment processed but failed to save payment record: " + e.getMessage());
            }

            // Award loyalty points based on amount paid
            if (amountToPay != null) {
                loyalty.awardPoints(student, amountToPay);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error placing order with discount: " + e.getMessage());
            return false;
        }
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

    /**
     * Get order history for a specific student
     * @param studentId The ID of the student
     * @return List of orders for the student
     */
    public List<Order> getOrderHistory(int studentId) {
        try {
            return orders.findOrdersByStudent(studentId);
        } catch (Exception e) {
            System.err.println("Error getting order history for student " + studentId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get all orders for admin dashboard
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        try {
            return orders.findAll();
        } catch (Exception e) {
            System.err.println("Error loading all orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get orders filtered by status
     * @param status Order status to filter by
     * @return List of orders with specified status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        try {
            // Use findAll() and filter in Java since findByStatus() doesn't exist in interface
            List<Order> allOrders = orders.findAll();
            return allOrders.stream()
                .filter(order -> order.getStatus() == status)
                .toList();
        } catch (Exception e) {
            System.err.println("Error loading orders by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Update order status (for admin)
     * @param orderId Order ID to update
     * @param newStatus New status to set
     * @return true if successful
     */
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        try {
            Order order = orders.findById(orderId);
            if (order != null) {
                order.setStatus(newStatus);
                orders.update(order); // Use update() instead of save() for existing orders
                System.out.println("✅ Order #" + orderId + " status updated to " + newStatus);
                return true;
            }
            System.err.println("❌ Order not found: " + orderId);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error updating order status: " + e.getMessage());
            return false;
        }
    }

    private MenuItem findMenuItemById(int itemId) {
        List<MenuItem> items = menu.listItems();
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method to extract payment method from payment processor
     */
    private PaymentMethod getPaymentMethodFromProcessor(PaymentProcessor processor) {
        if (processor.getLastUsedPaymentMethod() == null) {
            return PaymentMethod.CASH; // Default fallback
        }

        String methodName = processor.getLastUsedPaymentMethod().getPaymentMethodName();
        if (methodName.contains("Cash")) {
            return PaymentMethod.CASH;
        } else if (methodName.contains("Visa")) {
            return PaymentMethod.VISA;
        } else if (methodName.contains("MasterCard")) {
            return PaymentMethod.MASTERCARD;
        }

        return PaymentMethod.CASH; // Default fallback
    }
}
