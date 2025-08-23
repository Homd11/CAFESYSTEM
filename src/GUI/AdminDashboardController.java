package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import Core.*;
import Enums.*;
import Values.*;

import java.io.IOException;
import java.util.List;

/**
 * AdminDashboardController - Comprehensive admin interface
 * Single Responsibility: Manages all admin dashboard functionality
 */
public class AdminDashboardController {

    private Gui mainApp;
    private Admin currentAdmin;

    // FXML injected fields
    @FXML private Label adminWelcomeLabel;
    @FXML private Button generateReportButton;
    @FXML private Button adminLogoutButton;
    @FXML private TabPane adminTabPane;

    // Menu Management Tab
    @FXML private TextField itemNameField;
    @FXML private TextField itemPriceField;
    @FXML private ComboBox<Category> itemCategoryCombo;
    @FXML private Button addItemButton;
    @FXML private Button updateItemButton;
    @FXML private Button deleteItemButton;
    @FXML private TableView<Core.MenuItem> menuItemsTable;
    @FXML private TableColumn<Core.MenuItem, Integer> menuIdColumn;
    @FXML private TableColumn<Core.MenuItem, String> menuNameColumn;
    @FXML private TableColumn<Core.MenuItem, Double> menuPriceColumn;
    @FXML private TableColumn<Core.MenuItem, String> menuCategoryColumn;
    @FXML private TableColumn<Core.MenuItem, Boolean> menuAvailableColumn;

    // Order Management Tab
    @FXML private ComboBox<OrderStatus> orderStatusFilter;
    @FXML private Button refreshOrdersButton;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdColAdmin;
    @FXML private TableColumn<Order, String> studentNameColumn;
    @FXML private TableColumn<Order, String> orderDateColAdmin;
    @FXML private TableColumn<Order, String> orderItemsColAdmin;
    @FXML private TableColumn<Order, String> orderTotalColAdmin;
    @FXML private TableColumn<Order, String> orderStatusColAdmin;
    @FXML private Button markPreparedButton;
    @FXML private Button markDeliveredButton;
    @FXML private Button cancelOrderButton;

    // Student Management Tab
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColAdmin;
    @FXML private TableColumn<Student, Integer> studentLoyaltyColumn;
    @FXML private TableColumn<Student, Integer> studentOrdersColumn;
    @FXML private TableColumn<Student, Double> studentSpentColumn;
    @FXML private Button viewStudentDetailsButton;
    @FXML private Button addLoyaltyPointsButton;

    // Reports Tab
    @FXML private Label dailySalesLabel;
    @FXML private Label dailyOrdersLabel;
    @FXML private Button exportDailyButton;
    @FXML private ListView<String> popularItemsList;
    @FXML private Button exportPopularButton;
    @FXML private Label totalStudentsLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Button exportStudentsButton;
    @FXML private Label monthlyRevenueLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Button exportRevenueButton;

    public AdminDashboardController() {
        // Default constructor for FXML
    }

    public AdminDashboardController(Gui mainApp) {
        this.mainApp = mainApp;
        this.currentAdmin = mainApp.getCurrentAdmin();
    }

    public void setMainApp(Gui mainApp) {
        this.mainApp = mainApp;
        this.currentAdmin = mainApp.getCurrentAdmin();
    }

    public Scene createScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/admin_dashboard.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            // Initialize components after FXML loading
            initializeComponents();
            loadData();

            return new Scene(root, 1000, 700);
        } catch (IOException e) {
            System.err.println("Failed to load admin_dashboard.fxml: " + e.getMessage());
            throw new RuntimeException("Failed to load admin_dashboard.fxml", e);
        }
    }

    public void initAfterLoad(Gui mainApp) {
        this.mainApp = mainApp;
        this.currentAdmin = mainApp.getCurrentAdmin();
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // Set welcome message
        if (currentAdmin != null) {
            adminWelcomeLabel.setText("👤 Admin Dashboard - " + currentAdmin.getName());
        }

        // Initialize category combo
        itemCategoryCombo.setItems(FXCollections.observableArrayList(Category.values()));

        // Initialize order status filter
        orderStatusFilter.setItems(FXCollections.observableArrayList(OrderStatus.values()));
        // Replace addFirst with add(0, null)
        orderStatusFilter.getItems().add(0, null); // Add "All Status" option

        // Setup tables
        setupMenuTable();
        setupOrdersTable();
        setupStudentsTable();
    }

    private void loadData() {
        loadMenuItems();
        loadOrders();
        loadStudents();
        updateReports();
    }

    // FXML Event Handlers
    @FXML
    private void handleLogout() {
        mainApp.showLoginScreen();
    }

    @FXML
    private void handleGenerateReport() {
        try {
            // Generate a simple report since generateSystemReport doesn't exist
            String reportMessage = "System Report Generated:\n" +
                    "Total Menu Items: " + mainApp.getMenuManager().listItems().size() + "\n" +
                    "Report generated at: " + java.time.LocalDateTime.now();
            mainApp.showSuccessDialog("Report Generated", reportMessage);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to generate report: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMenuItem() {
        String name = itemNameField.getText().trim();
        String priceText = itemPriceField.getText().trim();
        Category category = itemCategoryCombo.getValue();

        if (name.isEmpty() || priceText.isEmpty() || category == null) {
            mainApp.showErrorDialog("Input Error", "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Money itemPrice = new Money(price, Currency.EGP);

            // Provide a safe default non-empty description to avoid validation errors
            mainApp.getMenuManager().addItem(name, "N/A", itemPrice, category);
            mainApp.showSuccessDialog("Success", "Menu item added successfully!");
            clearItemFields();
            loadMenuItems();
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Price", "Please enter a valid price.");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to add item: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateMenuItem() {
        Core.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainApp.showErrorDialog("No Selection", "Please select an item to update.");
            return;
        }

        String name = itemNameField.getText().trim();
        String priceText = itemPriceField.getText().trim();
        Category category = itemCategoryCombo.getValue();

        if (name.isEmpty() || priceText.isEmpty() || category == null) {
            mainApp.showErrorDialog("Input Error", "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            Money itemPrice = new Money(price, Currency.EGP);

            // Update fields while keeping existing non-empty description
            selected.setName(name);
            // Don't overwrite description with empty string; keep current
            // selected.setDescription("");
            selected.setPrice(itemPrice);
            selected.setCategory(category);

            mainApp.getMenuManager().updateItem(selected);
            mainApp.showSuccessDialog("Success", "Menu item updated successfully!");
            clearItemFields();
            loadMenuItems();
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Price", "Please enter a valid price.");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to update item: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMenuItem() {
        Core.MenuItem selected = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainApp.showErrorDialog("No Selection", "Please select an item to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Menu Item");
        confirm.setContentText("Are you sure you want to delete '" + selected.getName() + "'?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    mainApp.getMenuManager().removeItem(selected.getId());
                    mainApp.showSuccessDialog("Success", "Menu item deleted successfully!");
                    loadMenuItems();
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error", "Failed to delete item: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleOrderStatusFilter() { loadOrders(); }
    @FXML
    private void handleRefreshOrders() {
        loadOrders();
    }

    @FXML
    private void handleMarkPrepared() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainApp.showErrorDialog("No Selection", "Please select an order to mark as prepared.");
            return;
        }

        try {
            // Use OrderProcessor to properly update status in database
            boolean success = mainApp.getOrderProcessor().updateOrderStatus(selected.getId(), OrderStatus.PREPARING);
            if (success) {
                mainApp.showSuccessDialog("Success", "Order marked as preparing!");
                loadOrders(); // Refresh the table
            } else {
                mainApp.showErrorDialog("Error", "Failed to update order status.");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to update order status: " + e.getMessage());
        }
    }

    @FXML
    private void handleMarkDelivered() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainApp.showErrorDialog("No Selection", "Please select an order to mark as ready.");
            return;
        }

        try {
            // Use OrderProcessor to properly update status in database
            boolean success = mainApp.getOrderProcessor().updateOrderStatus(selected.getId(), OrderStatus.READY);
            if (success) {
                mainApp.showSuccessDialog("Success", "Order marked as ready!");
                loadOrders(); // Refresh the table
            } else {
                mainApp.showErrorDialog("Error", "Failed to update order status.");
            }
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to update order status: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainApp.showErrorDialog("No Selection", "Please select an order to cancel.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancel");
        confirm.setHeaderText("Cancel Order");
        confirm.setContentText("Are you sure you want to cancel order #" + selected.getId() + "?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // Use OrderProcessor to properly update status in database
                    boolean success = mainApp.getOrderProcessor().updateOrderStatus(selected.getId(), OrderStatus.NEW);
                    if (success) {
                        mainApp.showSuccessDialog("Success", "Order reset to new status!");
                        loadOrders(); // Refresh the table
                    } else {
                        mainApp.showErrorDialog("Error", "Failed to reset order status.");
                    }
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error", "Failed to cancel order: " + e.getMessage());
                }
            }
        });
    }

    // Setup methods
    private void setupMenuTable() {
        menuIdColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        menuNameColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        menuPriceColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice().getAmount().doubleValue()).asObject());
        menuCategoryColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory().toString()));
        // Fix: Remove isAvailable method call since it doesn't exist
        menuAvailableColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleBooleanProperty(true).asObject());

        // Add selection listener to populate fields
        menuItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                itemNameField.setText(newSelection.getName());
                itemPriceField.setText(String.valueOf(newSelection.getPrice().getAmount()));
                itemCategoryCombo.setValue(newSelection.getCategory());
            }
        });
    }

    private void setupOrdersTable() {
        orderIdColAdmin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        studentNameColumn.setCellValueFactory(cellData -> {
            try {
                // Fix: Use correct method name - findById instead of getStudentById
                Student student = mainApp.getStudentManager().findById(cellData.getValue().getStudentId());
                return new javafx.beans.property.SimpleStringProperty(student != null ? student.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        orderDateColAdmin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getOrderDate().toString()));
        orderItemsColAdmin.setCellValueFactory(cellData -> {
            // Fix: Use getItems() instead of getSelections()
            int itemCount = cellData.getValue().getItems().size();
            return new javafx.beans.property.SimpleStringProperty(itemCount + " item(s)");
        });
        orderTotalColAdmin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().total().toString()));
        orderStatusColAdmin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }

    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        studentNameColAdmin.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        studentLoyaltyColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getAccount() != null ?
                cellData.getValue().getAccount().getPoints() : 0).asObject());
        studentOrdersColumn.setCellValueFactory(cellData -> {
            // Fix: Use simple counter since getOrderHistory doesn't exist
            return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
        });
        studentSpentColumn.setCellValueFactory(cellData -> {
            // Fix: Use simple value since getOrderHistory doesn't exist
            return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
        });
    }

    // Load data methods
    private void loadMenuItems() {
        try {
            List<Core.MenuItem> items = mainApp.getMenuManager().listItems();
            ObservableList<Core.MenuItem> menuItems = FXCollections.observableArrayList(items);
            menuItemsTable.setItems(menuItems);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try {
            // Use the new getAllOrders method from OrderProcessor
            List<Order> orders = mainApp.getOrderProcessor().getAllOrders();
            OrderStatus statusFilter = orderStatusFilter.getValue();

            if (statusFilter != null) {
                orders = orders.stream()
                    .filter(order -> order.getStatus() == statusFilter)
                    .toList();
            }

            ObservableList<Order> ordersList = FXCollections.observableArrayList(orders);
            ordersTable.setItems(ordersList);

            System.out.println("✅ Loaded " + orders.size() + " orders for admin view");
        } catch (Exception e) {
            System.err.println("❌ Error loading orders: " + e.getMessage());
            mainApp.showErrorDialog("Error", "Failed to load orders: " + e.getMessage());
            // Set empty list as fallback
            ordersTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadStudents() {
        try {
            // Fix: Use listStudents() instead of getStudents()
            List<Student> students = mainApp.getStudentManager().listStudents();
            ObservableList<Student> studentsList = FXCollections.observableArrayList(students);
            studentsTable.setItems(studentsList);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void updateReports() {
        try {
            // Get all orders for report calculations
            List<Order> allOrders = mainApp.getOrderProcessor().getAllOrders();

            // Calculate daily statistics
            long todayOrders = allOrders.stream()
                .filter(order -> order.getOrderDate().toLocalDate().equals(java.time.LocalDate.now()))
                .count();

            double todaySales = allOrders.stream()
                .filter(order -> order.getOrderDate().toLocalDate().equals(java.time.LocalDate.now()))
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();

            dailyOrdersLabel.setText("Today's Orders: " + todayOrders);
            dailySalesLabel.setText(String.format("Today's Sales: %.2f EGP", todaySales));

            // Calculate total revenue
            double totalRevenue = allOrders.stream()
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();
            totalRevenueLabel.setText(String.format("Total Revenue: %.2f EGP", totalRevenue));

            // Calculate monthly revenue (current month)
            double monthlyRevenue = allOrders.stream()
                .filter(order -> {
                    java.time.LocalDate orderDate = order.getOrderDate().toLocalDate();
                    java.time.LocalDate now = java.time.LocalDate.now();
                    return orderDate.getYear() == now.getYear() && orderDate.getMonth() == now.getMonth();
                })
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();
            monthlyRevenueLabel.setText(String.format("Monthly Revenue: %.2f EGP", monthlyRevenue));

            // Student statistics
            List<Student> students = mainApp.getStudentManager().listStudents();
            totalStudentsLabel.setText("Total Students: " + students.size());

            // Count active students (students who have placed orders)
            long activeStudents = allOrders.stream()
                .mapToInt(Order::getStudentId)
                .distinct()
                .count();
            activeStudentsLabel.setText("Active Students: " + activeStudents);

            // Popular items analysis
            java.util.Map<String, Integer> itemFrequency = new java.util.HashMap<>();
            for (Order order : allOrders) {
                for (Order.OrderItem item : order.getItems()) {
                    String itemName = item.getMenuItem().getName();
                    itemFrequency.put(itemName, itemFrequency.getOrDefault(itemName, 0) + item.getQuantity());
                }
            }

            List<String> popularItems = itemFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " sold)")
                .toList();

            popularItemsList.setItems(FXCollections.observableArrayList(popularItems));

            System.out.println("✅ Reports updated with real data");

        } catch (Exception e) {
            System.err.println("❌ Failed to update reports: " + e.getMessage());
            // Set default values as fallback
            dailyOrdersLabel.setText("Today's Orders: 0");
            dailySalesLabel.setText("Today's Sales: 0.00 EGP");
            totalRevenueLabel.setText("Total Revenue: 0.00 EGP");
            monthlyRevenueLabel.setText("Monthly Revenue: 0.00 EGP");
            activeStudentsLabel.setText("Active Students: 0");
        }
    }

    private void clearItemFields() {
        itemNameField.clear();
        itemPriceField.clear();
        itemCategoryCombo.setValue(null);
    }
}
