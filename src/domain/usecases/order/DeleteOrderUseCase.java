package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;

public class DeleteOrderUseCase {
    private OrdersRepository ordersRepository;


    public DeleteOrderUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Order invoke(Order order) {
        return this.ordersRepository.Delete(int id, String title);
    }


}
