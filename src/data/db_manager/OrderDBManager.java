package data.db_manager;

import domain.entities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для управления заказами в базе данных.
 * Реализует все необходимые операции с таблицей orders.
 */
public class OrderDBManager extends DBManager {
    private static final String TABLE_NAME = "orders";
    private static final String URL = "jdbc:postgresql://localhost:5433/Orders";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private Connection connection;

    public OrderDBManager() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден драйвер PostgreSQL JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    /**
     * Загружает все заказы из базы данных.
     * Заказы сортируются по статусу и ID.
     * @return список всех заказов или null в случае ошибки
     */
    @Override
    public List<Order> load() {
        List<Order> all_orders = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " " +
                "ORDER BY CASE status " +
                "WHEN 'В исполнении' THEN 1 " +
                "WHEN 'В обработке' THEN 2 " +
                "WHEN 'Выполнен' THEN 3 " +
                "WHEN 'Отменен' THEN 4 " +
                "ELSE 5 END, id ASC";

        try (PreparedStatement orders = connection.prepareStatement(sql);
             ResultSet rs = orders.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("id"),
                    rs.getString("sourse"),
                    rs.getInt("count"),
                    rs.getString("street"),
                    rs.getString("building"),
                    rs.getInt("ID batches"),
                    rs.getString("date"),
                    rs.getString("number"),
                    rs.getString("status"),
                    rs.getInt("price")
                );
                all_orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке заказов: " + e.getMessage());
        }
        return all_orders;
    }

    /**
     * Добавляет новый заказ в базу данных.
     * @param entity объект заказа для добавления
     * @throws IllegalArgumentException если передан объект не типа Order
     */
    @Override
    public void add(Object entity) {
        if (!(entity instanceof Order)) {
            throw new IllegalArgumentException("Entity must be an Order");
        }
        Order order = (Order) entity;
        
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO " + TABLE_NAME + " " +
                "(id, sourse, count, street, building, date, number, status, \"ID batches\", price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, order.getId());
            pstmt.setString(2, order.getSourse());
            pstmt.setInt(3, order.getCount());
            pstmt.setString(4, order.getStreet());
            pstmt.setString(5, order.getBuilding());
            pstmt.setString(6, order.getDate());
            pstmt.setString(7, order.getNumber());
            pstmt.setString(8, order.getStatus());
            pstmt.setInt(9, order.getId_batches());
            pstmt.setInt(10, order.getPrice());
            pstmt.executeUpdate();
            System.out.println("Заказ добавлен в базу данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении заказа: " + e.getMessage());
        }
    }

    /**
     * Обновляет существующий заказ в базе данных.
     * @param entity объект заказа для обновления
     * @throws IllegalArgumentException если передан объект не типа Order
     */
    @Override
    public void update(Object entity) {
        if (!(entity instanceof Order)) {
            throw new IllegalArgumentException("Entity must be an Order");
        }
        Order order = (Order) entity;

        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "sourse = ?, count = ?, street = ?, building = ?, " +
                "date = ?, number = ?, status = ?, \"ID batches\" = ?, price = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, order.getSourse());
            pstmt.setInt(2, order.getCount());
            pstmt.setString(3, order.getStreet());
            pstmt.setString(4, order.getBuilding());
            pstmt.setString(5, order.getDate());
            pstmt.setString(6, order.getNumber());
            pstmt.setString(7, order.getStatus());
            pstmt.setInt(8, order.getId_batches());
            pstmt.setInt(9, order.getPrice());
            pstmt.setInt(10, order.getId());
            pstmt.executeUpdate();
            System.out.println("Заказ обновлен в базе данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении заказа: " + e.getMessage());
        }
    }

    /**
     * Удаляет заказ из базы данных по ID.
     * @param id ID заказа для удаления
     * @throws SQLException если произошла ошибка при удалении
     */
    @Override
    public void delete(int id) throws SQLException {
        super.delete(TABLE_NAME, id);
    }

    /**
     * Загружает заказы, отфильтрованные по ID партии.
     * @param batchId ID партии для фильтрации
     * @return список отфильтрованных заказов или null в случае ошибки
     */
    public List<Order> loadFilteredByBatch(int batchId) {
        List<Order> orders = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE \"ID batches\" = ? " +
                "ORDER BY CASE status " +
                "WHEN 'В исполнении' THEN 1 " +
                "WHEN 'В обработке' THEN 2 " +
                "WHEN 'Выполнен' THEN 3 " +
                "WHEN 'Отменен' THEN 4 " +
                "ELSE 5 END, id ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("id"),
                        rs.getString("sourse"),
                        rs.getInt("count"),
                        rs.getString("street"),
                        rs.getString("building"),
                        rs.getInt("ID batches"),
                        rs.getString("date"),
                        rs.getString("number"),
                        rs.getString("status"),
                        rs.getInt("price")
                    );
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке отфильтрованных заказов: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Обновляет значение ячейки в таблице заказов.
     * @param id ID заказа
     * @param title название таблицы (используется для совместимости)
     * @param column имя столбца
     * @param newValue новое значение
     */
    public void UpdateDBCell(Object id, String title, String column, String newValue) {
        switch (title) {
            case "Заказы":
                title = "orders";
                break;
            case "Расходы":
                title = "expenses";
                break;
            case "Партии":
                title = "batches";
                break;
        }

        String sql = "UPDATE public.\"" + title + "\" SET \"" + column + "\" = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Получаем SQL тип столбца
            int columnType = getColumnType(title, column);

            // Устанавливаем значение с учётом типа
            setPreparedStatementValue(pstmt, 1, columnType, newValue);

            // id всегда int (если у вас другой тип - поправьте)
            pstmt.setInt(2, Integer.parseInt(id.toString()));

            pstmt.executeUpdate();

            System.out.println("Ячейка обновлена в базе данных.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при изменении ячейки: " + e.getMessage());
        }
    }
}
