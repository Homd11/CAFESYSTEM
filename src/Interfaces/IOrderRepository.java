package Interfaces;

import Core.Order;

import java.util.List;

public interface IOrderRepository {
    public void save(Order order);
    public Order findById(int id);
    public List<Order> findPendingOrders();
}
