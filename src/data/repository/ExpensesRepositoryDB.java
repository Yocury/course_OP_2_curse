package data.repository;

import data.db_manager.ExpensesDBManager;
import domain.entities.Expenses;
import domain.port.ExpensesRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория для работы с расходами.
 * Использует ExpensesDBManager для взаимодействия с базой данных.
 */
public class ExpensesRepositoryDB implements ExpensesRepository {
    private List<Expenses> expenses = new ArrayList<>();
    private final ExpensesDBManager db;

    /**
     * Конструктор класса. Инициализирует менеджер базы данных и загружает расходы.
     */
    public ExpensesRepositoryDB() {
        this.db = new ExpensesDBManager();
        expenses = db.load();
    }

    /**
     * Получает список всех расходов.
     * @return список всех расходов
     */
    @Override
    public List<Expenses> getAll() {
        return expenses = db.load();
    }

    /**
     * Добавляет новый расход.
     * @param expense расход для добавления
     * @return добавленный расход
     */
    @Override
    public Expenses addNew(Expenses expense) {
        expenses.add(expense);
        db.add(expense);
        return expense;
    }

    /**
     * Удаляет расход по ID.
     * @param id ID расхода для удаления
     * @return true если удаление прошло успешно, false в случае ошибки
     */
    @Override
    public boolean delete(int id) {
        try {
            db.delete(id);
            expenses.removeIf(e -> e.getId() == id);
            return true;
        } catch (Exception e) {
            System.err.println("Ошибка при удалении расхода: " + e.getMessage());
            return false;
        }
    }

    /**
     * Получает расходы по типу.
     * @param type тип расхода для фильтрации
     * @return список отфильтрованных расходов
     */
    @Override
    public List<Expenses> getByType(String type) {
        return db.loadByType(type);
    }

    /**
     * Получает следующий доступный ID для нового расхода.
     * @return следующий доступный ID
     */
    @Override
    public int getNextId() {
        return db.getId("expenses") + 1;
    }

    /**
     * Обновляет существующий расход.
     * @param expense расход для обновления
     * @return обновленный расход
     */
    @Override
    public Expenses update(Expenses expense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expense.getId()) {
                expenses.set(i, expense);
                db.update(expense);
                break;
            }
        }
        return expense;
    }
}
