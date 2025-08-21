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

import Core.*;
import Enums.*;
import Values.*;
import Services.*;
import DB.*;

import java.util.List;

/**
 * AdminDashboardController - Comprehensive admin interface
 * Single Responsibility: Manages all admin dashboard functionality
 */
public class AdminDashboardController {

    private final Gui mainApp;
    private final Admin currentAdmin;
    private TabPane mainTabPane;

    // Menu Management Components
    private ListView<Core.MenuItem> adminMenuListView;
    private TextField itemNameField;
    private TextArea itemDescField;
    private TextField itemPriceField;
    private ComboBox<Category> categoryCombo;
    private Core.MenuItem selectedItem;

    // Student Management Components
    private ListView<Student> studentsListView;
    private Label totalStudentsLabel;

    // Order Management Components
    private ListView<Order> ordersListView;
    private Label totalOrdersLabel;
    private Label totalRevenueLabel;

    // Analytics Components
    private Label analyticsDataLabel;

    public AdminDashboardController(Gui mainApp) {
        this.mainApp = mainApp;
        this.currentAdmin = mainApp.getCurrentAdmin();
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Top: Header
        VBox header = createHeader();
        root.setTop(header);

        // Center: Main content with tabs
        mainTabPane = createMainTabPane();
        root.setCenter(mainTabPane);

        return new Scene(root, 1200, 800);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #dc3545;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Login");
        backButton.setStyle(getBackButtonStyle());
        backButton.setOnAction(e -> mainApp.showLoginScreen());

        Label welcomeLabel = new Label("👨‍💼 Admin Dashboard - Welcome " + currentAdmin.getName());
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setStyle(getSecondaryButtonStyle());
        logoutButton.setOnAction(e -> mainApp.showLoginScreen());

        topRow.getChildren().addAll(backButton, welcomeLabel, spacer, logoutButton);

        Label subtitleLabel = new Label("Comprehensive System Administration");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(topRow, subtitleLabel);
        return header;
    }

    private TabPane createMainTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Dashboard Overview Tab
        Tab overviewTab = createOverviewTab();

        // Menu Management Tab
        Tab menuTab = createMenuManagementTab();

        // Student Management Tab
        Tab studentsTab = createStudentManagementTab();

        // Order Management Tab
        Tab ordersTab = createOrderManagementTab();

        // Analytics Tab
        Tab analyticsTab = createAnalyticsTab();

        // System Settings Tab
        Tab settingsTab = createSystemSettingsTab();

        tabPane.getTabs().addAll(overviewTab, menuTab, studentsTab, ordersTab, analyticsTab, settingsTab);
        return tabPane;
    }

    private Tab createOverviewTab() {
        Tab tab = new Tab("📊 Dashboard Overview");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("System Overview");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Quick stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        // Load real data for stats cards
        int studentCount = getRealStudentCount();
        int menuItemCount = getRealMenuItemCount();
        int orderCount = getRealOrderCount();
        double totalRevenue = getRealTotalRevenue();

        // Student stats card
        VBox studentCard = createStatsCard("👥 Students", String.valueOf(studentCount), "Total Registered", "#007bff");

        // Menu items stats card
        VBox menuCard = createStatsCard("🍽️ Menu Items", String.valueOf(menuItemCount), "Available Items", "#28a745");

        // Orders stats card
        VBox ordersCard = createStatsCard("📦 Orders", String.valueOf(orderCount), "Total Orders", "#ffc107");

        // Revenue stats card
        VBox revenueCard = createStatsCard("💰 Revenue", String.format("%.2f EGP", totalRevenue), "Total Revenue", "#dc3545");

        statsGrid.add(studentCard, 0, 0);
        statsGrid.add(menuCard, 1, 0);
        statsGrid.add(ordersCard, 0, 1);
        statsGrid.add(revenueCard, 1, 1);

        // Quick actions
        Label actionsLabel = new Label("Quick Actions");
        actionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox quickActions = new HBox(15);

        Button addMenuButton = new Button("➕ Add Menu Item");
        addMenuButton.setStyle(getPrimaryButtonStyle());
        addMenuButton.setOnAction(e -> mainTabPane.getSelectionModel().select(1));

        Button viewStudentsButton = new Button("👥 View Students");
        viewStudentsButton.setStyle(getSuccessButtonStyle());
        viewStudentsButton.setOnAction(e -> mainTabPane.getSelectionModel().select(2));

        Button viewOrdersButton = new Button("📦 View Orders");
        viewOrdersButton.setStyle(getWarningButtonStyle());
        viewOrdersButton.setOnAction(e -> mainTabPane.getSelectionModel().select(3));

        Button refreshButton = new Button("🔄 Refresh Data");
        refreshButton.setStyle(getInfoButtonStyle());
        refreshButton.setOnAction(e -> refreshDashboard());

        quickActions.getChildren().addAll(addMenuButton, viewStudentsButton, viewOrdersButton, refreshButton);

        content.getChildren().addAll(titleLabel, statsGrid, actionsLabel, quickActions);

        tab.setContent(new ScrollPane(content));
        return tab;
    }

    // Methods to get real data counts
    private int getRealStudentCount() {
        try {
            List<Student> students = mainApp.getStudentManager().listStudents();
            return students.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getRealMenuItemCount() {
        try {
            List<Core.MenuItem> items = mainApp.getMenuManager().listItems();
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getRealOrderCount() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();
            return orders.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private double getRealTotalRevenue() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();
            return orders.stream()
                .filter(order -> order.total() != null)
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Add the missing createStatsCard method with fixed CSS
    private VBox createStatsCard(String title, String value, String subtitle, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(280);
        card.setPrefHeight(120);

        // Use safer CSS styling to avoid JavaFX warnings
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 10; " +
                     "-fx-border-color: " + color + "; " +
                     "-fx-border-radius: 10; " +
                     "-fx-border-width: 2;");

        // Title with icon
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(color));

        // Main value
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web("#1e3a8a")); // Use web color instead of Color.DARKBLUE

        // Subtitle
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        subtitleLabel.setTextFill(Color.web("#6b7280")); // Use web color instead of Color.GRAY

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    private void refreshDashboard() {
        // Refresh the overview tab by recreating it
        mainTabPane.getTabs().set(0, createOverviewTab());
        mainTabPane.getSelectionModel().select(0);
    }

    private Tab createMenuManagementTab() {
        Tab tab = new Tab("🍽️ Menu Management");

        HBox content = new HBox(20);
        content.setPadding(new Insets(20));

        // Left: Menu items list
        VBox menuListSection = createMenuListSection();

        // Right: Add/Edit form
        VBox formSection = createMenuFormSection();

        content.getChildren().addAll(menuListSection, formSection);
        HBox.setHgrow(menuListSection, Priority.ALWAYS);

        tab.setContent(content);
        return tab;
    }

    private Tab createStudentManagementTab() {
        Tab tab = new Tab("👥 Student Management");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Student Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Stats bar
        HBox statsBar = new HBox(20);
        totalStudentsLabel = new Label("Total Students: 0");
        totalStudentsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button refreshStudentsButton = new Button("🔄 Refresh");
        refreshStudentsButton.setStyle(getPrimaryButtonStyle());
        refreshStudentsButton.setOnAction(e -> loadStudents());

        statsBar.getChildren().addAll(totalStudentsLabel, refreshStudentsButton);

        // Students list
        studentsListView = new ListView<>();
        studentsListView.setPrefHeight(400);
        studentsListView.setCellFactory(listView -> new StudentListCell());

        // Student actions
        HBox studentActions = new HBox(15);

        Button viewStudentButton = new Button("👁️ View Details");
        viewStudentButton.setStyle(getInfoButtonStyle());
        viewStudentButton.setOnAction(e -> viewSelectedStudent());

        Button addLoyaltyPointsButton = new Button("⭐ Add Loyalty Points");
        addLoyaltyPointsButton.setStyle(getSuccessButtonStyle());
        addLoyaltyPointsButton.setOnAction(e -> addLoyaltyPoints());

        Button deleteStudentButton = new Button("🗑️ Delete Student");
        deleteStudentButton.setStyle(getDangerButtonStyle());
        deleteStudentButton.setOnAction(e -> deleteSelectedStudent());

        studentActions.getChildren().addAll(viewStudentButton, addLoyaltyPointsButton, deleteStudentButton);

        content.getChildren().addAll(titleLabel, statsBar, studentsListView, studentActions);

        // Load initial data
        loadStudents();

        tab.setContent(new ScrollPane(content));
        return tab;
    }

    private Tab createOrderManagementTab() {
        Tab tab = new Tab("📦 Order Management");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Order Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Stats bar
        HBox statsBar = new HBox(20);
        totalOrdersLabel = new Label("Total Orders: 0");
        totalOrdersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        totalRevenueLabel = new Label("Total Revenue: 0 EGP");
        totalRevenueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Button refreshOrdersButton = new Button("🔄 Refresh");
        refreshOrdersButton.setStyle(getPrimaryButtonStyle());
        refreshOrdersButton.setOnAction(e -> loadOrders());

        statsBar.getChildren().addAll(totalOrdersLabel, totalRevenueLabel, refreshOrdersButton);

        // Orders list
        ordersListView = new ListView<>();
        ordersListView.setPrefHeight(400);
        ordersListView.setCellFactory(listView -> new OrderListCell());

        // Order actions
        HBox orderActions = new HBox(15);

        Button viewOrderButton = new Button("👁️ View Order Details");
        viewOrderButton.setStyle(getInfoButtonStyle());
        viewOrderButton.setOnAction(e -> viewSelectedOrder());

        Button editStatusButton = new Button("📝 Edit Order Status");
        editStatusButton.setStyle(getWarningButtonStyle());
        editStatusButton.setOnAction(e -> editOrderStatus());

        orderActions.getChildren().addAll(viewOrderButton, editStatusButton);

        content.getChildren().addAll(titleLabel, statsBar, ordersListView, orderActions);

        // Load initial data
        loadOrders();

        tab.setContent(new ScrollPane(content));
        return tab;
    }

    private Tab createAnalyticsTab() {
        Tab tab = new Tab("📈 Analytics");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("System Analytics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Analytics grid
        GridPane analyticsGrid = new GridPane();
        analyticsGrid.setHgap(20);
        analyticsGrid.setVgap(20);

        // Popular items
        VBox popularItemsCard = new VBox(15);
        popularItemsCard.setPadding(new Insets(20));
        popularItemsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #007bff; -fx-border-radius: 10;");

        Label popularTitle = new Label("🏆 Most Popular Items");
        popularTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        analyticsDataLabel = new Label("Loading analytics data...");
        analyticsDataLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        popularItemsCard.getChildren().addAll(popularTitle, analyticsDataLabel);

        // Revenue by category
        VBox revenueCard = new VBox(15);
        revenueCard.setPadding(new Insets(20));
        revenueCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #28a745; -fx-border-radius: 10;");

        Label revenueTitle = new Label("💰 Revenue by Category");
        revenueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label revenueData = new Label("Main Course: 0 EGP\nDrinks: 0 EGP\nSnacks: 0 EGP");
        revenueData.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        revenueCard.getChildren().addAll(revenueTitle, revenueData);

        analyticsGrid.add(popularItemsCard, 0, 0);
        analyticsGrid.add(revenueCard, 1, 0);

        // Refresh button
        Button refreshAnalyticsButton = new Button("🔄 Refresh Analytics");
        refreshAnalyticsButton.setStyle(getPrimaryButtonStyle());
        refreshAnalyticsButton.setOnAction(e -> loadAnalytics());

        content.getChildren().addAll(titleLabel, analyticsGrid, refreshAnalyticsButton);

        // Load initial data
        loadAnalytics();

        tab.setContent(new ScrollPane(content));
        return tab;
    }

    private Tab createSystemSettingsTab() {
        Tab tab = new Tab("⚙️ System Settings");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("System Administration");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // System info
        VBox systemInfoBox = new VBox(15);
        systemInfoBox.setPadding(new Insets(20));
        systemInfoBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #6c757d; -fx-border-radius: 10;");

        Label systemInfoTitle = new Label("ℹ️ System Information");
        systemInfoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label adminInfo = new Label("Current Admin: " + currentAdmin.getName() + " (" + currentAdmin.getRole() + ")");
        adminInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        Label systemStatus = new Label("System Status: Online");
        systemStatus.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        systemStatus.setTextFill(Color.GREEN);

        systemInfoBox.getChildren().addAll(systemInfoTitle, adminInfo, systemStatus);

        // Admin actions
        VBox adminActionsBox = new VBox(15);
        adminActionsBox.setPadding(new Insets(20));
        adminActionsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #dc3545; -fx-border-radius: 10;");

        Label actionsTitle = new Label("🔧 Administrative Actions");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox actionButtons = new HBox(15);

        Button createAdminButton = new Button("👤 Create New Admin");
        createAdminButton.setStyle(getWarningButtonStyle());
        createAdminButton.setOnAction(e -> createNewAdmin());

        Button backupDataButton = new Button("💾 Backup System Data");
        backupDataButton.setStyle(getInfoButtonStyle());
        backupDataButton.setOnAction(e -> backupSystemData());

        Button systemReportButton = new Button("📋 Generate System Report");
        systemReportButton.setStyle(getSuccessButtonStyle());
        systemReportButton.setOnAction(e -> generateSystemReport());

        actionButtons.getChildren().addAll(createAdminButton, backupDataButton, systemReportButton);

        adminActionsBox.getChildren().addAll(actionsTitle, actionButtons);

        content.getChildren().addAll(titleLabel, systemInfoBox, adminActionsBox);

        tab.setContent(new ScrollPane(content));
        return tab;
    }

    // Menu Management methods
    private VBox createMenuListSection() {
        VBox menuSection = new VBox(15);
        menuSection.setPrefWidth(600);

        Label menuTitle = new Label("🍽️ Current Menu Items");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        menuTitle.setTextFill(Color.DARKBLUE);

        // Menu list
        adminMenuListView = new ListView<>();
        adminMenuListView.setPrefHeight(500);
        adminMenuListView.setCellFactory(listView -> new AdminMenuItemCell());
        adminMenuListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadItemForEdit(newValue);
                }
            });

        loadMenuItems();

        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle(getPrimaryButtonStyle());
        refreshButton.setOnAction(e -> loadMenuItems());

        Button clearSelectionButton = new Button("🗑️ Clear Selection");
        clearSelectionButton.setStyle(getWarningButtonStyle());
        clearSelectionButton.setOnAction(e -> clearForm());

        actionButtons.getChildren().addAll(refreshButton, clearSelectionButton);

        menuSection.getChildren().addAll(menuTitle, adminMenuListView, actionButtons);
        return menuSection;
    }

    private VBox createMenuFormSection() {
        VBox formSection = new VBox(20);
        formSection.setPrefWidth(380);
        formSection.setPadding(new Insets(20));
        formSection.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

        Label formTitle = new Label("➕ Add/Edit Menu Item");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.DARKGREEN);

        // Form fields
        VBox formFields = new VBox(15);

        // Name field
        VBox nameBox = new VBox(5);
        Label nameLabel = new Label("Item Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        itemNameField = new TextField();
        itemNameField.setPromptText("Enter item name");
        itemNameField.setStyle(getTextFieldStyle());
        nameBox.getChildren().addAll(nameLabel, itemNameField);

        // Description field
        VBox descBox = new VBox(5);
        Label descLabel = new Label("Description:");
        descLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        itemDescField = new TextArea();
        itemDescField.setPromptText("Enter item description");
        itemDescField.setPrefRowCount(3);
        itemDescField.setStyle(getTextFieldStyle());
        descBox.getChildren().addAll(descLabel, itemDescField);

        // Price field
        VBox priceBox = new VBox(5);
        Label priceLabel = new Label("Price (EGP):");
        priceLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        itemPriceField = new TextField();
        itemPriceField.setPromptText("Enter price (e.g., 25.50)");
        itemPriceField.setStyle(getTextFieldStyle());
        priceBox.getChildren().addAll(priceLabel, itemPriceField);

        // Category field
        VBox categoryBox = new VBox(5);
        Label categoryLabel = new Label("Category:");
        categoryLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(Category.values());
        categoryCombo.setValue(Category.MAIN_COURSE);
        categoryCombo.setPrefWidth(Double.MAX_VALUE);
        categoryBox.getChildren().addAll(categoryLabel, categoryCombo);

        formFields.getChildren().addAll(nameBox, descBox, priceBox, categoryBox);

        // Action buttons
        HBox formButtons = new HBox(10);
        formButtons.setAlignment(Pos.CENTER);

        Button addButton = new Button("➕ Add Item");
        addButton.setStyle(getSuccessButtonStyle());
        addButton.setPrefWidth(120);
        addButton.setOnAction(e -> addMenuItem());

        Button updateButton = new Button("✏️ Update Item");
        updateButton.setStyle(getPrimaryButtonStyle());
        updateButton.setPrefWidth(120);
        updateButton.setOnAction(e -> updateMenuItem());

        Button deleteButton = new Button("🗑️ Delete Item");
        deleteButton.setStyle(getDangerButtonStyle());
        deleteButton.setPrefWidth(120);
        deleteButton.setOnAction(e -> deleteMenuItem());

        formButtons.getChildren().addAll(addButton, updateButton, deleteButton);

        formSection.getChildren().addAll(formTitle, formFields, formButtons);
        return formSection;
    }

    private void loadMenuItems() {
        try {
            List<Core.MenuItem> items = mainApp.getMenuManager().listItems(); // Use Core.MenuItem explicitly
            ObservableList<Core.MenuItem> menuItems = FXCollections.observableArrayList(items);
            adminMenuListView.setItems(menuItems);
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    private void loadItemForEdit(Core.MenuItem item) {
        selectedItem = item;
        itemNameField.setText(item.getName());
        itemDescField.setText(item.getDescription());
        itemPriceField.setText(String.valueOf(item.getPrice().getAmount()));
        categoryCombo.setValue(item.getCategory());
    }

    private void clearForm() {
        selectedItem = null;
        itemNameField.clear();
        itemDescField.clear();
        itemPriceField.clear();
        categoryCombo.setValue(Category.MAIN_COURSE);
        adminMenuListView.getSelectionModel().clearSelection();
    }

    private void addMenuItem() {
        if (!validateForm()) return;

        try {
            String name = itemNameField.getText().trim();
            String description = itemDescField.getText().trim();
            double price = Double.parseDouble(itemPriceField.getText().trim());
            Category category = categoryCombo.getValue();

            // Use existing MenuManager.addItem method
            mainApp.getMenuManager().addItem(name, description, new Money(price, Currency.EGP), category);

            mainApp.showSuccessDialog("Success", "Menu item added successfully!");
            clearForm();
            loadMenuItems();
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Input", "Please enter a valid price");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Error adding menu item: " + e.getMessage());
        }
    }

    private void updateMenuItem() {
        if (selectedItem == null) {
            mainApp.showErrorDialog("No Selection", "Please select an item to update");
            return;
        }

        if (!validateForm()) return;

        try {
            String name = itemNameField.getText().trim();
            String description = itemDescField.getText().trim();
            double price = Double.parseDouble(itemPriceField.getText().trim());
            Category category = categoryCombo.getValue();

            // Use existing MenuManager.editItem method (not updateItem)
            mainApp.getMenuManager().editItem(selectedItem.getId(), name, description,
                new Money(price, Currency.EGP), category);

            mainApp.showSuccessDialog("Success", "Menu item updated successfully!");
            loadMenuItems();
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Input", "Please enter a valid price");
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Error updating menu item: " + e.getMessage());
        }
    }

    private void deleteMenuItem() {
        if (selectedItem == null) {
            mainApp.showErrorDialog("No Selection", "Please select an item to delete");
            return;
        }

        if (mainApp.showConfirmDialog("Confirm Delete",
            "Are you sure you want to delete '" + selectedItem.getName() + "'?")) {
            try {
                // Use existing MenuManager.removeItem method
                mainApp.getMenuManager().removeItem(selectedItem.getId());

                mainApp.showSuccessDialog("Success", "Menu item deleted successfully!");
                clearForm();
                loadMenuItems();
            } catch (Exception e) {
                mainApp.showErrorDialog("Error", "Error deleting menu item: " + e.getMessage());
            }
        }
    }

    private boolean validateForm() {
        if (itemNameField.getText().trim().isEmpty()) {
            mainApp.showErrorDialog("Invalid Input", "Please enter item name");
            return false;
        }

        if (itemDescField.getText().trim().isEmpty()) {
            mainApp.showErrorDialog("Invalid Input", "Please enter item description");
            return false;
        }

        try {
            double price = Double.parseDouble(itemPriceField.getText().trim());
            if (price <= 0) {
                mainApp.showErrorDialog("Invalid Input", "Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            mainApp.showErrorDialog("Invalid Input", "Please enter a valid price");
            return false;
        }

        if (categoryCombo.getValue() == null) {
            mainApp.showErrorDialog("Invalid Input", "Please select a category");
            return false;
        }

        return true;
    }

    // Custom cell for admin menu items
    private class AdminMenuItemCell extends ListCell<Core.MenuItem> {
        @Override
        protected void updateItem(Core.MenuItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                VBox cell = new VBox(8);
                cell.setPadding(new Insets(10));
                cell.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #eee; -fx-border-radius: 5;");

                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(item.getName());
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                nameLabel.setTextFill(Color.DARKBLUE);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label priceLabel = new Label(String.format("%.2f %s",
                    item.getPrice().getAmount(), item.getPrice().getCurrency()));
                priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                priceLabel.setTextFill(Color.DARKGREEN);

                Label categoryBadge = new Label(item.getCategory().toString());
                categoryBadge.setFont(Font.font("Arial", FontWeight.MEDIUM, 10));
                categoryBadge.setTextFill(Color.WHITE);
                categoryBadge.setStyle("-fx-background-color: #6c757d; -fx-background-radius: 10; -fx-padding: 2 8;");

                topRow.getChildren().addAll(nameLabel, spacer, priceLabel, categoryBadge);

                Label descLabel = new Label(item.getDescription());
                descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                descLabel.setTextFill(Color.GRAY);
                descLabel.setWrapText(true);

                Label idLabel = new Label("ID: " + item.getId());
                idLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                idLabel.setTextFill(Color.LIGHTGRAY);

                cell.getChildren().addAll(topRow, descLabel, idLabel);
                setGraphic(cell);
            }
        }
    }

    // Custom cell classes
    private class StudentListCell extends ListCell<Student> {
        @Override
        protected void updateItem(Student student, boolean empty) {
            super.updateItem(student, empty);

            if (empty || student == null) {
                setGraphic(null);
            } else {
                VBox cell = new VBox(8);
                cell.setPadding(new Insets(10));
                cell.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #eee; -fx-border-radius: 5;");

                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(student.getName());
                nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                nameLabel.setTextFill(Color.DARKBLUE);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label codeLabel = new Label("Code: " + student.getStudentCode());
                codeLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 12));
                codeLabel.setTextFill(Color.DARKGREEN);

                topRow.getChildren().addAll(nameLabel, spacer, codeLabel);

                Label pointsLabel = new Label("Loyalty Points: " +
                    (student.getAccount() != null ? student.getAccount().balance() : 0));
                pointsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                pointsLabel.setTextFill(Color.GRAY);

                Label idLabel = new Label("ID: " + student.getId());
                idLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                idLabel.setTextFill(Color.LIGHTGRAY);

                cell.getChildren().addAll(topRow, pointsLabel, idLabel);
                setGraphic(cell);
            }
        }
    }

    private class OrderListCell extends ListCell<Order> {
        @Override
        protected void updateItem(Order order, boolean empty) {
            super.updateItem(order, empty);

            if (empty || order == null) {
                setGraphic(null);
            } else {
                VBox cell = new VBox(8);
                cell.setPadding(new Insets(10));
                cell.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #eee; -fx-border-radius: 5;");

                HBox topRow = new HBox(10);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label orderLabel = new Label("Order #" + order.getId());
                orderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                orderLabel.setTextFill(Color.DARKBLUE);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Use total() instead of getTotalPrice()
                Label priceLabel = new Label(order.total() != null ?
                    String.format("%.2f %s", order.total().getAmount(), order.total().getCurrency()) : "0.00 EGP");
                priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                priceLabel.setTextFill(Color.DARKGREEN);

                Label statusBadge = new Label(order.getStatus().toString());
                statusBadge.setFont(Font.font("Arial", FontWeight.MEDIUM, 10));
                statusBadge.setTextFill(Color.WHITE);
                statusBadge.setStyle("-fx-background-color: #28a745; -fx-background-radius: 10; -fx-padding: 2 8;");

                topRow.getChildren().addAll(orderLabel, spacer, priceLabel, statusBadge);

                // Use a placeholder for student name since getStudent() returns null
                Label studentLabel = new Label("Student ID: " + order.getStudentId());
                studentLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
                studentLabel.setTextFill(Color.GRAY);

                // Use getOrderDate() instead of getCreatedAt()
                Label dateLabel = new Label("Date: " + order.getOrderDate());
                dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                dateLabel.setTextFill(Color.LIGHTGRAY);

                cell.getChildren().addAll(topRow, studentLabel, dateLabel);
                setGraphic(cell);
            }
        }
    }

    private void loadStudents() {
        try {
            List<Student> students = mainApp.getStudentManager().listStudents();
            ObservableList<Student> studentItems = FXCollections.observableArrayList(students);
            studentsListView.setItems(studentItems);

            // Update total students label
            totalStudentsLabel.setText("Total Students: " + students.size());
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void viewSelectedStudent() {
        Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            mainApp.showStudentDetails(selectedStudent);
        }
    }

    private void addLoyaltyPoints() {
        Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            // Open dialog to enter points
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Loyalty Points");
            dialog.setHeaderText("Enter loyalty points to add");
            dialog.setContentText("Points:");

            // Convert current points to string
            dialog.getEditor().setText(String.valueOf(selectedStudent.getAccount().balance()));

            dialog.showAndWait().ifPresent(pointsStr -> {
                try {
                    int points = Integer.parseInt(pointsStr);
                    if (points < 0) {
                        mainApp.showErrorDialog("Invalid Input", "Points cannot be negative");
                        return;
                    }

                    // Update student loyalty points
                    selectedStudent.getAccount().setPoints(points);
                    mainApp.getStudentManager().updateStudent(selectedStudent);

                    mainApp.showSuccessDialog("Success", "Loyalty points updated successfully!");
                    loadStudents();
                } catch (NumberFormatException e) {
                    mainApp.showErrorDialog("Invalid Input", "Please enter a valid number of points");
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error", "Failed to update loyalty points: " + e.getMessage());
                }
            });
        }
    }

    // Add delete student functionality
    private void deleteSelectedStudent() {
        Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            mainApp.showErrorDialog("No Selection", "Please select a student to delete");
            return;
        }

        // Show confirmation dialog with student details
        String confirmMessage = String.format(
            "Are you sure you want to delete this student?\n\n" +
            "Name: %s\n" +
            "Student Code: %s\n" +
            "Loyalty Points: %d\n\n" +
            "This action cannot be undone!",
            selectedStudent.getName(),
            selectedStudent.getStudentCode(),
            selectedStudent.getAccount() != null ? selectedStudent.getAccount().balance() : 0
        );

        if (mainApp.showConfirmDialog("Confirm Delete Student", confirmMessage)) {
            try {
                // Delete the student using StudentManager
                boolean success = mainApp.getStudentManager().deleteStudent(selectedStudent.getId());

                if (success) {
                    mainApp.showSuccessDialog("Success",
                        String.format("Student '%s' has been deleted successfully!", selectedStudent.getName()));

                    // Refresh the student list and dashboard stats
                    loadStudents();
                    refreshDashboard();
                } else {
                    mainApp.showErrorDialog("Delete Failed",
                        "Failed to delete student. The student may have active orders or dependencies.");
                }
            } catch (Exception e) {
                mainApp.showErrorDialog("Error", "Error deleting student: " + e.getMessage());
            }
        }
    }

    // Order Management methods
    private void loadOrders() {
        try {
            // Use OrderDAO directly since OrderProcessor doesn't have listOrders()
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();
            ObservableList<Order> orderItems = FXCollections.observableArrayList(orders);
            ordersListView.setItems(orderItems);

            // Update total orders and revenue labels
            totalOrdersLabel.setText("Total Orders: " + orders.size());
            double totalRevenue = orders.stream()
                .filter(order -> order.total() != null)
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();
            totalRevenueLabel.setText(String.format("Total Revenue: %.2f EGP", totalRevenue));
        } catch (Exception e) {
            mainApp.showErrorDialog("Error", "Failed to load orders: " + e.getMessage());
        }
    }

    private void viewSelectedOrder() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            mainApp.showOrderDetails(selectedOrder);
        }
    }

    private void editOrderStatus() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            mainApp.showErrorDialog("No Selection", "Please select an order to edit");
            return;
        }

        // Create a custom dialog with ComboBox for status selection
        Dialog<OrderStatus> dialog = new Dialog<>();
        dialog.setTitle("Edit Order Status");
        dialog.setHeaderText("Update the status of Order #" + selectedOrder.getId() +
                           "\nCurrent Status: " + selectedOrder.getStatus());

        // Create form content
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label statusLabel = new Label("New Status:");
        statusLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

        ComboBox<OrderStatus> statusCombo = new ComboBox<>(FXCollections.observableArrayList(OrderStatus.values()));
        statusCombo.setValue(selectedOrder.getStatus());
        statusCombo.setPrefWidth(200);

        // Add visual styling to status options
        statusCombo.setCellFactory(param -> new ListCell<OrderStatus>() {
            @Override
            protected void updateItem(OrderStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());
                    switch (status) {
                        case NEW:
                            setStyle("-fx-text-fill: #dc3545;"); // Red
                            break;
                        case PREPARING:
                            setStyle("-fx-text-fill: #ffc107;"); // Yellow
                            break;
                        case READY:
                            setStyle("-fx-text-fill: #28a745;"); // Green
                            break;
                    }
                }
            }
        });

        grid.add(statusLabel, 0, 0);
        grid.add(statusCombo, 1, 0);

        // Add order details for context
        Label orderInfoLabel = new Label("Order Details:");
        orderInfoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        String orderDetails = String.format("Student ID: %d\nDate: %s\nTotal: %s",
            selectedOrder.getStudentId(),
            selectedOrder.getOrderDate(),
            selectedOrder.total() != null ?
                String.format("%.2f %s", selectedOrder.total().getAmount(), selectedOrder.total().getCurrency()) :
                "0.00 EGP"
        );

        Label orderDetailsLabel = new Label(orderDetails);
        orderDetailsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        orderDetailsLabel.setTextFill(Color.GRAY);

        grid.add(orderInfoLabel, 0, 1);
        grid.add(orderDetailsLabel, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType updateButtonType = new ButtonType("Update Status", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(newStatus -> {
            if (newStatus != selectedOrder.getStatus()) {
                try {
                    OrderStatus oldStatus = selectedOrder.getStatus();
                    selectedOrder.setStatus(newStatus);

                    // Update order status in database
                    OrderDAO orderDAO = new OrderDAO();
                    orderDAO.update(selectedOrder);

                    mainApp.showSuccessDialog("Success",
                        String.format("Order #%d status updated from %s to %s!",
                            selectedOrder.getId(),
                            oldStatus.toString(),
                            newStatus.toString()));

                    loadOrders(); // Refresh the orders list
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error", "Failed to update order status: " + e.getMessage());
                }
            } else {
                mainApp.showInfoDialog("No Changes", "Order status was not changed.");
            }
        });
    }

    // Analytics methods
    private void loadAnalytics() {
        // For now, just simulate loading analytics data
        analyticsDataLabel.setText("Analytics data loaded successfully!");
    }

    // System Settings methods - Replace fake methods with real functionality
    private void createNewAdmin() {
        // Open admin creation dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Admin");
        dialog.setHeaderText("Enter details for new admin");

        // Form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "MANAGER", "STAFF");
        roleCombo.setValue("ADMIN");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Buttons
        ButtonType createButtonType = new ButtonType("Create Admin", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(result -> {
            if (result == createButtonType) {
                String username = usernameField.getText().trim();
                String name = nameField.getText().trim();
                String password = passwordField.getText().trim();
                String role = roleCombo.getValue();

                if (username.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    mainApp.showErrorDialog("Invalid Input", "All fields are required!");
                    return;
                }

                try {
                    boolean success = mainApp.getAdminManager().createAdmin(username, password, name, role);
                    if (success) {
                        mainApp.showSuccessDialog("Success", "Admin '" + username + "' created successfully!");
                    } else {
                        mainApp.showErrorDialog("Error", "Failed to create admin. Username might already exist.");
                    }
                } catch (Exception e) {
                    mainApp.showErrorDialog("Error", "Failed to create admin: " + e.getMessage());
                }
            }
        });
    }

    private void backupSystemData() {
        // Real backup functionality - Export data to files
        try {
            // Create backup directory
            java.io.File backupDir = new java.io.File("backup");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            // Backup students
            List<Student> students = mainApp.getStudentManager().listStudents();
            java.io.FileWriter studentFile = new java.io.FileWriter("backup/students_" + timestamp + ".csv");
            studentFile.write("ID,Name,StudentCode,LoyaltyPoints\n");
            for (Student student : students) {
                studentFile.write(String.format("%d,%s,%s,%d\n",
                    student.getId(),
                    student.getName(),
                    student.getStudentCode(),
                    student.getAccount() != null ? student.getAccount().balance() : 0));
            }
            studentFile.close();

            // Backup menu items
            List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();
            java.io.FileWriter menuFile = new java.io.FileWriter("backup/menu_items_" + timestamp + ".csv");
            menuFile.write("ID,Name,Description,Price,Category\n");
            for (Core.MenuItem item : menuItems) {
                menuFile.write(String.format("%d,%s,%s,%.2f,%s\n",
                    item.getId(),
                    item.getName().replace(",", ";"),
                    item.getDescription().replace(",", ";"),
                    item.getPrice().getAmount(),
                    item.getCategory()));
            }
            menuFile.close();

            // Backup orders
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();
            java.io.FileWriter orderFile = new java.io.FileWriter("backup/orders_" + timestamp + ".csv");
            orderFile.write("ID,StudentID,Status,Total,Date\n");
            for (Order order : orders) {
                orderFile.write(String.format("%d,%d,%s,%.2f,%s\n",
                    order.getId(),
                    order.getStudentId(),
                    order.getStatus(),
                    order.total() != null ? order.total().getAmount() : 0.0,
                    order.getOrderDate()));
            }
            orderFile.close();

            mainApp.showSuccessDialog("Backup Complete",
                "System data backed up successfully to backup/ folder!\n" +
                "Files created:\n" +
                "- students_" + timestamp + ".csv\n" +
                "- menu_items_" + timestamp + ".csv\n" +
                "- orders_" + timestamp + ".csv");

        } catch (Exception e) {
            mainApp.showErrorDialog("Backup Failed", "Error creating backup: " + e.getMessage());
        }
    }

    private void generateSystemReport() {
        try {
            // Generate real system report
            StringBuilder report = new StringBuilder();
            report.append("=== ITI CAFETERIA SYSTEM REPORT ===\n");
            report.append("Generated: ").append(java.time.LocalDateTime.now()).append("\n\n");

            // Student statistics
            List<Student> students = mainApp.getStudentManager().listStudents();
            report.append("STUDENT STATISTICS:\n");
            report.append("Total Students: ").append(students.size()).append("\n");

            int totalLoyaltyPoints = students.stream()
                .mapToInt(s -> s.getAccount() != null ? s.getAccount().balance() : 0)
                .sum();
            report.append("Total Loyalty Points Distributed: ").append(totalLoyaltyPoints).append("\n\n");

            // Menu statistics
            List<Core.MenuItem> menuItems = mainApp.getMenuManager().listItems();
            report.append("MENU STATISTICS:\n");
            report.append("Total Menu Items: ").append(menuItems.size()).append("\n");

            long mainCourses = menuItems.stream().filter(item -> item.getCategory() == Category.MAIN_COURSE).count();
            long drinks = menuItems.stream().filter(item -> item.getCategory() == Category.DRINK).count();
            long snacks = menuItems.stream().filter(item -> item.getCategory() == Category.SNACK).count();

            report.append("Main Courses: ").append(mainCourses).append("\n");
            report.append("Drinks: ").append(drinks).append("\n");
            report.append("Snacks: ").append(snacks).append("\n\n");

            // Order statistics
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.findAll();
            report.append("ORDER STATISTICS:\n");
            report.append("Total Orders: ").append(orders.size()).append("\n");

            double totalRevenue = orders.stream()
                .filter(order -> order.total() != null)
                .mapToDouble(order -> order.total().getAmount().doubleValue())
                .sum();
            report.append("Total Revenue: ").append(String.format("%.2f EGP", totalRevenue)).append("\n");

            if (!orders.isEmpty()) {
                double avgOrderValue = totalRevenue / orders.size();
                report.append("Average Order Value: ").append(String.format("%.2f EGP", avgOrderValue)).append("\n");
            }

            // Save report to file
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            java.io.File reportsDir = new java.io.File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            java.io.FileWriter reportFile = new java.io.FileWriter("reports/system_report_" + timestamp + ".txt");
            reportFile.write(report.toString());
            reportFile.close();

            // Show report in dialog
            Alert reportDialog = new Alert(Alert.AlertType.INFORMATION);
            reportDialog.setTitle("System Report");
            reportDialog.setHeaderText("System Report Generated Successfully");

            TextArea textArea = new TextArea(report.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefRowCount(20);
            textArea.setPrefColumnCount(60);

            reportDialog.getDialogPane().setContent(textArea);
            reportDialog.getDialogPane().setPrefSize(800, 600);
            reportDialog.showAndWait();

        } catch (Exception e) {
            mainApp.showErrorDialog("Report Generation Failed", "Error generating report: " + e.getMessage());
        }
    }

    // Styling methods
    private String getTextFieldStyle() {
        return "-fx-background-color: white; " +
               "-fx-border-color: #ddd; " +
               "-fx-border-radius: 5; " +
               "-fx-background-radius: 5; " +
               "-fx-padding: 8; " +
               "-fx-font-size: 14;";
    }

    private String getPrimaryButtonStyle() {
        return "-fx-background-color: #007bff; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getSecondaryButtonStyle() {
        return "-fx-background-color: #6c757d; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getSuccessButtonStyle() {
        return "-fx-background-color: #28a745; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getWarningButtonStyle() {
        return "-fx-background-color: #ffc107; " +
               "-fx-text-fill: black; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getDangerButtonStyle() {
        return "-fx-background-color: #dc3545; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getBackButtonStyle() {
        return "-fx-background-color: #28a745; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }

    private String getInfoButtonStyle() {
        return "-fx-background-color: #17a2b8; " +
               "-fx-text-fill: white; " +
               "-fx-background-radius: 5; " +
               "-fx-font-size: 12; " +
               "-fx-padding: 8 16; " +
               "-fx-cursor: hand;";
    }
}
