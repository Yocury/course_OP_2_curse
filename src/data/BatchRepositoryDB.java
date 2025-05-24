package data;

import domain.entities.Batch;
import domain.port.BatchRepository;

import java.util.ArrayList;
import java.util.List;

public class BatchRepositoryDB implements BatchRepository {
    List<Batch> batches = new ArrayList<>();
    DBManager db = new DBManager();

    public BatchRepositoryDB()
    {
        batches = db.LoadDBButhes();
    }

    @Override
    public List<Batch> getAll() {
        return batches;
    }

    @Override
    public Batch addNew(Batch batch) {
        batches.add(batch);
        db.addButchesDB(batch);
        return batch;
    }
}
