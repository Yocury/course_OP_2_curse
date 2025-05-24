package presentation;

import data.BatchRepositoryDB;
import data.OrdersRepositoryDB;
import data.RepositoryDB;
import domain.port.BatchRepository;
import domain.port.OrdersRepository;
import domain.port.Repository;
import domain.usecases.DeleteUseCase;
import domain.usecases.batche.AddBatchUseCase;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.order.AddOrderUseCase;
import domain.usecases.order.GetAllOrderUseCase;

public class MyConfig {
    private static MyConfig myConfig = null;
    private OrdersRepository ordersRepository;
    private BatchRepository batchRepository;
    private Repository repository;

    private MyConfig(){
        ordersRepository = new OrdersRepositoryDB();
        batchRepository = new BatchRepositoryDB();
        repository = new RepositoryDB();
        this.myConfig = this;
    }

    public GetAllOrderUseCase getAllOrderUseCase() {
        return new GetAllOrderUseCase(ordersRepository);
    }
    public AddOrderUseCase addOrderUseCase(){return new AddOrderUseCase(ordersRepository);}
    public GetAllBatchUseCases getAllBatchUseCases(){return new GetAllBatchUseCases(batchRepository);}
    public AddBatchUseCase addBatchUseCase() {return new AddBatchUseCase(batchRepository);}
    public DeleteUseCase deleteUseCase(String title, int id) {return new DeleteUseCase(title,id);}

    public static MyConfig instance(){
        if(myConfig == null){
            myConfig = new MyConfig();
        }
        return myConfig;
    }
}
