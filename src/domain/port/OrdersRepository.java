package domain.port;

import domain.entities.Order;

import java.util.List;

public interface OrdersRepository {
    List<Order> getAll();
    Order addNew(Order order);
    Order Delete(Order order, int id);
}
