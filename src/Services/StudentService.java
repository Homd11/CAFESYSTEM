package Services;

import Core.Student;
import Core.MenuItem;
import Core.Order;
import DB.OrderDAO;
import Services.StudentManager;
import Services.LoyaltyProgram;
import Services.DiscountManager;
import Services.MenuManager;
import Services.OrderProcessor;
import Values.Selection;
import Values.Discount;
import Values.Money;
import Enums.Category;
import Enums.OrderStatus;
import Interfaces.IStudentService;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentService implements IStudentService {
    private StudentManager studentManager;
    private LoyaltyProgram loyaltyProgram;
    private MenuManager menuManager;
    private OrderProcessor orderProcessor;

    // Constructor with dependency injection (follows DIP)
    public StudentService(StudentManager studentManager, LoyaltyProgram loyaltyProgram,
                         MenuManager menuManager, OrderProcessor orderProcessor) {
        this.studentManager = studentManager;
        this.loyaltyProgram = loyaltyProgram;
        this.menuManager = menuManager;
        this.orderProcessor = orderProcessor;
    }

    // Default constructor for backwards compatibility
    public StudentService() {
        this.studentManager = new StudentManager();
        this.loyaltyProgram = new LoyaltyProgram();
        this.menuManager = new MenuManager();
        this.orderProcessor = new OrderProcessor();
    }

    /**
     * Register new student with interactive UI
     */
    public Student registerStudent(Scanner scanner) {
        System.out.println("\n📝 STUDENT REGISTRATION");
        System.out.println("-".repeat(30));
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty!");
            return null;
        }

        try {
            Student student = studentManager.register(name);
            System.out.println("\n✅ Registration successful!");
            System.out.println("🎓 Student Name: " + student.getName());
            System.out.println("🆔 Student Code: " + student.getStudentCode());
            System.out.println("💳 Starting Loyalty Points: " + student.getAccount().balance());
            System.out.println("\n💡 Please remember your student code for future logins!");

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return student;
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Login student with interactive UI
     */
    public Student loginStudent(Scanner scanner) {
        System.out.println("\n🔑 STUDENT LOGIN");
        System.out.println("-".repeat(20));
        System.out.print("Enter your Student Code: ");
        String studentCode = scanner.nextLine().trim();

        if (studentCode.isEmpty()) {
            System.out.println("❌ Student code cannot be empty!");
            return null;
        }

        try {
            Student student = studentManager.login(studentCode);
            if (student != null) {
                System.out.println("\n✅ Login successful! Welcome back, " + student.getName() + "!");
                System.out.println("💳 Your current loyalty points: " + loyaltyProgram.getBalance(student));
                return student;
            } else {
                System.out.println("❌ Invalid student code! Please check and try again.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * View menu and place order with interactive UI
     */
    public void viewMenuAndOrder(Student student, Scanner scanner) {
        System.out.println("\n🍽️  CAFETERIA MENU");
        System.out.println("=".repeat(80));

        try {
            List<MenuItem> menuItems = menuManager.listItems();
            if (menuItems.isEmpty()) {
                System.out.println("📭 No menu items available at the moment.");
                return;
            }

            // Display menu by category
            displayMenuByCategory(menuItems, Category.MAIN_COURSE, "🍔 MAIN COURSES");
            displayMenuByCategory(menuItems, Category.DRINK, "🥤 DRINKS");
            displayMenuByCategory(menuItems, Category.SNACK, "🍿 SNACKS");

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Would you like to place an order? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y") || response.equals("yes")) {
                placeOrder(student, menuItems, scanner);
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading menu: " + e.getMessage());
        }
    }

    /**
     * View order history for student
     */
    public void viewOrderHistory(Student student, Scanner scanner) {
        System.out.println("\n📋 YOUR ORDER HISTORY");
        System.out.println("=".repeat(60));

        try {
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findOrdersByStudent(student.getId());

            if (orders.isEmpty()) {
                System.out.println("📭 You haven't placed any orders yet.");
                System.out.println("💡 Start ordering to build your history!");
            } else {
                System.out.printf("%-8s | %-12s | %-15s | %-20s | %s%n",
                    "Order ID", "Total", "Status", "Date", "Items");
                System.out.println("-".repeat(60));

                for (Order order : orders) {
                    String itemsText = order.getItems().size() + " item(s)";
                    String dateText = order.getOrderDate().toString().substring(0, 16);

                    System.out.printf("%-8d | %-12s | %-15s | %-20s | %s%n",
                        order.getId(),
                        order.total() != null ? order.total().toString() : "N/A",
                        order.getStatus(),
                        dateText,
                        itemsText);
                }

                System.out.println("\n💡 Enter an Order ID to see details, or 0 to go back:");
                int orderId = getIntInput(scanner);

                if (orderId > 0) {
                    showOrderDetails(student, orderId, scanner);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading order history: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Redeem loyalty points for student
     */
    public void redeemLoyaltyPoints(Student student, Scanner scanner) {
        System.out.println("\n🎁 REDEEM LOYALTY POINTS");
        System.out.println("-".repeat(30));

        int currentPoints = loyaltyProgram.getBalance(student);
        System.out.println("💳 Your current points: " + currentPoints);

        // Show existing available discounts
        if (DiscountManager.hasAvailableDiscounts(student)) {
            Money totalDiscount = DiscountManager.getTotalDiscountValue(student);
            System.out.println("🎫 You have available discounts worth: " + totalDiscount.getAmount() + " EGP");
        }

        if (currentPoints == 0) {
            System.out.println("😔 You don't have any points to redeem yet.");
            System.out.println("💡 Earn points by placing orders! (1 point per EGP spent)");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.println("💡 Exchange rate: 10 points = 1 EGP discount");
        System.out.println("💰 You can get up to " + (currentPoints * 0.1) + " EGP discount");

        System.out.print("Enter points to redeem (or 0 to cancel): ");
        int pointsToRedeem = getIntInput(scanner);

        if (pointsToRedeem == 0) {
            System.out.println("Redemption cancelled.");
            return;
        }

        if (pointsToRedeem > currentPoints) {
            System.out.println("❌ You don't have enough points!");
            return;
        }

        try {
            Discount discount = loyaltyProgram.redeem(student, pointsToRedeem);
            DiscountManager.addDiscount(student, discount);

            System.out.println("\n✅ REDEEMPTION SUCCESSFUL!");
            System.out.println("🎫 Discount Amount: " + discount.getAmount() + " EGP");
            System.out.println("📝 Description: " + discount.getDescription());
            System.out.println("💳 Remaining Points: " + loyaltyProgram.getBalance(student));
            System.out.println("💡 This discount will be automatically applied to your next order!");

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Redemption failed: " + e.getMessage());
        }
    }

    /**
     * View student profile
     */
    public void viewProfile(Student student, Scanner scanner) {
        System.out.println("\n👤 YOUR PROFILE");
        System.out.println("=".repeat(40));
        System.out.println("🎓 Name: " + student.getName());
        System.out.println("🆔 Student Code: " + student.getStudentCode());
        System.out.println("💳 Loyalty Points: " + loyaltyProgram.getBalance(student));
        System.out.println("💰 Discount Value: " + (loyaltyProgram.getBalance(student) * 0.1) + " EGP");

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Check order status for student
     */
    public void checkOrderStatus(Student student, Scanner scanner) {
        System.out.println("\n🔄 CHECK ORDER STATUS");
        System.out.println("-".repeat(30));
        System.out.print("Enter Order ID: ");
        int orderId = getIntInput(scanner);

        try {
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                System.out.println("❌ Order not found! Please check the Order ID.");
                return;
            }

            if (order.getStudentId() != student.getId()) {
                System.out.println("❌ This order doesn't belong to you!");
                return;
            }

            System.out.println("\n📊 ORDER STATUS DETAILS");
            System.out.println("=".repeat(40));
            System.out.println("🆔 Order ID: " + order.getId());
            System.out.println("📅 Order Date: " + order.getOrderDate());
            System.out.println("📊 Current Status: " + order.getStatus());
            System.out.println("💰 Total Amount: " + (order.total() != null ? order.total() : "N/A"));

            // Show status progression
            System.out.println("\n📈 Status Progression:");
            System.out.println("✅ NEW - Order received");
            if (order.getStatus().ordinal() >= OrderStatus.PREPARING.ordinal()) {
                System.out.println("✅ PREPARING - Kitchen is preparing your order");
            } else {
                System.out.println("⏳ PREPARING - Waiting...");
            }
            if (order.getStatus().ordinal() >= OrderStatus.READY.ordinal()) {
                System.out.println("✅ READY - Order is ready for pickup!");
            } else {
                System.out.println("⏳ READY - Not ready yet...");
            }

            // Show estimated time or pickup instructions
            switch (order.getStatus()) {
                case NEW:
                    System.out.println("\n⏰ Estimated time: 10-15 minutes");
                    break;
                case PREPARING:
                    System.out.println("\n👨‍🍳 Your order is being prepared! Please wait.");
                    break;
                case READY:
                    System.out.println("\n🔔 Your order is ready! Please come to the counter for pickup.");
                    break;
            }

        } catch (Exception e) {
            System.out.println("❌ Error checking order status: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Helper methods
    private void displayMenuByCategory(List<MenuItem> items, Category category, String title) {
        System.out.println("\n" + title);
        System.out.println("-".repeat(50));
        boolean hasItems = false;

        for (MenuItem item : items) {
            if (item.getCategory() == category) {
                System.out.printf("ID: %-3d | %-25s | %s | %s%n",
                    item.getId(),
                    item.getName(),
                    item.getPrice(),
                    item.getDescription());
                hasItems = true;
            }
        }

        if (!hasItems) {
            System.out.println("   No items available in this category");
        }
    }

    private void placeOrder(Student student, List<MenuItem> menuItems, Scanner scanner) {
        System.out.println("\n🛒 PLACE YOUR ORDER");
        System.out.println("-".repeat(30));

        if (DiscountManager.hasAvailableDiscounts(student)) {
            Money totalDiscount = DiscountManager.getTotalDiscountValue(student);
            System.out.println("🎫 You have available discounts worth: " + totalDiscount.getAmount() + " EGP");
            System.out.println("💡 These will be automatically applied to your order!");
            System.out.println();
        }

        List<Selection> selections = new ArrayList<>();

        while (true) {
            System.out.print("Enter Menu Item ID (or 0 to finish): ");
            int itemId = getIntInput(scanner);

            if (itemId == 0) break;

            MenuItem selectedItem = null;
            for (MenuItem item : menuItems) {
                if (item.getId() == itemId) {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem == null) {
                System.out.println("❌ Invalid item ID! Please try again.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = getIntInput(scanner);

            if (quantity <= 0) {
                System.out.println("❌ Quantity must be positive!");
                continue;
            }

            selections.add(new Selection(itemId, quantity));
            System.out.println("✅ Added: " + quantity + "x " + selectedItem.getName() +
                             " (" + selectedItem.getPrice() + " each)");
        }

        if (selections.isEmpty()) {
            System.out.println("🛒 No items selected. Order cancelled.");
            return;
        }

        try {
            // Calculate order total before payment
            Order tempOrder = new Order(student.getId());
            for (Selection selection : selections) {
                MenuItem menuItem = findMenuItemById(menuItems, selection.getItemId());
                if (menuItem != null) {
                    tempOrder.addItem(menuItem, selection.getQty());
                }
            }

            Money originalTotal = tempOrder.total();
            Money finalTotal = originalTotal;
            boolean discountApplied = false;

            // Apply discounts if available
            if (DiscountManager.hasAvailableDiscounts(student)) {
                finalTotal = DiscountManager.applyDiscounts(student, originalTotal);
                discountApplied = true;
            }

            // Show order summary before payment
            System.out.println("\n📋 ORDER SUMMARY");
            System.out.println("=".repeat(40));
            if (discountApplied) {
                System.out.println("💰 Original Total: " + originalTotal);
                Money discountAmount = new Money(originalTotal.getAmount().doubleValue() - finalTotal.getAmount().doubleValue(), originalTotal.getCurrency());
                System.out.println("🎫 Discount Applied: -" + discountAmount.getAmount() + " EGP");
                System.out.println("💰 Final Total: " + finalTotal);
            } else {
                System.out.println("💰 Total Amount: " + finalTotal);
            }
            System.out.println("=".repeat(40));

            // Process payment and place order
            Order order = orderProcessor.placeOrderWithPayment(student, selections, scanner);

            if (order != null) {
                System.out.println("\n🎉 ORDER CONFIRMED!");
                System.out.println("=".repeat(40));
                System.out.println("🆔 Order ID: " + order.getId());
                System.out.println("📅 Order Date: " + order.getOrderDate());
                System.out.println("📊 Status: " + order.getStatus());
                System.out.println("⏰ Estimated Time: 10-15 minutes");
                System.out.println("=".repeat(40));
                System.out.println("💡 You can check your order status from the main menu!");
            } else {
                System.out.println("❌ Order was not placed due to payment issues.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error placing order: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Helper method to find menu item by ID
    private MenuItem findMenuItemById(List<MenuItem> menuItems, int itemId) {
        return menuItems.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    private void showOrderDetails(Student student, int orderId, Scanner scanner) {
        try {
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.findById(orderId);

            if (order == null || order.getStudentId() != student.getId()) {
                System.out.println("❌ Order not found or doesn't belong to you!");
                return;
            }

            System.out.println("\n📋 ORDER DETAILS");
            System.out.println("=".repeat(50));
            System.out.println("🆔 Order ID: " + order.getId());
            System.out.println("📅 Date: " + order.getOrderDate());
            System.out.println("📊 Status: " + order.getStatus());
            System.out.println("💰 Total: " + (order.total() != null ? order.total() : "N/A"));

            System.out.println("\n🛒 Items Ordered:");
            System.out.println("-".repeat(50));

            for (Order.OrderItem item : order.getItems()) {
                System.out.printf("• %dx %s - %s each%n",
                    item.getQuantity(),
                    item.getMenuItem().getName(),
                    item.getUnitPrice());
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading order details: " + e.getMessage());
        }
    }

    private int getIntInput(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number! Please enter a valid integer: ");
            }
        }
    }

    // GUI-compatible methods implementation
    public Student registerStudent(String name, String studentCode) {
        try {
            // StudentManager.register() only takes name, it generates the student code automatically
            return studentManager.register(name);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    public Student loginStudent(String studentCode) {
        try {
            // Use the correct method name from StudentManager
            return studentManager.login(studentCode);
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}
