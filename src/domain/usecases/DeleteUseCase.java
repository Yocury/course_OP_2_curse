package domain.usecases;

import data.DBManager;

import java.sql.SQLException;

public class DeleteUseCase {
    private String title;
    private int id;
    private DBManager db = new DBManager();

    public DeleteUseCase(String title, int id)
    {
        this.title = title;
        this.id = id;
    }
    public void invoke(String title, int id) throws SQLException {
        db.DeleteLineDB(title,id);
    }
}
