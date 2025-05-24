package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;
public class AddOrderUseCase {
    private OrdersRepository ordersRepository;


    public AddOrderUseCase(OrdersRepository ordersRepository){
        this.ordersRepository = ordersRepository;
    }

    public Order invoke(Order order){
        return this.ordersRepository.addNew(order);
    }

}
