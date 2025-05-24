package data;

import domain.entities.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDBManager extends DBManager{

    private static final String URL = "jdbc:postgresql://localhost:5433/Orders";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private Connection connection;


    public void addOrderDB(Order order) {
        if (this.connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        int id = order.getId();
        String source = order.getSourse();
        int count = order.getCount();
        String street = order.getStreet();
        String building = order.getBuilding();
        String date = order.getDate();
        String phone = order.getNumber();
        String status = order.getStatus();
        int batchId = order.getId_batches();
        int price = order.getPrice();

        String sql = "INSERT INTO public.\"orders\" " +
                "(id, sourse, count, street, building, date, number, status, \"ID batches\", price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);  // bigint
            pstmt.setString(2, source);
            pstmt.setLong(3, Long.parseLong(String.valueOf(count)));  // bigint
            pstmt.setString(4, street);
            pstmt.setString(5, building);
            pstmt.setString(6, date);
            pstmt.setString(7, phone);
            pstmt.setString(8, status);
            pstmt.setInt(9, batchId);  // integer
            pstmt.setLong(10, Long.parseLong(String.valueOf(price)));  // bigint
            pstmt.executeUpdate();
            System.out.println("Заказ добавлен в базу данных.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при добавлении заказа: " + e.getMessage());
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


}
