package Data;

import Entities.Butches;
import Entities.Expenses;
import Entities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_manager {

    private static final String URL = "jdbc:postgresql://localhost:5433/Orders";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private Connection connection;

    public DB_manager() {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден драйвер PostgreSQL JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Соединение с базой данных установлено.");
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к БД: " + e.getMessage());
        }
    }

    public int getId(String title) {
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return -1; // Или другое значение, указывающее на ошибку
        }

        String sql = "SELECT MAX(id) FROM public." + title;
        int maxId = 0; // Значение по умолчанию

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                maxId = rs.getInt(1); // Получаем значение из первого столбца результата
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении максимального ID: " + e.getMessage());
            return -1; // Или другое значение, указывающее на ошибку
        }

        return maxId;
    }

    public int getColumnType(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
            if (columns.next()) {
                return columns.getInt("DATA_TYPE"); // возвращает java.sql.Types
            } else {
                throw new SQLException("Столбец " + columnName + " в таблице " + tableName + " не найден");
            }
        }
    }

    public void setPreparedStatementValue(PreparedStatement pstmt, int parameterIndex, int sqlType, String value) throws SQLException {
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
                pstmt.setDate(parameterIndex, java.sql.Date.valueOf(value)); // ожидается формат "yyyy-MM-dd"
                break;
            case Types.TIMESTAMP:
                pstmt.setTimestamp(parameterIndex, java.sql.Timestamp.valueOf(value)); // формат "yyyy-MM-dd hh:mm:ss"
                break;
            default:
                pstmt.setString(parameterIndex, value);
                break;
        }
    }


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


    public List<Butches> LoadDBButhes() {
        List<Butches> all_butches = new ArrayList<>();
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
                Butches butches = new Butches(id, provider, date, amount, status, count, avgPrice);
                all_butches.add(butches); // заполняем лист заказов.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_butches;
    }

    public void updateBatchStatus(int batch, String status) throws SQLException {
        String sql = "UPDATE batches SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, batch);
            pstmt.executeUpdate();
        }
    }

    public List<Expenses> LoadFilterExpensesTypes(String filterType) {
        List<Expenses> all_Expenses = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM expenses WHERE \"type\" = ?";

        try (PreparedStatement expenses = connection.prepareStatement(sql)) {
            expenses.setString(1, filterType);  // Устанавливаем параметр перед выполнением запроса
            try (ResultSet re = expenses.executeQuery()) {
                while (re.next()) {
                    int id = re.getInt("id");
                    String description = re.getString("description");
                    String type = re.getString("type");
                    String date = re.getString("date");
                    int amount = re.getInt("amount");

                    Expenses expense = new Expenses(id, description, type, amount, date);
                    all_Expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_Expenses;
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


    public List<Order> LoadDBOrders() {
        List<Order> all_orders = new ArrayList<>(); //Лист для передачи всех заказов из бд
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM orders " +
                "ORDER BY CASE status " +
                "WHEN 'В исполнении' THEN 1 " +
                "WHEN 'В обработке' THEN 2 " +
                "WHEN 'Выполнен' THEN 3 " +
                "WHEN 'Отменен' THEN 4 " +
                "ELSE 5 END, id ASC";


        try (PreparedStatement orders = connection.prepareStatement(sql);
             ResultSet ro = orders.executeQuery()) { //ro -  result orders.

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


    public void DeleteLineDB(String title, int id) throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.err.println("Нет подключения к базе данных!");
            throw new SQLException("Нет подключения к базе данных!"); // Прерываем выполнение
        }

        String sql = "DELETE FROM public." + title + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Расход с ID " + id + " удален из базы данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении расхода из базы данных: " + e.getMessage());
            throw e; // Пробрасываем исключение выше для обработки
        }

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

                Expenses expense = new Expenses(id, description, type, amount, date);
                all_Expenses.add(expense); // заполняем лист заказов.
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке данных из базы.");
        }
        return all_Expenses;
    }


    public void addOrderDB(int id, int batchId, String source, String count, String street, String building, 
                          String date, String phone, String status, String price) {
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO public.\"orders\" " +
                "(id, sourse, count, street, building, date, number, status, \"ID batches\", price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);  // bigint
            pstmt.setString(2, source);
            pstmt.setLong(3, Long.parseLong(count));  // bigint
            pstmt.setString(4, street);
            pstmt.setString(5, building);
            pstmt.setString(6, date);
            pstmt.setString(7, phone);
            pstmt.setString(8, status);
            pstmt.setInt(9, batchId);  // integer
            pstmt.setLong(10, Long.parseLong(price));  // bigint
            pstmt.executeUpdate();
            System.out.println("Заказ добавлен в базу данных.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при добавлении заказа: " + e.getMessage());
        }
    }

    public void addButchesDB(int id, String provider, String amount, String status, String date, int count) {
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO public.\"batches\" " +
                "(id, provider, amount, status, date, count)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, provider);
            pstmt.setInt(3, Integer.parseInt(amount));
            pstmt.setString(4, status);
            pstmt.setString(5, date);
            pstmt.setInt(6, count);
            pstmt.executeUpdate();
            System.out.println("Партия сохранена в базу данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении партии в базу: " + e.getMessage());
        }
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



