package Interfaces;

import Core.Order;

import java.util.List;

public interface IOrderRepository {
    public void save(Order order);
    public Order findById(int id);
    public List<Order> findPendingOrders();
    public void update(Order order);

    // Add the two missing methods for order history functionality
    public List<Order> findByStudentId(int studentId);
    public List<Order> findAll();
}
