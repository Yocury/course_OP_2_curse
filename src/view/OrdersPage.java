package view;

import Data.DataService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class OrdersPage extends PagePanel {
    private DataService dataService;
    private JButton searchButton;
    private JButton filterButton;

    public OrdersPage() {
        super("Заказы");
        dataService = new DataService(); // Логика получения данных
    }

    @Override
    public boolean CheckForEdit(int column)
    {
        return (column > 1) && (column != 4);
    }

    @Override
    protected void addCustomButtons() { //Передаем кнопки.

        //создаем кнопки для их передачи
        searchButton = new JButton("Поиск");
        filterButton = new JButton("Фильтрация по партиям");
        addButtonToPanel(searchButton);
        addButtonToPanel(filterButton);
        filterButton.addActionListener(e -> filteringOrdersToBatches());
    }

    @Override
    public String getColumnNameByIndex(int column) {
        ArrayList<String> columnNames = new ArrayList<>();
        Collections.addAll(columnNames, "id", "sourse", "count", "street","building","ID batches", "date", "number", "status");
        return columnNames.get(column);
    }

    protected void filteringOrdersToBatches() {
        String butchesId;
        butchesId = JOptionPane.showInputDialog(this, "Введите партию");
        var orders = dataService.getFilterOrdersDB(butchesId);
        String[][] tableData = new String[orders.size()][8]; // Увеличиваем размер массива
        for (int i = 0; i < orders.size(); i++) {
            tableData[i] = orders.get(i); // Данные заказа
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Источник", "Кол-во", "Улица","Дом","ID партии", "Дата", "Номер телефона", "Состояние"} // Добавляем "ID"
        ));
    }

    public void showOrdersForBatch(String batchId) {
        var orders = dataService.getFilterOrdersDB(batchId);
        String[][] tableData = new String[orders.size()][9]; // Убедитесь, что размер совпадает с количеством столбцов
        for (int i = 0; i < orders.size(); i++) {
            tableData[i] = orders.get(i);
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Источник", "Кол-во", "Улица","Дом","ID партии", "Дата", "Номер телефона", "Состояние","Стоимость за ед."}
        ));
    }



    @Override
    public void updateData() {
        var orders = dataService.getOrdersFromDB();
        String[][] tableData = new String[orders.size()][9]; // Увеличиваем размер массива
        for (int i = 0; i < orders.size(); i++) {
            tableData[i] = orders.get(i); // Данные заказа
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Источник", "Кол-во", "Улица","Дом","ID партии", "Дата", "Номер телефона", "Состояние","Стоимость за ед."} // Добавляем "ID"
        ));
    }
}
