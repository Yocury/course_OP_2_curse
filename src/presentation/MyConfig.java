package presentation;

import data.repository.BatchRepositoryDB;
import data.repository.OrdersRepositoryDB;
import data.repository.ExpensesRepositoryDB;
import domain.port.BatchRepository;
import domain.port.OrdersRepository;
import domain.port.ExpensesRepository;
import domain.usecases.DeleteUseCase;
import domain.usecases.batche.AddBatchUseCase;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.batche.DeleteBatchUseCase;
import domain.usecases.batche.UpdateBatchUseCase;
import domain.usecases.batche.AnalyzeBatchUseCase;
import domain.usecases.batche.FilterBatchesUseCase;
import domain.usecases.batche.GetNextBatchIdUseCase;
import domain.usecases.order.AddOrderUseCase;
import domain.usecases.order.GetAllOrderUseCase;
import domain.usecases.order.FilterOrdersByStatusUseCase;
import domain.usecases.order.FilterOrdersByBatchUseCase;
import domain.usecases.order.GetNextOrderIdUseCase;
import domain.usecases.order.DeleteOrderUseCase;
import domain.usecases.order.UpdateOrderUseCase;
import domain.usecases.expenses.AddExpensesUseCase;
import domain.usecases.expenses.GetAllExpensesUseCase;
import domain.usecases.expenses.DeleteExpensesUseCase;
import domain.usecases.expenses.UpdateExpensesUseCase;
import domain.usecases.expenses.FilterExpensesUseCase;
import domain.usecases.expenses.GetNextExpenseIdUseCase;
import data.db_manager.OrderDBManager;
import data.db_manager.ExpensesDBManager;
import data.db_manager.BatchDBManager;

public class MyConfig {
    private static MyConfig instance;
    private final OrderDBManager orderDBManager;
    private final ExpensesDBManager expensesDBManager;
    private final BatchDBManager batchDBManager;
    private final OrdersRepository ordersRepository;
    private final ExpensesRepository expensesRepository;
    private final BatchRepository batchRepository;

    private MyConfig() {
        this.orderDBManager = new OrderDBManager();
        this.expensesDBManager = new ExpensesDBManager();
        this.batchDBManager = new BatchDBManager();
        
        this.ordersRepository = new OrdersRepositoryDB();
        this.expensesRepository = new ExpensesRepositoryDB();
        this.batchRepository = new BatchRepositoryDB();
    }

    public static MyConfig instance() {
        if (instance == null) {
            instance = new MyConfig();
        }
        return instance;
    }

    public DeleteUseCase deleteUseCase(String title) {
        switch (title.toLowerCase()) {
            case "orders":
                return new DeleteUseCase(orderDBManager);
            case "expenses":
                return new DeleteUseCase(expensesDBManager);
            case "batches":
                return new DeleteUseCase(batchDBManager);
            default:
                throw new IllegalArgumentException("Неизвестный тип сущности: " + title);
        }
    }

    public OrdersRepository ordersRepository() {
        return ordersRepository;
    }

    public ExpensesRepository expensesRepository() {
        return expensesRepository;
    }

    public BatchRepository batchRepository() {
        return batchRepository;
    }

    public GetAllOrderUseCase getAllOrderUseCase() {
        return new GetAllOrderUseCase(ordersRepository);
    }
    public AddOrderUseCase addOrderUseCase(){return new AddOrderUseCase(ordersRepository);}
    public GetAllBatchUseCases getAllBatchUseCases(){return new GetAllBatchUseCases(batchRepository);}
    public AddBatchUseCase addBatchUseCase() {return new AddBatchUseCase(batchRepository);}
    public FilterOrdersByStatusUseCase filterOrdersUseCase() { return new FilterOrdersByStatusUseCase(ordersRepository); }
    public FilterOrdersByBatchUseCase filterOrdersByBatchUseCase() { return new FilterOrdersByBatchUseCase(ordersRepository); }
    public GetNextOrderIdUseCase getNextOrderIdUseCase() { return new GetNextOrderIdUseCase(ordersRepository); }
    public DeleteOrderUseCase deleteOrderUseCase() { return new DeleteOrderUseCase(ordersRepository); }
    public UpdateOrderUseCase updateOrderUseCase() { return new UpdateOrderUseCase(ordersRepository); }
    public AddExpensesUseCase addExpensesUseCase() { return new AddExpensesUseCase(expensesRepository); }
    public GetAllExpensesUseCase getAllExpensesUseCase() { return new GetAllExpensesUseCase(expensesRepository); }
    public DeleteExpensesUseCase deleteExpensesUseCase() { return new DeleteExpensesUseCase(expensesRepository); }
    public UpdateExpensesUseCase updateExpensesUseCase() { return new UpdateExpensesUseCase(expensesRepository); }
    public FilterExpensesUseCase filterExpensesUseCase() { return new FilterExpensesUseCase(expensesRepository); }
    public DeleteBatchUseCase deleteBatchUseCase() { return new DeleteBatchUseCase(batchRepository); }
    public UpdateBatchUseCase updateBatchUseCase() { return new UpdateBatchUseCase(batchRepository); }
    public AnalyzeBatchUseCase analyzeBatchUseCase() { return new AnalyzeBatchUseCase(batchRepository); }
    public FilterBatchesUseCase filterBatchesUseCase() { return new FilterBatchesUseCase(batchRepository); }
    public GetNextBatchIdUseCase getNextBatchIdUseCase() { return new GetNextBatchIdUseCase(batchRepository); }
    public GetNextExpenseIdUseCase getNextExpenseIdUseCase() { return new GetNextExpenseIdUseCase(expensesRepository); }
}
