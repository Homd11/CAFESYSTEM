package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.Platform;

import Core.Student;
import Core.Admin;
import Services.StudentManager;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * LoginController - Handles login and registration UI
 * Single Responsibility: Manages login/registration interface
 * Open/Closed: Can be extended for additional authentication methods
 */
public class LoginController implements Initializable {

    private static Gui mainApp;

    // FXML injected fields
    @FXML private TextField studentCodeField;
    @FXML private TextField nameField;
    @FXML private PasswordField adminPasswordField;
    @FXML private Button studentLoginButton;
    @FXML private Button registerButton;
    @FXML private Button adminLoginButton;

    public LoginController() {
        // Default constructor required by FXML
    }

    public static void setMainApp(Gui app) {
        mainApp = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ LoginController initialized successfully");

        // Add enter key support
        if (studentCodeField != null) {
            studentCodeField.setOnAction(e -> handleStudentLogin());
        }
        if (nameField != null) {
            nameField.setOnAction(e -> handleRegister());
        }
        if (adminPasswordField != null) {
            adminPasswordField.setOnAction(e -> handleAdminLogin());
        }
    }

    private static URL resolveFxml() {
        try {
            URL url = LoginController.class.getResource("/resources/fxml/login.fxml");
            if (url != null) return url;
        } catch (Exception ignore) {}
        try {
            File f = new File("src/resources/fxml/login.fxml");
            if (f.exists()) return f.toURI().toURL();
        } catch (Exception ignore) {}
        return null;
    }

    public static Scene createScene() {
        try {
            System.out.println("🔍 Loading login.fxml...");
            FXMLLoader loader = new FXMLLoader(resolveFxml());
            BorderPane root = loader.load();
            System.out.println("✅ login.fxml loaded successfully");

            Scene scene = new Scene(root, 800, 600);
            return scene;

        } catch (IOException e) {
            System.err.println("❌ Failed to load login.fxml: " + e.getMessage());
            e.printStackTrace();

            // Create a fallback simple interface
            return createFallbackScene();
        }
    }

    private static Scene createFallbackScene() {
        System.out.println("🔄 Creating fallback login interface...");

        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50; -fx-alignment: center;");

        Label title = new Label("🏫 ITI Cafeteria System");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        TextField studentCode = new TextField();
        studentCode.setPromptText("Enter Student Code");
        studentCode.setMaxWidth(300);

        Button loginBtn = new Button("Student Login");
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10;");
        loginBtn.setOnAction(e -> {
            String code = studentCode.getText().trim();
            if (!code.isEmpty()) {
                try {
                    StudentManager sm = new StudentManager();
                    Student student = sm.login(code);
                    if (student != null) {
                        System.out.println("✅ Student login successful: " + student.getName());
                        Platform.runLater(() -> mainApp.showStudentDashboard(student));
                    } else {
                        System.out.println("❌ Student not found");
                        showAlert("Login Failed", "Student code not found");
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Login error: " + ex.getMessage());
                    showAlert("Error", "Login failed: " + ex.getMessage());
                }
            }
        });

        root.getChildren().addAll(title, studentCode, loginBtn);
        return new Scene(root, 800, 600);
    }

    // FXML Event Handlers
    @FXML
    private void handleStudentLogin() {
        String code = studentCodeField.getText().trim();
        System.out.println("🎓 Attempting student login with code: " + code);

        if (code.isEmpty()) {
            showAlert("Input Error", "Please enter your student code");
            return;
        }

        try {
            StudentManager studentManager = new StudentManager();
            Student student = studentManager.login(code);

            if (student != null) {
                System.out.println("✅ Student login successful: " + student.getName());
                mainApp.showStudentDashboard(student);
            } else {
                System.out.println("❌ Student not found with code: " + code);
                showAlert("Login Failed", "Student code not found. Please check your code or register first.");
            }
        } catch (Exception e) {
            System.err.println("❌ Student login error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Login Error", "Error during login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        System.out.println("📝 Attempting registration for: " + name);

        if (name.isEmpty()) {
            showAlert("Input Error", "Please enter your name");
            return;
        }

        try {
            StudentManager studentManager = new StudentManager();
            Student student = studentManager.register(name);

            if (student != null) {
                System.out.println("✅ Registration successful: " + student.getName() + " (Code: " + student.getStudentCode() + ")");

                String message = "Welcome " + student.getName() + "!\n\n" +
                               "Your student code is: " + student.getStudentCode() + "\n\n" +
                               "Please save this code - you'll need it to login!";

                showAlert("Registration Successful", message);

                // Clear fields and put code in login field
                nameField.clear();
                studentCodeField.setText(student.getStudentCode());

            } else {
                System.out.println("❌ Registration failed");
                showAlert("Registration Failed", "Could not create student account");
            }
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Registration Error", "Error during registration: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdminLogin() {
        String password = adminPasswordField.getText();
        System.out.println("👨‍💼 Attempting admin login...");

        if (password.isEmpty()) {
            showAlert("Input Error", "Please enter admin password");
            return;
        }

        try {
            Admin admin = mainApp.getAdminManager().login("admin", password);

            if (admin != null) {
                System.out.println("✅ Admin login successful: " + admin.getName());
                mainApp.showAdminDashboard(admin);
            } else {
                System.out.println("❌ Admin login failed");
                showAlert("Login Failed", "Invalid admin password!\n\nDefault password: admin123");
            }
        } catch (Exception e) {
            System.err.println("❌ Admin login error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Login Error", "Error during admin login: " + e.getMessage());
        }
    }

    private static void showAlert(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Error showing alert: " + e.getMessage());
        }
    }
}
