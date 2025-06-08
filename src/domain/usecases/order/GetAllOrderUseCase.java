package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;

import java.util.List;

public class GetAllOrderUseCase {
    private OrdersRepository ordersRepository;
    public GetAllOrderUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    public List<Order> invoke(){
        return this.ordersRepository.getAll();
    }
}
