package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;

public class UpdateOrderUseCase {
    private final OrdersRepository ordersRepository;
    public UpdateOrderUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    public Order invoke(Order order) {
        return ordersRepository.updateOrder(order);
    }
} 