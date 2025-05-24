package data;

import domain.port.Repository;

import java.sql.SQLException;

public class RepositoryDB implements Repository {
    DBManager db = new DBManager();

    @Override
    public void delete(String title, int id) throws SQLException {
        db.DeleteLineDB(title,id);
    }
}
