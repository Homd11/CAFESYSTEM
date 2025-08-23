package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import Core.*;
import Services.*;

import java.io.File;
import java.net.URL;

/**
 * Main GUI Application class following SOLID principles
 * Single Responsibility: Handles UI initialization and scene management
 * Open/Closed: Extensible for new screens
 * Dependency Inversion: Depends on abstractions (interfaces)
 */
public class Gui extends Application {

    // Services (using existing services, not duplicating functionality)
    private SystemService systemService;
    private StudentService studentService;
    private AdminManager adminManager;
    private MenuManager menuManager;
    private OrderProcessor orderProcessor;

    // UI Components
    private Stage primaryStage;
    private Scene loginScene, studentDashboardScene, adminDashboardScene;
    private Student currentStudent;
    private Admin currentAdmin;

    // UI Controllers
    private LoginController loginController;
    private StudentDashboardController studentController;
    private AdminDashboardController adminController;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;

            System.out.println("🚀 Initializing GUI Application...");

            initializeServices();
            initializeControllers();
            setupStage();

            // Initialize system and show login
            System.out.println("📡 Testing database connection...");
            if (systemService.testDatabaseConnection()) {
                System.out.println("✅ Database connected successfully");
                systemService.initializeSampleData();
                showLoginScreen();
            } else {
                System.err.println("❌ Database connection failed");
                showErrorDialog("Database Connection Failed",
                              "Could not connect to database. Please check MySQL server.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error starting GUI: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Startup Error", "Failed to start application: " + e.getMessage());
        }
    }

    private void initializeServices() {
        try {
            System.out.println("🔧 Initializing services...");
            this.systemService = new SystemService();
            this.studentService = new StudentService();
            this.adminManager = new AdminManager();
            this.menuManager = new MenuManager();
            this.orderProcessor = new OrderProcessor();
            System.out.println("✅ Services initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing services: " + e.getMessage());
            throw e;
        }
    }

    private void initializeControllers() {
        try {
            System.out.println("🎮 Initializing controllers...");
            // Set static reference for FXML controllers
            LoginController.setMainApp(this);
            System.out.println("✅ Controllers initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing controllers: " + e.getMessage());
            throw e;
        }
    }

    private void setupStage() {
        try {
            System.out.println("🎨 Setting up stage...");
            primaryStage.setTitle("🏫 ITI Cafeteria System");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();

            // Don't set fullscreen initially - let user choose
            primaryStage.setFullScreenExitHint("Press 'Esc' to exit full screen mode");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("ESC"));

            // Handle close request
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("👋 Application closing...");
                System.exit(0);
            });

            System.out.println("✅ Stage setup complete");
        } catch (Exception e) {
            System.err.println("❌ Error setting up stage: " + e.getMessage());
            throw e;
        }
    }

    private String resolveAppCss() {
        try {
            // Prefer dev source first
            File f2 = new File("src/resources/css/app.css");
            if (f2.exists()) return f2.toURI().toString();
            // Then copied resources folder if present
            File f1 = new File("resources/css/app.css");
            if (f1.exists()) return f1.toURI().toString();
        } catch (Exception ignore) {}
        // Finally try classpath
        try {
            var url = getClass().getResource("/resources/css/app.css");
            if (url != null) return url.toExternalForm();
        } catch (Exception ignore) {}
        return null;
    }

    private URL resolveFxml(String classpathResource, String srcRelativePath) {
        try {
            File f = new File(srcRelativePath);
            if (f.exists()) return f.toURI().toURL();
        } catch (Exception ignore) {}
        try {
            URL url = getClass().getResource(classpathResource);
            if (url != null) return url;
        } catch (Exception ignore) {}
        return null;
    }

    // Navigation methods
    public void showLoginScreen() {
        try {
            System.out.println("🔐 Showing login screen...");
            loginScene = LoginController.createScene();
            // Apply global stylesheet
            try {
                String css = resolveAppCss();
                if (css != null) {
                    loginScene.getStylesheets().add(css);
                    System.out.println("🎨 Stylesheet applied to login scene: " + css);
                } else {
                    System.err.println("⚠️ Stylesheet not found for login scene");
                }
            } catch (Exception ex) {
                System.err.println("⚠️ Could not load stylesheet for login scene: " + ex.getMessage());
            }
            primaryStage.setScene(loginScene);
            primaryStage.show();
            System.out.println("✅ Login screen displayed");
        } catch (Exception e) {
            System.err.println("❌ Error showing login screen: " + e.getMessage());
            e.printStackTrace();

            // Create emergency fallback
            VBox fallback = new VBox(20);
            fallback.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50; -fx-alignment: center;");
            Label errorLabel = new Label("Error loading login screen. Application will continue with basic interface.");
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            fallback.getChildren().add(errorLabel);

            Scene errorScene = new Scene(fallback, 800, 600);
            primaryStage.setScene(errorScene);
            primaryStage.show();
        }
    }

    public void showStudentDashboard(Student student) {
        try {
            System.out.println("👨‍🎓 Showing student dashboard for: " + student.getName());
            this.currentStudent = student;

            URL fxmlUrl = resolveFxml("/resources/fxml/student_dashboard.fxml", "src/resources/fxml/student_dashboard.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            BorderPane root = loader.load();
            studentController = loader.getController();
            studentController.initAfterLoad(this);

            studentDashboardScene = new Scene(root, 1000, 700);
            // Apply global stylesheet
            try {
                String css = resolveAppCss();
                if (css != null) {
                    studentDashboardScene.getStylesheets().add(css);
                    System.out.println("🎨 Stylesheet applied to student dashboard: " + css);
                } else {
                    System.err.println("⚠️ Stylesheet not found for student dashboard");
                }
            } catch (Exception ex) {
                System.err.println("⚠️ Could not load stylesheet for student dashboard: " + ex.getMessage());
            }
            primaryStage.setScene(studentDashboardScene);
            System.out.println("✅ Student dashboard displayed");

        } catch (Exception e) {
            System.err.println("❌ Error showing student dashboard: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Dashboard Error", "Failed to show student dashboard: " + e.getMessage());
            showLoginScreen(); // Fallback to login
        }
    }

    public void showAdminDashboard(Admin admin) {
        try {
            System.out.println("👨‍💼 Showing admin dashboard for: " + admin.getName());
            this.currentAdmin = admin;

            URL fxmlUrl = resolveFxml("/resources/fxml/admin_dashboard.fxml", "src/resources/fxml/admin_dashboard.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            BorderPane root = loader.load();
            adminController = loader.getController();
            adminController.initAfterLoad(this);

            adminDashboardScene = new Scene(root, 1000, 700);
            // Apply global stylesheet
            try {
                String css = resolveAppCss();
                if (css != null) {
                    adminDashboardScene.getStylesheets().add(css);
                    System.out.println("🎨 Stylesheet applied to admin dashboard: " + css);
                } else {
                    System.err.println("⚠️ Stylesheet not found for admin dashboard");
                }
            } catch (Exception ex) {
                System.err.println("⚠️ Could not load stylesheet for admin dashboard: " + ex.getMessage());
            }
            primaryStage.setScene(adminDashboardScene);
            System.out.println("✅ Admin dashboard displayed");
        } catch (Exception e) {
            System.err.println("❌ Error showing admin dashboard: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Dashboard Error", "Failed to show admin dashboard: " + e.getMessage());
            showLoginScreen(); // Fallback to login
        }
    }

    // Utility methods
    public void showErrorDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing error dialog: " + e.getMessage());
        }
    }

    public void showSuccessDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing success dialog: " + e.getMessage());
        }
    }

    public boolean showConfirmDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
        } catch (Exception e) {
            System.err.println("❌ Error showing confirm dialog: " + e.getMessage());
            return false;
        }
    }

    public void showInfoDialog(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing info dialog: " + e.getMessage());
        }
    }

    public void showStudentDetails(Student student) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Student Details");
            alert.setHeaderText("Student Information");
            String details = String.format(
                "Name: %s\nStudent Code: %s\nLoyalty Points: %d\nID: %d",
                student.getName(),
                student.getStudentCode(),
                student.getLoyaltyAccount().balance(),
                student.getId()
            );
            alert.setContentText(details);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing student details: " + e.getMessage());
        }
    }

    public void showOrderDetails(Order order) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Details");
            alert.setHeaderText("Order Information");
            String details = String.format(
                "Order ID: %d\nStudent ID: %d\nTotal: %s\nStatus: %s\nDate: %s",
                order.getId(),
                order.getStudentId(),
                order.total() != null ? order.total().getAmount() + " " + order.total().getCurrency() : "0.00 EGP",
                order.getStatus(),
                order.getOrderDate()
            );
            alert.setContentText(details);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing order details: " + e.getMessage());
        }
    }

    // Getters for existing services (proper SOLID approach)
    public SystemService getSystemService() { return systemService; }
    public StudentService getStudentService() { return studentService; }
    public AdminManager getAdminManager() { return adminManager; }
    public MenuManager getMenuManager() { return menuManager; }
    public OrderProcessor getOrderProcessor() { return orderProcessor; }
    public Student getCurrentStudent() { return currentStudent; }
    public Admin getCurrentAdmin() { return currentAdmin; }
    public StudentManager getStudentManager() {
        return new StudentManager(); // Create instance as needed
    }

    public OrderProcessor getOrderManager() {
        return orderProcessor;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        System.out.println("🎯 Starting ITI Cafeteria Application...");
        launch(args);
    }
}
