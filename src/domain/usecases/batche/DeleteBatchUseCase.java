package domain.usecases.batche;

import domain.port.BatchRepository;

public class DeleteBatchUseCase {
    private final BatchRepository batchRepository;
    public DeleteBatchUseCase(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }
    public boolean invoke(int id) {
        return batchRepository.delete(id);
    }
}
