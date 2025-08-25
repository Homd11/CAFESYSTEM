package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import Core.Student;
import Core.Order;
import Enums.*;
import Values.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * StudentDashboardController - Handles student interface
 * Single Responsibility: Manages student dashboard and ordering
 */
public class StudentDashboardController {

    private Gui mainApp;
    private Map<Integer, Integer> cart; // MenuItem ID -> Quantity
    // Cache menu items by ID for fast lookup and stability
    private final Map<Integer, Core.MenuItem> menuCache = new HashMap<>();

    // Add variables for discount tracking
    private double appliedDiscount = 0.0;
    private int appliedPoints = 0;

    // FXML injected fields
    @FXML private Label welcomeLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private Button logoutButton;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private Button refreshMenuButton;
    @FXML private ListView<Core.MenuItem> menuListView;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartButton;
    @FXML private TableView<CartRow> cartTable;
    @FXML private TableColumn<CartRow, String> cartItemCol;
    @FXML private TableColumn<CartRow, String> cartQtyCol;
    @FXML private TableColumn<CartRow, String> cartUnitCol;
    @FXML private TableColumn<CartRow, String> cartLineTotalCol;
    @FXML private Button removeFromCartButton;
    @FXML private Button clearCartButton;
    @FXML private TextField pointsTextField;
    @FXML private Button applyPointsButton;
    @FXML private Label discountLabel;
    @FXML private ComboBox<PaymentMethod> paymentMethodCombo;
    @FXML private Label totalLabel;
    @FXML private Button placeOrderButton;
    @FXML private TableView<Order> orderHistoryTable;
    @FXML private TableColumn<Order, Integer> orderIdColumn;
    @FXML private TableColumn<Order, String> orderDateColumn;
    @FXML private TableColumn<Order, String> orderItemsColumn;
    @FXML private TableColumn<Order, String> orderTotalColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;

    public StudentDashboardController() {
        this.cart = new HashMap<>();
    }

    // Window control methods
    @FXML
    private void handleMinimize() {
        try {
            if (mainApp != null && mainApp.getPrimaryStage() != null) {
                mainApp.getPrimaryStage().setIconified(true);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Minimize failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleToggleFullScreen() {
        try {
            if (mainApp != null && mainApp.getPrimaryStage() != null) {
                var stage = mainApp.getPrimaryStage();
                stage.setFullScreen(!stage.isFullScreen());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Toggle fullscreen failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        try {
            javafx.application.Platform.exit();
        } catch (Exception e) {
            System.err.println("⚠️ Close failed: " + e.getMessage());
            System.exit(0);
        }
    }

    public Scene createScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/student_dashboard.fxml"));
            loader.setController(this); // Use THIS instance instead of creating new one
            BorderPane root = loader.load();

            // Initialize components after FXML loading
            initializeComponents();
            loadMenuItems();
            updateCartDisplay();
            updateLoyaltyPointsDisplay();

            return new Scene(root, 1000, 700);
        } catch (IOException e) {
            System.err.println("❌ Failed to load student_dashboard.fxml: " + e.getMessage());

            // Create a fallback student dashboard
            return createFallbackStudentDashboard();
        }
    }

    private Scene createFallbackStudentDashboard() {
        System.out.println("🔄 Creating fallback student dashboard...");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Top bar
        HBox topBar = new HBox(20);
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15; -fx-alignment: center-left;");

        Label welcome = new Label("Welcome, " + mainApp.getCurrentStudent().getName() + "!");
        welcome.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Label loyaltyLabel = new Label("Loyalty Points: " + mainApp.getCurrentStudent().getAccount().getPoints());
        loyaltyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15;");
        logoutBtn.setOnAction(event -> mainApp.showLoginScreen());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(welcome, spacer, loyaltyLabel, logoutBtn);

        // Center content
        VBox center = new VBox(20);
        center.setStyle("-fx-padding: 30; -fx-alignment: center;");

        Label menuTitle = new Label("🍽️ ITI Cafeteria Menu");
        menuTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label info = new Label("Menu loading... Please check if FXML files are properly configured.");
        info.setStyle("-fx-font-size: 16; -fx-text-fill: #7f8c8d;");

        Button refreshBtn = new Button("🔄 Refresh Menu");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 14;");
        refreshBtn.setOnAction(event -> {
            try {
                Scene newScene = createScene();
                mainApp.getPrimaryStage().setScene(newScene);
            } catch (Exception ex) {
                System.err.println("Failed to reload: " + ex.getMessage());
            }
        });

        center.getChildren().addAll(menuTitle, info, refreshBtn);

        root.setTop(topBar);
        root.setCenter(center);

        return new Scene(root, 1000, 700);
    }

    public void setMainApp(Gui mainApp) {
        this.mainApp = mainApp;
        // Don't reinitialize cart if it already exists
        if (this.cart == null) {
            this.cart = new HashMap<>();
        }
    }

    public void initAfterLoad(Gui mainApp) {
        this.mainApp = mainApp;
        initializeComponents();
        loadMenuItems();
        updateCartDisplay();
        updateLoyaltyPointsDisplay();
    }

    private void initializeComponents() {
        // Set welcome message
        Student currentStudent = mainApp.getCurrentStudent();
        if (currentStudent != null) {
            welcomeLabel.setText("Welcome, " + currentStudent.getName() + "!");
        }

        // Initialize category combo
        categoryCombo.setItems(FXCollections.observableArrayList(Category.values()));
        // Replace addFirst with add(0, null)
        categoryCombo.getItems().add(0, null); // Add "All Categories" option
        categoryCombo.setValue(null);

        // Initialize payment method combo
        paymentMethodCombo.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        paymentMethodCombo.setValue(PaymentMethod.CASH);

        // Initialize spinners
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        // Setup menu list cell factory
        setupMenuListView();

        // Setup order history table
        setupOrderHistoryTable();

        // Setup cart table columns
        setupCartTable();
    }

    private void setupMenuListView() {
        menuListView.setCellFactory(param -> new MenuItemCell());
    }

    private void setupOrderHistoryTable() {
        // Setup table columns with proper cell value factories
        orderIdColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        orderDateColumn.setCellValueFactory(cellData -> {
            try {
                String dateOnly = cellData.getValue().getOrderDate().toLocalDate().toString();
                String timeOnly = cellData.getValue().getOrderDate().toLocalTime().toString();
                // Safely format time - take up to 8 characters or full string if shorter
                String shortTime = timeOnly.length() > 8 ? timeOnly.substring(0, 8) : timeOnly;
                return new javafx.beans.property.SimpleStringProperty(dateOnly + " " + shortTime);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Invalid Date");
            }
        });

        orderItemsColumn.setCellValueFactory(cellData -> {
            try {
                // Fix: Use Order.OrderItem instead of MenuItem
                List<Order.OrderItem> orderItems = cellData.getValue().getItems();

                if (orderItems.isEmpty()) {
                    return new javafx.beans.property.SimpleStringProperty("No items");
                }

                StringBuilder itemsText = new StringBuilder();
                for (Order.OrderItem item : orderItems) {
                    if (!itemsText.isEmpty()) itemsText.append(", ");
                    itemsText.append(item.getMenuItem().getName());
                    if (item.getQuantity() > 1) {
                        itemsText.append(" x").append(item.getQuantity());
                    }
                }

                return new javafx.beans.property.SimpleStringProperty(itemsText.toString());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error loading items");
            }
        });

        orderTotalColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f EGP", cellData.getValue().total().getAmount().doubleValue())));

        orderStatusColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));

        // Load order history
        loadOrderHistory();
    }

    private void setupCartTable() {
        if (cartTable != null) {
            cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            cartItemCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
            cartQtyCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getQty())));
            cartUnitCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.format("%.2f EGP", data.getValue().getUnitPrice())));
            cartLineTotalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.format("%.2f EGP", data.getValue().getLineTotal())));
            cartTable.setPlaceholder(new Label("Cart is empty"));
        }
    }

    // FXML Event Handlers
    @FXML
    private void handleLogout() {
        mainApp.showLoginScreen();
    }

    @FXML
    private void handleCategoryFilter() {
        Category selectedCategory = categoryCombo.getValue();
        filterMenuItems(selectedCategory);
    }

    @FXML
    private void refreshMenu() {
        loadMenuItems();
    }

    @FXML
    private void handleAddToCart() {
        Core.MenuItem selectedItem = menuListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            mainApp.showErrorDialog("No Selection", "Please select a menu item to add to cart.");
            return;
        }

        int quantity = quantitySpinner.getValue();
        for (int i = 0; i < quantity; i++) {
            addToCart(selectedItem);
        }

        mainApp.showSuccessDialog("Added to Cart",
            String.format("Added %d x %s to your cart!", quantity, selectedItem.getName()));
    }

    @FXML
    private void handleRemoveFromCart() {
        if (cartTable == null) return;
        CartRow row = cartTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            mainApp.showErrorDialog("No Selection", "Please select an item to remove from cart.");
            return;
        }
        cart.remove(row.getItemId());
        updateCartDisplay();
        mainApp.showSuccessDialog("Removed", "Item removed from cart!");
    }

    @FXML
    private void handleClearCart() {
        if (cart.isEmpty()) {
            mainApp.showErrorDialog("Empty Cart", "Your cart is already empty.");
            return;
        }

        clearCart();
        discountLabel.setText("Discount Applied: 0.00 EGP");
        mainApp.showSuccessDialog("Cart Cleared", "All items removed from cart!");
    }

    @FXML
    private void handleApplyPoints() {
        String pointsText = pointsTextField.getText().trim();

        if (pointsText.isEmpty()) {
            mainApp.showErrorDialog("Input Error", "Please enter the number of points to use.");
            return;
        }

        try {
            int pointsToUse = Integer.parseInt(pointsText);
            int availablePoints = mainApp.getCurrentStudent().getAccount().getPoints();

            if (pointsToUse <= 0) {
                mainApp.showErrorDialog("Invalid Points", "Please enter a positive number of points.");
                return;
            }

            // Clamp by available points and subtotal-based maximum
            double subtotal = calculateSubtotal();
            int maxBySubtotal = (int) Math.floor(subtotal / 0.1); // 10 points = 1 EGP -> 0.1 EGP per point
            int maxUsable = Math.max(0, Math.min(availablePoints, maxBySubtotal));

            if (maxUsable == 0) {
                mainApp.showErrorDialog("Not Applicable", "Cart total is too low to apply points.");
                return;
            }

            if (pointsToUse > maxUsable) {
                pointsToUse = maxUsable;
                mainApp.showInfoDialog("Adjusted Points", "Adjusted to max usable points: " + maxUsable);
            }

            // Apply discount (10 points = 1 EGP)
            double discount = pointsToUse * 0.1;
            appliedDiscount = discount;
            appliedPoints = pointsToUse;

            updateCartDisplay();
            discountLabel.setText(String.format("Discount Applied: %.2f EGP", discount));

            mainApp.showSuccessDialog("Points Applied",
                String.format("Applied %d points for %.2f EGP discount!", pointsToUse, discount));

            // Clear the text field after successful application
            pointsTextField.clear();

        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Input", "Please enter a valid number for points.");
        }
    }

    @FXML
    private void handlePlaceOrder() {
        placeOrder();
    }

    private void loadOrderHistory() {
        try {
            Student currentStudent = mainApp.getCurrentStudent();
            if (currentStudent != null) {
                // Fix: Use the newly added getOrderHistory method from OrderProcessor
                List<Order> studentOrders = mainApp.getOrderProcessor().getOrderHistory(currentStudent.getId());
                ObservableList<Order> orders = FXCollections.observableArrayList(studentOrders);
                orderHistoryTable.setItems(orders);

                System.out.println("✅ Order history loaded: " + studentOrders.size() + " orders for student: " + currentStudent.getName());
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load order history: " + e.getMessage());
            // Set empty list as fallback
            orderHistoryTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void updateLoyaltyPointsDisplay() {
        Student currentStudent = mainApp.getCurrentStudent();
        if (currentStudent != null && currentStudent.getAccount() != null) {
            int points = currentStudent.getAccount().getPoints();
            loyaltyPointsLabel.setText("🎁 Loyalty Points: " + points);
        }
    }

    // Helper methods
    private void loadMenuItems() {
        try {
            List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();
            // Update cache
            menuCache.clear();
            for (Core.MenuItem mi : menuItems) {
                menuCache.put(mi.getId(), mi);
            }
            ObservableList<Core.MenuItem> items = FXCollections.observableArrayList(menuItems);
            menuListView.setItems(items);
            System.out.println("Menu loaded: " + items.size() + " items");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    private void filterMenuItems(Category category) {
        try {
            List<Core.MenuItem> allItems = mainApp.getMenuManager().listItems();
            // Refresh cache to be safe
            menuCache.clear();
            for (Core.MenuItem mi : allItems) {
                menuCache.put(mi.getId(), mi);
            }

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
            System.out.println("Filter applied: " + (category == null ? "All" : category) + ", visible items: " + items.size());
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to filter menu items: " + e.getMessage());
        }
    }

    private double calculateTotal() {
        double subtotal = calculateSubtotal();
        double total = subtotal - appliedDiscount;
        if (total < 0) total = 0.0;
        return total;
    }

    private double calculateSubtotal() {
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
            .sum();
    }

    private void updateCartDisplay() {
        if (mainApp == null) {
            System.err.println("updateCartDisplay called before mainApp is set");
            return;
        }

        // Build table rows
        List<CartRow> rows = new ArrayList<>();
        try {
            if (menuCache.isEmpty()) {
                List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();
                for (Core.MenuItem mi : menuItems) menuCache.put(mi.getId(), mi);
            }
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                Core.MenuItem item = menuCache.get(entry.getKey());
                if (item != null) {
                    rows.add(new CartRow(item.getId(), item.getName(), entry.getValue(), item.getPrice().getAmount().doubleValue()));
                } else {
                    rows.add(new CartRow(entry.getKey(), "Item #" + entry.getKey(), entry.getValue(), 0.0));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to build cart table: " + e.getMessage());
        }

        if (cartTable != null) {
            cartTable.setItems(FXCollections.observableArrayList(rows));
        }

        double total = calculateTotal();
        totalLabel.setText(String.format("Total: %.2f EGP", total));
        System.out.println("Cart render -> lines=" + rows.size() + ", total=" + total);
    }

    private void addToCart(Core.MenuItem item) {
        int newQty = cart.getOrDefault(item.getId(), 0) + 1;
        cart.put(item.getId(), newQty);
        System.out.println("Added to cart -> id=" + item.getId() + ", name=" + item.getName() + ", qty=" + newQty);
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

            // Calculate totals
            double subtotal = calculateSubtotal();
            double totalAmount = calculateTotal();

            // Use OrderProcessor to place order with payment and discount applied
            boolean orderSuccess = mainApp.getOrderProcessor().placeOrderWithPayment(
                mainApp.getCurrentStudent(),
                selections,
                paymentMethodCombo.getValue(),
                totalAmount < subtotal ? (subtotal - totalAmount) : 0.0
            );

            if (orderSuccess) {
                // Deduct applied points from account and persist
                if (appliedPoints > 0) {
                    mainApp.getCurrentStudent().getAccount().deduct(appliedPoints);
                    try {
                        mainApp.getStudentManager().updateStudent(mainApp.getCurrentStudent());
                    } catch (Exception ignore) {}
                }

                String successMessage = String.format(
                    "Your order has been placed successfully!%n" +
                    "Subtotal: %.2f EGP%n" +
                    (appliedDiscount > 0 ? String.format("Discount: -%.2f EGP%n", appliedDiscount) : "") +
                    "Total Paid: %.2f EGP%n" +
                    "Payment Method: %s",
                    subtotal, totalAmount, paymentMethodCombo.getValue());

                mainApp.showSuccessDialog("Order Placed!", successMessage);

                // Clear cart and update display
                clearCart();
                updateLoyaltyPoints();
                loadOrderHistory(); // Refresh order history
            } else {
                mainApp.showErrorDialog("Order Failed", "Failed to process your order. Please try again.");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Order Failed", "Failed to place order: " + e.getMessage());
        }
    }

    private void updateLoyaltyPoints() {
        loyaltyPointsLabel.setText("🎁 Loyalty Points: " +
            mainApp.getCurrentStudent().getAccount().getPoints());
    }

    private String getPrimaryButtonStyle() {
        return "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;";
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
            addButton.setOnAction(event -> addToCart(item));

            container.getChildren().addAll(itemInfo, spacer, addButton);
            setGraphic(container);
        }
    }

    // View model for cart table rows
    public static class CartRow {
        private final int itemId;
        private final String name;
        private final int qty;
        private final double unitPrice;

        public CartRow(int itemId, String name, int qty, double unitPrice) {
            this.itemId = itemId;
            this.name = name;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }
        public int getItemId() { return itemId; }
        public String getName() { return name; }
        public int getQty() { return qty; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return unitPrice * qty; }
    }
}
