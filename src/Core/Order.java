package Core;

import Values.Money;
import Enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int studentId;
    private OrderStatus status;
    private List<OrderItem> items;
    private LocalDateTime orderDate;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.NEW;
    }

    public Order(int studentId) {
        this();
        this.studentId = studentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(MenuItem item, int qty) {
        if (item == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        OrderItem orderItem = new OrderItem(item.getId(), item.getName(), item.getPrice(), qty);
        items.add(orderItem);
    }

    public Money total() {
        if (items.isEmpty()) {
            return null;
        }

        Money total = null;
        for (OrderItem item : items) {
            Money lineTotal = item.lineTotal();
            if (total == null) {
                total = lineTotal;
            } else {
                total = total.add(lineTotal);
            }
        }
        return total;
    }

    public void markPreparing() {
        this.status = OrderStatus.PREPARING;
    }

    public void markReady() {
        this.status = OrderStatus.READY;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // Legacy methods for compatibility
    public Student getStudent() {
        return null; // Will be loaded from repository
    }

    public void setStudent(Student student) {
        if (student != null) {
            this.studentId = student.getId();
        }
    }

    public Money getTotalAmount() {
        return total();
    }

    // Helper methods for DAO loading
    public void clearItems() {
        this.items.clear();
    }

    public void addItemWithoutCalculation(MenuItem menuItem, int quantity) {
        OrderItem orderItem = new OrderItem(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), quantity);
        items.add(orderItem);
    }

    public void setTotalAmount(Money totalAmount) {
        // Used by DAO when loading from database
    }

    // Inner class for order items
    public static class OrderItem {
        private int menuItemId;
        private String nameSnapshot;
        private Money unitPrice;
        private int qty;

        public OrderItem(int menuItemId, String nameSnapshot, Money unitPrice, int qty) {
            this.menuItemId = menuItemId;
            this.nameSnapshot = nameSnapshot;
            this.unitPrice = unitPrice;
            this.qty = qty;
        }

        public int getMenuItemId() {
            return menuItemId;
        }

        public String getNameSnapshot() {
            return nameSnapshot;
        }

        public Money getUnitPrice() {
            return unitPrice;
        }

        public int getQty() {
            return qty;
        }

        public Money lineTotal() {
            return unitPrice.multiply(qty);
        }

        // Legacy compatibility method
        public MenuItem getMenuItem() {
            MenuItem item = new MenuItem();
            item.setId(menuItemId);
            item.setName(nameSnapshot);
            item.setPrice(unitPrice);
            return item;
        }

        public int getQuantity() {
            return qty;
        }

        public void setQuantity(int qty) {
            if (qty <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.qty = qty;
        }
    }
}
