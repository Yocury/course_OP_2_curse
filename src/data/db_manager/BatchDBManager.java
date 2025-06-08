package data.db_manager;

import domain.entities.Batch;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для управления партиями в базе данных.
 * Реализует все необходимые операции с таблицей batches.
 */
public class BatchDBManager extends DBManager {
    private static final String TABLE_NAME = "batches";

    /**
     * Загружает все партии из базы данных.
     * Для каждой партии вычисляется средняя цена (amount / count).
     * @return список всех партий или null в случае ошибки
     */
    @Override
    public List<Batch> load() {
        List<Batch> batches = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + 
                     " ORDER BY CASE status " +
                     "WHEN 'В продаже' THEN 1 " +
                     "WHEN 'В обработке' THEN 2 " +
                     "WHEN 'Продана' THEN 3 " +
                     "WHEN 'Отменена' THEN 4 " +
                     "ELSE 5 END, id ASC"; // Добавляем сортировку по id для стабильности порядка

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String provider = rs.getString("provider");
                String date = rs.getString("date");
                int amount = rs.getInt("amount");
                String status = rs.getString("status");
                int count = rs.getInt("count");
                int avgPrice = count > 0 ? amount / count : 0; // подсчёт средней цены.

                Batch batch = new Batch(id, provider, date, amount, status, count, avgPrice);
                batches.add(batch);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке партий: " + e.getMessage());
        }
        return batches;
    }

    /**
     * Добавляет новую партию в базу данных.
     * @param entity объект партии для добавления
     * @throws IllegalArgumentException если передан объект не типа Batch
     */
    @Override
    public void add(Object entity) {
        if (!(entity instanceof Batch)) {
            throw new IllegalArgumentException("Entity must be a Batch");
        }
        Batch batch = (Batch) entity;
        
        if (connection == null) {
            System.err.println("Нет подключения к базе данных!");
            return;
        }

        String sql = "INSERT INTO " + TABLE_NAME + " " +
                "(id, provider, amount, status, date, count) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, batch.getId());
            pstmt.setString(2, batch.getProvider());
            pstmt.setInt(3, batch.getAmount());
            pstmt.setString(4, batch.getStatus());
            pstmt.setString(5, batch.getDate());
            pstmt.setInt(6, batch.getCount());
            pstmt.executeUpdate();
            System.out.println("Партия добавлена в базу данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении партии: " + e.getMessage());
        }
    }

    /**
     * Обновляет существующую партию в базе данных.
     * @param entity объект партии для обновления
     * @throws IllegalArgumentException если передан объект не типа Batch
     */
    @Override
    public void update(Object entity) {
        if (!(entity instanceof Batch)) {
            throw new IllegalArgumentException("Entity must be a Batch");
        }
        Batch batch = (Batch) entity;

        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "provider = ?, amount = ?, status = ?, date = ?, count = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, batch.getProvider());
            pstmt.setInt(2, batch.getAmount());
            pstmt.setString(3, batch.getStatus());
            pstmt.setString(4, batch.getDate());
            pstmt.setInt(5, batch.getCount());
            pstmt.setInt(6, batch.getId());
            pstmt.executeUpdate();
            System.out.println("Партия обновлена в базе данных.");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении партии: " + e.getMessage());
        }
    }

    /**
     * Удаляет партию из базы данных по ID.
     * @param id ID партии для удаления
     * @throws SQLException если произошла ошибка при удалении
     */
    @Override
    public void delete(int id) throws SQLException {
        super.delete(TABLE_NAME, id);
    }

    /**
     * Обновляет статус партии в базе данных.
     * @param batchId ID партии
     * @param status новый статус
     * @throws SQLException если произошла ошибка при обновлении
     */
    public void updateStatus(int batchId, String status) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, batchId);
            pstmt.executeUpdate();
            System.out.println("Статус партии обновлен в базе данных.");
        }
    }

    /**
     * Загружает партии, отфильтрованные по статусу.
     * Для каждой партии вычисляется средняя цена (amount / count).
     * @param status статус для фильтрации
     * @return список отфильтрованных партий или null в случае ошибки
     */
    public List<Batch> loadByStatus(String status) {
        List<Batch> batches = new ArrayList<>();
        if (connection == null) {
            System.err.println("Нет подключения к базе.");
            return null;
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = ?" +
                     " ORDER BY CASE status " +
                     "WHEN 'В продаже' THEN 1 " +
                     "WHEN 'В обработке' THEN 2 " +
                     "WHEN 'Продана' THEN 3 " +
                     "WHEN 'Отменена' THEN 4 " +
                     "ELSE 5 END, id ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String provider = rs.getString("provider");
                    String date = rs.getString("date");
                    int amount = rs.getInt("amount");
                    String batchStatus = rs.getString("status");
                    int count = rs.getInt("count");
                    int avgPrice = count > 0 ? amount / count : 0; // Calculate average price

                    Batch batch = new Batch(id, provider, date, amount, batchStatus, count, avgPrice);
                    batches.add(batch);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке партий по статусу: " + e.getMessage());
        }
        return batches;
    }
}
