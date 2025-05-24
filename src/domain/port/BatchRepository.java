package domain.port;

import domain.entities.Batch;

import java.util.List;

public interface BatchRepository {
    List<Batch> getAll();
    Batch addNew(Batch batch);
}
