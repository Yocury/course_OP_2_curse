package domain.usecases.batche;

import domain.entities.Batch;
import domain.port.BatchRepository;

public class UpdateBatchUseCase {
    private final BatchRepository batchRepository;
    public UpdateBatchUseCase(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public Batch invoke(Batch batch) {
        return batchRepository.update(batch);
    }
} 