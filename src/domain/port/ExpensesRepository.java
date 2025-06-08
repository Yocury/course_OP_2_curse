package domain.port;

import domain.entities.Expenses;
import java.util.List;

public interface ExpensesRepository {
    List<Expenses> getAll();
    Expenses addNew(Expenses expenses);
    boolean delete(int id);
    List<Expenses> getByType(String type);
    int getNextId();
    Expenses update(Expenses expenses);
}
