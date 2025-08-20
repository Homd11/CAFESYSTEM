package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

import Core.Student;
import Core.Admin;
import Services.StudentManager;

/**
 * LoginController - Handles login and registration UI
 * Single Responsibility: Manages login/registration interface
 * Open/Closed: Can be extended for additional authentication methods
 */
public class LoginController {

    private final Gui mainApp;
    private TextField studentCodeField;
    private TextField nameField;
    private PasswordField adminPasswordField;
    private TabPane tabPane;

    public LoginController(Gui mainApp) {
        this.mainApp = mainApp;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Header
        VBox header = createHeader();

        // Main content with tabs
        VBox content = createMainContent();

        root.getChildren().addAll(header, content);

        return new Scene(root, 1000, 700);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🏫 ITI Cafeteria System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(createDropShadow());

        Label subtitleLabel = new Label("University Cafeteria Order & Loyalty System");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.WHITE);

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Student Login Tab
        Tab studentLoginTab = createStudentLoginTab();

        // Student Registration Tab
        Tab studentRegisterTab = createStudentRegisterTab();

        // Admin Login Tab
        Tab adminLoginTab = createAdminLoginTab();

        tabPane.getTabs().addAll(studentLoginTab, studentRegisterTab, adminLoginTab);

        content.getChildren().add(tabPane);
        return content;
    }

    private Tab createStudentLoginTab() {
        Tab tab = new Tab("🔑 Student Login");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Welcome Back!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        Label codeLabel = new Label("Student Code:");
        codeLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        studentCodeField = new TextField();
        studentCodeField.setPromptText("Enter your student code");
        studentCodeField.setStyle(getTextFieldStyle());
        studentCodeField.setPrefHeight(40);

        Button loginButton = new Button("🔑 Login");
        loginButton.setStyle(getPrimaryButtonStyle());
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);
        loginButton.setOnAction(e -> handleStudentLogin());

        formBox.getChildren().addAll(codeLabel, studentCodeField, loginButton);
        content.getChildren().addAll(titleLabel, formBox);

        tab.setContent(content);
        return tab;
    }

    private Tab createStudentRegisterTab() {
        Tab tab = new Tab("📝 Student Register");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Join Our Community!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKGREEN);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle(getTextFieldStyle());
        nameField.setPrefHeight(40);

        Label codeLabel = new Label("Student Code:");
        codeLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        TextField regCodeField = new TextField();
        regCodeField.setPromptText("Enter your student code");
        regCodeField.setStyle(getTextFieldStyle());
        regCodeField.setPrefHeight(40);

        Button registerButton = new Button("📝 Register");
        registerButton.setStyle(getSuccessButtonStyle());
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(45);
        registerButton.setOnAction(e -> handleStudentRegistration(nameField.getText(), regCodeField.getText()));

        formBox.getChildren().addAll(nameLabel, nameField, codeLabel, regCodeField, registerButton);
        content.getChildren().addAll(titleLabel, formBox);

        tab.setContent(content);
        return tab;
    }

    private Tab createAdminLoginTab() {
        Tab tab = new Tab("👨‍💼 Admin Login");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Admin Access");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKRED);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        Label passwordLabel = new Label("Admin Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        adminPasswordField = new PasswordField();
        adminPasswordField.setPromptText("Enter admin password");
        adminPasswordField.setStyle(getTextFieldStyle());
        adminPasswordField.setPrefHeight(40);

        Button adminLoginButton = new Button("👨‍💼 Admin Login");
        adminLoginButton.setStyle(getWarningButtonStyle());
        adminLoginButton.setPrefWidth(200);
        adminLoginButton.setPrefHeight(45);
        adminLoginButton.setOnAction(e -> handleAdminLogin());

        formBox.getChildren().addAll(passwordLabel, adminPasswordField, adminLoginButton);
        content.getChildren().addAll(titleLabel, formBox);

        tab.setContent(content);
        return tab;
    }

    private void handleStudentLogin() {
        String code = studentCodeField.getText().trim();
        if (code.isEmpty()) {
            mainApp.showErrorDialog("Input Error", "Please enter your student code");
            return;
        }

        try {
            // Use the existing StudentManager.login() method directly
            StudentManager studentManager = new StudentManager();
            Student student = studentManager.login(code);
            if (student != null) {
                mainApp.showStudentDashboard(student);
            } else {
                mainApp.showErrorDialog("Login Failed", "Student code not found. Please register first.");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Login Error", "Error during login: " + e.getMessage());
        }
    }

    private void handleStudentRegistration(String name, String code) {
        if (name.trim().isEmpty()) {
            mainApp.showErrorDialog("Input Error", "Please enter your name");
            return;
        }

        try {
            // Use the existing StudentManager.register() method directly
            StudentManager studentManager = new StudentManager();
            Student student = studentManager.register(name.trim());
            if (student != null) {
                mainApp.showSuccessDialog("Registration Successful",
                    "Welcome " + student.getName() + "! You can now login with code: " + student.getStudentCode());
                tabPane.getSelectionModel().select(0); // Switch to login tab
                studentCodeField.clear();
                nameField.clear();
            } else {
                mainApp.showErrorDialog("Registration Failed", "Registration failed");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Registration Error", "Error during registration: " + e.getMessage());
        }
    }

    private void handleAdminLogin() {
        String password = adminPasswordField.getText();
        if (password.isEmpty()) {
            mainApp.showErrorDialog("Input Error", "Please enter admin password");
            return;
        }

        try {
            System.out.println("🔍 DEBUG: Attempting admin login with password: " + password);

            // Use the existing AdminManager.login() method directly
            Admin admin = mainApp.getAdminManager().login("admin", password);

            System.out.println("🔍 DEBUG: Admin login result: " + (admin != null ? "SUCCESS" : "FAILED"));

            if (admin != null) {
                System.out.println("✅ Admin login successful for: " + admin.getName());
                mainApp.showAdminDashboard(admin);
            } else {
                System.out.println("❌ Admin login failed - checking if admin exists in database...");
                mainApp.showErrorDialog("Login Failed",
                    "Invalid admin password!\n\nDefault credentials:\nUsername: admin\nPassword: admin123\n\nCheck console for debug information.");
            }
        } catch (Exception e) {
            System.err.println("❌ Exception during admin login: " + e.getMessage());
            e.printStackTrace();
            mainApp.showErrorDialog("Login Error", "Error during admin login: " + e.getMessage());
        }
    }

    // Styling methods
    private String getTextFieldStyle() {
        return "-fx-background-color: white; " +
               "-fx-border-color: #ddd; " +
               "-fx-border-radius: 5; " +
               "-fx-background-radius: 5; " +
               "-fx-padding: 10; " +
               "-fx-font-size: 14;";
    }

    private String getPrimaryButtonStyle() {
        return "-fx-background-color: #007bff; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 14; " +
               "-fx-font-weight: bold; " +
               "-fx-cursor: hand;";
    }

    private String getSuccessButtonStyle() {
        return "-fx-background-color: #28a745; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 14; " +
               "-fx-font-weight: bold; " +
               "-fx-cursor: hand;";
    }

    private String getWarningButtonStyle() {
        return "-fx-background-color: #dc3545; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 14; " +
               "-fx-font-weight: bold; " +
               "-fx-cursor: hand;";
    }

    private DropShadow createDropShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(3.0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0.4, 0.5, 0.5));
        return shadow;
    }
}
