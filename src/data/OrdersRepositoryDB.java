package data;

import domain.entities.Order;
import domain.port.OrdersRepository;

import java.util.ArrayList;
import java.util.List;

public class OrdersRepositoryDB implements OrdersRepository {
    List<Order> orders = new ArrayList<>();
    OrderDBManager db = new OrderDBManager();

    public OrdersRepositoryDB(){
        orders = db.LoadDBOrders();
    }

    @Override
    public List<Order> getAll() {
        return orders;
    }

    @Override
    public Order addNew(Order order) {
        orders.add(order);
        db.addOrderDB(order);
        return order;
    }

    @Override
    public boolean Delete(String title, int id) {
        db.DeleteLineDB();
        return true;
    }

}
