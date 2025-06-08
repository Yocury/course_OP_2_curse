package domain.usecases.batche;

import domain.port.BatchRepository;

public class AnalyzeBatchUseCase {
    private final BatchRepository batchRepository;
    public AnalyzeBatchUseCase(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public Object invoke(int batchId) {
        return batchRepository.analyzeBatch(batchId);
    }
} 