package data.repository;

import data.db_manager.DBManager;
import domain.port.Repository;

public abstract class RepositoryDB implements Repository {
    protected final DBManager db;

    protected RepositoryDB(DBManager db) {
        this.db = db;
    }
}
