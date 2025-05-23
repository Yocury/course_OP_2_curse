package view;

import Data.DataService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class BatchesPage extends PagePanel {
    private final DataService dataService;
    private MainFrame mainFrame;

    public BatchesPage(MainFrame mainFrame) {
        super("Партии");
        this.mainFrame = mainFrame;
        dataService = new DataService();
    }


    public BatchesPage() {
        super("Партии");
        dataService = new DataService();
    }

    private void redirection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите партию для фильтрации.");
            return;
        }
        String batchId = table.getValueAt(selectedRow, 0).toString();
        mainFrame.showFilteredOrders(batchId);
    }


    @Override
    protected void addCustomButtons() { //Передаем кнопки.

        //создаем кнопки для их передачи
        JButton filterButton = new JButton("Фильтрация по партиям");
        addButtonToPanel(filterButton);
        filterButton.addActionListener(e -> redirection());
        JButton analysisButton = new JButton("Анализ партии");
        addButtonToPanel(analysisButton);
        analysisButton.addActionListener(e -> showAnalysis());
    }

    private void showAnalysis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите партию для анализа.");
            return;
        }
        String batchIdStr = table.getValueAt(selectedRow, 0).toString();
        int batchId = Integer.parseInt(batchIdStr);

        DataService.BatchAnalysis result = dataService.analyzeBatch(batchId);
        if (result == null) {
            JOptionPane.showMessageDialog(this, "Партия не найдена.");
            return;
        }

        String message = String.format(
                "Анализ партии №%d:\n" +
                        "Общее количество товара: %d\n" +
                        "Количество заказов: %d\n" +
                        "Продано товара: %d\n" +
                        "Остаток товара: %d\n" +
                        "Стоимость партии: %d\n" +
                        "Общая сумма по заказам: %d\n" +
                        "Средняя цена за единицу (покупка): %.2f\n" +
                        "Средняя цена за единицу (продажа): %.2f\n" +
                        "Доход с партии: %d",
                result.batchId,
                result.totalCount,
                result.ordersCount,
                result.soldCount,
                result.remainingCount,
                result.batchAmount,
                result.ordersTotalSum,
                result.avgPurchasePricePerUnit,
                result.avgPricePerUnit,
                result.profit
        );

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Анализ партии", JOptionPane.INFORMATION_MESSAGE);
    }


    @Override
    public boolean CheckForEdit(int column) {
        return ((column > 1) && (column != 3)) && (column != 6) && (column != 7);
    }

    @Override
    public String getColumnNameByIndex(int column) {
        ArrayList<String> columnNames = new ArrayList<>();
        Collections.addAll(columnNames, "id", "provider", "date", "amount", "status", "count", "avg_price", "remainder");
        return columnNames.get(column);
    }

    @Override
    public void updateData() {
        var batches = dataService.getButchesFromDB();
        String[][] tableData = new String[batches.size()][8]; // Увеличиваем размер массива до 8 для нового столбца
        for (int i = 0; i < batches.size(); i++) {
            tableData[i] = batches.get(i); // Данные партий
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Источник", "Дата", "Стоимость", "Состояние", "Количество товара в партии", "Средняя цена", "Остаток товара"}
        ));
    }
}
