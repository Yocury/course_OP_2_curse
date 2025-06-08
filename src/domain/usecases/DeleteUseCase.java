package domain.usecases;

import data.db_manager.DBManager;

public class DeleteUseCase {
    private final DBManager db;

    public DeleteUseCase(DBManager db) {
        this.db = db;
    }

    public void delete(String title, int id) {
        try {
            db.delete(title, id);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении: " + e.getMessage());
        }
    }
}
