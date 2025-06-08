package data.db_manager;

import domain.entities.Expenses;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для управления расходами в базе данных.
 * Реализует все необходимые операции с таблицей expenses.
 */
public class ExpensesDBManager extends DBManager {
    private static final String TABLE_NAME = "expenses";

    /**
     * Загружает все расходы из базы данных.
     * @return список всех расходов или null в случае ошибки
     */
    @Override
    public List<Expenses> load() {
        List<Expenses> expenses = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME;

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Expenses expense = new Expenses(
                    rs.getInt("id"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getInt("amount"),
                    rs.getString("date"),
                    rs.getInt("batch_id")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке расходов: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Добавляет новый расход в базу данных.
     * @param entity объект расхода для добавления
     * @throws IllegalArgumentException если передан объект не типа Expenses
     */
    @Override
    public void add(Object entity) {
        if (!(entity instanceof Expenses)) {
            throw new IllegalArgumentException("Entity must be an Expenses");
        }
        Expenses expense = (Expenses) entity;
        
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO " + TABLE_NAME + " " +
                "(id, description, type, amount, date, batch_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expense.getId());
            pstmt.setString(2, expense.getDescription());
            pstmt.setString(3, expense.getType());
            pstmt.setInt(4, expense.getAmount());
            pstmt.setString(5, expense.getDate());
            pstmt.setInt(6, expense.getBatchId());
            pstmt.executeUpdate();
            System.out.println("Расход добавлен в базу данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении расхода: " + e.getMessage());
        }
    }

    /**
     * Обновляет существующий расход в базе данных.
     * @param entity объект расхода для обновления
     * @throws IllegalArgumentException если передан объект не типа Expenses
     */
    @Override
    public void update(Object entity) {
        if (!(entity instanceof Expenses)) {
            throw new IllegalArgumentException("Entity must be an Expenses");
        }
        Expenses expense = (Expenses) entity;

        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "description = ?, type = ?, amount = ?, date = ?, batch_id = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, expense.getDescription());
            pstmt.setString(2, expense.getType());
            pstmt.setInt(3, expense.getAmount());
            pstmt.setString(4, expense.getDate());
            pstmt.setInt(5, expense.getBatchId());
            pstmt.setInt(6, expense.getId());
            pstmt.executeUpdate();
            System.out.println("Расход обновлен в базе данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении расхода: " + e.getMessage());
        }
    }

    /**
     * Удаляет расход из базы данных по ID.
     * @param id ID расхода для удаления
     * @throws SQLException если произошла ошибка при удалении
     */
    @Override
    public void delete(int id) throws SQLException {
        super.delete(TABLE_NAME, id);
    }

    /**
     * Загружает расходы, отфильтрованные по типу.
     * @param type тип расхода для фильтрации
     * @return список отфильтрованных расходов или null в случае ошибки
     */
    public List<Expenses> loadByType(String type) {
        List<Expenses> expenses = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE type = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Expenses expense = new Expenses(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getInt("amount"),
                        rs.getString("date"),
                        rs.getInt("batch_id")
                    );
                    expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке расходов по типу: " + e.getMessage());
        }
        return expenses;
    }
}
