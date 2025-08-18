package Services;

import Core.Student;
import Core.MenuItem;
import Services.MenuManager;
import Services.StudentManager;
import Values.Money;
import Enums.Currency;
import Enums.Category;
import Interfaces.ISystemService;

import java.util.List;

public class SystemService implements ISystemService {
    private MenuManager menuManager;
    private StudentManager studentManager;

    public SystemService() {
        this.menuManager = new MenuManager();
        this.studentManager = new StudentManager();
    }

    /**
     * Test database connection
     */
    public boolean testDatabaseConnection() {
        try {
            menuManager.listItems();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initialize sample data if database is empty
     */
    public void initializeSampleData() {
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

                initializeSampleStudents();
            }
        } catch (Exception e) {
            System.out.println("⚠️  Could not initialize sample data: " + e.getMessage());
        }
    }

    /**
     * Initialize sample students with loyalty points
     */
    private void initializeSampleStudents() {
        try {
            System.out.println("👥 Creating sample students with loyalty points...");

            Student student1 = studentManager.register("Ahmed Mohamed Ali");
            student1.getAccount().add(50);

            Student student2 = studentManager.register("Sara Hassan Ibrahim");
            student2.getAccount().add(150);

            Student student3 = studentManager.register("Omar Khaled Mahmoud");
            student3.getAccount().add(25);

            Student student4 = studentManager.register("Fatma Youssef Ahmed");
            student4.getAccount().add(200);

            Student student5 = studentManager.register("Mohamed Tarek Said");

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
}
