package domain.usecases.batche;

import domain.entities.Batch;
import domain.port.BatchRepository;
import java.util.List;

public class FilterBatchesUseCase {
    private final BatchRepository batchRepository;
    public FilterBatchesUseCase(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public List<Batch> invoke(String filter) {
        return batchRepository.filter(filter);
    }
} 