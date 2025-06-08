package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;
import java.util.List;

public class FilterOrdersByStatusUseCase {
    private final OrdersRepository ordersRepository;
    public FilterOrdersByStatusUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    public List<Order> invoke(String status) {
        return ordersRepository.filter(status);
    }
} 