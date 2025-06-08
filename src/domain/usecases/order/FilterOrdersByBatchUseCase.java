package domain.usecases.order;

import domain.entities.Order;
import domain.port.OrdersRepository;
import java.util.List;

public class FilterOrdersByBatchUseCase {
    private final OrdersRepository ordersRepository;
    public FilterOrdersByBatchUseCase(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    public List<Order> invoke(int batchId) {
        return ordersRepository.filterOnBatches(batchId);
    }
} 