package domain.usecases.order;

import domain.port.OrdersRepository;

public class GetNextOrderIdUseCase {
    private final OrdersRepository ordersRepository;
    public GetNextOrderIdUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    public int invoke() {
        return ordersRepository.getNextId();
    }
} 