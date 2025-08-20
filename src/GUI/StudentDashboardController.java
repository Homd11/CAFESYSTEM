package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Core.MenuItem;
import Core.Student;
import Core.Order;
import Enums.*;
import Values.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * StudentDashboardController - Handles student interface
 * Single Responsibility: Manages student dashboard and ordering
 */
public class StudentDashboardController {

    private final Gui mainApp;
    private ListView<Core.MenuItem> menuListView;
    private ListView<String> cartListView;
    private Label totalLabel;
    private Label loyaltyPointsLabel;
    private ComboBox<PaymentMethod> paymentMethodCombo;
    private Map<Integer, Integer> cart; // MenuItem ID -> Quantity

    // Add variables for discount tracking
    private double appliedDiscount = 0.0;
    private int appliedPoints = 0;

    public StudentDashboardController(Gui mainApp) {
        this.mainApp = mainApp;
        this.cart = new HashMap<>();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Top: Header with student info
        VBox header = createHeader();
        root.setTop(header);

        // Center: Main content (menu and cart)
        HBox mainContent = createMainContent();
        root.setCenter(mainContent);

        // Bottom: Order actions
        VBox orderActions = createOrderActions();
        root.setBottom(orderActions);

        return new Scene(root, 1000, 700);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #343a40;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Add back button
        Button backButton = new Button("← Back to Login");
        backButton.setStyle(getBackButtonStyle());
        backButton.setOnAction(e -> mainApp.showLoginScreen());

        Label welcomeLabel = new Label("Welcome, " + mainApp.getCurrentStudent().getName() + "! 👋");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        loyaltyPointsLabel = new Label("🎁 Loyalty Points: " +
            mainApp.getCurrentStudent().getLoyaltyAccount().getPoints());
        loyaltyPointsLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
        loyaltyPointsLabel.setTextFill(Color.LIGHTGREEN);

        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle(getSecondaryButtonStyle());
        logoutButton.setOnAction(e -> mainApp.showLoginScreen());

        Button deleteAccountButton = new Button("🗑️ Delete Account");
        deleteAccountButton.setStyle(getDangerButtonStyle());
        deleteAccountButton.setOnAction(e -> deleteStudentAccount());

        topRow.getChildren().addAll(backButton, welcomeLabel, spacer, loyaltyPointsLabel, deleteAccountButton, logoutButton);

        Label studentCodeLabel = new Label("Student Code: " + mainApp.getCurrentStudent().getStudentCode());
        studentCodeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        studentCodeLabel.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(topRow, studentCodeLabel);
        return header;
    }

    private HBox createMainContent() {
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20));

        // Left: Menu browsing
        VBox menuSection = createMenuSection();
        HBox.setHgrow(menuSection, Priority.ALWAYS);

        // Right: Cart and order summary
        VBox cartSection = createCartSection();
        cartSection.setPrefWidth(350);

        mainContent.getChildren().addAll(menuSection, cartSection);
        return mainContent;
    }

    private VBox createMenuSection() {
        VBox menuSection = new VBox(15);

        Label menuTitle = new Label("🍽️ Cafeteria Menu");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Category filter
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter by Category:");
        ComboBox<Category> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add(null); // For "All Categories"
        categoryFilter.getItems().addAll(Category.values());
        categoryFilter.setValue(null);
        categoryFilter.setOnAction(e -> filterMenuItems(categoryFilter.getValue()));

        filterBox.getChildren().addAll(filterLabel, categoryFilter);

        // Menu items list
        menuListView = new ListView<>();
        menuListView.setPrefHeight(400);
        menuListView.setCellFactory(listView -> new MenuItemCell());

        menuSection.getChildren().addAll(menuTitle, filterBox, menuListView);

        // Load menu items
        loadMenuItems();

        return menuSection;
    }

    private VBox createCartSection() {
        VBox cartSection = new VBox(15);

        Label cartTitle = new Label("🛒 Your Cart");
        cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Cart items list
        cartListView = new ListView<>();
        cartListView.setPrefHeight(200);

        // Cart actions
        HBox cartActions = new HBox(10);

        Button clearCartButton = new Button("🗑️ Clear Cart");
        clearCartButton.setStyle(getWarningButtonStyle());
        clearCartButton.setOnAction(e -> clearCart());

        cartActions.getChildren().add(clearCartButton);

        // Total and loyalty section
        VBox totalSection = new VBox(10);
        totalSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-border-color: #dee2e6; -fx-border-radius: 5;");

        totalLabel = new Label("Total: 0.00 EGP");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Loyalty points redemption
        HBox loyaltyBox = new HBox(10);
        loyaltyBox.setAlignment(Pos.CENTER_LEFT);

        Label loyaltyLabel = new Label("Redeem Points:");
        TextField pointsField = new TextField();
        pointsField.setPromptText("Points to redeem");
        pointsField.setPrefWidth(100);

        Button redeemButton = new Button("💎 Redeem");
        redeemButton.setStyle(getSuccessButtonStyle());
        redeemButton.setOnAction(e -> redeemPoints(pointsField));

        loyaltyBox.getChildren().addAll(loyaltyLabel, pointsField, redeemButton);

        totalSection.getChildren().addAll(totalLabel, loyaltyBox);

        cartSection.getChildren().addAll(cartTitle, cartListView, cartActions, totalSection);
        return cartSection;
    }

    private VBox createOrderActions() {
        VBox orderActions = new VBox(15);
        orderActions.setPadding(new Insets(20));
        orderActions.setStyle("-fx-background-color: #e9ecef;");

        Label orderTitle = new Label("💳 Complete Your Order");
        orderTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Payment method selection
        HBox paymentBox = new HBox(15);
        paymentBox.setAlignment(Pos.CENTER_LEFT);

        Label paymentLabel = new Label("Payment Method:");
        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll(PaymentMethod.values());
        paymentMethodCombo.setValue(PaymentMethod.CASH);

        Button placeOrderButton = new Button("🛍️ Place Order");
        placeOrderButton.setStyle(getPrimaryButtonStyle());
        placeOrderButton.setOnAction(e -> placeOrder());

        paymentBox.getChildren().addAll(paymentLabel, paymentMethodCombo, placeOrderButton);

        orderActions.getChildren().addAll(orderTitle, paymentBox);
        return orderActions;
    }

    private void loadMenuItems() {
        try {
            List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();
            ObservableList<Core.MenuItem> items = FXCollections.observableArrayList(menuItems);
            menuListView.setItems(items);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    private void filterMenuItems(Category category) {
        try {
            List<Core.MenuItem> allItems = mainApp.getMenuManager().listItems();
            List<Core.MenuItem> filteredItems;

            if (category == null) {
                filteredItems = allItems;
            } else {
                filteredItems = allItems.stream()
                    .filter(item -> item.getCategory() == category)
                    .toList();
            }

            ObservableList<Core.MenuItem> items = FXCollections.observableArrayList(filteredItems);
            menuListView.setItems(items);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to filter menu items: " + e.getMessage());
        }
    }

    private double calculateTotal() {
        return cart.entrySet().stream()
            .mapToDouble(entry -> {
                try {
                    List<Core.MenuItem> items = mainApp.getMenuManager().listItems();
                    Core.MenuItem item = items.stream()
                        .filter(menuItem -> menuItem.getId() == entry.getKey())
                        .findFirst()
                        .orElse(null);
                    if (item != null) {
                        return item.getPrice().getAmount().doubleValue() * entry.getValue();
                    }
                    return 0.0;
                } catch (Exception e) {
                    return 0.0;
                }
            })
            .sum() - appliedDiscount;
    }

    private void updateCartDisplay() {
        List<String> cartItems = new ArrayList<>();

        try {
            List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();

            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Core.MenuItem item = menuItems.stream()
                    .filter(menuItem -> menuItem.getId() == entry.getKey())
                    .findFirst()
                    .orElse(null);

                if (item != null) {
                    double itemTotal = item.getPrice().getAmount().doubleValue() * entry.getValue();
                    cartItems.add(String.format("%s x%d - %.2f EGP",
                        item.getName(), entry.getValue(), itemTotal));
                }
            }
        } catch (Exception e) {
            // Handle error silently for display purposes
        }

        cartListView.setItems(FXCollections.observableArrayList(cartItems));

        double total = calculateTotal();
        totalLabel.setText(String.format("Total: %.2f EGP", total));
    }

    private void addToCart(Core.MenuItem item) {
        cart.put(item.getId(), cart.getOrDefault(item.getId(), 0) + 1);
        updateCartDisplay();
        updateLoyaltyPoints();
    }

    private void clearCart() {
        cart.clear();
        appliedDiscount = 0.0;
        appliedPoints = 0;
        updateCartDisplay();
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            mainApp.showErrorDialog("Empty Cart", "Please add items to your cart before placing an order.");
            return;
        }

        try {
            List<Selection> selections = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                selections.add(new Selection(entry.getKey(), entry.getValue()));
            }

            // Calculate total amount
            double totalAmount = calculateTotal();

            // Use OrderProcessor to place order with payment
            boolean orderSuccess = mainApp.getOrderProcessor().placeOrderWithPayment(
                mainApp.getCurrentStudent(),
                selections,
                paymentMethodCombo.getValue()
            );

            if (orderSuccess) {
                // Add loyalty points (1 point per 10 EGP spent)
                int earnedPoints = (int) (totalAmount / 10);
                if (earnedPoints > 0) {
                    mainApp.getCurrentStudent().getLoyaltyAccount().addPoints(earnedPoints);
                }

                mainApp.showSuccessDialog("Order Placed!",
                    String.format("Your order has been placed successfully!\n" +
                                "Total: %.2f EGP\n" +
                                "Payment Method: %s\n" +
                                "Loyalty Points Earned: %d",
                                totalAmount, paymentMethodCombo.getValue(), earnedPoints));

                // Clear cart and update display
                clearCart();
                updateLoyaltyPoints();
            } else {
                mainApp.showErrorDialog("Order Failed", "Failed to process your order. Please try again.");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Order Failed", "Failed to place order: " + e.getMessage());
        }
    }

    private void redeemPoints(TextField pointsField) {
        try {
            int pointsToRedeem = Integer.parseInt(pointsField.getText());
            int availablePoints = mainApp.getCurrentStudent().getLoyaltyAccount().getPoints();

            if (pointsToRedeem <= 0) {
                mainApp.showErrorDialog("Invalid Points", "Please enter a positive number of points.");
                return;
            }

            if (pointsToRedeem > availablePoints) {
                mainApp.showErrorDialog("Insufficient Points",
                    String.format("You only have %d points available.", availablePoints));
                return;
            }

            // Each point = 0.1 EGP discount (10 points = 1 EGP)
            double discount = pointsToRedeem * 0.1;
            appliedDiscount = discount;
            appliedPoints = pointsToRedeem;

            // Deduct points
            mainApp.getCurrentStudent().getLoyaltyAccount().deductPoints(pointsToRedeem);

            updateCartDisplay();
            updateLoyaltyPoints();

            mainApp.showSuccessDialog("Points Redeemed!",
                String.format("%.2f EGP discount applied using %d points!", discount, pointsToRedeem));

            pointsField.clear();
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Input", "Please enter a valid number of points.");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to redeem points: " + e.getMessage());
        }
    }

    private void updateLoyaltyPoints() {
        loyaltyPointsLabel.setText("🎁 Loyalty Points: " +
            mainApp.getCurrentStudent().getLoyaltyAccount().getPoints());
    }

    // Add delete student account functionality
    private void deleteStudentAccount() {
        Student currentStudent = mainApp.getCurrentStudent();

        // Show detailed confirmation dialog with account information
        String confirmMessage = String.format(
            "⚠️ WARNING: You are about to permanently delete your account!\n\n" +
            "Account Details:\n" +
            "Name: %s\n" +
            "Student Code: %s\n" +
            "Current Loyalty Points: %d\n\n" +
            "This action will:\n" +
            "• Permanently delete your account\n" +
            "• Remove all your loyalty points\n" +
            "• Delete your order history\n" +
            "• Cannot be undone\n\n" +
            "Are you absolutely sure you want to proceed?",
            currentStudent.getName(),
            currentStudent.getStudentCode(),
            currentStudent.getLoyaltyAccount() != null ? currentStudent.getLoyaltyAccount().getPoints() : 0
        );

        // Create custom confirmation dialog for account deletion
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Account - Final Confirmation");
        confirmDialog.setHeaderText("⚠️ PERMANENT ACCOUNT DELETION");
        confirmDialog.setContentText(confirmMessage);

        // Customize button text
        ButtonType deleteButtonType = new ButtonType("Yes, Delete My Account", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

        // Make the dialog larger to accommodate the text
        confirmDialog.getDialogPane().setPrefWidth(500);
        confirmDialog.getDialogPane().setPrefHeight(400);

        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == deleteButtonType) {
                try {
                    // Delete the student account using StudentManager
                    boolean success = mainApp.getStudentManager().deleteStudent(currentStudent.getId());

                    if (success) {
                        // Show success message
                        mainApp.showSuccessDialog("Account Deleted",
                            String.format("Your account '%s' has been permanently deleted.\n\n" +
                                        "Thank you for using our cafeteria system!\n" +
                                        "You will now be returned to the login screen.",
                                        currentStudent.getName()));

                        // Clear the current student and return to login
                        mainApp.showLoginScreen();
                    } else {
                        mainApp.showErrorDialog("Delete Failed",
                            "Failed to delete your account. This may be due to:\n" +
                            "• Active pending orders\n" +
                            "• System constraints\n" +
                            "• Database connectivity issues\n\n" +
                            "Please contact support if this problem persists.");
                    }
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error",
                        "An error occurred while deleting your account: " + e.getMessage() +
                        "\n\nPlease contact support for assistance.");
                }
            }
        });
    }

    // Style methods
    private String getPrimaryButtonStyle() {
        return "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
    }

    private String getSecondaryButtonStyle() {
        return "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
    }

    private String getSuccessButtonStyle() {
        return "-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
    }

    private String getDangerButtonStyle() {
        return "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
    }

    private String getWarningButtonStyle() {
        return "-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
    }

    private String getBackButtonStyle() {
        return "-fx-background-color: #495057; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;";
    }

    // Inner class for menu item cells
    private class MenuItemCell extends ListCell<Core.MenuItem> {
        @Override
        protected void updateItem(Core.MenuItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            HBox container = new HBox(15);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(10));

            VBox itemInfo = new VBox(5);
            Label nameLabel = new Label(item.getName());
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label descLabel = new Label(item.getDescription());
            descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            descLabel.setTextFill(Color.GRAY);

            Label priceLabel = new Label(item.getPrice().getAmount() + " " + item.getPrice().getCurrency());
            priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            priceLabel.setTextFill(Color.GREEN);

            itemInfo.getChildren().addAll(nameLabel, descLabel, priceLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button addButton = new Button("➕ Add");
            addButton.setStyle(getPrimaryButtonStyle());
            addButton.setOnAction(e -> addToCart(item));

            container.getChildren().addAll(itemInfo, spacer, addButton);
            setGraphic(container);
        }
    }
}
