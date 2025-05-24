package domain.usecases.batche;

import domain.entities.Batch;
import domain.port.BatchRepository;

public class AddBatchUseCase {
    private BatchRepository batchRepository;

    public AddBatchUseCase(BatchRepository batchRepository){
        this.batchRepository = batchRepository;
    }
    public Batch invoke(Batch batch){
        return this.batchRepository.addNew(batch);
    }
}
