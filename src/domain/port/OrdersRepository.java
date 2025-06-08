package domain.port;

import domain.entities.Order;

import java.util.List;

public interface OrdersRepository {
    List<Order> getAll();
    Order addNew(Order order);
    boolean Delete(String title, int id);
    List<Order> filter(String filter);
    List<Order> filterOnBatches(int id); //фильтр по партиям
    int getNextId();
    Order updateOrder(Order order);
}
