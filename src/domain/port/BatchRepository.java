package domain.port;

import data.DataService;
import domain.entities.Batch;
import domain.entities.Order;

import java.util.ArrayList;
import java.util.List;

public interface BatchRepository {
    List<Batch> getAll();

    Batch addNew(Batch batch);

    boolean delete(int id);

    Batch update(Batch batch);

    Object analyzeBatch(int batchId);

    List<Batch> filter(String filter);

    DataService.BatchAnalysis analyze(int id);

    int getNextId();
}
