package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Core.*;
import Services.*;
import Interfaces.*;
import Enums.*;
import Values.*;

import java.util.List;
import java.util.ArrayList;

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
        this.primaryStage = primaryStage;
        initializeServices();
        initializeControllers();
        setupStage();

        // Initialize system and show login
        if (systemService.testDatabaseConnection()) {
            systemService.initializeSampleData();
            showLoginScreen();
        } else {
            showErrorDialog("Database Connection Failed",
                          "Could not connect to database. Please check MySQL server.");
        }
    }

    private void initializeServices() {
        this.systemService = new SystemService();
        this.studentService = new StudentService();
        this.adminManager = new AdminManager();
        this.menuManager = new MenuManager();
        this.orderProcessor = new OrderProcessor();
    }

    private void initializeControllers() {
        this.loginController = new LoginController(this);
        this.studentController = new StudentDashboardController(this);
        // Don't create AdminDashboardController here - create it when needed
    }

    private void setupStage() {
        primaryStage.setTitle("🏫 ITI Cafeteria System");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    // Navigation methods
    public void showLoginScreen() {
        loginScene = loginController.createScene();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public void showStudentDashboard(Student student) {
        this.currentStudent = student;
        studentDashboardScene = studentController.createScene();
        primaryStage.setScene(studentDashboardScene);
    }

    public void showAdminDashboard(Admin admin) {
        this.currentAdmin = admin;  // Set the current admin first
        adminController = new AdminDashboardController(this); // Initialize here
        adminDashboardScene = adminController.createScene();
        primaryStage.setScene(adminDashboardScene);
    }

    // Utility methods
    public void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showStudentDetails(Student student) {
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
    }

    public void showOrderDetails(Order order) {
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

    public static void main(String[] args) {
        launch(args);
    }
}
