package data.repository;

import data.db_manager.OrderDBManager;
import domain.entities.Order;
import domain.port.OrdersRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория для работы с заказами.
 * Использует OrderDBManager для взаимодействия с базой данных.
 */
public class OrdersRepositoryDB implements OrdersRepository {
    private List<Order> orders = new ArrayList<>();
    private final OrderDBManager db;

    /**
     * Конструктор класса. Инициализирует менеджер базы данных и загружает заказы.
     */
    public OrdersRepositoryDB() {
        this.db = new OrderDBManager();
        orders = db.load();
    }

    /**
     * Получает список всех заказов.
     * @return список всех заказов
     */
    @Override
    public List<Order> getAll() {
        return orders = db.load();
    }

    /**
     * Добавляет новый заказ.
     * @param order заказ для добавления
     * @return добавленный заказ
     */
    @Override
    public Order addNew(Order order) {
        orders.add(order);
        db.add(order);
        return order;
    }

    /**
     * Удаляет заказ по ID.
     * @param title название таблицы (не используется)
     * @param id ID заказа для удаления
     * @return true если удаление прошло успешно, false в случае ошибки
     */
    @Override
    public boolean Delete(String title, int id) {
        try {
            db.delete(id);
            orders.removeIf(o -> o.getId() == id);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении заказа: " + e.getMessage());
            return false;
        }
    }

    /**
     * Фильтрует заказы по статусу.
     * @param filter статус для фильтрации
     * @return список отфильтрованных заказов
     */
    @Override
    public List<Order> filter(String filter) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus().equalsIgnoreCase(filter)) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * Фильтрует заказы по ID партии.
     * @param id ID партии для фильтрации
     * @return список отфильтрованных заказов
     */
    @Override
    public List<Order> filterOnBatches(int id) {
        return db.loadFilteredByBatch(id);
    }

    /**
     * Получает следующий доступный ID для нового заказа.
     * @return следующий доступный ID
     */
    @Override
    public int getNextId() {
        return db.getId("orders") + 1;
    }

    /**
     * Обновляет существующий заказ.
     * @param order заказ для обновления
     * @return обновленный заказ
     */
    @Override
    public Order updateOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == order.getId()) {
                orders.set(i, order);
                db.update(order);
                break;
            }
        }
        return order;
    }
}
