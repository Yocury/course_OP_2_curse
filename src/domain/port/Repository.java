package domain.port;

import java.sql.SQLException;

public interface Repository {
    void delete(String title, int id) throws SQLException;
}
