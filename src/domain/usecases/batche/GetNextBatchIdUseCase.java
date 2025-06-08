package domain.usecases.batche;

import domain.port.BatchRepository;

public class GetNextBatchIdUseCase {
    private final BatchRepository batchRepository;
    public GetNextBatchIdUseCase(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public int invoke() {
        return batchRepository.getNextId();
    }
} 