package domain.usecases.batche;

import domain.entities.Batch;
import domain.port.BatchRepository;

import java.util.List;

public class GetAllBatchUseCases {
    private BatchRepository batchRepository;

    public GetAllBatchUseCases(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public List<Batch> invoke(){
        return this.batchRepository.getAll();
    }
}
