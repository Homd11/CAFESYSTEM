import Core.*;
import Enums.Currency;
import Services.*;
import Values.*;
import Enums.*;
import DB.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager studentManager = new StudentManager();
    private static final MenuManager menuManager = new MenuManager();
    private static final OrderProcessor orderProcessor = new OrderProcessor();
    private static final LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
    private static final DiscountManager discountManager = new DiscountManager();
    private static final AdminManager adminManager = new AdminManager();
    private static Student currentStudent = null;
    private static Admin currentAdmin = null;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       🏫 UNIVERSITY CAFETERIA ORDER & LOYALTY SYSTEM       ║");
        System.out.println("║              Welcome to ITI Cafeteria System              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        // Test database connection first
        if (!testDatabaseConnection()) {
            System.out.println("\n❌ Database connection failed! Please check your MySQL server.");
            System.out.println("💡 Make sure MySQL is running and cafeteriadb database exists.");
            return;
        }

        System.out.println("✅ Database connection successful!");
        System.out.println("🗄️  System initialized and ready to serve!\n");

        // Initialize with some sample menu items if database is empty
        initializeSampleData();

        // Main application loop
        while (true) {
            try {
                if (currentStudent == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static boolean testDatabaseConnection() {
        try {
            // Try to get menu items to test database connection
            menuManager.listItems();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void initializeSampleData() {
        try {
            List<MenuItem> existingItems = menuManager.listItems();
            if (existingItems.isEmpty()) {
                System.out.println("📋 Initializing comprehensive sample data...");

                // Main Courses
                menuManager.addItem("Classic Beef Burger", "Juicy beef patty with lettuce, tomato, and special sauce",
                    new Money(25.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Grilled Chicken Sandwich", "Tender grilled chicken breast with vegetables",
                    new Money(22.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Margherita Pizza", "Fresh tomato sauce, mozzarella, and basil",
                    new Money(35.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Club Sandwich", "Triple-layer sandwich with chicken, bacon, and vegetables",
                    new Money(20.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Falafel Wrap", "Traditional Egyptian falafel with tahini sauce",
                    new Money(18.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Pasta Bolognese", "Italian pasta with rich meat sauce",
                    new Money(28.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Grilled Fish Fillet", "Fresh fish with lemon and herbs",
                    new Money(32.0, Currency.EGP), Category.MAIN_COURSE);
                menuManager.addItem("Chicken Caesar Salad", "Fresh lettuce with grilled chicken and caesar dressing",
                    new Money(24.0, Currency.EGP), Category.MAIN_COURSE);

                // Drinks
                menuManager.addItem("Arabic Coffee", "Traditional hot arabic coffee",
                    new Money(15.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Turkish Coffee", "Strong traditional turkish coffee",
                    new Money(12.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Fresh Orange Juice", "100% natural orange juice",
                    new Money(12.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Mango Juice", "Fresh mango juice",
                    new Money(14.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Coca Cola", "Classic Coca Cola",
                    new Money(8.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Pepsi", "Classic Pepsi Cola",
                    new Money(8.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Mineral Water", "Pure mineral water",
                    new Money(5.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Green Tea", "Healthy green tea",
                    new Money(10.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Black Tea", "Classic black tea",
                    new Money(8.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Cappuccino", "Italian cappuccino with steamed milk",
                    new Money(18.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Latte", "Smooth coffee with milk",
                    new Money(16.0, Currency.EGP), Category.DRINK);
                menuManager.addItem("Iced Coffee", "Refreshing iced coffee",
                    new Money(14.0, Currency.EGP), Category.DRINK);

                // Snacks
                menuManager.addItem("Crispy Chips", "Golden crispy potato chips",
                    new Money(8.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Chocolate Croissant", "Buttery croissant with chocolate filling",
                    new Money(12.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Cheese Cake", "Rich and creamy cheesecake slice",
                    new Money(15.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Apple Pie", "Traditional apple pie slice",
                    new Money(14.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Cookies (3 pieces)", "Chocolate chip cookies",
                    new Money(10.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Mixed Nuts", "Assorted roasted nuts",
                    new Money(16.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Fruit Salad", "Fresh seasonal fruit mix",
                    new Money(18.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Yogurt Cup", "Natural yogurt with honey",
                    new Money(9.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Granola Bar", "Healthy granola energy bar",
                    new Money(7.0, Currency.EGP), Category.SNACK);
                menuManager.addItem("Popcorn", "Buttered popcorn",
                    new Money(6.0, Currency.EGP), Category.SNACK);

                System.out.println("✅ Comprehensive menu with 30 items added successfully!");
                System.out.println("🍔 Main Courses: 8 items");
                System.out.println("🥤 Drinks: 12 items");
                System.out.println("🍿 Snacks: 10 items");

                // Initialize sample students with different loyalty points
                initializeSampleStudents();
            }
        } catch (Exception e) {
            System.out.println("⚠️  Could not initialize sample data: " + e.getMessage());
        }
    }

    private static void initializeSampleStudents() {
        try {
            System.out.println("👥 Creating sample students with loyalty points...");

            // Create students with different loyalty point balances
            Student student1 = studentManager.register("Ahmed Mohamed Ali");
            student1.getAccount().add(50);  // 50 points

            Student student2 = studentManager.register("Sara Hassan Ibrahim");
            student2.getAccount().add(150); // 150 points

            Student student3 = studentManager.register("Omar Khaled Mahmoud");
            student3.getAccount().add(25);  // 25 points

            Student student4 = studentManager.register("Fatma Youssef Ahmed");
            student4.getAccount().add(200); // 200 points

            Student student5 = studentManager.register("Mohamed Tarek Said");
            // student5 keeps 0 points (new student)

            System.out.println("✅ Sample students created:");
            System.out.println("   " + student1.getStudentCode() + " - " + student1.getName() + " (50 points)");
            System.out.println("   " + student2.getStudentCode() + " - " + student2.getName() + " (150 points)");
            System.out.println("   " + student3.getStudentCode() + " - " + student3.getName() + " (25 points)");
            System.out.println("   " + student4.getStudentCode() + " - " + student4.getName() + " (200 points)");
            System.out.println("   " + student5.getStudentCode() + " - " + student5.getName() + " (0 points)");

            System.out.println("\n💡 You can login with any of these student codes to test the system!");

        } catch (Exception e) {
            System.out.println("⚠️  Could not create sample students: " + e.getMessage());
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔐 STUDENT LOGIN & REGISTRATION");
        System.out.println("=".repeat(60));
        System.out.println("1. 📝 Register New Student");
        System.out.println("2. 🔑 Login with Student Code");
        System.out.println("3. 👨‍💼 Admin Menu (Menu Management)");
        System.out.println("4. 🚪 Exit System");
        System.out.println("=".repeat(60));
        System.out.print("👉 Choose option (1-4): ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                registerStudent();
                break;
            case 2:
                loginStudent();
                break;
            case 3:
                showAdminMenu();
                break;
            case 4:
                exitSystem();
                break;
            default:
                System.out.println("❌ Invalid option! Please choose 1-4.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🏠 MAIN MENU - Welcome " + currentStudent.getName() +
                          " (Code: " + currentStudent.getStudentCode() + ")");
        System.out.println("💳 Loyalty Points: " + loyaltyProgram.getBalance(currentStudent) + " points");
        System.out.println("=".repeat(70));
        System.out.println("1. 🍽️  View Menu & Place Order");
        System.out.println("2. 📋 View My Order History");
        System.out.println("3. 🎁 Redeem Loyalty Points");
        System.out.println("4. 👤 View My Profile");
        System.out.println("5. 🔄 Check Order Status");
        System.out.println("6. 🚪 Logout");
        System.out.println("=".repeat(70));
        System.out.print("👉 Choose option (1-6): ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewMenuAndOrder();
                break;
            case 2:
                viewOrderHistory();
                break;
            case 3:
                redeemLoyaltyPoints();
                break;
            case 4:
                viewProfile();
                break;
            case 5:
                checkOrderStatus();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("❌ Invalid option! Please choose 1-6.");
        }
    }

    private static void registerStudent() {
        System.out.println("\n📝 STUDENT REGISTRATION");
        System.out.println("-".repeat(30));
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty!");
            return;
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
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
    }

    private static void loginStudent() {
        System.out.println("\n🔑 STUDENT LOGIN");
        System.out.println("-".repeat(20));
        System.out.print("Enter your Student Code: ");
        String studentCode = scanner.nextLine().trim();

        if (studentCode.isEmpty()) {
            System.out.println("❌ Student code cannot be empty!");
            return;
        }

        try {
            Student student = studentManager.login(studentCode);
            if (student != null) {
                currentStudent = student;
                System.out.println("\n✅ Login successful! Welcome back, " + student.getName() + "!");
                System.out.println("💳 Your current loyalty points: " + loyaltyProgram.getBalance(student));
            } else {
                System.out.println("❌ Invalid student code! Please check and try again.");
            }
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
    }

    private static void viewMenuAndOrder() {
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
                placeOrder(menuItems);
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading menu: " + e.getMessage());
        }
    }

    private static void displayMenuByCategory(List<MenuItem> items, Category category, String title) {
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

    private static void placeOrder(List<MenuItem> menuItems) {
        System.out.println("\n🛒 PLACE YOUR ORDER");
        System.out.println("-".repeat(30));

        // Show available discounts first
        if (DiscountManager.hasAvailableDiscounts(currentStudent)) {
            Money totalDiscount = DiscountManager.getTotalDiscountValue(currentStudent);
            System.out.println("🎫 You have available discounts worth: " + totalDiscount.getAmount() + " EGP");
            System.out.println("💡 These will be automatically applied to your order!");
            System.out.println();
        }

        List<Selection> selections = new ArrayList<>();

        while (true) {
            System.out.print("Enter Menu Item ID (or 0 to finish): ");
            int itemId = getIntInput();

            if (itemId == 0) break;

            // Find the menu item
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
            int quantity = getIntInput();

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
            Order order = orderProcessor.placeOrder(currentStudent, selections);
            Money originalTotal = order.total();

            // Apply discounts if available
            Money finalTotal = originalTotal;
            boolean discountApplied = false;
            if (DiscountManager.hasAvailableDiscounts(currentStudent)) {
                finalTotal = DiscountManager.applyDiscounts(currentStudent, originalTotal);
                discountApplied = true;
            }

            System.out.println("\n✅ ORDER PLACED SUCCESSFULLY!");
            System.out.println("🆔 Order ID: " + order.getId());

            if (discountApplied) {
                System.out.println("💰 Original Total: " + originalTotal);
                Money discountAmount = new Money(originalTotal.getAmount().doubleValue() - finalTotal.getAmount().doubleValue(), originalTotal.getCurrency());
                System.out.println("🎫 Discount Applied: -" + discountAmount.getAmount() + " EGP");
                System.out.println("💰 Final Total: " + finalTotal);
            } else {
                System.out.println("💰 Total Amount: " + finalTotal);
            }

            System.out.println("📅 Order Date: " + order.getOrderDate());
            System.out.println("📊 Status: " + order.getStatus());
            System.out.println("🎁 Loyalty Points Earned: " + (int)finalTotal.getAmount().doubleValue());

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Order failed: " + e.getMessage());
        }
    }

    private static void viewOrderHistory() {
        System.out.println("\n📋 YOUR ORDER HISTORY");
        System.out.println("=".repeat(60));

        try {
            // Get the OrderDAO to access order history
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findOrdersByStudent(currentStudent.getId());

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
                int orderId = getIntInput();

                if (orderId > 0) {
                    showOrderDetails(orderId);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading order history: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void showOrderDetails(int orderId) {
        try {
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.findById(orderId);

            if (order == null || order.getStudentId() != currentStudent.getId()) {
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

    private static void redeemLoyaltyPoints() {
        System.out.println("\n🎁 REDEEM LOYALTY POINTS");
        System.out.println("-".repeat(30));

        int currentPoints = loyaltyProgram.getBalance(currentStudent);
        System.out.println("💳 Your current points: " + currentPoints);

        // Show existing available discounts
        if (DiscountManager.hasAvailableDiscounts(currentStudent)) {
            Money totalDiscount = DiscountManager.getTotalDiscountValue(currentStudent);
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
        int pointsToRedeem = getIntInput();

        if (pointsToRedeem == 0) {
            System.out.println("Redemption cancelled.");
            return;
        }

        if (pointsToRedeem > currentPoints) {
            System.out.println("❌ You don't have enough points!");
            return;
        }

        try {
            Discount discount = loyaltyProgram.redeem(currentStudent, pointsToRedeem);
            // Store the discount for use in next order
            DiscountManager.addDiscount(currentStudent, discount);

            System.out.println("\n✅ REDEEMPTION SUCCESSFUL!");
            System.out.println("🎫 Discount Amount: " + discount.getAmount() + " EGP");
            System.out.println("📝 Description: " + discount.getDescription());
            System.out.println("💳 Remaining Points: " + loyaltyProgram.getBalance(currentStudent));
            System.out.println("💡 This discount will be automatically applied to your next order!");

            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("❌ Redemption failed: " + e.getMessage());
        }
    }

    private static void viewProfile() {
        System.out.println("\n👤 YOUR PROFILE");
        System.out.println("=".repeat(40));
        System.out.println("🎓 Name: " + currentStudent.getName());
        System.out.println("🆔 Student Code: " + currentStudent.getStudentCode());
        System.out.println("💳 Loyalty Points: " + loyaltyProgram.getBalance(currentStudent));
        System.out.println("💰 Discount Value: " + (loyaltyProgram.getBalance(currentStudent) * 0.1) + " EGP");

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void checkOrderStatus() {
        System.out.println("\n🔄 CHECK ORDER STATUS");
        System.out.println("-".repeat(30));
        System.out.print("Enter Order ID: ");
        int orderId = getIntInput();

        try {
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                System.out.println("❌ Order not found! Please check the Order ID.");
                return;
            }

            // Check if this order belongs to the current student
            if (order.getStudentId() != currentStudent.getId()) {
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

    private static void showAdminMenu() {
        // First check if admin is already logged in
        if (currentAdmin == null) {
            if (!adminLogin()) {
                return; // Failed login, go back to main menu
            }
        }

        // Admin is logged in, show the menu
        while (currentAdmin != null) {
            System.out.println("\n👨‍💼 ADMIN PANEL - Welcome " + currentAdmin.getName());
            System.out.println("=".repeat(60));
            System.out.println("1. 📋 View All Menu Items");
            System.out.println("2. ➕ Add New Menu Item");
            System.out.println("3. ✏️  Edit Menu Item");
            System.out.println("4. 🗑️  Remove Menu Item");
            System.out.println("5. 📊 View All Orders");
            System.out.println("6. 👥 View All Students");
            System.out.println("7. 🚪 Admin Logout");
            System.out.println("=".repeat(60));
            System.out.print("👉 Choose option (1-7): ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    viewAllMenuItems();
                    break;
                case 2:
                    addNewMenuItem();
                    break;
                case 3:
                    editMenuItem();
                    break;
                case 4:
                    removeMenuItem();
                    break;
                case 5:
                    viewAllOrders();
                    break;
                case 6:
                    viewAllStudents();
                    break;
                case 7:
                    adminLogout();
                    return;
                default:
                    System.out.println("❌ Invalid option! Please choose 1-7.");
            }
        }
    }

    private static boolean adminLogin() {
        System.out.println("\n🔐 ADMIN LOGIN");
        System.out.println("=".repeat(30));
        System.out.println("💡 Default admin credentials:");
        System.out.println("   Username: admin");
        System.out.println("   Password: admin123");
        System.out.println("-".repeat(30));

        // Allow 3 login attempts
        for (int attempts = 1; attempts <= 3; attempts++) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            Admin admin = adminManager.login(username, password);
            if (admin != null) {
                currentAdmin = admin;
                System.out.println("\n✅ Admin login successful! Welcome, " + admin.getName() + "!");
                return true;
            } else {
                System.out.println("❌ Invalid credentials! Attempt " + attempts + " of 3");
                if (attempts < 3) {
                    System.out.println("Please try again.\n");
                }
            }
        }

        System.out.println("🚫 Too many failed attempts. Access denied.");
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
        return false;
    }

    private static void adminLogout() {
        System.out.println("\n👋 Admin logout successful. Goodbye, " + currentAdmin.getName() + "!");
        currentAdmin = null;
    }

    private static void viewAllOrders() {
        System.out.println("\n📋 ALL ORDERS");
        System.out.println("=".repeat(80));

        try {
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();

            if (orders.isEmpty()) {
                System.out.println("📭 No orders found.");
            } else {
                System.out.printf("%-8s | %-12s | %-12s | %-15s | %s%n",
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

    private static void viewAllStudents() {
        System.out.println("\n👥 ALL STUDENTS");
        System.out.println("=".repeat(80));

        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.findAll();

            if (students.isEmpty()) {
                System.out.println("📭 No students found.");
            } else {
                System.out.printf("%-8s | %-15s | %-30s | %s%n",
                    "ID", "Student Code", "Name", "Loyalty Points");
                System.out.println("-".repeat(80));

                for (Student student : students) {
                    int points = student.getAccount() != null ? student.getAccount().balance() : 0;

                    System.out.printf("%-8d | %-15s | %-30s | %d%n",
                        student.getId(),
                        student.getStudentCode(),
                        student.getName(),
                        points);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading students: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewAllMenuItems() {
        System.out.println("\n📋 ALL MENU ITEMS");
        System.out.println("=".repeat(80));

        try {
            List<MenuItem> items = menuManager.listItems();
            if (items.isEmpty()) {
                System.out.println("📭 No menu items found.");
            } else {
                System.out.printf("%-4s | %-25s | %-12s | %-15s | %s%n",
                    "ID", "NAME", "PRICE", "CATEGORY", "DESCRIPTION");
                System.out.println("-".repeat(80));

                for (MenuItem item : items) {
                    System.out.printf("%-4d | %-25s | %-12s | %-15s | %s%n",
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getCategory(),
                        item.getDescription());
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading menu items: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void addNewMenuItem() {
        System.out.println("\n➕ ADD NEW MENU ITEM");
        System.out.println("-".repeat(30));

        System.out.print("Enter item name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter price (EGP): ");
        double price = getDoubleInput();

        System.out.println("Select category:");
        System.out.println("1. MAIN_COURSE");
        System.out.println("2. DRINK");
        System.out.println("3. SNACK");
        System.out.print("Enter choice (1-3): ");

        int categoryChoice = getIntInput();
        Category category;
        switch (categoryChoice) {
            case 1: category = Category.MAIN_COURSE; break;
            case 2: category = Category.DRINK; break;
            case 3: category = Category.SNACK; break;
            default:
                System.out.println("❌ Invalid category choice!");
                return;
        }

        try {
            menuManager.addItem(name, description, new Money(price, Currency.EGP), category);
            System.out.println("✅ Menu item added successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error adding menu item: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void editMenuItem() {
        System.out.println("\n✏️  EDIT MENU ITEM");
        System.out.println("-".repeat(25));

        // Show all menu items first
        viewAllMenuItems();

        System.out.print("Enter ID of item to edit (or 0 to cancel): ");
        int itemId = getIntInput();

        if (itemId == 0) {
            return;
        }

        // Find the item to edit
        try {
            List<MenuItem> items = menuManager.listItems();
            MenuItem itemToEdit = null;

            for (MenuItem item : items) {
                if (item.getId() == itemId) {
                    itemToEdit = item;
                    break;
                }
            }

            if (itemToEdit == null) {
                System.out.println("❌ Item not found!");
                return;
            }

            System.out.println("\nCurrent item details:");
            System.out.println("Name: " + itemToEdit.getName());
            System.out.println("Description: " + itemToEdit.getDescription());
            System.out.println("Price: " + itemToEdit.getPrice());
            System.out.println("Category: " + itemToEdit.getCategory());

            System.out.print("\nEnter new name (or press Enter to keep current): ");
            String newName = scanner.nextLine().trim();
            if (newName.isEmpty()) newName = itemToEdit.getName();

            System.out.print("Enter new description (or press Enter to keep current): ");
            String newDescription = scanner.nextLine().trim();
            if (newDescription.isEmpty()) newDescription = itemToEdit.getDescription();

            System.out.print("Enter new price (or press Enter to keep current): ");
            String priceInput = scanner.nextLine().trim();
            double newPrice = itemToEdit.getPrice().getAmount().doubleValue();
            if (!priceInput.isEmpty()) {
                try {
                    newPrice = Double.parseDouble(priceInput);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid price format! Keeping current price.");
                }
            }

            // Update the item
            itemToEdit.setName(newName);
            itemToEdit.setDescription(newDescription);
            itemToEdit.setPrice(newPrice);

            menuManager.updateItem(itemToEdit);
            System.out.println("✅ Menu item updated successfully!");

        } catch (Exception e) {
            System.out.println("❌ Error editing menu item: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void removeMenuItem() {
        System.out.println("\n🗑️  REMOVE MENU ITEM");
        System.out.println("-".repeat(25));

        // Show current items first
        viewAllMenuItems();

        System.out.print("Enter ID of item to remove (or 0 to cancel): ");
        int itemId = getIntInput();

        if (itemId == 0) {
            return;
        }

        try {
            menuManager.removeItem(itemId);
            System.out.println("✅ Menu item removed successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error removing menu item: " + e.getMessage());
        }

        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number! Please enter a valid integer: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number! Please enter a valid decimal: ");
            }
        }
    }

    private static void logout() {
        System.out.println("\n👋 Goodbye, " + currentStudent.getName() + "!");
        System.out.println("Thank you for using ITI Cafeteria System!");
        currentStudent = null;
    }

    private static void exitSystem() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🙏 Thank you for using ITI Cafeteria System!");
        System.out.println("👋 Goodbye and have a great day!");
        System.out.println("=".repeat(60));
        scanner.close();
        System.exit(0);
    }
}
