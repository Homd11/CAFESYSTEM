package Services;

import Core.Admin;
import Core.Student;
import Core.MenuItem;
import DB.AdminDAO;
import DB.StudentDAO;
import Values.Money;
import Enums.Currency;
import Enums.Category;
import Interfaces.IAdminManager;
import java.util.List;
import java.util.Scanner;

public class AdminManager implements IAdminManager {
    private AdminDAO adminDAO;
    private MenuManager menuManager;

    public AdminManager() {
        this.adminDAO = new AdminDAO();
        this.menuManager = new MenuManager();
        // Ensure admin table exists and create default admin if none exist
        initializeAdminSystem();
    }

    public AdminManager(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
        this.menuManager = new MenuManager();
        initializeAdminSystem();
    }

    private void initializeAdminSystem() {
        try {
            if (!adminDAO.adminExists()) {
                adminDAO.createDefaultAdmin();
                // System.out.println("✅ Default admin created: username='admin', password='admin123'");
            } else {
                // System.out.println("✅ Admin account already exists");
            }
        } catch (Exception e) {
            System.err.println("Error initializing admin system: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Admin login(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return null;
        }

        Admin admin = adminDAO.findByUsername(username.trim());
        if (admin != null && password.equals(admin.getPasswordHash())) {
            return admin;
        }
        return null;
    }

    public boolean createAdmin(String username, String password, String name, String role) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            return false;
        }

        // Check if username already exists
        if (adminDAO.findByUsername(username.trim()) != null) {
            return false;
        }

        String hashedPassword = PasswordHasher.hashPassword(password);
        Admin admin = new Admin(username.trim(), hashedPassword, name.trim(), role);

        try {
            adminDAO.save(admin);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Interactive admin login with UI and multiple attempts
     */
    public Admin performLogin(Scanner scanner) {
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

            Admin admin = login(username, password);
            if (admin != null) {
                System.out.println("\n✅ Admin login successful! Welcome, " + admin.getName() + "!");
                return admin;
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
        return null;
    }

    /**
     * Admin logout with UI
     */
    public void performLogout(Admin admin) {
        System.out.println("\n👋 Admin logout successful. Goodbye, " + admin.getName() + "!");
    }

    /**
     * View all students with formatted output
     */
    public void viewAllStudents(Scanner scanner) {
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

    /**
     * View all menu items with formatted output
     */
    public void viewAllMenuItems(Scanner scanner) {
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

    /**
     * Add new menu item with interactive UI
     */
    public void addNewMenuItem(Scanner scanner) {
        System.out.println("\n➕ ADD NEW MENU ITEM");
        System.out.println("-".repeat(30));

        System.out.print("Enter item name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter price (EGP): ");
        double price = getDoubleInput(scanner);

        System.out.println("Select category:");
        System.out.println("1. MAIN_COURSE");
        System.out.println("2. DRINK");
        System.out.println("3. SNACK");
        System.out.print("Enter choice (1-3): ");

        int categoryChoice = getIntInput(scanner);
        Category category;
        switch (categoryChoice) {
            case 1: category = Category.MAIN_COURSE; break;
            case 2: category = Category.DRINK; break;
            case 3: category = Category.SNACK; break;
            default:
                System.out.println("❌ Invalid category choice!");
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
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

    /**
     * Edit menu item with interactive UI
     */
    public void editMenuItem(Scanner scanner) {
        System.out.println("\n✏️  EDIT MENU ITEM");
        System.out.println("-".repeat(25));

        // Show all menu items first
        viewAllMenuItems(scanner);

        System.out.print("Enter ID of item to edit (or 0 to cancel): ");
        int itemId = getIntInput(scanner);

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
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
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

    /**
     * Remove menu item with interactive UI
     */
    public void removeMenuItem(Scanner scanner) {
        System.out.println("\n🗑️  REMOVE MENU ITEM");
        System.out.println("-".repeat(25));

        // Show current items first
        viewAllMenuItems(scanner);

        System.out.print("Enter ID of item to remove (or 0 to cancel): ");
        int itemId = getIntInput(scanner);

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

    public boolean addAdmin(Admin admin) {
        if (admin == null || admin.getUsername() == null || admin.getPasswordHash() == null || admin.getName() == null) {
            return false;
        }

        // Check if username already exists
        if (adminDAO.findByUsername(admin.getUsername().trim()) != null) {
            return false;
        }

        try {
            adminDAO.save(admin);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Helper methods for input validation
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

    private double getDoubleInput(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number! Please enter a valid decimal: ");
            }
        }
    }
}
