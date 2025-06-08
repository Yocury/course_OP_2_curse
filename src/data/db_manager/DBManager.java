package data.db_manager;

import domain.entities.Batch;
import domain.entities.Expenses;
import domain.entities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный базовый класс для управления базой данных.
 * Содержит общую функциональность для работы с базой данных и определяет
 * абстрактные методы, которые должны быть реализованы в классах-наследниках.
 */
public abstract class DBManager {
    // Константы для подключения к базе данных
    protected static final String URL = "jdbc:postgresql://localhost:5433/Orders";
    protected static final String USER = "postgres";
    protected static final String PASSWORD = "admin";
    protected Connection connection;

    /**
     * Конструктор класса. Инициализирует подключение к базе данных.
     */
    public DBManager() {
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
     * Устанавливает новое подключение к базе данных.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Соединение с базой данных установлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к БД: " + e.getMessage());
        }
    }

    /**
     * Получает максимальный ID из указанной таблицы.
     * @param tableName имя таблицы
     * @return максимальный ID или -1 в случае ошибки
     */
    public int getId(String tableName) {
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return -1;
        }

        String sql = "SELECT MAX(id) FROM public." + tableName;
        int maxId = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении максимального ID: " + e.getMessage());
            return -1;
        }
        return maxId;
    }

    /**
     * Получает тип данных столбца в указанной таблице.
     * @param tableName имя таблицы
     * @param columnName имя столбца
     * @return SQL тип данных столбца
     * @throws SQLException если столбец не найден или произошла ошибка
     */
    protected int getColumnType(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
            if (columns.next()) {
                return columns.getInt("DATA_TYPE");
            } else {
                throw new SQLException("Столбец " + columnName + " в таблице " + tableName + " не найден");
            }
        }
    }

    /**
     * Устанавливает значение параметра в PreparedStatement с учетом его типа.
     * @param pstmt PreparedStatement для установки значения
     * @param parameterIndex индекс параметра
     * @param sqlType SQL тип данных
     * @param value значение для установки
     * @throws SQLException если произошла ошибка при установке значения
     */
    protected void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, int sqlType, String value) throws SQLException {
        if (value == null) {
            pstmt.setNull(parameterIndex, sqlType);
            return;
        }
        switch (sqlType) {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                pstmt.setInt(parameterIndex, Integer.parseInt(value));
                break;
            case Types.BIGINT:
                pstmt.setLong(parameterIndex, Long.parseLong(value));
                break;
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                pstmt.setDouble(parameterIndex, Double.parseDouble(value));
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                pstmt.setBigDecimal(parameterIndex, new java.math.BigDecimal(value));
                break;
            case Types.BOOLEAN:
            case Types.BIT:
                pstmt.setBoolean(parameterIndex, Boolean.parseBoolean(value));
                break;
            case Types.DATE:
                pstmt.setDate(parameterIndex, java.sql.Date.valueOf(value));
                break;
            case Types.TIMESTAMP:
                pstmt.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf(value));
                break;
            default:
                pstmt.setString(parameterIndex, value);
                break;
        }
    }

    /**
     * Удаляет запись из указанной таблицы по ID.
     * @param tableName имя таблицы
     * @param id ID записи для удаления
     * @throws SQLException если произошла ошибка при удалении
     */
    public void delete(String tableName, int id) throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.err.println("Нет подключения к базе данных!");
            throw new SQLException("Нет подключения к базе данных!");
        }

        String sql = "DELETE FROM public." + tableName + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println(tableName + " с ID " + id + " удален из базы данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи из базы данных: " + e.getMessage());
            throw e;
        }
    }


    // Абстрактные методы, которые должны быть реализованы в классах-наследниках

    /**
     * Загружает все записи из таблицы.
     * @return список загруженных объектов
     */
    public abstract List<?> load();

    /**
     * Добавляет новую запись в таблицу.
     * @param entity объект для добавления
     */
    public abstract void add(Object entity);

    /**
     * Обновляет существующую запись в таблице.
     * @param entity объект для обновления
     */
    public abstract void update(Object entity);

    /**
     * Удаляет запись из таблицы по ID.
     * @param id ID записи для удаления
     * @throws SQLException если произошла ошибка при удалении
     */
    public abstract void delete(int id) throws SQLException;

    public List<Batch> LoadDBButhes() {
        List<Batch> all_butches = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе");
            return null;
        }

        String sql = "SELECT * FROM batches " +
                "ORDER BY CASE status " +
                "WHEN 'В продаже' THEN 1 " +
                "WHEN 'В обработке' THEN 2 " +
                "WHEN 'Продана' THEN 3 " +
                "WHEN 'Отменена' THEN 4 " +
                "ELSE 5 END, id ASC";

        try (PreparedStatement orders = connection.prepareStatement(sql);
             ResultSet ro = orders.executeQuery()) { //ro -  result orders.

            //Читаем данные из базы и добавляем в лист заказов
            while (ro.next()) {
                int id = ro.getInt("id");
                String provider = ro.getString("provider");
                int amount = ro.getInt("amount");
                String status = ro.getString("status");
                String date = ro.getString("date");
                int count = ro.getInt("count");
                double avgPriceDot = ro.getDouble("avg_price");
                int avgPrice = (int) avgPriceDot;
                Batch batch = new Batch(id, provider, date, amount, status, count, avgPrice);
                all_butches.add(batch); // заполняем лист заказов.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_butches;
    }

    public List<Order> LoadDBFilterOrders(int ButchesId) {
        List<Order> all_orders = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }
        String sql = "SELECT * FROM orders WHERE \"ID batches\" = ? "
                + "ORDER BY CASE status "
                + "WHEN 'В исполнении' THEN 1 "
                + "WHEN 'В обработке' THEN 2 "
                + "WHEN 'Выполнен' THEN 3 "
                + "WHEN 'Отменен' THEN 4 "
                + "ELSE 5 END, id ASC";

        try (PreparedStatement orders = connection.prepareStatement(sql)) {
            orders.setInt(1, ButchesId);
            ResultSet ro = orders.executeQuery();  //ro -  result orders.

            //Читаем данные из базы и добавляем в лист заказов
            while (ro.next()) {
                int id = ro.getInt("id");
                String sourse = ro.getString("sourse");
                int count = ro.getInt("count");
                String street = ro.getString("street");
                String building = ro.getString("building");
                String date = ro.getString("date");
                String status = ro.getString("status");
                int id_batches = ro.getInt("ID batches");
                String number = ro.getString("number");
                int price = ro.getInt("price");

                Order order = new Order(id, sourse, count, street, building, id_batches, date, number, status, price);
                all_orders.add(order); // заполняем лист заказов.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_orders;
    }


    public List<Expenses> LoadDBExpenses() {
        List<Expenses> all_Expenses = new ArrayList<>(); //Лист для передачи всех заказов из бд
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM expenses";

        try (PreparedStatement expenses = connection.prepareStatement(sql);
             ResultSet re = expenses.executeQuery()) { //re -  result expenses.

            //Читаем данные из базы и добавляем в лист заказов
            while (re.next()) {
                int id = re.getInt("id");
                String description = re.getString("description");
                String type = re.getString("type");
                String date = re.getString("date");
                int amount = re.getInt("amount");
                int batchId = re.getInt("batch_id"); // Читаем batch_id

                Expenses expense = new Expenses(id, description, type, amount, date, batchId);
                all_Expenses.add(expense); // заполняем лист заказов.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_Expenses;
    }

    public void addExpensesDB(int id, String description, String type, String amount, String date) {
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO public.\"expenses\" " +
                "(id, description, type, amount, date)" +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, description);
            pstmt.setString(3, type);
            pstmt.setInt(4, Integer.parseInt(amount));
            pstmt.setString(5, date);
            pstmt.executeUpdate();
            System.out.println("Заказ сохранён в базу данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении заказа в базу: " + e.getMessage());
        }
    }
}



