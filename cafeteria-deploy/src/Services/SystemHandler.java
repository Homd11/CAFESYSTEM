package Services;

import Core.Admin;
import Core.Student;
import Interfaces.ISystemService;
import Interfaces.IStudentService;
import Interfaces.IAdminManager;
import java.util.Scanner;

/**
 * SystemHandler - Handles all UI flow and user interactions
 * This class acts as a presentation controller, coordinating between
 * the Main class and the various service classes.
 */
public class SystemHandler {
    private final Scanner scanner;
    private final ISystemService systemService;
    private final IStudentService studentService;
    private final IAdminManager adminManager;
    private final OrderProcessor orderProcessor;

    private Student currentStudent = null;
    private Admin currentAdmin = null;

    // Constructor with dependency injection for better testability
    public SystemHandler(Scanner scanner, ISystemService systemService,
                        IStudentService studentService, IAdminManager adminManager,
                        OrderProcessor orderProcessor) {
        this.scanner = scanner;
        this.systemService = systemService;
        this.studentService = studentService;
        this.adminManager = adminManager;
        this.orderProcessor = orderProcessor;
    }

    // Default constructor for backwards compatibility
    public SystemHandler(Scanner scanner) {
        this.scanner = scanner;
        this.systemService = new SystemService();
        this.studentService = new StudentService();
        this.adminManager = new AdminManager();
        this.orderProcessor = new OrderProcessor();
    }

    /**
     * Initialize the system - check database and load sample data
     */
    public boolean initializeSystem() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       🏫 UNIVERSITY CAFETERIA ORDER & LOYALTY SYSTEM       ║");
        System.out.println("║              Welcome to ITI Cafeteria System              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

        // Test database connection first
        if (!systemService.testDatabaseConnection()) {
            System.out.println("\n❌ Database connection failed! Please check your MySQL server.");
            System.out.println("💡 Make sure MySQL is running and cafeteriadb database exists.");
            return false;
        }

        System.out.println("✅ Database connection successful!");
        System.out.println("🗄️  System initialized and ready to serve!\n");

        // Initialize with some sample data if database is empty
        systemService.initializeSampleData();
        return true;
    }

    /**
     * Main application flow controller
     */
    public void runApplication() {
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

    /**
     * Display and handle login menu
     */
    private void showLoginMenu() {
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

    /**
     * Display and handle main student menu
     */
    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🏠 MAIN MENU - Welcome " + currentStudent.getName() +
                          " (Code: " + currentStudent.getStudentCode() + ")");
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
                studentService.viewMenuAndOrder(currentStudent, scanner);
                break;
            case 2:
                studentService.viewOrderHistory(currentStudent, scanner);
                break;
            case 3:
                studentService.redeemLoyaltyPoints(currentStudent, scanner);
                break;
            case 4:
                studentService.viewProfile(currentStudent, scanner);
                break;
            case 5:
                studentService.checkOrderStatus(currentStudent, scanner);
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("❌ Invalid option! Please choose 1-6.");
        }
    }

    /**
     * Display and handle admin menu
     */
    private void showAdminMenu() {
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
            System.out.println("6. 🔄 Manage Order Statuses");
            System.out.println("7. 👥 View All Students");
            System.out.println("8. 🚪 Admin Logout");
            System.out.println("=".repeat(60));
            System.out.print("👉 Choose option (1-8): ");

            int choice = getIntInput();

            switch (choice) {
                case 1:
                    adminManager.viewAllMenuItems(scanner);
                    break;
                case 2:
                    adminManager.addNewMenuItem(scanner);
                    break;
                case 3:
                    adminManager.editMenuItem(scanner);
                    break;
                case 4:
                    adminManager.removeMenuItem(scanner);
                    break;
                case 5:
                    orderProcessor.viewAllOrders(scanner);
                    break;
                case 6:
                    orderProcessor.manageOrderStatuses(scanner);
                    break;
                case 7:
                    adminManager.viewAllStudents(scanner);
                    break;
                case 8:
                    adminLogout();
                    return;
                default:
                    System.out.println("❌ Invalid option! Please choose 1-8.");
            }
        }
    }

    // User action methods
    private void registerStudent() {
        Student student = studentService.registerStudent(scanner);
        // Don't auto-login after registration, let user choose to login
    }

    private void loginStudent() {
        currentStudent = studentService.loginStudent(scanner);
    }

    private boolean adminLogin() {
        Admin admin = adminManager.performLogin(scanner);
        if (admin != null) {
            currentAdmin = admin;
            return true;
        }
        return false;
    }

    private void adminLogout() {
        adminManager.performLogout(currentAdmin);
        currentAdmin = null;
    }

    private void logout() {
        System.out.println("\n👋 Goodbye, " + currentStudent.getName() + "!");
        System.out.println("Thank you for using ITI Cafeteria System!");
        currentStudent = null;
    }

    private void exitSystem() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🙏 Thank you for using ITI Cafeteria System!");
        System.out.println("👋 Goodbye and have a great day!");
        System.out.println("=".repeat(60));
        scanner.close();
        System.exit(0);
    }

    // Utility method
    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number! Please enter a valid integer: ");
            }
        }
    }
}
